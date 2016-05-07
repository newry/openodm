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
import com.openodm.impl.entity.ct.ControlTerminology;

@Entity
@Table(name = "SDTM_VERSION")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class SDTMVersion extends PersistentObject {
	private static final long serialVersionUID = -3234882974006387434L;
	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "oid", nullable = false, length = 255)
	private String oid;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", nullable = true, length = 4096)
	private String description;

	@Column(name = "define_version", nullable = false, length = 255)
	private String defineVersion;

	@Column(name = "standard_name", nullable = false, length = 255)
	private String standardName;

	@Column(name = "standard_version", nullable = false, length = 255)
	private String standardVersion;

	@ManyToOne(targetEntity = ControlTerminology.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "control_terminology_id", nullable = false)
	@JsonIgnore
	private ControlTerminology controlTerminology;

	public ControlTerminology getControlTerminology() {
		return controlTerminology;
	}

	public void setControlTerminology(ControlTerminology controlTerminology) {
		this.controlTerminology = controlTerminology;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

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

	public String getDefineVersion() {
		return defineVersion;
	}

	public void setDefineVersion(String defineVersion) {
		this.defineVersion = defineVersion;
	}

	public String getStandardName() {
		return standardName;
	}

	public void setStandardName(String standardName) {
		this.standardName = standardName;
	}

	public String getStandardVersion() {
		return standardVersion;
	}

	public void setStandardVersion(String standardVersion) {
		this.standardVersion = standardVersion;
	}

	public Long getId() {
		return id;
	}
}
