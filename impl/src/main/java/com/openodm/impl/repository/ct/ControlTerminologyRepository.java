package com.openodm.impl.repository.ct;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;

public interface ControlTerminologyRepository extends CrudRepository<ControlTerminology, Long> {
	@Query("SELECT d FROM ControlTerminology d WHERE d.name=:name")
	public List<ControlTerminology> findByName(@Param("name") String name);

	@Query("SELECT d FROM ControlTerminology d WHERE d.status='active'")
	public List<ControlTerminology> findAll();

	@Query("SELECT codeList FROM ControlTerminology d join d.codeLists as codeList WHERE d.id=:id and codeList.searchTerm like %:q% and codeList.status='active'")
	public List<CodeList> queryCodeList(@Param("q") String q, @Param("id") Long id);

}
