package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainXref;

public interface SDTMProjectDomainXrefRepository extends CrudRepository<SDTMProjectDomainXref, Long> {
	@Query("SELECT d FROM SDTMProjectDomainXref d WHERE d.sdtmProject.id=:projectId and d.status='active'")
	public List<SDTMProjectDomainXref> findByProjectId(@Param("projectId") Long projectId);

	@Query("SELECT d.sdtmDomain FROM SDTMProjectDomainXref d WHERE d.sdtmProject.id=:projectId order by d.orderNumber")
	public List<SDTMDomain> findDomainByProjectId(@Param("projectId") Long projectId);

	@Query("SELECT d FROM SDTMProjectDomainXref d WHERE d.sdtmProject.id=:projectId and d.sdtmDomain.id=:domainId")
	public SDTMProjectDomainXref findByProjectIdAndDomainId(@Param("projectId") Long projectId, @Param("domainId") Long domainId);

}
