package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMDomain;

public interface SDTMDomainRepository extends CrudRepository<SDTMDomain, Long> {
	@Query("SELECT d FROM SDTMDomain d WHERE d.sdtmVersion.id=:versionId and d.oid=:oid")
	public List<SDTMDomain> findByVersionIdAndOid(@Param("versionId") Long verionId, @Param("oid") String oid);

}
