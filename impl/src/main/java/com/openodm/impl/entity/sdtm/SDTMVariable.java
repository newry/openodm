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

import com.openodm.impl.entity.PersistentObject;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.CustomizedCodeList;
import com.openodm.impl.entity.ct.EnumeratedItem;

@Entity
@Table(name = "SDTM_VARIABLE")
@DynamicUpdate
public class SDTMVariable extends PersistentObject {

	private static final long serialVersionUID = -798805519475960716L;
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

	@Column(name = "data_type", nullable = false, length = 32)
	private String dataType;

	@Column(name = "length", nullable = true)
	private Integer length;

	@ManyToOne(targetEntity = SDTMDomain.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_DOMAIN_ID", nullable = false)
	private SDTMDomain sdtmDomain;

	@ManyToOne(targetEntity = CodeList.class, optional = true)
	@JoinColumn(name = "code_list_id", nullable = true)
	private CodeList codeList;

	@ManyToOne(targetEntity = CustomizedCodeList.class, optional = true)
	@JoinColumn(name = "customized_code_list_id", nullable = true)
	private CustomizedCodeList customizedCodeList;

	@ManyToMany(targetEntity = EnumeratedItem.class)
	@JoinTable(name = "SDTM_VARIABLE_ENUM_ITEM_XREF", joinColumns = { @JoinColumn(name = "SDTM_VARIABLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "ENUMERATED_ITEM_ID") })
	private List<EnumeratedItem> enumeratedItems;

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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public CodeList getCodeList() {
		return codeList;
	}

	public void setCodeList(CodeList codeList) {
		this.codeList = codeList;
	}

	public CustomizedCodeList getCustomizedCodeList() {
		return customizedCodeList;
	}

	public void setCustomizedCodeList(CustomizedCodeList customizedCodeList) {
		this.customizedCodeList = customizedCodeList;
	}

	public List<EnumeratedItem> getEnumeratedItems() {
		return enumeratedItems;
	}

	public void setEnumeratedItems(List<EnumeratedItem> enumeratedItems) {
		this.enumeratedItems = enumeratedItems;
	}

	public Long getId() {
		return id;
	}

	public SDTMDomain getSdtmDomain() {
		return sdtmDomain;
	}

	public void setSdtmDomain(SDTMDomain sdtmDomain) {
		this.sdtmDomain = sdtmDomain;
	}

}
