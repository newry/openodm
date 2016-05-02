package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMProjectVariableRef;
import com.openodm.impl.entity.sdtm.SDTMVariable;

public interface SDTMProjectVariableRefRepository extends CrudRepository<SDTMProjectVariableRef, Long> {
	@Query("SELECT d FROM SDTMProjectVariableRef d WHERE d.sdtmProject.id=:projectId and d.sdtmVariable.id=:variableId")
	public SDTMProjectVariableRef findByProjectIdAndVariableId(@Param("projectId") Long projectId, @Param("variableId") Long variableId);

	@Query("SELECT d FROM SDTMProjectVariableRef d WHERE d.sdtmProject.id=:projectId and d.status='active'")
	public List<SDTMProjectVariableRef> findByProjectId(@Param("projectId") Long projectId);

	@Query("SELECT d.sdtmVariable FROM SDTMProjectVariableRef d WHERE d.sdtmProject.id=:projectId and d.status='active'")
	public List<SDTMVariable> findVariableByProjectId(@Param("projectId") Long projectId);
}
