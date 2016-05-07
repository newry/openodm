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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.openodm.impl.entity.PersistentObject;

@Entity
@Table(name = "SDTM_PROJECT_DOMAIN_XREF")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class SDTMProjectDomainXref extends PersistentObject {

	private static final long serialVersionUID = 9195706482774759770L;
	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "mapping_rule", nullable = true, length = 4096)
	private String mappingRule;

	@Column(name = "mapping_action", nullable = true, length = 4096)
	private String mappingAction;

	@Column(name = "order_number", nullable = false)
	private Integer orderNumber;

	@ManyToOne(targetEntity = SDTMProject.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_PROJECT_ID", nullable = false)
	@JsonIgnore
	private SDTMProject sdtmProject;

	@ManyToOne(targetEntity = SDTMDomain.class, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "SDTM_DOMAIN_ID", nullable = false)
	private SDTMDomain sdtmDomain;

	public String getMappingRule() {
		return mappingRule;
	}

	public void setMappingRule(String mappingRule) {
		this.mappingRule = mappingRule;
	}

	public String getMappingAction() {
		return mappingAction;
	}

	public void setMappingAction(String mappingAction) {
		this.mappingAction = mappingAction;
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

	public Long getId() {
		return id;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

}
