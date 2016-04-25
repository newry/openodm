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

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.openodm.impl.entity.PersistentObject;

@Entity
@Table(name = "SDTM_VARIABLE_REF")
@DynamicUpdate
public class SDTMVariableRef extends PersistentObject {
	private static final long serialVersionUID = 9195706482774759770L;
	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "mandatory", nullable = false, length = 32)
	private String mandatory;

	@Column(name = "order_number", nullable = false)
	private Integer orderNumber;

	@Column(name = "role", nullable = false, length = 32)
	private String role;

	@ManyToOne(targetEntity = SDTMDomain.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_DOMAIN_ID", nullable = false)
	private SDTMDomain sdtmDomain;

	@ManyToOne(targetEntity = SDTMVariable.class, optional = false)
	@JoinColumn(name = "SDTM_VARIABLE_ID", nullable = false)
	private SDTMVariable sdtmVariable;

	public Long getId() {
		return id;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
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

	public SDTMDomain getSdtmDomain() {
		return sdtmDomain;
	}

	public void setSdtmDomain(SDTMDomain sdtmDomain) {
		this.sdtmDomain = sdtmDomain;
	}

	public SDTMVariable getSdtmVariable() {
		return sdtmVariable;
	}

	public void setSdtmVariable(SDTMVariable sdtmVariable) {
		this.sdtmVariable = sdtmVariable;
	}

}
