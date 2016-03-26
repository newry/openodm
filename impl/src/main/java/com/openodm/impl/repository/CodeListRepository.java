package com.openodm.impl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.CodeList;

public interface CodeListRepository extends CrudRepository<CodeList, Long> {

	@Query("SELECT d FROM CodeList d WHERE d.metaDataVersion.id=:metaDataVersionId and d.oid=:oid")
	public List<CodeList> findByMetaDataVersionIdAndOid(@Param("metaDataVersionId") Long metaDataVersionId,
			@Param("oid") String oid);

	@Query("SELECT d FROM CodeList d WHERE d.metaDataVersion.id=:metaDataVersionId and d.status='active'")
	public List<CodeList> findByMetaDataVersionId(@Param("metaDataVersionId") Long metaDataVersionId);

	@Query("SELECT d FROM CodeList d WHERE d.searchTerm like %:q% and d.status='active'")
	public List<CodeList> query(@Param("q") String q);

}
