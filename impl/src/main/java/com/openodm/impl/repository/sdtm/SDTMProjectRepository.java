package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.openodm.impl.entity.sdtm.SDTMProject;

public interface SDTMProjectRepository extends CrudRepository<SDTMProject, Long> {
	@Query("SELECT d FROM SDTMProject d WHERE d.status='active'")
	public List<SDTMProject> findAll();

}
