package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMProjectKeyVariableXref;

public interface SDTMProjectKeyVariableXrefRepository extends CrudRepository<SDTMProjectKeyVariableXref, Long> {
	@Query("SELECT d FROM SDTMProjectKeyVariableXref d WHERE d.sdtmProject.id=:projectId and d.sdtmDomain.id=:domainId and d.status='active' order by d.orderNumber")
	public List<SDTMProjectKeyVariableXref> findByProjectIdAndDomainId(@Param("projectId") Long projectId, @Param("domainId") Long domainId);

	@Query("SELECT d FROM SDTMProjectKeyVariableXref d WHERE d.sdtmProject.id=:projectId and d.status='active' order by d.sdtmDomain.id, d.orderNumber")
	public List<SDTMProjectKeyVariableXref> findByProjectId(@Param("projectId") Long projectId);

	@Query("SELECT d FROM SDTMProjectKeyVariableXref d WHERE d.sdtmProject.id=:projectId and d.sdtmDomain.id=:domainId and d.sdtmVariable.id=:varId")
	public SDTMProjectKeyVariableXref findByProjectIdAndDomainIdAndVariableId(@Param("projectId") Long projectId, @Param("domainId") Long domainId,
			@Param("varId") Long varId);

}
