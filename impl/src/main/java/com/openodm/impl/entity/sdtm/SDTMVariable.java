package com.openodm.impl.entity.sdtm;

import javax.persistence.OneToOne;

import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.CustomizedCodeList;

public class SDTMVariable {

	@OneToOne(targetEntity = CodeList.class)
	private CodeList codeList;

	@OneToOne(targetEntity = CodeList.class)
	private CustomizedCodeList customizedCodeList;

}
