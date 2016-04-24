package com.openodm.impl.repository.ct;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.ct.EnumeratedItem;

public interface EnumeratedItemRepository extends CrudRepository<EnumeratedItem, Long> {

	@Query("SELECT d FROM EnumeratedItem d WHERE d.codeList.id=:codeListId and d.codedValue=:codedValue")
	public List<EnumeratedItem> findByCodeListIdAndCodeValue(@Param("codeListId") Long codeListId, @Param("codedValue") String codedValue);

	@Query("SELECT d FROM EnumeratedItem d WHERE d.codeList.id=:codeListId and d.status='active'")
	public List<EnumeratedItem> findByCodeListId(@Param("codeListId") Long codeListId);

}
