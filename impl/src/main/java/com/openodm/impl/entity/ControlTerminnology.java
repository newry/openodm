package com.openodm.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CONTROL_TERMINOLOGY")
@DynamicUpdate
public class ControlTerminnology extends PersistentObject {
	private static final long serialVersionUID = 2049768769970644062L;

	@Id
	@GeneratedValue(generator = "identity", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "identity", strategy = "identity")
	@Column(name = "ID", unique = true, nullable = false, precision = 22)
	private Long id;

	@Column(name = "name", nullable = false, length = 255, unique = true)
	private String name;

	@Column(name = "description", nullable = false, length = 4096)
	private String description;

}
