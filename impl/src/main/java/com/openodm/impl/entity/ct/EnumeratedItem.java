package com.openodm.impl.entity.ct;

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
@Table(name = "ENUMERATED_ITEM")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class EnumeratedItem extends AbstractEnumeratedItem {

	private static final long serialVersionUID = 2872152713034624883L;
	@ManyToOne(targetEntity = CodeList.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "CODE_LIST_ID", nullable = false)
	@JsonIgnore
	private CodeList codeList;

	@Transient
	private boolean customized = false;

	@Transient
	private boolean extended = false;

	@Transient
	private boolean included;

	public CodeList getCodeList() {
		return codeList;
	}

	public void setCodeList(CodeList codeList) {
		this.codeList = codeList;
	}

	public boolean isCustomized() {
		return customized;
	}

	public void setCustomized(boolean customized) {
		this.customized = customized;
	}

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
	}

	public boolean isIncluded() {
		return included;
	}

	public void setIncluded(boolean included) {
		this.included = included;
	}

}
