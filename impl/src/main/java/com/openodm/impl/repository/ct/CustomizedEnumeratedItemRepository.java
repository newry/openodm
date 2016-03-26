package com.openodm.impl.repository.ct;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.ct.CustomizedEnumeratedItem;

public interface CustomizedEnumeratedItemRepository extends
		CrudRepository<CustomizedEnumeratedItem, Long> {

	@Query("SELECT d FROM CustomizedEnumeratedItem d WHERE d.customizedCodeList.id=:codeListId and d.codedValue=:codedValue")
	public List<CustomizedEnumeratedItem> findByCodeListIdAndCodeValue(
			@Param("codeListId") Long codeListId,
			@Param("codedValue") String codedValue);

	@Query("SELECT d FROM CustomizedEnumeratedItem d WHERE d.customizedCodeList.id=:codeListId and d.status='active'")
	public List<CustomizedEnumeratedItem> findByCodeListId(
			@Param("codeListId") Long codeListId);

}
