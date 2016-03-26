package com.openodm.impl.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class AbstractCodeList extends PersistentObject {

	private static final long serialVersionUID = 717305204879022737L;

	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "oid", nullable = true, length = 255)
	private String oid;

	@Column(name = "name", nullable = false, length = 255, unique = true)
	private String name;

	@Column(name = "description", nullable = true, length = 4096)
	private String description;

	@Column(name = "data_type", nullable = false, length = 255)
	private String dataType = "text";

	@Column(name = "ext_code_id", nullable = true, length = 255)
	private String extCodeId;

	@Column(name = "extensible", nullable = true, length = 255)
	private String codeListExtensible = "Yes";

	@Column(name = "CDISC_Submission_Value", nullable = true, length = 255)
	private String CDISCSubmissionValue;

	@Column(name = "CDISC_Synonym", nullable = true, length = 255)
	private String CDISCSynonym;

	@Column(name = "Preferred_Term", nullable = true, length = 255)
	private String preferredTerm;

	public Long getId() {
		return id;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getExtCodeId() {
		return extCodeId;
	}

	public void setExtCodeId(String extCodeId) {
		this.extCodeId = extCodeId;
	}

	public String getCodeListExtensible() {
		return codeListExtensible;
	}

	public void setCodeListExtensible(String codeListExtensible) {
		this.codeListExtensible = codeListExtensible;
	}

	public String getCDISCSubmissionValue() {
		return CDISCSubmissionValue;
	}

	public void setCDISCSubmissionValue(String cDISCSubmissionValue) {
		CDISCSubmissionValue = cDISCSubmissionValue;
	}

	public String getCDISCSynonym() {
		return CDISCSynonym;
	}

	public void setCDISCSynonym(String cDISCSynonym) {
		CDISCSynonym = cDISCSynonym;
	}

	public String getPreferredTerm() {
		return preferredTerm;
	}

	public void setPreferredTerm(String preferredTerm) {
		this.preferredTerm = preferredTerm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCodeList other = (AbstractCodeList) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
