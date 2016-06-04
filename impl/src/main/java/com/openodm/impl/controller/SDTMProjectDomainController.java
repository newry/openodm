package com.openodm.impl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
import org.springframework.web.bind.annotation.RestController;

import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainDataSet;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainXref;
import com.openodm.impl.entity.sdtm.SDTMProjectKeyVariableXref;
import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;
import com.openodm.impl.entity.sdtm.SDTMVariable;
import com.openodm.impl.repository.ct.CodeListRepository;
import com.openodm.impl.repository.ct.ControlTerminologyRepository;
import com.openodm.impl.repository.ct.EnumeratedItemRepository;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMOriginRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectDomainDataSetRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectDomainXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectKeyVariableXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectLibraryRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectVariableXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRefRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRepository;
import com.openodm.impl.repository.sdtm.SDTMVersionRepository;

@RestController
public class SDTMProjectDomainController {
	private static final Logger LOG = LoggerFactory.getLogger(SDTMProjectDomainController.class);
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
	@Autowired
	private SDTMProjectDomainDataSetRepository sdtmProjectDomainDataSetRepository;

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/allVariable", method = RequestMethod.GET)
	public List<SDTMProjectVariableXref> listAllProjectDomainVariables(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId) {
		List<SDTMProjectVariableXref> refs = new ArrayList<SDTMProjectVariableXref>(0);
		SDTMProjectDomainXref projectDomainXref = this.sdtmProjectDomainXrefRepository.findByProjectIdAndDomainId(id, domainId);
		if (projectDomainXref != null) {
			refs = sdtmProjectVariableXrefRepository.findByProjectIdAndDomainId(id, domainId);
		}
		return refs;
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/allKeyVariable", method = RequestMethod.GET)
	public List<SDTMProjectKeyVariableXref> listAllProjectDomainKeyVariables(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId) {
		List<SDTMProjectKeyVariableXref> vars = new ArrayList<SDTMProjectKeyVariableXref>(0);
		SDTMProjectDomainXref projectDomainXref = this.sdtmProjectDomainXrefRepository.findByProjectIdAndDomainId(id, domainId);
		if (projectDomainXref != null) {
			List<SDTMProjectKeyVariableXref> refs = this.sdtmProjectKeyVariableXrefRepository.findByProjectIdAndDomainId(id, domainId);
			Set<Long> varIds = new HashSet<Long>();
			for (SDTMProjectKeyVariableXref ref : refs) {
				SDTMVariable var = ref.getSdtmVariable();
				var.setKey(true);
				vars.add(ref);
				varIds.add(var.getId());
			}
			List<SDTMProjectVariableXref> varRefs = this.sdtmProjectVariableXrefRepository.findByProjectIdAndDomainId(id, domainId);
			for (SDTMProjectVariableXref varRef : varRefs) {
				if (!varIds.contains(varRef.getSdtmVariable().getId())) {
					SDTMProjectKeyVariableXref keyVarRef = new SDTMProjectKeyVariableXref();
					keyVarRef.setSdtmVariable(varRef.getSdtmVariable());
					vars.add(keyVarRef);
				}
			}
		}
		return vars;
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/keyVariable/{varId}", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> addAllProjectDomainKeyVariables(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId,
			@PathVariable("varId") Long varId) {

		SDTMProjectKeyVariableXref keyVarRef = this.sdtmProjectKeyVariableXrefRepository.findByProjectIdAndDomainIdAndVariableId(id, domainId, varId);
		boolean needSave = false;
		if (keyVarRef != null) {
			if (!keyVarRef.getStatus().equals(ObjectStatus.active)) {
				needSave = true;
				keyVarRef.setStatus(ObjectStatus.active);
				keyVarRef.setUpdatedBy("admin");
				keyVarRef.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
			}
		} else {
			keyVarRef = new SDTMProjectKeyVariableXref();
			keyVarRef.setCreator("admin");
			keyVarRef.setUpdatedBy("admin");
			keyVarRef.setSdtmProject(this.sdtmProjectRepository.findOne(id));
			keyVarRef.setSdtmDomain(this.sdtmDomainRepository.findOne(domainId));
			keyVarRef.setSdtmVariable(this.sdtmVariableRepository.findOne(varId));
			needSave = true;
		}
		if (needSave) {
			List<SDTMProjectKeyVariableXref> refs = this.sdtmProjectKeyVariableXrefRepository.findByProjectIdAndDomainId(id, domainId);
			if (CollectionUtils.isEmpty(refs)) {
				keyVarRef.setOrderNumber(1);
			} else {
				keyVarRef.setOrderNumber(refs.get(refs.size() - 1).getOrderNumber() + 1);
			}
			return updateProjectKeyVariableXrefs(Arrays.asList(keyVarRef));
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/keyVariable/{varId}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> removeAllProjectDomainKeyVariables(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId,
			@PathVariable("varId") Long varId) {
		SDTMProjectKeyVariableXref keyVarRef = this.sdtmProjectKeyVariableXrefRepository.findByProjectIdAndDomainIdAndVariableId(id, domainId, varId);
		if (keyVarRef != null && keyVarRef.getStatus().equals(ObjectStatus.active)) {
			keyVarRef.setUpdatedBy("admin");
			keyVarRef.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
			keyVarRef.setStatus(ObjectStatus.deleted);
			return updateProjectKeyVariableXrefs(Arrays.asList(keyVarRef));
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/domain/{domainId}/keyVariable/order", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> updateKeyVariableOrderForProject(@PathVariable("projectId") Long projectId,
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
		List<SDTMProjectKeyVariableXref> keyVarXrefs = this.sdtmProjectKeyVariableXrefRepository.findByProjectIdAndDomainId(projectId, domainId);
		List<SDTMProjectKeyVariableXref> changedKeyVarXrefs = new ArrayList<SDTMProjectKeyVariableXref>();
		if (!CollectionUtils.isEmpty(keyVarXrefs)) {
			for (SDTMProjectKeyVariableXref keyVarXref : keyVarXrefs) {
				Integer newOrder = orderMap.get(keyVarXref.getSdtmVariable().getId());
				if (newOrder != null && !newOrder.equals(keyVarXref.getOrderNumber())) {
					keyVarXref.setUpdatedBy("admin");
					keyVarXref.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
					keyVarXref.setOrderNumber(newOrder);
					changedKeyVarXrefs.add(keyVarXref);
				}
			}
		}
		if (!CollectionUtils.isEmpty(changedKeyVarXrefs)) {
			return this.updateProjectKeyVariableXrefs(changedKeyVarXrefs);
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);

	}

	private ResponseEntity<OperationResponse> updateProjectKeyVariableXrefs(List<SDTMProjectKeyVariableXref> varXrefs) {
		try {
			this.sdtmProjectKeyVariableXrefRepository.save(varXrefs);
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

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain", method = RequestMethod.GET)
	public List<SDTMProjectDomainXref> listProjectDomains(@PathVariable("id") Long id) {
		List<SDTMProjectDomainXref> domainXrefs = this.sdtmProjectDomainXrefRepository.findByProjectId(id);
		if (!CollectionUtils.isEmpty(domainXrefs)) {
			List<SDTMProjectKeyVariableXref> keyVariables = sdtmProjectKeyVariableXrefRepository.findByProjectId(id);
			if (!CollectionUtils.isEmpty(keyVariables)) {
				Map<String, List<SDTMProjectKeyVariableXref>> keyVarMap = new HashMap<String, List<SDTMProjectKeyVariableXref>>();
				for (SDTMProjectKeyVariableXref keyVariable : keyVariables) {
					String key = id + "_" + keyVariable.getSdtmDomain().getId();
					List<SDTMProjectKeyVariableXref> list = keyVarMap.get(key);
					if (list == null) {
						list = new ArrayList<SDTMProjectKeyVariableXref>();
						keyVarMap.put(key, list);
					}
					list.add(keyVariable);
				}
				for (SDTMProjectDomainXref domainXref : domainXrefs) {
					String key = id + "_" + domainXref.getSdtmDomain().getId();
					List<SDTMProjectKeyVariableXref> list = keyVarMap.get(key);
					if (!CollectionUtils.isEmpty(list)) {
						domainXref.getSdtmDomain().setKeyVariables(list);
					}
				}
			}
			List<SDTMProjectDomainDataSet> dataSets = this.sdtmProjectDomainDataSetRepository.findByProjectId(id);

			if (!CollectionUtils.isEmpty(dataSets)) {
				Map<Long, List<SDTMProjectDomainDataSet>> keyVarMap = new HashMap<Long, List<SDTMProjectDomainDataSet>>();
				for (SDTMProjectDomainDataSet dataSet : dataSets) {
					Long key = dataSet.getSdtmDomain().getId();
					List<SDTMProjectDomainDataSet> list = keyVarMap.get(key);
					if (list == null) {
						list = new ArrayList<SDTMProjectDomainDataSet>();
						keyVarMap.put(key, list);
					}
					list.add(dataSet);
				}
				for (SDTMProjectDomainXref domainXref : domainXrefs) {
					List<SDTMProjectDomainDataSet> list = keyVarMap.get(domainXref.getSdtmDomain().getId());
					if (!CollectionUtils.isEmpty(list)) {
						domainXref.getSdtmDomain().setDataSets(list);
					}
				}
			}
		}
		return domainXrefs;
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}", method = RequestMethod.GET)
	public ResponseEntity<SDTMProjectDomainXref> getProjectDomainById(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId) {
		SDTMProjectDomainXref domainXref = this.sdtmProjectDomainXrefRepository.findByProjectIdAndDomainId(id, domainId);
		if (domainXref != null) {
			return new ResponseEntity<SDTMProjectDomainXref>(domainXref, HttpStatus.OK);
		}
		return new ResponseEntity<SDTMProjectDomainXref>(domainXref, HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/domain", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> updateDomainOrderForProject(@PathVariable("projectId") Long projectId, @RequestBody List<Map<String, Long>> request) {
		SDTMProject project = this.sdtmProjectRepository.findOne(projectId);
		if (project == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid project id!");
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
		List<SDTMProjectDomainXref> domainXrefs = sdtmProjectDomainXrefRepository.findByProjectId(projectId);
		List<SDTMProjectDomainXref> changedDomainXrefs = new ArrayList<SDTMProjectDomainXref>();
		if (!CollectionUtils.isEmpty(domainXrefs)) {
			for (SDTMProjectDomainXref domainXref : domainXrefs) {
				Integer newOrder = orderMap.get(domainXref.getId());
				if (newOrder != null && !newOrder.equals(domainXref.getOrderNumber())) {
					domainXref.setUpdatedBy("admin");
					domainXref.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
					domainXref.setOrderNumber(newOrder);
					changedDomainXrefs.add(domainXref);
				}
			}
		}
		if (!CollectionUtils.isEmpty(changedDomainXrefs)) {
			return this.updateProjectDomainXrefs(changedDomainXrefs);
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);

	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/domain/{domainId}", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> addDomainToProject(@PathVariable("projectId") Long projectId, @PathVariable("domainId") Long domainId) {
		SDTMProject project = this.sdtmProjectRepository.findOne(projectId);
		if (project == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid project id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		SDTMDomain domain = this.sdtmDomainRepository.findOne(domainId);
		if (domain == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid domain id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		} else {
			if (!domain.getSdtmVersion().getId().equals(project.getSdtmVersion().getId())) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("Invalid domain id!");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
			}
		}

		SDTMProjectDomainXref domainXref = sdtmProjectDomainXrefRepository.findByProjectIdAndDomainId(projectId, domainId);

		if (domainXref != null) {
			if (!domainXref.getStatus().equals(ObjectStatus.active)) {
				domainXref.setStatus(ObjectStatus.active);
				domainXref.setUpdatedBy("admin");
				domainXref.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				return this.updateProjectDomainXref(domainXref);
			}
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/domain/{domainId}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> removeDomainFromProject(@PathVariable("projectId") Long projectId, @PathVariable("domainId") Long domainId) {
		SDTMProject project = this.sdtmProjectRepository.findOne(projectId);
		if (project == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid project id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		SDTMDomain domain = this.sdtmDomainRepository.findOne(domainId);
		if (domain == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid domain id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		SDTMProjectDomainXref domainXref = sdtmProjectDomainXrefRepository.findByProjectIdAndDomainId(projectId, domainId);

		if (domainXref != null) {
			domainXref.setStatus(ObjectStatus.inactive);
			domainXref.setUpdatedBy("admin");
			domainXref.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
			return this.updateProjectDomainXref(domainXref);
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);

	}

	private ResponseEntity<OperationResponse> updateProjectDomainXrefs(List<SDTMProjectDomainXref> domainXrefs) {
		try {
			this.sdtmProjectDomainXrefRepository.save(domainXrefs);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during update project domain xref", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the Project");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<OperationResponse> updateProjectDomainXref(SDTMProjectDomainXref domainXref) {
		return this.updateProjectDomainXrefs(Arrays.asList(domainXref));
	}

}