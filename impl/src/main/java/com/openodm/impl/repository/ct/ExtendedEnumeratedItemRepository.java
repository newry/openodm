package com.openodm.impl.repository.ct;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.openodm.impl.entity.ct.ExtendedEnumeratedItem;

public interface ExtendedEnumeratedItemRepository extends CrudRepository<ExtendedEnumeratedItem, Long> {

	@Query("SELECT d FROM ExtendedEnumeratedItem d WHERE d.controlTerminology.id=:controlTerminologyId and d.codeList.id=:codeListId and d.codedValue=:codedValue")
	public ExtendedEnumeratedItem findByCtIdAndCodeListIdAndCodeValue(@Param("controlTerminologyId") Long controlTerminologyId,
			@Param("codeListId") Long codeListId, @Param("codedValue") String codedValue);

	@Query("SELECT d FROM ExtendedEnumeratedItem d WHERE d.controlTerminology.id=:controlTerminologyId and d.codeList.id=:codeListId and d.status='active'")
	public List<ExtendedEnumeratedItem> findByCodeListId(@Param("controlTerminologyId") Long controlTerminologyId, @Param("codeListId") Long codeListId);

}
