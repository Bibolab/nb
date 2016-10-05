package administrator.model;

import administrator.dao.LanguageDAO;
import com.exponentus.common.model.Attachment;
import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.util.TimeUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "_langs")
@NamedQuery(name = "Language.findAll", query = "SELECT m FROM Language AS m ORDER BY m.regDate")
public class Language extends AppEntity<UUID> {
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 7, unique = true)
    private LanguageCode code = LanguageCode.UNKNOWN;

    @Column(length = 128, unique = true)
    private String name;

    @Column(name = "localized_name")
    private Map<LanguageCode, String> localizedName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguageCode(String id) {
        this.code = LanguageCode.valueOf(id);
    }

    private int position;

    @Override
    public String toString() {
        return name;
    }

    public Map<LanguageCode, String> getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(Map<LanguageCode, String> name) {
        this.localizedName = name;
    }

    public LanguageCode getCode() {
        return code;
    }

    public void setCode(LanguageCode code) {
        this.code = code;
    }

    public String getLocalizedName(LanguageCode lang) {
        try {
            return localizedName.get(lang);
        } catch (Exception e) {
            return name;
        }
    }

    @Override
    public List<Attachment> getAttachments() {
        return new ArrayList<>();
    }

    @Override
    public void setAttachments(List<Attachment> attachments) {

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String getShortXMLChunk(_Session ses) {
        return "<lang id=\"" + code + "\">" + localizedName.get(code) + "</lang>";
    }

    @Override
    public String getFullXMLChunk(_Session ses) {
        StringBuilder chunk = new StringBuilder(1000);
        chunk.append("<regdate>" + TimeUtil.dateToStringSilently(regDate) + "</regdate>");
        if (name != null) {
            chunk.append("<name>" + name + "</name>");
        }
        if (code != LanguageCode.UNKNOWN) {
            chunk.append("<code>" + code + "</code>");
        }
        chunk.append("<position>" + position + "</position>");

        chunk.append("<localizednames>");
        LanguageDAO lDao = new LanguageDAO(ses);
        List<Language> list = lDao.findAll();
        String localizedName = "";
        for (Language l : list) {
            if (name != null) {
                localizedName = getLocalizedName(l.getCode());
            }
            chunk.append("<entry id=\"" + l.getCode() + "\">" + localizedName + "</entry>");
        }
        chunk.append("</localizednames>");

        return chunk.toString();
    }
}
