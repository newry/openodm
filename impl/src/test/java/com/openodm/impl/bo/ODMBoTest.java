package com.openodm.impl.bo;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.CTVersion;
import com.openodm.impl.repository.ct.CTVersionRepository;
import com.openodm.impl.repository.ct.CodeListRepository;
import com.openodm.impl.repository.ct.EnumeratedItemRepository;

public class ODMBoTest {
	@InjectMocks
	private ODMBO odmBo;
	@Mock
	private CTVersionRepository metaDataVersionRepository;
	@Mock
	private CodeListRepository codeListRepository;
	@Mock
	private EnumeratedItemRepository enumeratedItemRepository;

	@Test
	public void test() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(
				metaDataVersionRepository.save(Mockito
						.any(CTVersion.class))).thenReturn(
				new CTVersion());
		Mockito.when(codeListRepository.save(Mockito.any(CodeList.class)))
				.thenReturn(new CodeList());
		odmBo.importMedatDataVersion(ODMBO.class.getClassLoader()
				.getResourceAsStream("SDTM Terminology.odm.xml"));
	}

}
