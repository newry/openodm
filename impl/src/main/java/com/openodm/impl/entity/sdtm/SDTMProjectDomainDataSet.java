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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.openodm.impl.entity.PersistentObject;

@Entity
@Table(name = "SDTM_PROJECT_DOMAIN_DATASET")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class SDTMProjectDomainDataSet extends PersistentObject {

	private static final long serialVersionUID = 9195706482774759770L;
	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "name", nullable = false, length = 256)
	private String name;

	@Column(name = "join_type", nullable = false, length = 32)
	private String joinType;

	@Column(name = "meta_data", nullable = false, length = 1024 * 1024)
	private String metaData;

	@Column(name = "sql", nullable = false, length = 1024 * 1024)
	private String sql;

	@ManyToOne(targetEntity = SDTMProjectLibrary.class, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "SDTM_PROJECT_LIBRARY_ID", nullable = false)
	private SDTMProjectLibrary sdtmProjectLibrary;

	@ManyToOne(targetEntity = SDTMDomain.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_DOMAIN_ID", nullable = false)
	private SDTMDomain sdtmDomain;

	public SDTMDomain getSdtmDomain() {
		return sdtmDomain;
	}

	public void setSdtmDomain(SDTMDomain sdtmDomain) {
		this.sdtmDomain = sdtmDomain;
	}

	public Long getId() {
		return id;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJoinType() {
		return joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public SDTMProjectLibrary getSdtmProjectLibrary() {
		return sdtmProjectLibrary;
	}

	public void setSdtmProjectLibrary(SDTMProjectLibrary sdtmProjectLibrary) {
		this.sdtmProjectLibrary = sdtmProjectLibrary;
	}

}
