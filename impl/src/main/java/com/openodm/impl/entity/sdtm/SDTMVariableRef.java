package com.openodm.impl.entity.sdtm;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

public class SDTMVariableRef {
	@ManyToOne(targetEntity = SDTMDomain.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_DOMAIN_ID", nullable = false)
	private SDTMDomain sdtmDomain;

	@OneToOne(targetEntity = SDTMVariable.class)
	private SDTMVariable sdtmVariable;

}
