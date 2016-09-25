package com.openodm.impl.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainDataSet;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainXref;
import com.openodm.impl.entity.sdtm.SDTMProjectKeyVariableXref;
import com.openodm.impl.entity.sdtm.SDTMProjectLibrary;
import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;
import com.openodm.impl.entity.sdtm.SDTMVariable;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectDomainDataSetRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectDomainXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectKeyVariableXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectLibraryRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectVariableXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRepository;
import com.openodm.impl.util.Utils;

@RestController
@SuppressWarnings("unchecked")
public class SDTMProjectDomainController {
	private static final String SORT = "sort";
	private static final Logger LOG = LoggerFactory.getLogger(SDTMProjectDomainController.class);
	@Autowired
	private SDTMDomainRepository sdtmDomainRepository;
	@Autowired
	private SDTMVariableRepository sdtmVariableRepository;
	@Autowired
	private SDTMProjectRepository sdtmProjectRepository;
	@Autowired
	private SDTMProjectVariableXrefRepository sdtmProjectVariableXrefRepository;
	@Autowired
	private SDTMProjectKeyVariableXrefRepository sdtmProjectKeyVariableXrefRepository;
	@Autowired
	private SDTMProjectDomainXrefRepository sdtmProjectDomainXrefRepository;
	@Autowired
	private SDTMProjectDomainDataSetRepository sdtmProjectDomainDataSetRepository;
	@Autowired
	private SDTMProjectLibraryRepository sdtmProjectLibraryRepository;

	@Value("${project.rootPath}")
	private String rootPath;
	ObjectMapper om = new ObjectMapper();

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
			int order = 0;
			for (SDTMProjectKeyVariableXref ref : refs) {
				SDTMVariable var = ref.getSdtmVariable();
				var.setKey(true);
				vars.add(ref);
				varIds.add(var.getId());
				order = ref.getOrderNumber();
			}
			List<SDTMProjectVariableXref> varRefs = this.sdtmProjectVariableXrefRepository.findByProjectIdAndDomainId(id, domainId);
			for (SDTMProjectVariableXref varRef : varRefs) {
				if (!varIds.contains(varRef.getSdtmVariable().getId())) {
					SDTMProjectKeyVariableXref keyVarRef = new SDTMProjectKeyVariableXref();
					keyVarRef.setOrderNumber(++order);
					keyVarRef.setSdtmVariable(varRef.getSdtmVariable());
					vars.add(keyVarRef);
				}
			}
		}
		return vars;
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/dataSet", method = RequestMethod.GET)
	public List<Map<String, Object>> listProjectLibrariesDataSet(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId) {
		List<SDTMProjectDomainDataSet> dataSets = this.sdtmProjectDomainDataSetRepository.findByProjectIdAndDomainId(id, domainId);
		List<Map<String, Object>> dataSetList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(dataSets)) {
			for (SDTMProjectDomainDataSet dataSet : dataSets) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", dataSet.getId());
				map.put("id", dataSet.getId());
				map.put("joinType", dataSet.getJoinType());
				map.put("label", dataSet.getName());
				map.put("output", dataSet.getName());
				try {
					String metaData = dataSet.getMetaData();
					JsonNode jsonNode = om.reader().readTree(metaData);
					if (StringUtils.equalsIgnoreCase(dataSet.getJoinType(), SORT)) {
						JsonNode libNode = jsonNode.get("libraryId");
						String input = jsonNode.get("dataSet").asText();
						if (libNode != null && !libNode.isNull()) {
							Long LibId = libNode.asLong();
							SDTMProjectLibrary lib = this.sdtmProjectLibraryRepository.findOne(LibId);
							if (input.indexOf('.') > -1) {
								input = input.substring(0, input.lastIndexOf('.'));
							}
							map.put("input", lib.getName() + "." + input);
						} else {
							String name = this.sdtmProjectDomainDataSetRepository.findOne(Long.valueOf(input)).getName();
							map.put("input", name);
						}
					}
					dataSetList.add(map);
				} catch (IOException e) {
					LOG.error("listProjectLibrariesDataSet", e);
				}
			}
		}
		return dataSetList;
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/dataSet/{dataSetId}", method = RequestMethod.GET)
	public List<Map<String, Object>> listProjectLibrariesDataSetColumns(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId,
			@PathVariable("dataSetId") Long dataSetId) {
		SDTMProjectDomainDataSet dataSet = sdtmProjectDomainDataSetRepository.findOne(dataSetId);
		List<Map<String, Object>> dataSetColumns = new ArrayList<Map<String, Object>>();
		if (dataSet != null) {
			try {
				if (StringUtils.equalsIgnoreCase(dataSet.getJoinType(), SORT)) {
					String metaData = dataSet.getMetaData();
					JsonNode jsonNode = om.reader().readTree(metaData);
					ArrayNode arrayNode = (ArrayNode) jsonNode.get("columns");
					for (int i = 0; i < arrayNode.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name", arrayNode.get(i).get("name").asText());
						dataSetColumns.add(map);
					}
				}
			} catch (IOException e) {
				LOG.error("listProjectLibrariesDataSetColumns", e);
			}

		}
		return dataSetColumns;
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/dataSet", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createProjectDomainDataSet(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId,
			@RequestBody Map<String, Object> request) {
		String name = (String) request.get("name");
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		SDTMProjectDomainDataSet existingOne = sdtmProjectDomainDataSetRepository.findByProjectIdAndDomainIdAndName(id, domainId, name);
		if (existingOne != null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("DataSet with same name existed!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		String joinType = (String) request.get("joinType");
		SDTMProjectDomainDataSet entity = new SDTMProjectDomainDataSet();
		entity.setCreator("admin");
		entity.setUpdatedBy("admin");
		entity.setName(name);
		entity.setJoinType(joinType);
		entity.setSdtmProject(this.sdtmProjectRepository.findOne(id));
		entity.setSdtmDomain(this.sdtmDomainRepository.findOne(domainId));
		entity.setSql((String) request.get("sql"));
		ObjectNode node = buildMetadata(entity, request);
		entity.setMetaData(node.toString());
		LOG.debug("request={}", entity.getMetaData());
		try {
			sdtmProjectDomainDataSetRepository.save(entity);
		} catch (Exception e) {
			LOG.error("Error during create SDTMProjectDomainDataSet", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/dataSet/{dataSetId}", method = RequestMethod.PUT)
	public ResponseEntity<OperationResponse> updateProjectDomainDataSet(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId,
			@PathVariable("dataSetId") Long dataSetId, @RequestBody Map<String, Object> request) {
		SDTMProjectDomainDataSet entity = sdtmProjectDomainDataSetRepository.findOne(dataSetId);
		if (entity == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid data set id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		String name = (String) request.get("name");
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		SDTMProjectDomainDataSet existingOne = sdtmProjectDomainDataSetRepository.findByProjectIdAndDomainIdAndName(id, domainId, name);
		if (existingOne != null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("DataSet with same name existed!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		entity.setName(name);
		Utils.updatePODataLastModified(entity);
		entity.setUpdatedBy("admin");
		entity.setSql((String) request.get("sql"));
		ObjectNode node = buildMetadata(entity, request);
		entity.setMetaData(node.toString());
		LOG.debug("metadata={}", entity.getMetaData());
		try {
			sdtmProjectDomainDataSetRepository.save(entity);
		} catch (Exception e) {
			LOG.error("Error during update SDTMProjectDomainDataSet", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/dataSet/{dataSetId}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> deleteProjectDomainDataSet(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId,
			@PathVariable("dataSetId") Long dataSetId) {
		SDTMProjectDomainDataSet entity = sdtmProjectDomainDataSetRepository.findOne(dataSetId);
		if (entity == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid data set id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		List<SDTMProjectDomainDataSet> entities = sdtmProjectDomainDataSetRepository.findByUsedDataSetId(dataSetId);
		if (!CollectionUtils.isEmpty(entities)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Data set is still used, cannot be removed!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		Utils.updatePODataLastModified(entity);
		entity.setUpdatedBy("admin");
		entity.setStatus(ObjectStatus.deleted);
		try {
			sdtmProjectDomainDataSetRepository.save(entity);
		} catch (Exception e) {
			LOG.error("Error during update SDTMProjectDomainDataSet", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
	}

	private ObjectNode buildMetadata(SDTMProjectDomainDataSet entity, Map<String, Object> request) {
		ObjectNode node = om.createObjectNode();
		if (StringUtils.equalsIgnoreCase(entity.getJoinType(), SORT)) {
			List<String> columns = (List<String>) request.get("columns");
			List<String> aliasColumns = (List<String>) request.get("aliasColumns");
			List<String> sortColumns = (List<String>) request.get("sortColumns");
			if (!CollectionUtils.isEmpty(columns)) {
				ArrayNode columnArray = node.putArray("columns");
				Map<String, String> aliasMap = new HashMap<String, String>();
				Map<String, String> sortMap = new HashMap<String, String>();
				if (!CollectionUtils.isEmpty(aliasColumns)) {
					for (String aliasColumn : aliasColumns) {
						int index = aliasColumn.indexOf("=");
						if (index > -1) {
							aliasMap.put(aliasColumn.substring(0, index), aliasColumn.substring(index + 1));
						}
					}
				}
				if (!CollectionUtils.isEmpty(sortColumns)) {
					for (String sortColumn : sortColumns) {
						if (sortColumn.startsWith("descending ")) {
							sortMap.put(sortColumn.substring("descending ".length()), "desc");
						} else {
							sortMap.put(sortColumn, "asc");
						}
					}
				}
				for (String column : columns) {
					String alias = aliasMap.get(column);
					String sortOrder = sortMap.get(column);
					ObjectNode columnNode = om.createObjectNode();
					if (StringUtils.isNotEmpty(alias)) {
						columnNode.put("name", alias);
						columnNode.put("originalName", column);
					} else {
						columnNode.put("name", column);
					}
					if (StringUtils.isNotEmpty(sortOrder)) {
						columnNode.put("sortOrder", sortOrder);
					}

					columnArray.add(columnNode);
				}
			}
			String condition = (String) request.get("condition");
			if (StringUtils.isNotEmpty(condition)) {
				node.put("condition", condition);
			}
			boolean hadlibId = false;
			String libraryId = (String) request.get("libraryId");
			if (StringUtils.isNotEmpty(libraryId) && StringUtils.isNumeric(libraryId)) {
				Long libId = Long.valueOf(libraryId);
				node.put("libraryId", libId);
				hadlibId = true;
			}

			String dataSet = (String) request.get("dataSet");
			if (StringUtils.isNotEmpty(dataSet)) {
				node.put("dataSet", dataSet);
				if (!hadlibId && StringUtils.isNumeric(dataSet)) {
					// store used dataset ids
					List<SDTMProjectDomainDataSet> usedDataSets = new ArrayList<>(1);
					usedDataSets.add(this.sdtmProjectDomainDataSetRepository.findOne(Long.valueOf(dataSet)));
					entity.setUsedDataSets(usedDataSets);
				}
			}
		}
		return node;
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
	public ResponseEntity<OperationResponse> updateDomainOrderForProject(@PathVariable("projectId") Long projectId,
			@RequestBody List<Map<String, Long>> request) {
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