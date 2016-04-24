package com.openodm.impl.repository.ct;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.ct.CustomizedCodeList;

public interface CustomizedCodeListRepository extends CrudRepository<CustomizedCodeList, Long> {
	@Query("SELECT d FROM CustomizedCodeList d WHERE d.name=:name and d.status='active'")
	public List<CustomizedCodeList> findByName(@Param("name") String name);

}
