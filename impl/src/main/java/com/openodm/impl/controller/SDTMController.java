package com.openodm.impl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openodm.impl.bo.SDTMBO;
import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.ct.EnumeratedItem;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMOrigin;
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainXref;
import com.openodm.impl.entity.sdtm.SDTMProjectKeyVariableXref;
import com.openodm.impl.entity.sdtm.SDTMProjectLibrary;
import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;
import com.openodm.impl.entity.sdtm.SDTMVariable;
import com.openodm.impl.entity.sdtm.SDTMVariableRef;
import com.openodm.impl.entity.sdtm.SDTMVersion;
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

@SuppressWarnings("unchecked")
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
		SDTMVersion sdtmVersion = sdtmVersionRepository.findOne(versionId);
		if (sdtmVersion != null) {
			List<ControlTerminology> versions = sdtmVersionRepository.findCTByOid(sdtmVersion.getOid());
			return versions;
		}
		return new ArrayList<ControlTerminology>();
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

	@RequestMapping(value = "/sdtm/v1/project", method = RequestMethod.GET)
	public List<SDTMProject> listProjects() {
		return this.sdtmProjectRepository.findAll();
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}", method = RequestMethod.GET)
	public ResponseEntity<SDTMProject> getProjectById(@PathVariable("id") Long id) {
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		if (prj != null) {
			List<SDTMProjectLibrary> libs = this.sdtmProjectLibraryRepository.findByProjectId(id);
			prj.setLibraries(libs);
			return new ResponseEntity<SDTMProject>(prj, HttpStatus.OK);
		} else {
			return new ResponseEntity<SDTMProject>(prj, HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/sdtm/v1/project", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<OperationResponse> createProject(@RequestBody Map<String, Object> request) {
		String name = StringUtils.trim((String) request.get("name"));
		String desc = StringUtils.trim((String) request.get("description"));
		Object versionIdObj = request.get("versionId");
		Object ctIdObj = request.get("ctId");
		List<Map<String, String>> libraryList = (List<Map<String, String>>) request.get("libraryList");

		SDTMProject project = new SDTMProject();
		project.setCreator("admin");
		project.setUpdatedBy("admin");
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		SDTMVersion sdtmVersion;
		if (versionIdObj == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("version is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		} else {
			sdtmVersion = sdtmVersionRepository.findOne(Long.valueOf(versionIdObj.toString()));
			if (sdtmVersion == null) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("version is required");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
			}
		}
		if (!CollectionUtils.isEmpty(libraryList)) {
			for (Map<String, String> library : libraryList) {
				String libName = library.get("name");
				String libPath = library.get("path");
				if (StringUtils.isEmpty(libName)) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("lib name is required");
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
				} else {
					if (!libName.matches("[a-zA-Z][a-zA-Z0-9]{1,7}")) {
						OperationResponse or = new OperationResponse();
						OperationResult result = new OperationResult();
						result.setSuccess(false);
						result.setError("lib name is invalid");
						or.setResult(result);
						return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
					}
				}
				if (StringUtils.isEmpty(libPath)) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("lib path is required");
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
				}

			}
		}

		ControlTerminology ct;
		if (ctIdObj == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("ct is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		} else {
			ct = this.controlTerminologyRepository.findOne(Long.valueOf(ctIdObj.toString()));
			if (ct == null) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("ct is required");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
			}
		}

		List<SDTMVersion> versions = this.sdtmVersionRepository.findByOidAndCtId(sdtmVersion.getOid(), ct.getId());
		if (CollectionUtils.isEmpty(versions)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("version is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		project.setSdtmVersion(versions.get(0));
		project.setName(name);
		project.setDescription(desc);
		try {
			List<SDTMDomain> domains = this.sdtmDomainRepository.findByVersionId(project.getSdtmVersion().getId());
			int i = 1;
			List<SDTMProjectDomainXref> domainXrefs = new ArrayList<SDTMProjectDomainXref>();
			List<SDTMProjectVariableXref> varXrefs = new ArrayList<SDTMProjectVariableXref>();
			for (SDTMDomain domain : domains) {
				SDTMProjectDomainXref domainXref = new SDTMProjectDomainXref();
				domainXref.setCreator("admin");
				domainXref.setUpdatedBy("admin");
				domainXref.setSdtmDomain(domain);
				domainXref.setSdtmProject(project);
				domainXref.setOrderNumber(i++);
				domainXrefs.add(domainXref);
				List<SDTMVariableRef> varRefs = this.sdtmVariableRefRepository.findCoreVariableByDomainId(domain.getId());
				for (SDTMVariableRef varRef : varRefs) {
					SDTMProjectVariableXref varXref = new SDTMProjectVariableXref();
					varXref.setCreator("admin");
					varXref.setUpdatedBy("admin");
					varXref.setSdtmDomain(domain);
					varXref.setSdtmProject(project);
					varXref.setOrderNumber(varRef.getOrderNumber());
					varXref.setSdtmDomain(domain);
					varXref.setSdtmVariable(varRef.getSdtmVariable());
					varXref.setCore(varRef.getCore());
					varXref.setRole(varRef.getRole());
					varXref.setCodeList(varRef.getSdtmVariable().getCodeList());
					varXref.setEnumeratedItems(new ArrayList<EnumeratedItem>(varRef.getSdtmVariable().getEnumeratedItems()));
					varXrefs.add(varXref);
				}
			}

			List<SDTMProjectLibrary> libs = new ArrayList<SDTMProjectLibrary>();
			if (!CollectionUtils.isEmpty(libraryList)) {
				for (Map<String, String> library : libraryList) {
					String libName = library.get("name");
					String libPath = library.get("path");
					SDTMProjectLibrary lib = new SDTMProjectLibrary();
					lib.setCreator("admin");
					lib.setUpdatedBy("admin");
					lib.setName(libName);
					lib.setPath(libPath);
					lib.setSdtmProject(project);
					libs.add(lib);
				}
			}
			this.sdtmProjectRepository.save(project);

			// create all domain xrefs
			this.sdtmProjectLibraryRepository.save(libs);

			// create all domain xrefs
			this.sdtmProjectDomainXrefRepository.save(domainXrefs);

			// create all variable xrefs
			this.sdtmProjectVariableXrefRepository.save(varXrefs);

			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.CREATED);
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

	@RequestMapping(value = "/sdtm/v1/project/{id}", method = RequestMethod.PUT)
	public ResponseEntity<OperationResponse> updateProject(@PathVariable("id") Long id, @RequestBody Map<String, Object> request) {
		String name = StringUtils.trim((String) request.get("name"));
		String desc = StringUtils.trim((String) request.get("description"));
		SDTMProject project = this.sdtmProjectRepository.findOne(id);
		if (project == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("invalid project id");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		project.setName(name);
		project.setDescription(desc);
		project.setUpdatedBy("admin");
		project.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
		List<Map<String, String>> libraryList = (List<Map<String, String>>) request.get("libraryList");
		if (!CollectionUtils.isEmpty(libraryList)) {
			for (Map<String, String> library : libraryList) {
				String libName = library.get("name");
				String libPath = library.get("path");
				if (StringUtils.isEmpty(libName)) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("lib name is required");
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
				} else {
					if (!libName.matches("[a-zA-Z][a-zA-Z0-9]{1,7}")) {
						OperationResponse or = new OperationResponse();
						OperationResult result = new OperationResult();
						result.setSuccess(false);
						result.setError("lib name is invalid");
						or.setResult(result);
						return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
					}
				}
				if (StringUtils.isEmpty(libPath)) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("lib path is required");
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
				}

			}
		}
		List<SDTMProjectLibrary> existLibs = this.sdtmProjectLibraryRepository.findByProjectId(id);
		List<SDTMProjectLibrary> libs = new ArrayList<SDTMProjectLibrary>();
		if (!CollectionUtils.isEmpty(libraryList)) {
			for (Map<String, String> library : libraryList) {
				String libName = library.get("name");
				String libPath = library.get("path");
				SDTMProjectLibrary lib = new SDTMProjectLibrary();
				lib.setCreator("admin");
				lib.setUpdatedBy("admin");
				lib.setName(libName);
				lib.setPath(libPath);
				lib.setSdtmProject(project);
				libs.add(lib);
			}
		}

		try {
			this.sdtmProjectRepository.save(project);
			if (!CollectionUtils.isEmpty(libraryList)) {
				sdtmProjectLibraryRepository.delete(existLibs);
			}
			if (!CollectionUtils.isEmpty(libs)) {
				this.sdtmProjectLibraryRepository.save(libs);
			}
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during update the Project", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the Project");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/variable", method = RequestMethod.GET)
	public List<SDTMProjectVariableXref> listProjectVariables(@PathVariable("id") Long id) {
		List<SDTMProjectVariableXref> refs = new ArrayList<SDTMProjectVariableXref>(0);

		List<SDTMDomain> domains = this.sdtmProjectDomainXrefRepository.findDomainByProjectId(id);
		if (!CollectionUtils.isEmpty(domains)) {
			for (SDTMDomain domain : domains) {
				List<SDTMVariableRef> varRefs = this.sdtmVariableRefRepository.findByDomainId(domain.getId());
				for (SDTMVariableRef varRef : varRefs) {
					SDTMProjectVariableXref ref = new SDTMProjectVariableXref();
					ref.setCore(StringUtils.isEmpty(varRef.getCore()) ? "Perm" : varRef.getCore());
					ref.setOrderNumber(varRef.getOrderNumber());
					ref.setRole(varRef.getRole());
					ref.setSdtmVariable(varRef.getSdtmVariable());
					ref.setSdtmDomain(domain);
					refs.add(ref);
				}
			}
		}

		return refs;
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/codeList", method = RequestMethod.GET)
	public List<CodeList> listProjectCodeList(@PathVariable("id") Long id) {
		SDTMProject project = sdtmProjectRepository.findOne(id);
		if (project != null) {
			return project.getSdtmVersion().getControlTerminology().getCodeLists();
		}
		return new ArrayList<CodeList>(0);
	}

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

	@RequestMapping(value = "/sdtm/v1/project/{id}/allDomain", method = RequestMethod.GET)
	public List<SDTMDomain> listAllDomains(@PathVariable("id") Long id) {
		SDTMProject project = this.sdtmProjectRepository.findOne(id);
		if (project != null) {
			List<SDTMDomain> domainList = this.sdtmDomainRepository.findByVersionId(project.getSdtmVersion().getId());
			List<SDTMDomain> domains = this.sdtmProjectDomainXrefRepository.findDomainByProjectId(id);
			if (!CollectionUtils.isEmpty(domains)) {
				Iterator<SDTMDomain> it = domainList.iterator();
				while (it.hasNext()) {
					SDTMDomain domain = it.next();
					if (domains.contains(domain)) {
						it.remove();
					}
				}
				for (SDTMDomain domain : domains) {
					domain.setAdded(true);
					domainList.add(domain);
				}
			}
			return domainList;
		}
		return new ArrayList<SDTMDomain>(0);
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

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/domain/{domainId}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> removeDomainToProject(@PathVariable("projectId") Long projectId, @PathVariable("domainId") Long domainId) {
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

	private ResponseEntity<OperationResponse> updateProjectDomainXrefs(List<SDTMProjectDomainXref> domainXrefs) {
		try {
			this.sdtmProjectDomainXrefRepository.save(domainXrefs);
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

	private ResponseEntity<OperationResponse> updateProjectDomainXref(SDTMProjectDomainXref domainXref) {
		return this.updateProjectDomainXrefs(Arrays.asList(domainXref));
	}

}
