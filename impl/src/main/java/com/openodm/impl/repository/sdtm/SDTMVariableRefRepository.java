package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMVariableRef;

public interface SDTMVariableRefRepository extends CrudRepository<SDTMVariableRef, Long> {
	@Query("SELECT d FROM SDTMVariableRef d WHERE d.sdtmDomain.id=:domainId and d.sdtmVariable.id=:variableId")
	public List<SDTMVariableRef> findByDomainIdAndVariableId(@Param("domainId") Long domainId, @Param("variableId") Long oid);

	@Query("SELECT d FROM SDTMVariableRef d WHERE d.sdtmDomain.id=:domainId and d.status='active'")
	public List<SDTMVariableRef> findByDomainId(@Param("domainId") Long domainId);
}
