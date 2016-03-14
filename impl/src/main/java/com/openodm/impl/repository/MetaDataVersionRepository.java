package com.openodm.impl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.MetaDataVersion;

public interface MetaDataVersionRepository extends
		CrudRepository<MetaDataVersion, Long> {

	@Query("SELECT d FROM MetaDataVersion d WHERE d.oid=:oid")
	public List<MetaDataVersion> findByOid(@Param("oid") String oid);

	@Query("SELECT d FROM MetaDataVersion d WHERE d.status='active'")
	public List<MetaDataVersion> findAll();

}
