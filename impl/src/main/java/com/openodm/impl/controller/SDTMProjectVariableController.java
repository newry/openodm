package com.openodm.impl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.ct.EnumeratedItem;
import com.openodm.impl.entity.sdtm.SDTMOrigin;
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainXref;
import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;
import com.openodm.impl.repository.ct.CodeListRepository;
import com.openodm.impl.repository.ct.ControlTerminologyRepository;
import com.openodm.impl.repository.ct.EnumeratedItemRepository;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMOriginRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectDomainXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectKeyVariableXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectLibraryRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectVariableXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRefRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRepository;
import com.openodm.impl.repository.sdtm.SDTMVersionRepository;

@RestController
@SuppressWarnings("unchecked")
public class SDTMProjectVariableController {
	private static final Logger LOG = LoggerFactory.getLogger(SDTMProjectVariableController.class);
	@Autowired
	private SDTMVersionRepository sdtmVersionRepository;
	@Autowired
	private SDTMDomainRepository sdtmDomainRepository;
	@Autowired
	private SDTMVariableRefRepository sdtmVariableRefRepository;
	@Autowired
	private SDTMVariableRepository sdtmVariableRepository;
	@Autowired
	private EnumeratedItemRepository enumeratedItemRepository;
	@Autowired
	private SDTMProjectRepository sdtmProjectRepository;
	@Autowired
	private SDTMOriginRepository sdtmOriginRepository;
	@Autowired
	private SDTMProjectVariableXrefRepository sdtmProjectVariableXrefRepository;
	@Autowired
	private SDTMProjectKeyVariableXrefRepository sdtmProjectKeyVariableXrefRepository;
	@Autowired
	private SDTMProjectDomainXrefRepository sdtmProjectDomainXrefRepository;
	@Autowired
	private ControlTerminologyRepository controlTerminologyRepository;
	@Autowired
	private SDTMProjectLibraryRepository sdtmProjectLibraryRepository;
	@Autowired
	private CodeListRepository codeListRepository;

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/domain/{domainId}/variable/order", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> updateVariableOrderForProjectAndDomain(@PathVariable("projectId") Long projectId,
			@PathVariable("domainId") Long domainId, @RequestBody List<Map<String, Long>> request) {
		SDTMProjectDomainXref domainXref = this.sdtmProjectDomainXrefRepository.findByProjectIdAndDomainId(projectId, domainId);
		if (domainXref == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid project id or domain id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		if (CollectionUtils.isEmpty(request)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid request!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		Map<Long, Integer> orderMap = new HashMap<Long, Integer>();
		for (Map<String, Long> m : request) {
			orderMap.put(m.get("id"), m.get("orderNumber").intValue());
		}
		Iterable<SDTMProjectVariableXref> varXrefs = this.sdtmProjectVariableXrefRepository.findAll(orderMap.keySet());
		List<SDTMProjectVariableXref> changedVarXrefs = new ArrayList<SDTMProjectVariableXref>();
		for (SDTMProjectVariableXref varXref : varXrefs) {
			Integer newOrder = orderMap.get(varXref.getId());
			if (newOrder != null && !newOrder.equals(varXref.getOrderNumber())) {
				setUpdatedBy(varXref);
				varXref.setOrderNumber(newOrder);
				changedVarXrefs.add(varXref);
			}
		}
		if (!CollectionUtils.isEmpty(changedVarXrefs)) {
			return this.updateProjectVariableXrefs(changedVarXrefs);
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);

	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/enumeratedItems", method = RequestMethod.GET)
	public List<EnumeratedItem> getVariableEnumerateItems(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		if (varXref != null) {
			List<EnumeratedItem> enumeratedItems = varXref.getEnumeratedItems();
			for (EnumeratedItem ei : enumeratedItems) {
				ei.setIncluded(true);
			}
			return enumeratedItems;
		}
		return new ArrayList<>(0);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/allEnumeratedItems", method = RequestMethod.GET)
	public List<EnumeratedItem> getAllVariableEnumerateItems(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		if (varXref != null) {
			CodeList codeList = varXref.getCodeList();
			if (codeList != null) {
				List<EnumeratedItem> enumeratedItems = varXref.getEnumeratedItems();
				for (EnumeratedItem ei : enumeratedItems) {
					ei.setIncluded(true);
				}
				List<EnumeratedItem> eis = this.enumeratedItemRepository.findByCodeListId(codeList.getId());
				if (!CollectionUtils.isEmpty(eis)) {
					for (EnumeratedItem ei : eis) {
						if (!enumeratedItems.contains(ei)) {
							enumeratedItems.add(ei);
						}
					}

				}
				return enumeratedItems;
			}
		}
		return new ArrayList<>(0);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/codeListQuery", method = RequestMethod.GET)
	public List<CodeList> queryCodeList(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId,
			@RequestParam(value = "q", required = false) String q) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		List<CodeList> codeLists = new ArrayList<CodeList>();
		if (varXref != null) {
			CodeList codeList = varXref.getCodeList();
			if (codeList != null) {
				codeList.setAdded(true);
				codeLists.add(codeList);
			}
		}

		if (StringUtils.isNotBlank(q)) {
			SDTMProject project = this.sdtmProjectRepository.findOne(projectId);
			if (project != null) {
				ControlTerminology ct = project.getSdtmVersion().getControlTerminology();
				if (ct != null) {
					List<CodeList> result = this.controlTerminologyRepository.queryCodeList(StringUtils.lowerCase(q), ct.getId());
					if (!CollectionUtils.isEmpty(result)) {
						for (CodeList codeList : result) {
							if (!codeLists.contains(codeList)) {
								codeLists.add(codeList);
							}
						}
					}
				}
			}
		}
		return codeLists;
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/codeList", method = RequestMethod.GET)
	public List<CodeList> getCodeList(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		List<CodeList> codeLists = new ArrayList<CodeList>();
		if (varXref != null) {
			CodeList codeList = varXref.getCodeList();
			if (codeList != null) {
				codeList.setAdded(true);
				codeLists.add(codeList);
			}
		}

		return codeLists;
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/codeList/{id}", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> updateCodeList(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId,
			@PathVariable("id") Long id) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		if (varXref != null) {
			CodeList codeList = this.codeListRepository.findOne(id);
			if (codeList != null) {
				varXref.setCodeList(codeList);
				setUpdatedBy(varXref);
				return this.updateProjectVariableXref(varXref);
			}
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/codeList/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> removeCodeList(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId,
			@PathVariable("id") Long id) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		if (varXref != null) {
			CodeList codeList = this.codeListRepository.findOne(id);
			if (codeList != null && codeList.equals(varXref.getCodeList())) {
				varXref.setCodeList(null);
				setUpdatedBy(varXref);
				return this.updateProjectVariableXref(varXref);
			}
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/allEnumeratedItemsQuery", method = RequestMethod.GET)
	public List<EnumeratedItem> queryEnumeratedItem(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId,
			@RequestParam(value = "q", required = false) String q) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		if (varXref != null) {
			CodeList codeList = varXref.getCodeList();
			if (codeList != null) {
				List<EnumeratedItem> enumeratedItems = varXref.getEnumeratedItems();
				for (EnumeratedItem ei : enumeratedItems) {
					ei.setIncluded(true);
				}
				List<EnumeratedItem> eis;
				if (StringUtils.isEmpty(q)) {
					eis = this.enumeratedItemRepository.findByCodeListId(codeList.getId());
				} else {
					eis = this.enumeratedItemRepository.query(q, codeList.getId());
				}
				if (!CollectionUtils.isEmpty(eis)) {
					for (EnumeratedItem ei : eis) {
						if (!enumeratedItems.contains(ei)) {
							enumeratedItems.add(ei);
						}
					}

				}
				return enumeratedItems;
			}
		}
		return new ArrayList<>(0);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/enumeratedItem/{id}", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> addEnumeratedItem(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId,
			@PathVariable("id") Long id) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		if (varXref != null) {
			CodeList codeList = varXref.getCodeList();
			if (codeList != null) {
				List<EnumeratedItem> eis = varXref.getEnumeratedItems();
				EnumeratedItem ei = this.enumeratedItemRepository.findOne(id);
				if (!eis.contains(ei)) {
					eis.add(ei);
					setUpdatedBy(varXref);
				}
				return this.updateProjectVariableXref(varXref);
			}
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	private void setUpdatedBy(SDTMProjectVariableXref varXref) {
		varXref.setUpdatedBy("admin");
		varXref.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}/enumeratedItem/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> removeEnumeratedItem(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId,
			@PathVariable("id") Long id) {
		SDTMProjectVariableXref varXref = sdtmProjectVariableXrefRepository.findOne(variableId);
		if (varXref != null) {
			CodeList codeList = varXref.getCodeList();
			if (codeList != null) {
				List<EnumeratedItem> eis = varXref.getEnumeratedItems();
				EnumeratedItem ei = this.enumeratedItemRepository.findOne(id);
				if (eis.contains(ei)) {
					eis.remove(ei);
					setUpdatedBy(varXref);
				}
				return this.updateProjectVariableXref(varXref);
			}
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/domain/{domainId}/variable", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> updateVariableForProjectAndDomain(@PathVariable("projectId") Long projectId,
			@PathVariable("domainId") Long domainId, @RequestBody List<Map<String, Object>> request) {
		SDTMProjectDomainXref domainXref = this.sdtmProjectDomainXrefRepository.findByProjectIdAndDomainId(projectId, domainId);
		if (domainXref == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid project id or domain id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		if (CollectionUtils.isEmpty(request)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid request!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		List<SDTMProjectVariableXref> changedVarXrefs = new ArrayList<SDTMProjectVariableXref>();
		Map<Long, SDTMOrigin> originMap = new HashMap<Long, SDTMOrigin>();
		for (SDTMOrigin origin : this.sdtmOriginRepository.findAll()) {
			originMap.put(origin.getId(), origin);
		}
		for (Map<String, Object> m : request) {
			Number id = (Number) m.get("id");
			SDTMProjectVariableXref varXref = this.sdtmProjectVariableXrefRepository.findOne(id.longValue());
			if (varXref != null) {
				String pageNo = (String) m.get("crfPageNo");
				if (StringUtils.isNumeric(pageNo)) {
					varXref.setCrfPageNo(pageNo);
				}
				String length = (String) m.get("length");
				if (StringUtils.isNumeric(pageNo)) {
					varXref.setLength(Integer.valueOf(length));
				}
				List<Number> originList = (List<Number>) m.get("originList");
				if (!CollectionUtils.isEmpty(originList)) {
					varXref.getOrigins().clear();
					for (Number originId : originList) {
						SDTMOrigin newValue = originMap.get(originId.longValue());
						if (newValue != null) {
							varXref.getOrigins().add(newValue);
						}
					}
				}
				boolean needCRFPageNo = false;
				for (SDTMOrigin origin : varXref.getOrigins()) {
					if (origin.getName().equals("CRF Page")) {
						needCRFPageNo = true;
						break;
					}
				}
				if (needCRFPageNo && StringUtils.isEmpty(varXref.getCrfPageNo())) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("Invalid request for variable named " + varXref.getSdtmVariable().getName());
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
				}
				setUpdatedBy(varXref);
				changedVarXrefs.add(varXref);

			}
		}
		if (!CollectionUtils.isEmpty(changedVarXrefs)) {
			return this.updateProjectVariableXrefs(changedVarXrefs);
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);

	}

	private ResponseEntity<OperationResponse> updateProjectVariableXref(SDTMProjectVariableXref varXref) {
		return this.updateProjectVariableXrefs(Arrays.asList(varXref));
	}

	private ResponseEntity<OperationResponse> updateProjectVariableXrefs(List<SDTMProjectVariableXref> varXrefs) {
		try {
			this.sdtmProjectVariableXrefRepository.save(varXrefs);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during creating the Project", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the Project");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
