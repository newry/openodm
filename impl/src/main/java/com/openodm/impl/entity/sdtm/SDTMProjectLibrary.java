package com.openodm.impl.entity.sdtm;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "SDTM_PROJECT_LIBRARY")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class SDTMProjectLibrary extends PersistentObject {
	private static final long serialVersionUID = 2111845415050802396L;

	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id = -1L;

	@Column(name = "name", nullable = false, length = 24)
	private String name;

	@Column(name = "path", nullable = true, length = 4096)
	private String path;

	@ManyToOne(targetEntity = SDTMProject.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_PROJECT_ID", nullable = false)
	@JsonIgnore
	private SDTMProject sdtmProject;

	@Column(name = "type", nullable = false, length = 32)
	@Enumerated(EnumType.STRING)
	private SDTMProjectLibraryType type = SDTMProjectLibraryType.file;

	@Transient
	private List<Map<String, Object>> dataSetList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public List<Map<String, Object>> getDataSetList() {
		return dataSetList;
	}

	public void setDataSetList(List<Map<String, Object>> dataSetList) {
		this.dataSetList = dataSetList;
	}

	public SDTMProjectLibraryType getType() {
		return type;
	}

	public void setType(SDTMProjectLibraryType type) {
		this.type = type;
	}

}
