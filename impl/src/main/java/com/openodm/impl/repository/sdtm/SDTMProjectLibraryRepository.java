package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMProjectLibrary;

public interface SDTMProjectLibraryRepository extends CrudRepository<SDTMProjectLibrary, Long> {
	@Query("SELECT d FROM SDTMProjectLibrary d WHERE d.sdtmProject.id=:projectId")
	public List<SDTMProjectLibrary> findByProjectId(@Param("projectId") Long projectId);

}
