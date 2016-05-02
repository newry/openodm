package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMProjectVariableRef;

public interface SDTMProjectVariableRefRepository extends CrudRepository<SDTMProjectVariableRef, Long> {
	@Query("SELECT d FROM SDTMProjectVariableRef d WHERE d.sdtmProject.id=:projectId and d.sdtmVariable.id=:variableId")
	public List<SDTMProjectVariableRef> findByProjectIdAndVariableId(@Param("projectId") Long projectId, @Param("variableId") Long oid);

	@Query("SELECT d FROM SDTMProjectVariableRef d WHERE d.sdtmProject.id=:projectId")
	public List<SDTMProjectVariableRef> findByProjectId(@Param("projectId") Long projectId);
}
