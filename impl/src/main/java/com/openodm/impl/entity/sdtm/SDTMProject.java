package com.openodm.impl.entity.sdtm;

import java.util.List;

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
@Table(name = "SDTM_PROJECT")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class SDTMProject extends PersistentObject {
	private static final long serialVersionUID = 2111845415050802396L;

	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", nullable = true, length = 4096)
	private String description;

	@ManyToOne(targetEntity = SDTMVersion.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_VERSION_ID", nullable = false)
	@JsonIgnore
	private SDTMVersion sdtmVersion;

	@Transient
	private List<SDTMProjectLibrary> libraries;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SDTMVersion getSdtmVersion() {
		return sdtmVersion;
	}

	public void setSdtmVersion(SDTMVersion sdtmVersion) {
		this.sdtmVersion = sdtmVersion;
	}

	public Long getId() {
		return id;
	}

	public List<SDTMProjectLibrary> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<SDTMProjectLibrary> libraries) {
		this.libraries = libraries;
	}

}
