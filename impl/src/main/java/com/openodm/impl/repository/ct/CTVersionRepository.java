package com.openodm.impl.repository.ct;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.ct.CTVersion;

public interface CTVersionRepository extends
		CrudRepository<CTVersion, Long> {

	@Query("SELECT d FROM MetaDataVersion d WHERE d.oid=:oid")
	public List<CTVersion> findByOid(@Param("oid") String oid);

	@Query("SELECT d FROM MetaDataVersion d WHERE d.status='active'")
	public List<CTVersion> findAll();

}
