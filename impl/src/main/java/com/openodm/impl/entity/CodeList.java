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
@Table(name = "CODE_LIST")
@DynamicUpdate
public class CodeList extends PersistentObject {

	private static final long serialVersionUID = -8200234754662897172L;
	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "oid", nullable = false, length = 255)
	private String oid;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", nullable = false, length = 4096)
	private String description;

	@Column(name = "data_type", nullable = false, length = 255)
	private String dataType;

	@Column(name = "ext_code_id", nullable = false, length = 255)
	private String extCodeId;

	@Column(name = "extensible", nullable = false, length = 255)
	private String codeListExtensible;

	@Column(name = "CDISC_Submission_Value", nullable = false, length = 255)
	private String CDISCSubmissionValue;

	@Column(name = "CDISC_Synonym", nullable = false, length = 255)
	private String CDISCSynonym;

	@Column(name = "Preferred_Term", nullable = false, length = 255)
	private String preferredTerm;

	@ManyToOne(targetEntity = MetaDataVersion.class, optional = false)
	@JoinColumn(name = "META_DATA_VERSION_ID", nullable = false)
	private MetaDataVersion metaDataVersion;

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

	public MetaDataVersion getMetaDataVersion() {
		return metaDataVersion;
	}

	public void setMetaDataVersion(MetaDataVersion metaDataVersion) {
		this.metaDataVersion = metaDataVersion;
	}

	@Override
	public String toString() {
		return "CodeList [id=" + id + ", oid=" + oid + ", name=" + name
				+ ", description=" + description + ", dataType=" + dataType
				+ ", extCodeId=" + extCodeId + ", codeListExtensible="
				+ codeListExtensible + ", CDISCSubmissionValue="
				+ CDISCSubmissionValue + ", CDISCSynonym=" + CDISCSynonym
				+ ", PreferredTerm=" + preferredTerm + ", metaDataVersion="
				+ metaDataVersion + "]";
	}

}
