package com.openodm.impl.entity.ct;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import com.openodm.impl.entity.PersistentObject;

@MappedSuperclass
public abstract class AbstractEnumeratedItem extends PersistentObject {

	private static final long serialVersionUID = -2798346117093927001L;

	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "ext_code_id", nullable = false, length = 255)
	private String extCodeId;

	@Column(name = "coded_value", nullable = false, length = 255)
	private String codedValue;

	@Column(name = "CDISC_Synonym", nullable = true, length = 4096)
	private String CDISCSynonym;

	@Column(name = "CDISC_Definition", nullable = true, length = 4096)
	private String CDISCDefinition;

	@Column(name = "Preferred_Term", nullable = true, length = 255)
	private String PreferredTerm;

	public Long getId() {
		return id;
	}

	public String getExtCodeId() {
		return extCodeId;
	}

	public void setExtCodeId(String extCodeId) {
		this.extCodeId = extCodeId;
	}

	public String getCodedValue() {
		return codedValue;
	}

	public void setCodedValue(String codedValue) {
		this.codedValue = codedValue;
	}

	public String getCDISCSynonym() {
		return CDISCSynonym;
	}

	public void setCDISCSynonym(String cDISCSynonym) {
		CDISCSynonym = cDISCSynonym;
	}

	public String getCDISCDefinition() {
		return CDISCDefinition;
	}

	public void setCDISCDefinition(String cDISCDefinition) {
		CDISCDefinition = cDISCDefinition;
	}

	public String getPreferredTerm() {
		return PreferredTerm;
	}

	public void setPreferredTerm(String preferredTerm) {
		PreferredTerm = preferredTerm;
	}

}
