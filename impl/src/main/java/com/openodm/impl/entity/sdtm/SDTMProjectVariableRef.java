package com.openodm.impl.entity.sdtm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.openodm.impl.entity.PersistentObject;

@Entity
@Table(name = "SDTM_PROJECT_VARIABLE_REF")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class SDTMProjectVariableRef extends PersistentObject {

	private static final long serialVersionUID = 9195706482774759770L;
	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "core", nullable = true, length = 32)
	private String core;

	@Column(name = "order_number", nullable = false)
	private Integer orderNumber;

	@Column(name = "role", nullable = true, length = 255)
	private String role;

	@ManyToOne(targetEntity = SDTMProject.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_PROJECT_ID", nullable = false)
	@JsonIgnore
	private SDTMProject sdtmProject;

	@ManyToOne(targetEntity = SDTMVariable.class, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "SDTM_VARIABLE_ID", nullable = false)
	private SDTMVariable sdtmVariable;

	@Transient
	private SDTMDomain sdtmDomain;

	@Transient
	private boolean excluded;

	public String getCore() {
		return core;
	}

	public void setCore(String core) {
		this.core = core;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public SDTMVariable getSdtmVariable() {
		return sdtmVariable;
	}

	public void setSdtmVariable(SDTMVariable sdtmVariable) {
		this.sdtmVariable = sdtmVariable;
	}

	public Long getId() {
		return id;
	}

	public SDTMProject getSdtmProject() {
		return sdtmProject;
	}

	public void setSdtmProject(SDTMProject sdtmProject) {
		this.sdtmProject = sdtmProject;
	}

	public SDTMDomain getSdtmDomain() {
		return sdtmDomain;
	}

	public void setSdtmDomain(SDTMDomain sdtmDomain) {
		this.sdtmDomain = sdtmDomain;
	}

	public boolean isExcluded() {
		return excluded;
	}

	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}

}
