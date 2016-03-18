package com.openodm.impl.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CONTROL_TERMINOLOGY")
@DynamicUpdate
public class ControlTerminology extends PersistentObject {
	private static final long serialVersionUID = 2049768769970644062L;

	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "name", nullable = false, length = 255, unique = true)
	private String name;

	@Column(name = "description", nullable = true, length = 4096)
	private String description;

	@ManyToMany(targetEntity = CodeList.class)
	@JoinTable(name = "CONTROL_TERMINOLOGY_CODE_LIST_XREF", joinColumns = { @JoinColumn(name = "CONTROL_TERMINOLOGY_ID") }, inverseJoinColumns = { @JoinColumn(name = "CODE_LIST_ID") })
	private List<CodeList> codeLists;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the codeLists
	 */
	public List<CodeList> getCodeLists() {
		return codeLists;
	}

	/**
	 * @param codeLists
	 *            the codeLists to set
	 */
	public void setCodeLists(List<CodeList> codeLists) {
		this.codeLists = codeLists;
	}

}
