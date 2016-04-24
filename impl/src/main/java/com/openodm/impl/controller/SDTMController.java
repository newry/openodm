package com.openodm.impl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openodm.impl.bo.SDTMBO;

@RestController
public class SDTMController {
	private static final Logger LOG = LoggerFactory.getLogger(SDTMController.class);
	@Autowired
	private SDTMBO sdtmBo;

	@RequestMapping(value = "/sdtm/v1/controlTerminology/{id}/import", method = RequestMethod.POST)
	public void upload(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) throws Exception {
		LOG.info("Before import, id={}", id);
		sdtmBo.importSDTMVersion(file.getInputStream(), id);

	}
}
