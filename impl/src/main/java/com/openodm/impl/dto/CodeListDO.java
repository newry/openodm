package com.openodm.impl.dto;

public class CodeListDO {
	private Long id;

	private String oid;

	private String name;

	private String description;

	private String dataType;

	private String extCodeId;

	private String codeListExtensible;

	private String CDISCSubmissionValue;

	private String CDISCSynonym;

	private String preferredTerm;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the oid
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * @param oid the oid to set
	 */
	public void setOid(String oid) {
		this.oid = oid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the extCodeId
	 */
	public String getExtCodeId() {
		return extCodeId;
	}

	/**
	 * @param extCodeId the extCodeId to set
	 */
	public void setExtCodeId(String extCodeId) {
		this.extCodeId = extCodeId;
	}

	/**
	 * @return the codeListExtensible
	 */
	public String getCodeListExtensible() {
		return codeListExtensible;
	}

	/**
	 * @param codeListExtensible the codeListExtensible to set
	 */
	public void setCodeListExtensible(String codeListExtensible) {
		this.codeListExtensible = codeListExtensible;
	}

	/**
	 * @return the cDISCSubmissionValue
	 */
	public String getCDISCSubmissionValue() {
		return CDISCSubmissionValue;
	}

	/**
	 * @param cDISCSubmissionValue the cDISCSubmissionValue to set
	 */
	public void setCDISCSubmissionValue(String cDISCSubmissionValue) {
		CDISCSubmissionValue = cDISCSubmissionValue;
	}

	/**
	 * @return the cDISCSynonym
	 */
	public String getCDISCSynonym() {
		return CDISCSynonym;
	}

	/**
	 * @param cDISCSynonym the cDISCSynonym to set
	 */
	public void setCDISCSynonym(String cDISCSynonym) {
		CDISCSynonym = cDISCSynonym;
	}

	/**
	 * @return the preferredTerm
	 */
	public String getPreferredTerm() {
		return preferredTerm;
	}

	/**
	 * @param preferredTerm the preferredTerm to set
	 */
	public void setPreferredTerm(String preferredTerm) {
		this.preferredTerm = preferredTerm;
	}
}
