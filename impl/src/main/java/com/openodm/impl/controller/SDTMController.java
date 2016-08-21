package com.openodm.impl.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openodm.impl.bo.SDTMBO;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.ct.EnumeratedItem;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMOrigin;
import com.openodm.impl.entity.sdtm.SDTMVariable;
import com.openodm.impl.entity.sdtm.SDTMVariableRef;
import com.openodm.impl.entity.sdtm.SDTMVersion;
import com.openodm.impl.repository.ct.EnumeratedItemRepository;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMOriginRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRefRepository;
import com.openodm.impl.repository.sdtm.SDTMVersionRepository;

@RestController
public class SDTMController {
	private static final Logger LOG = LoggerFactory.getLogger(SDTMController.class);
	@Autowired
	private SDTMBO sdtmBo;
	@Autowired
	private SDTMVersionRepository sdtmVersionRepository;
	@Autowired
	private SDTMDomainRepository sdtmDomainRepository;
	@Autowired
	private SDTMVariableRefRepository sdtmVariableRefRepository;
	@Autowired
	private EnumeratedItemRepository enumeratedItemRepository;
	@Autowired
	private SDTMOriginRepository sdtmOriginRepository;

	@RequestMapping(value = "/sdtm/v1/origin", method = RequestMethod.POST)
	public void importOrigins() throws Exception {
		sdtmBo.importSDTMOrigin();
	}

	@RequestMapping(value = "/sdtm/v1/origin", method = RequestMethod.GET)
	public List<SDTMOrigin> listOriginList() throws Exception {
		return sdtmOriginRepository.findAll();
	}

	@RequestMapping(value = "/sdtm/v1/controlTerminology/{id}/import", method = RequestMethod.POST)
	public void upload(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) throws Exception {
		LOG.info("Before import, id={}", id);
		sdtmBo.importSDTMVersion(file.getInputStream(), id);

	}

	@RequestMapping(value = "/sdtm/v1/version", method = RequestMethod.GET)
	public List<SDTMVersion> getSDTMVersions() throws Exception {
		List<SDTMVersion> versions = sdtmVersionRepository.findAll();
		Set<String> set = new HashSet<String>();
		List<SDTMVersion> result = new ArrayList<SDTMVersion>();
		for (SDTMVersion version : versions) {
			if (set.add(version.getOid())) {
				result.add(version);
			}
		}
		return result;
	}

	@RequestMapping(value = "/sdtm/v1/version/{versionId}/ct", method = RequestMethod.GET)
	public List<ControlTerminology> getCTsByVersionId(@PathVariable("versionId") Long versionId) throws Exception {
		return sdtmVersionRepository.findCTById(versionId);
	}

	@RequestMapping(value = "/sdtm/v1/version/{versionId}", method = RequestMethod.GET)
	public ResponseEntity<SDTMVersion> getSDTMVersionById(@PathVariable("versionId") Long versionId) throws Exception {
		SDTMVersion sdtmVersion = sdtmVersionRepository.findOne(versionId);
		if (sdtmVersion == null) {
			return new ResponseEntity<SDTMVersion>(sdtmVersion, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<SDTMVersion>(sdtmVersion, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/sdtm/v1/version/{versionId}/domain", method = RequestMethod.GET)
	public ResponseEntity<List<SDTMDomain>> getSDTMVDomainsByVersionId(@PathVariable("versionId") Long versionId) throws Exception {
		List<SDTMDomain> domains = sdtmDomainRepository.findByVersionId(versionId);
		return new ResponseEntity<List<SDTMDomain>>(domains, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/domain/{domainId}/variable", method = RequestMethod.GET)
	public ResponseEntity<List<SDTMVariableRef>> getSDTMVVariablesByDomainId(@PathVariable("domainId") Long domainId) throws Exception {
		List<SDTMVariableRef> varRefs = sdtmVariableRefRepository.findByDomainId(domainId);
		for (SDTMVariableRef varRef : varRefs) {
			SDTMVariable var = varRef.getSdtmVariable();
			if (var != null) {
				CodeList codeList = var.getCodeList();
				List<EnumeratedItem> possibleValues = new ArrayList<EnumeratedItem>();
				if (codeList != null) {
					possibleValues.addAll(enumeratedItemRepository.findByCodeListId(codeList.getId()));
				}
				List<EnumeratedItem> enumeratedItems = var.getEnumeratedItems();
				if (enumeratedItems != null) {
					possibleValues.addAll(enumeratedItems);
				}
				var.setPossibleValues(possibleValues);
			}
		}
		return new ResponseEntity<List<SDTMVariableRef>>(varRefs, HttpStatus.OK);
	}

}
