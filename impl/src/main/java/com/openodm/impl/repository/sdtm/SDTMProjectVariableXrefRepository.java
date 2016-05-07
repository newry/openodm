package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;
import com.openodm.impl.entity.sdtm.SDTMVariable;

public interface SDTMProjectVariableXrefRepository extends CrudRepository<SDTMProjectVariableXref, Long> {
	@Query("SELECT d FROM SDTMProjectVariableXref d WHERE d.sdtmProject.id=:projectId and d.sdtmVariable.id=:variableId")
	public SDTMProjectVariableXref findByProjectIdAndVariableId(@Param("projectId") Long projectId, @Param("variableId") Long variableId);

	@Query("SELECT d FROM SDTMProjectVariableXref d WHERE d.sdtmProject.id=:projectId and d.status='active'")
	public List<SDTMProjectVariableXref> findByProjectId(@Param("projectId") Long projectId);

	@Query("SELECT d.sdtmVariable FROM SDTMProjectVariableXref d WHERE d.sdtmProject.id=:projectId and d.status='active'")
	public List<SDTMVariable> findVariableByProjectId(@Param("projectId") Long projectId);
}
