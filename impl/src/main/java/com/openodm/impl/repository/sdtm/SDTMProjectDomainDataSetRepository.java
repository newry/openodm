package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMProjectDomainDataSet;

public interface SDTMProjectDomainDataSetRepository extends CrudRepository<SDTMProjectDomainDataSet, Long> {
	@Query("SELECT d FROM SDTMProjectDomainDataSet d WHERE d.sdtmProjectLibrary.sdtmProject.id=:projectId and d.status='active'")
	public List<SDTMProjectDomainDataSet> findByProjectId(@Param("projectId") Long projectId);

}
