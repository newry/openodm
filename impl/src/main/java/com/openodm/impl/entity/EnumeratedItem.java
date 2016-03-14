package com.openodm.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ENUMERATED_ITEM")
@DynamicUpdate
public class EnumeratedItem extends PersistentObject {

	private static final long serialVersionUID = 2872152713034624883L;
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

	@ManyToOne(targetEntity = CodeList.class, optional = false)
	@JoinColumn(name = "CODE_LIST_ID", nullable = false)
	private CodeList codeList;

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

	public CodeList getCodeList() {
		return codeList;
	}

	public void setCodeList(CodeList codeList) {
		this.codeList = codeList;
	}

	@Override
	public String toString() {
		return "EnumeratedItem [id=" + id + ", extCodeId=" + extCodeId
				+ ", codedValue=" + codedValue + ", CDISCSynonym="
				+ CDISCSynonym + ", CDISCDefinition=" + CDISCDefinition
				+ ", PreferredTerm=" + PreferredTerm + ", codeList=" + codeList
				+ "]";
	}

}
