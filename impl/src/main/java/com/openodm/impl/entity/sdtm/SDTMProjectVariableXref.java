package com.openodm.impl.entity.sdtm;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.openodm.impl.entity.PersistentObject;
import com.openodm.impl.entity.ct.CodeList;

@Entity
@Table(name = "SDTM_PROJECT_VARIABLE_XREF")
@DynamicUpdate
@JsonInclude(Include.NON_NULL)
public class SDTMProjectVariableXref extends PersistentObject {

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

	@Column(name = "length", nullable = true)
	private Integer length;

	@ManyToOne(targetEntity = SDTMProject.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_PROJECT_ID", nullable = false)
	@JsonIgnore
	private SDTMProject sdtmProject;

	@ManyToOne(targetEntity = SDTMVariable.class, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "SDTM_VARIABLE_ID", nullable = false)
	private SDTMVariable sdtmVariable;

	@ManyToOne(targetEntity = SDTMDomain.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_DOMAIN_ID", nullable = false)
	@JsonIgnore
	private SDTMDomain sdtmDomain;

	@ManyToOne(targetEntity = CodeList.class, optional = true)
	@JoinColumn(name = "code_list_id", nullable = true)
	private CodeList codeList;

	@ManyToMany(targetEntity = SDTMOrigin.class)
	@JoinTable(name = "SDTM_PROJECT_VARIABLE_ORIGIN_XREF", joinColumns = { @JoinColumn(name = "SDTM_PROJECT_VARIABLE_XREF_ID") }, inverseJoinColumns = { @JoinColumn(name = "sdtm_origin_id") })
	private List<SDTMOrigin> origins;

	@Column(name = "crf_page_no", nullable = true, length = 32)
	private String crfPageNo;

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

	public CodeList getCodeList() {
		return codeList;
	}

	public void setCodeList(CodeList codeList) {
		this.codeList = codeList;
	}

	public String getCrfPageNo() {
		return crfPageNo;
	}

	public void setCrfPageNo(String crfPageNo) {
		this.crfPageNo = crfPageNo;
	}

	public List<SDTMOrigin> getOrigins() {
		return origins;
	}

	public void setOrigins(List<SDTMOrigin> origins) {
		this.origins = origins;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

}
