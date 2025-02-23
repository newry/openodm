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
@Table(name = "CUSTOMIZED_ENUMERATED_ITEM")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
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
