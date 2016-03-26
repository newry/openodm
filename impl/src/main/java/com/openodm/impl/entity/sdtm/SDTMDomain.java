package com.openodm.impl.entity.sdtm;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class SDTMDomain {
	@ManyToOne(targetEntity = SDTMVersion.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SDTM_VERSION_ID", nullable = false)
	private SDTMVersion sdtmVersion;

}
