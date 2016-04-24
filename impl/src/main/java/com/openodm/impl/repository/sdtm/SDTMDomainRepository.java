package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.sdtm.SDTMDomain;

public interface SDTMDomainRepository extends CrudRepository<CodeList, SDTMDomain> {
	@Query("SELECT d FROM SDTMDomain d WHERE d.oid=:oid")
	public List<SDTMDomain> findByOid(@Param("oid") String oid);

}
