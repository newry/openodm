package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMVersion;

public interface SDTMVersionRepository extends CrudRepository<SDTMVersion, Long> {
	@Query("SELECT d FROM SDTMVersion d WHERE d.oid=:oid")
	public List<SDTMVersion> findByOid(@Param("oid") String oid);

	@Query("SELECT d FROM SDTMVersion d WHERE d.status='active'")
	public List<SDTMVersion> findAll();
}
