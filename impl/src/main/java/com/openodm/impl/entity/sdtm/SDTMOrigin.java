package com.openodm.impl.entity.sdtm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.openodm.impl.entity.PersistentObject;

@Entity
@Table(name = "SDTM_ORIGIN")
@DynamicUpdate
@JsonInclude(Include.NON_EMPTY)
public class SDTMOrigin extends PersistentObject {
	private static final long serialVersionUID = -2826339921164023327L;
	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
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
		SDTMOrigin other = (SDTMOrigin) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
