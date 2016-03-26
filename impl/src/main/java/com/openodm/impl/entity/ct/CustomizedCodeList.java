package com.openodm.impl.entity.ct;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "CUSTOMIZED_CODE_LIST")
@DynamicUpdate
public class CustomizedCodeList extends AbstractCodeList {
	private static final long serialVersionUID = 717305204879022737L;
	@Transient
	private boolean customized = true;

	public boolean isCustomized() {
		return customized;
	}

	public void setCustomized(boolean customized) {
		this.customized = customized;
	}
}
