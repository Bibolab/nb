package administrator.model;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.exponentus.common.model.SimpleReferenceEntity;

@Entity
@Table(name = "_sentences")
@NamedQuery(name = "Sentence.findAll", query = "SELECT m FROM Sentence AS m ORDER BY m.regDate")
public class Sentence extends SimpleReferenceEntity {

	private int hits;

}
