package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMProjectDomainDataSet;

public interface SDTMProjectDomainDataSetRepository extends CrudRepository<SDTMProjectDomainDataSet, Long> {
	@Query("SELECT d FROM SDTMProjectDomainDataSet d WHERE d.sdtmProject.id=:projectId and d.status='active'")
	public List<SDTMProjectDomainDataSet> findByProjectId(@Param("projectId") Long projectId);

	@Query("SELECT d FROM SDTMProjectDomainDataSet d WHERE d.sdtmProject.id=:projectId and d.sdtmDomain.id=:domainId and d.status='active'")
	public List<SDTMProjectDomainDataSet> findByProjectIdAndDomainId(@Param("projectId") Long projectId, @Param("domainId") Long domainId);

	@Query("SELECT d FROM SDTMProjectDomainDataSet d WHERE d.sdtmProject.id=:projectId and d.sdtmDomain.id=:domainId and name=:name and d.status='active'")
	public SDTMProjectDomainDataSet findByProjectIdAndDomainIdAndName(@Param("projectId") Long projectId, @Param("domainId") Long domainId,
			@Param("name") String name);

	@Query("SELECT d FROM SDTMProjectDomainDataSet d join d.usedDataSets u WHERE u.id=:dataSetId and d.status='active'")
	public List<SDTMProjectDomainDataSet> findByUsedDataSetId(@Param("dataSetId") Long dataSetId);

}
