package administrator.init;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.exponentus.dataengine.jpa.constants.AppCode;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.env.Site;
import com.exponentus.localization.LanguageCode;
import com.exponentus.server.Server;

import administrator.model.Application;
import administrator.model.Language;

public class ServerConst {

	public static Language getLanguage(LanguageCode code) {
		Map<LanguageCode, String> langName = new HashMap<LanguageCode, String>();

		if (code == LanguageCode.ENG) {
			langName.put(LanguageCode.ENG, "English");
			langName.put(LanguageCode.KAZ, "Ангылсша");
			langName.put(LanguageCode.RUS, "Английский");
			langName.put(LanguageCode.BUL, "Англиски");
			langName.put(LanguageCode.CHI, "英语");
			langName.put(LanguageCode.DEU, "Englisch");
			langName.put(LanguageCode.POR, "Inglês");
			langName.put(LanguageCode.SPA, "Inglés");
		} else if (code == LanguageCode.KAZ) {
			langName.put(LanguageCode.ENG, "Kazakh");
			langName.put(LanguageCode.KAZ, "Қазақ");
			langName.put(LanguageCode.RUS, "Казахский");
			langName.put(LanguageCode.BUL, "Казахски");
			langName.put(LanguageCode.CHI, "哈萨克人");
			langName.put(LanguageCode.DEU, "Kasachisch");
			langName.put(LanguageCode.POR, "Kazakh");
			langName.put(LanguageCode.SPA, "Kazajo");
		} else if (code == LanguageCode.RUS) {
			langName.put(LanguageCode.ENG, "Russian");
			langName.put(LanguageCode.KAZ, "Орысша");
			langName.put(LanguageCode.RUS, "Русский");
			langName.put(LanguageCode.BUL, "Руски");
			langName.put(LanguageCode.CHI, "Russian");
			langName.put(LanguageCode.DEU, "Russian");
			langName.put(LanguageCode.POR, "Russo");
			langName.put(LanguageCode.SPA, "Ruso");
		} else if (code == LanguageCode.BUL) {
			langName.put(LanguageCode.ENG, "Bulgarian");
			langName.put(LanguageCode.KAZ, "Болгар");
			langName.put(LanguageCode.RUS, "Болгарский");
			langName.put(LanguageCode.BUL, "Български");
			langName.put(LanguageCode.CHI, "保加利亚语");
			langName.put(LanguageCode.DEU, "Bulgarisch");
			langName.put(LanguageCode.POR, "Búlgaro");
			langName.put(LanguageCode.SPA, "Búlgaro");
		} else if (code == LanguageCode.CHI) {
			langName.put(LanguageCode.ENG, "Chinese");
			langName.put(LanguageCode.KAZ, "Қытай");
			langName.put(LanguageCode.RUS, "Китайский");
			langName.put(LanguageCode.BUL, "Китаиски");
			langName.put(LanguageCode.CHI, "中文");
			langName.put(LanguageCode.DEU, "Chinesisch");
			langName.put(LanguageCode.POR, "Chinês");
			langName.put(LanguageCode.SPA, "Chino");
		} else if (code == LanguageCode.DEU) {
			langName.put(LanguageCode.ENG, "German");
			langName.put(LanguageCode.KAZ, "Неміс");
			langName.put(LanguageCode.RUS, "Немецкий");
			langName.put(LanguageCode.BUL, "Немски");
			langName.put(LanguageCode.CHI, "德语");
			langName.put(LanguageCode.DEU, "Deutsche");
			langName.put(LanguageCode.POR, "Alemão");
			langName.put(LanguageCode.SPA, "Alemán");
		} else if (code == LanguageCode.POR) {
			langName.put(LanguageCode.ENG, "Portuguese");
			langName.put(LanguageCode.KAZ, "Португал");
			langName.put(LanguageCode.RUS, "Португальский");
			langName.put(LanguageCode.BUL, "Португалски");
			langName.put(LanguageCode.CHI, "葡萄牙语");
			langName.put(LanguageCode.DEU, "Portugiesisch");
			langName.put(LanguageCode.POR, "Português");
			langName.put(LanguageCode.SPA, "Portugués");
		} else if (code == LanguageCode.SPA) {
			langName.put(LanguageCode.ENG, "Spanish");
			langName.put(LanguageCode.KAZ, "Испандық");
			langName.put(LanguageCode.RUS, "Испанский");
			langName.put(LanguageCode.BUL, "Испански");
			langName.put(LanguageCode.CHI, "西班牙语");
			langName.put(LanguageCode.DEU, "Spanisch");
			langName.put(LanguageCode.POR, "Еspanhol");
			langName.put(LanguageCode.SPA, "Español");
		}

		Language entity = new Language();
		entity.setName(code.toString());
		entity.setLocalizedName(langName);
		entity.setCode(code);

		return entity;
	}

	public static Application getApplication(Site site) {
		Application entity = new Application();
		try {
			Class<?> c = Class.forName(site.name.toLowerCase() + ".init.AppConst");
			try {
				Field f = c.getDeclaredField("NAME");
				entity.setName((String) f.get(null));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				entity.setName(site.name);
			}

			try {
				Field f = c.getDeclaredField("CODE");
				entity.setCode((AppCode) f.get(null));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				entity.setCode(AppCode.CUSTOM);
			}

			try {
				Field f = c.getDeclaredField("DEFAULT_URL");
				entity.setDefaultURL((String) f.get(null));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				entity.setDefaultURL("p");
			}

			Map<LanguageCode, String> localizedName = new HashMap<LanguageCode, String>();
			for (LanguageCode lc1 : Environment.langs) {
				try {
					Field f = c.getDeclaredField("NAME_" + lc1.name());
					localizedName.put(lc1, (String) f.get(null));
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					localizedName.put(lc1, entity.getName());
				}
			}
			entity.setLocalizedName(localizedName);
		} catch (ClassNotFoundException e) {
			Map<LanguageCode, String> localizedName = new HashMap<LanguageCode, String>();
			for (LanguageCode lc1 : Environment.langs) {
				localizedName.put(lc1, site.name);
			}
			entity.setLocalizedName(localizedName);
			entity.setName(site.name);
			if (site.name.equalsIgnoreCase(EnvConst.ADMINISTRATOR_APP_NAME)) {
				entity.setCode(AppCode.ADMINISTRATOR);
				entity.setDefaultURL("p?id=user-view");
			} else {
				Server.logger.errorLogEntry(e.toString());
				entity.setCode(AppCode.CUSTOM);
			}
		}
		return entity;
	}
}
