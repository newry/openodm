package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMVariable;

public interface SDTMVariableRepository extends CrudRepository<SDTMVariable, Long> {
	@Query("SELECT d FROM SDTMVariable d WHERE d.sdtmDomain.id=:domainId and d.oid=:oid")
	public List<SDTMVariable> findByDomainIdAndOid(@Param("domainId") Long domainId, @Param("oid") String oid);

	@Query("SELECT d FROM SDTMVariable d WHERE d.sdtmDomain.id=:domainId and d.name=:name")
	public List<SDTMVariable> findByDomainIdAndName(@Param("domainId") Long domainId, @Param("name") String name);
}
