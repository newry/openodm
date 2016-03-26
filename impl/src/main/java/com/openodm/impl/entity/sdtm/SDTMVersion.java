package com.openodm.impl.entity.sdtm;

import javax.persistence.OneToOne;

import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;

public class SDTMVersion {
	@OneToOne(targetEntity = CodeList.class)
	private ControlTerminology controlTerminology;

}
