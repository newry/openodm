package com.openodm.impl.bo;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.openodm.impl.entity.CodeList;
import com.openodm.impl.entity.MetaDataVersion;
import com.openodm.impl.repository.CodeListRepository;
import com.openodm.impl.repository.EnumeratedItemRepository;
import com.openodm.impl.repository.MetaDataVersionRepository;

public class ODMBoTest {
	@InjectMocks
	private ODMBo odmBo;
	@Mock
	private MetaDataVersionRepository metaDataVersionRepository;
	@Mock
	private CodeListRepository codeListRepository;
	@Mock
	private EnumeratedItemRepository enumeratedItemRepository;

	@Test
	public void test() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(
				metaDataVersionRepository.save(Mockito
						.any(MetaDataVersion.class))).thenReturn(
				new MetaDataVersion());
		Mockito.when(codeListRepository.save(Mockito.any(CodeList.class)))
				.thenReturn(new CodeList());
		odmBo.importMedatDataVersion(ODMBo.class.getClassLoader()
				.getResourceAsStream("SDTM Terminology.odm.xml"));
	}

}
