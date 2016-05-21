package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;

public interface SDTMProjectVariableXrefRepository extends CrudRepository<SDTMProjectVariableXref, Long> {
	@Query("SELECT d FROM SDTMProjectVariableXref d WHERE d.sdtmProject.id=:projectId and d.sdtmDomain.id=:domainId order by d.orderNumber")
	public List<SDTMProjectVariableXref> findByProjectIdAndDomainId(@Param("projectId") Long projectId, @Param("domainId") Long domainId);

}
