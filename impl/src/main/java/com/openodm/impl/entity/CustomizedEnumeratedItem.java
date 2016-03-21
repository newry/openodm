package com.openodm.impl.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CUSTOMIZED_ENUMERATED_ITEM")
@DynamicUpdate
public class CustomizedEnumeratedItem extends AbstractEnumeratedItem {

	private static final long serialVersionUID = -2798346117093927001L;

	@ManyToOne(targetEntity = CustomizedCodeList.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMIZED_CODE_LIST_ID", nullable = true)
	@JsonIgnore
	private CustomizedCodeList customizedCodeList;

	@Transient
	private boolean customized = true;

	@Transient
	private boolean extended = false;

	public CustomizedCodeList getCustomizedCodeList() {
		return customizedCodeList;
	}

	public void setCustomizedCodeList(CustomizedCodeList customizedCodeList) {
		this.customizedCodeList = customizedCodeList;
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

}
