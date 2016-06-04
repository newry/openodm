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

	@Column(name = "sql", nullable = true, length = 1024 * 1024)
	private String sql;

	@ManyToOne(targetEntity = SDTMProjectLibrary.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_PROJECT_LIBRARY_ID", nullable = false)
	@JsonIgnore
	private SDTMProjectLibrary sdtmProjectLibrary;

	@ManyToOne(targetEntity = SDTMDomain.class, optional = false, fetch = FetchType.EAGER)
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

}
