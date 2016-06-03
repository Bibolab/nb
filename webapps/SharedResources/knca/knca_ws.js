var knca = (function() {

    'use strict';

    var wd = window.document,
        noty,
        isReady = false,
        t_o,
        LOCAL_STORAGE_PREFS_KEY = 'knca_storage_prefs';
    var initPromise;

    var storage = {
        alias: 'PKCS12',
        path: '',
        keyAlias: '',
        keyType: 'SIGN',
        pwd: '',
        name: '',
        keys: []
    };

    var ws,
        pingInterval = null,
        heartbitMSG = '--heartbeat--',
        callback;

    function init() {
        if (isReady) {
            return initPromise;
        }

        noty = nb.notify({
            message: nb.getText('ncalayer_init', 'Инициализация программы подписи')
        }).show();
        nb.uiBlock();
        initPromise = initPromise || $.Deferred();
        initWSConnection();
        log('init');
        return initPromise;
    }

    function ready() {
        if (pingInterval === null) {
            pingInterval = setInterval(pingWSLayer, 10000);
        }

        restorePrefsFromLocalStorage();
        nb.uiUnblock();
        noty && noty.remove();
        noty = null;

        $(window).on('beforeunload', function() {
            if (ws !== null) {
                ws.close();
            }
        });

        render();
        isReady = true;
        initPromise.resolve('ready');
        log('ready');
    }

    function log(msg) {
        console.log('knca_ws > ', msg);
    }

    function pingWSLayer() {
        try {
            ws.send(heartbitMSG);
        } catch (error) {
            clearInterval(pingInterval);
            pingInterval = null;
            ws.close();
        }
    }

    function initWSConnection() {
        ws = new WebSocket('wss://127.0.0.1:13579/');

        ws.onclose = function(event) {
            log('ws > onclose');
            if (!event.wasClean) {

            }
        };

        ws.onerror = function() {
            log('ws > onerror');
            nb.uiUnblock();
            noty && noty.remove();
            noty = null;
            initPromise.reject('ncalayer_unavailable');
            var dlg = nb.dialog.warn({
                title: nb.getText('ncalayer_connect_error', 'Ошибка подключения к NCALayer'),
                message: '<p>Убедитесь что программа запущена.</p><a href="http://pki.gov.kz/index.php/ru/ncalayer" target="blank">Инструкция по установке</a>',
                height: 200,
                buttons: {
                    Ok: function() {
                        dlg.dialog('close');
                    }
                }
            });
        };

        ws.onopen = function() {
            ready();
        };

        ws.onmessage = function(event) {
            if (event.data === heartbitMSG) {
                return;
            }
            var result = JSON.parse(event.data);
            callback(result);
        };
    }

    function savePrefsToLocalStorage() {
        localStorage.setItem(LOCAL_STORAGE_PREFS_KEY, JSON.stringify({
            alias: storage.alias,
            keyType: storage.keyType,
            keyAlias: storage.keyAlias,
            path: storage.path,
            name: storage.name
        }));

        log('save prefs to LocalStorage');
    }

    function restorePrefsFromLocalStorage() {
        var ls = localStorage.getItem(LOCAL_STORAGE_PREFS_KEY);
        if (ls) {
            ls = JSON.parse(ls);
            storage = {
                alias: ls.alias,
                keyType: ls.keyType,
                keyAlias: ls.keyAlias,
                path: ls.path,
                name: ls.name
            };

            log('restored prefs from LocalStorage');
        }
    }

    function isValidStorage() {
        return storage.alias && storage.path && storage.keyAlias && storage.keyType && storage.pwd && storage.keys.length;
    }

    function sendMethod(method, args, cbMethod) {
        var requestJson = {
            'method': method,
            'args': args
        };

        callback = cbMethod;
        ws.send(JSON.stringify(requestJson));

        log([method, args]);
    }

    /**
     * nca layer api
     */

    function fillKeys() {
        storage.keys = [];
        sendMethod('getKeys', [storage.alias, storage.path, storage.pwd, storage.keyType], function(data) {
            if (data.errorCode === 'NONE') {
                var slots = data.result.split('\n');
                for (var i = 0; i < slots.length; i++) {
                    if (slots[i]) {
                        storage.keys.push(slots[i]);
                    }
                }
                render();
            } else {
                throw new Error(data.errorCode);
            }
        });
    }

    function browseKeyStore(callback) {
        sendMethod('browseKeyStore', [storage.alias, 'P12', storage.path], function(data) {
            if (data.errorCode === 'NONE') {
                if (data.result) {
                    storage.path = data.result;
                    storage.keys = [];
                    storage.keyAlias = '';
                    storage.name = '';
                }
                fillKeys();
                render();
            } else {
                throw new Error(data.errorCode);
            }
        });
    }

    function signXml(data) {
        var promise = $.Deferred();
        sendMethod('signXml', [storage.alias, storage.path, storage.keyAlias, storage.pwd, data], function(data) {
            if (data.errorCode === 'NONE') {
                promise.resolve({
                    sign: data.result,
                    filePath: storage.path
                });
            } else {
                promise.reject(data.errorCode);
            }
        });
        return promise;
    }

    //
    function render() {
        var edsNode = wd.getElementById('eds-property');
        if (!edsNode) {
            // html
            var htm = [];
            htm.push('<header>' + nb.getText('eds_title', 'ЭЦП') + '</header>');
            htm.push('<section>');
            htm.push('  <select name="storageAlias" class="native" style="display:none">');
            htm.push('    <option value="PKCS12" selected="selected">Ваш Компьютер</option>');
            htm.push('    <option value="AKKaztokenStore">Казтокен</option>');
            htm.push('    <option value="AKKZIDCardStore">Личное Удостоверение</option>');
            htm.push('    <option value="AKEToken72KStore">EToken Java 72k</option>');
            htm.push('    <option value="AKJaCartaStore">AK JaCarta</option>');
            htm.push('  </select>');
            htm.push('  <input type="radio" value="SIGN" name="keyType" checked="checked"/>');
            htm.push('  <button class="btn" name="chooseStorage" type="button">Выбрать ЭЦП</button>');
            htm.push('  <div class="eds-file-name"></div>');
            htm.push('  <input type="password" placeholder="' + nb.getText('password', 'Пароль') + '" style="display:none"/>');
            htm.push('  <select name="keys" class="native" style="display:none"></select>');
            htm.push('</section>');
            htm.push('<footer>');
            htm.push('  <button class="btn" type="button" name="cancel">' + nb.getText('cancel', 'Отмена') + '</button>');
            htm.push('  <button class="btn btn-primary" type="button" name="ok" disabled></button>');
            htm.push('</footer>');

            edsNode = wd.createElement('form');
            edsNode.id = 'eds-property';
            edsNode.className = 'eds';
            edsNode.autocomplete = 'off';
            edsNode.innerHTML = htm.join('');
            wd.body.appendChild(edsNode);

            setTimeout(function() {
                edsNode.reset();
            }, 100);
            //
            var overlay = wd.createElement('div');
            overlay.className = 'eds-overlay';
            edsNode.parentNode.insertBefore(overlay, edsNode.nextSibling);
            //
            $(edsNode).on('submit', function(event) {
                event.preventDefault();
            });
            $(edsNode).find('[name=chooseStorage]').on('click', function() {
                browseKeyStore();
            });
            $(edsNode).find('[type=password]').val('').on('keyup blur', function() {
                var el = this;
                storage.pwd = this.value;

                clearTimeout(t_o);
                t_o = setTimeout(function() {
                    try {
                        if (storage.pwd) {
                            el.classList.remove('invalid');
                            fillKeys();
                        }
                    } catch (e) {
                        el.classList.add('invalid');
                        storage.pwd = '';
                        el.value = '';
                    }
                    render();
                }, 260);
            });
            $(edsNode).find('[name=cancel]').on('click', function() {
                hidePropertyModal();
                dialogPromise.reject('cancel');
            });
            $(edsNode).find('[name=ok]').on('click', function() {
                if (isValidStorage()) {
                    savePrefsToLocalStorage();
                    try {
                        dialogPromise.resolve();
                    } catch (e) {
                        log(e.message);
                    }
                } else {
                    log('invalid storage');
                    log(storage);
                }
            });
        }

        // eds-file-name
        $('.eds-file-name', edsNode).each(function() {
            var fp = storage.path.replace(/\\/g, '/').split('/');
            $(this).html(fp[fp.length - 1]);
        });

        // ['RSA'|name|?|alias]
        var keysEl = $(edsNode).find('[name=keys]');
        if (storage.keys && storage.keys.length) {
            var key;
            keysEl.empty();
            for (var k in storage.keys) {
                key = storage.keys[k].split('|');
                $('<option value=' + key[3] + '>' + key[1] + '</option>').appendTo(keysEl);
                storage.keyAlias = key[3];
                storage.name = key[1];
            }
            keysEl.show();
        } else if (storage.path && storage.keyAlias && storage.name) {
            keysEl.empty();
            $('<option value=' + storage.keyAlias + '>' + storage.name + '</option>').appendTo(keysEl);
        } else {
            keysEl.empty().hide();
        }

        // show/hide
        $(edsNode).find('[name=ok]').attr('disabled', !isValidStorage());
        if (storage.path && !storage.pwd) {
            $(edsNode).find('[type=password]').show();
        }

        log('render');
    }

    function showPropertyModal(action) {
        if (action === 'sign') {
            $('#eds-property').find('[name=ok]').text(nb.getText('sign', 'Подписать'));
        } else if (action === 'verify') {
            $('#eds-property').find('[name=ok]').text(nb.getText('verify_sign', 'Проверить'));
        }
        wd.getElementById('eds-property').classList.add('open');
    }

    function hidePropertyModal() {
        wd.getElementById('eds-property').classList.remove('open');
    }

    //
    var dialogPromise;

    function resolveStorage(action) {
        var promise = $.Deferred();
        if (isValidStorage()) {
            return promise.resolve();
        } else {
            log('show select property dialog');
            dialogPromise = promise;
            promise.always(hidePropertyModal);
            showPropertyModal(action);
        }

        return promise;
    }

    // public api
    return {
        signXml: function(data) {
            return init().then(function() {
                return resolveStorage('sign').then(function() {
                    return signXml('<?xml version="1.1" encoding="UTF-8"?><sign><base64>' + data + '</base64></sign>');
                });
            });
        }
    };
})();
