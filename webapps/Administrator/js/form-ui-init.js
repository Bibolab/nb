$(function() {
    $.datepicker.setDefaults($.datepicker.regional['ru']);

    // fix fox memorize checkbox, blyat'
    $(':checkbox.all').attr('checked', false);

    $('input[type=number]').each(function() {
        $(this).attr({
            'type': 'text',
            'data-type': 'number'
        }).numericField();
    });

    $('input[type=date]').each(function() {
        $(this).attr({
            'type': 'text',
            'data-type': 'date',
            /*'readonly': 'readonly',*/
        }).datepicker({ dateFormat: nb.options.dateFormat });
    });

    // init action
    $('[data-action=save_and_close]').click(function(event) {
        event.preventDefault();
        nb.submitForm(nb.getForm(this));
    });

    $('[data-action=test_message_xmpp], [data-action=test_message_slack], [data-action=test_message_email]').click(function(event) {
        event.preventDefault();
        var msgtype = $(this).data("msgtype");
        var addr = $("input[name="+ msgtype +"]").val();

        $.ajax({
            url: 'Provider?id=sendtestmsg-action',
            type: 'POST',
            cache: false,
            data: "type="+msgtype+"&addr="+addr ,
            dataType: 'json',
            success: function(result) {
                alert(result.type)
            },
            error: function(err) {
                console.log(err);
            },
            complete: function() {

            }
        });

    });

    function uploadUpdate(fileInput, fsid) {
        var formData = new FormData();
        formData.append('file', fileInput.files[0]);
        formData.append('fsid', fsid);
        formData.append('fieldname', fileInput.name);
        var time = new Date().getTime();

        return $.ajax({
            url: 'UploadFile?time=' + time,
            type: 'POST',
            cache: false,
            contentType: false,
            processData: false,
            data: formData,
            dataType: 'json',
            xhr: function() {
                var customXhr = $.ajaxSettings.xhr();
                if (customXhr.upload) {
                    customXhr.upload.addEventListener('progress', onProgress, false);
                }
                return customXhr;
            },
            success: function(result) {
                var fileName = result.files[0];
                if (fileInput.name == 'uporder') {
                    $(".update-order").text(fileName);
                } else {
                    //renderFilePanel(fileName, fsid);
                    //clearLocalStorage();
                    //$("#btn-update-file-excel").addClass("disabled");
                }
                return result;
            },
            error: function(err) {
                console.log(err);
            },
            complete: function() {
                $('progress').attr({
                    value: 0,
                    max: 100
                });
                fileInput.form.reset();
                insertParam('fsid', fsid);
                if (fileInput.name != 'uporder') {
                    insertParam('step', 1);
                    insertParam('uploadtype', $("input[name=uploadtype]:checked").val());
                    reloadPage();
                }
            }
        });
    }



    $('[data-action=delete_document]').click(function(event) {
        event.preventDefault();

        var docids = nb.getSelectedEntityIDs('docid');
        if (!docids.length) {
            return;
        }

        nb.xhrDelete(location.href + '&docid=' + docids.join('&docid=')).then(function() {
            location.reload();
        });
    });

    $('[data-action=delete_document]').attr('disabled', true);
    $(':checkbox').bind('change', function() {
        var countChecked = $('[name=docid]:checked').length;
        $('[data-action=delete_document]').attr('disabled', countChecked === 0);
    });

    $('[name=docid]:checked').attr('checked', false);
});
