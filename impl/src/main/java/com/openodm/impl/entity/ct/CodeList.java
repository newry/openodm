package com.openodm.impl.entity.ct;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "CODE_LIST")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class CodeList extends AbstractCodeList {

	private static final long serialVersionUID = 5332668059816056373L;

	@ManyToOne(targetEntity = CTVersion.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "META_DATA_VERSION_ID", nullable = false)
	@JsonIgnore
	private CTVersion metaDataVersion;

	@Column(name = "Search_Term", nullable = false, length = 4096)
	private String searchTerm;

	@Transient
	private boolean customized;

	@Transient
	private boolean added;

	public boolean isCustomized() {
		return customized;
	}

	public CTVersion getMetaDataVersion() {
		return metaDataVersion;
	}

	public void setMetaDataVersion(CTVersion metaDataVersion) {
		this.metaDataVersion = metaDataVersion;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public boolean isAdded() {
		return added;
	}

	public void setAdded(boolean added) {
		this.added = added;
	}

}
