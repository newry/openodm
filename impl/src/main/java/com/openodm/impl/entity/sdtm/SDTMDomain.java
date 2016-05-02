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
@Table(name = "SDTM_DOMAIN")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class SDTMDomain extends PersistentObject {
	private static final long serialVersionUID = 1650031777258323841L;

	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "domain", nullable = false, length = 255)
	private String domain;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "oid", nullable = false, length = 255, unique = true)
	private String oid;

	@Column(name = "description", nullable = true, length = 4096)
	private String description;

	@Column(name = "purpose", nullable = false, length = 255)
	private String purpose;

	@Column(name = "repeating", nullable = false, length = 255)
	private String repeating;

	@Column(name = "def_class", nullable = false, length = 255)
	private String defClass;

	@Column(name = "structure", nullable = false, length = 255)
	private String structure;

	@ManyToOne(targetEntity = SDTMVersion.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_VERSION_ID", nullable = false)
	@JsonIgnore
	private SDTMVersion sdtmVersion;

	@Transient
	private boolean added;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getDefClass() {
		return defClass;
	}

	public void setDefClass(String defClass) {
		this.defClass = defClass;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
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

	public String getRepeating() {
		return repeating;
	}

	public void setRepeating(String repeating) {
		this.repeating = repeating;
	}

	public boolean isAdded() {
		return added;
	}

	public void setAdded(boolean added) {
		this.added = added;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SDTMDomain other = (SDTMDomain) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
