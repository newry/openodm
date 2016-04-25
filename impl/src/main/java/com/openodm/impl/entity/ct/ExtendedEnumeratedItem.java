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
@Table(name = "EXTENDED_ENUMERATED_ITEM")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class ExtendedEnumeratedItem extends AbstractEnumeratedItem {

	private static final long serialVersionUID = -2798346117093927001L;

	@ManyToOne(targetEntity = CodeList.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "CODE_LIST_ID", nullable = true)
	@JsonIgnore
	private CodeList codeList;

	@ManyToOne(targetEntity = ControlTerminology.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Control_Terminology_ID", nullable = true)
	@JsonIgnore
	private ControlTerminology controlTerminology;

	@Transient
	private boolean customized = true;

	@Transient
	private boolean extended = true;

	public boolean isCustomized() {
		return customized;
	}

	public void setCustomized(boolean customized) {
		this.customized = customized;
	}

	public CodeList getCodeList() {
		return codeList;
	}

	public void setCodeList(CodeList codeList) {
		this.codeList = codeList;
	}

	public ControlTerminology getControlTerminology() {
		return controlTerminology;
	}

	public void setControlTerminology(ControlTerminology controlTerminology) {
		this.controlTerminology = controlTerminology;
	}

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
	}

}
