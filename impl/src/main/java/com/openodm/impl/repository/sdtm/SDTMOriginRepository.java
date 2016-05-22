package com.openodm.impl.repository.sdtm;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.sdtm.SDTMOrigin;

public interface SDTMOriginRepository extends CrudRepository<SDTMOrigin, Long> {
	@Query("SELECT d FROM SDTMOrigin d WHERE d.status='active'")
	public List<SDTMOrigin> findAll();

	@Query("SELECT d FROM SDTMOrigin d WHERE d.name=:name")
	public List<SDTMOrigin> findByName(@Param("name") String name);

}
