package com.openodm.impl.controller;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainXref;
import com.openodm.impl.entity.sdtm.SDTMProjectLibrary;
import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;
import com.openodm.impl.entity.sdtm.SDTMVariable;
import com.openodm.impl.entity.sdtm.SDTMVariableRef;
import com.openodm.impl.entity.sdtm.SDTMVersion;
import com.openodm.impl.repository.ct.ControlTerminologyRepository;
import com.openodm.impl.repository.ct.EnumeratedItemRepository;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectDomainXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectLibraryRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectVariableXrefRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRefRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRepository;
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
	private SDTMVariableRepository sdtmVariableRepository;
	@Autowired
	private EnumeratedItemRepository enumeratedItemRepository;
	@Autowired
	private SDTMProjectRepository sdtmProjectRepository;
	@Autowired
	private SDTMProjectVariableXrefRepository sdtmProjectVariableXrefRepository;
	@Autowired
	private SDTMProjectDomainXrefRepository sdtmProjectDomainXrefRepository;
	@Autowired
	private ControlTerminologyRepository controlTerminologyRepository;
	@Autowired
	private SDTMProjectLibraryRepository sdtmProjectLibraryRepository;

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

	@RequestMapping(value = "/sdtm/v1/project", method = RequestMethod.POST)
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
			this.sdtmProjectRepository.save(project);
			List<SDTMDomain> domains = this.sdtmDomainRepository.findByVersionId(project.getSdtmVersion().getId());
			int i = 1;
			List<SDTMProjectDomainXref> domainXrefs = new ArrayList<SDTMProjectDomainXref>();
			for (SDTMDomain domain : domains) {
				SDTMProjectDomainXref domainXref = new SDTMProjectDomainXref();
				domainXref.setCreator("admin");
				domainXref.setUpdatedBy("admin");
				domainXref.setSdtmDomain(domain);
				domainXref.setSdtmProject(project);
				domainXref.setOrderNumber(i++);
				domainXrefs.add(domainXref);
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
			// create all domain xrefs
			this.sdtmProjectLibraryRepository.save(libs);

			// create all domain xrefs
			this.sdtmProjectDomainXrefRepository.save(domainXrefs);

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
	public ResponseEntity<OperationResponse> updateProject(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
		String name = StringUtils.trim(request.get("name"));
		String desc = StringUtils.trim(request.get("description"));
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
		try {
			this.sdtmProjectRepository.save(project);
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

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/variable", method = RequestMethod.GET)
	public List<SDTMProjectVariableXref> listProjectDomainVariables(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId) {
		List<SDTMProjectVariableXref> refs = new ArrayList<SDTMProjectVariableXref>(0);
		List<SDTMDomain> domains = this.sdtmProjectDomainXrefRepository.findDomainByProjectId(id);
		if (!CollectionUtils.isEmpty(domains)) {
			for (SDTMDomain domain : domains) {
				if (domain.getId().equals(domainId)) {
					List<SDTMVariable> prjVars = this.sdtmProjectVariableXrefRepository.findVariableByProjectId(id);
					List<SDTMVariableRef> varRefs = this.sdtmVariableRefRepository.findByDomainId(domain.getId());
					for (SDTMVariableRef varRef : varRefs) {
						if (!prjVars.contains(varRef.getSdtmVariable())) {
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
			}
		}
		return refs;
	}

	@RequestMapping(value = "/sdtm/v1/project/{id}/domain/{domainId}/allVariable", method = RequestMethod.GET)
	public List<SDTMProjectVariableXref> listAllProjectDomainVariables(@PathVariable("id") Long id, @PathVariable("domainId") Long domainId) {
		List<SDTMProjectVariableXref> refs = new ArrayList<SDTMProjectVariableXref>(0);
		List<SDTMDomain> domains = this.sdtmProjectDomainXrefRepository.findDomainByProjectId(id);
		if (!CollectionUtils.isEmpty(domains)) {
			for (SDTMDomain domain : domains) {
				if (domain.getId().equals(domainId)) {
					List<SDTMVariable> prjVars = this.sdtmProjectVariableXrefRepository.findVariableByProjectId(id);
					List<SDTMVariableRef> varRefs = this.sdtmVariableRefRepository.findByDomainId(domain.getId());
					for (SDTMVariableRef varRef : varRefs) {
						SDTMProjectVariableXref ref = new SDTMProjectVariableXref();
						ref.setCore(StringUtils.isEmpty(varRef.getCore()) ? "Perm" : varRef.getCore());
						ref.setOrderNumber(varRef.getOrderNumber());
						ref.setRole(varRef.getRole());
						ref.setSdtmVariable(varRef.getSdtmVariable());
						ref.setSdtmDomain(domain);
						if (prjVars.contains(varRef.getSdtmVariable())) {
							ref.setExcluded(true);
						}
						refs.add(ref);
					}
				}

			}
		}
		return refs;
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
	public List<SDTMDomain> listProjectDomains(@PathVariable("id") Long id) {
		return this.sdtmProjectDomainXrefRepository.findDomainByProjectId(id);
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
			domainXref.setStatus(ObjectStatus.deleted);
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

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> removeVariableFromProject(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId) {
		SDTMProjectVariableXref prjVarRef = sdtmProjectVariableXrefRepository.findByProjectIdAndVariableId(projectId, variableId);
		if (prjVarRef != null) {
			if (!prjVarRef.getStatus().equals(ObjectStatus.deleted)) {
				prjVarRef.setStatus(ObjectStatus.deleted);
				prjVarRef.setUpdatedBy("admin");
				prjVarRef.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				return this.updateProjectVariableXref(prjVarRef);
			}
		}
		OperationResponse or = new OperationResponse();
		OperationResult result = new OperationResult();
		result.setSuccess(true);
		or.setResult(result);
		return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);

	}

	private ResponseEntity<OperationResponse> updateProjectDomainXref(SDTMProjectDomainXref domainXref) {
		try {
			this.sdtmProjectDomainXrefRepository.save(domainXref);
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

	private ResponseEntity<OperationResponse> updateProjectVariableXref(SDTMProjectVariableXref prjVarRef) {
		try {
			sdtmProjectVariableXrefRepository.save(prjVarRef);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during creating the Project Variable Ref", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the Project Variable Ref");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/sdtm/v1/project/{projectId}/variable/{variableId}", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> addVariableFromProject(@PathVariable("projectId") Long projectId, @PathVariable("variableId") Long variableId) {
		SDTMProject project = this.sdtmProjectRepository.findOne(projectId);
		if (project == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid project id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		SDTMVariable var = this.sdtmVariableRepository.findOne(variableId);
		if (var == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid variable id!");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		} else {
			if (!var.getSdtmDomain().getSdtmVersion().getId().equals(project.getSdtmVersion().getId())) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("Invalid variable id!");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
			}
		}

		SDTMProjectVariableXref prjVarRef = sdtmProjectVariableXrefRepository.findByProjectIdAndVariableId(projectId, variableId);
		if (prjVarRef != null) {
			prjVarRef.setStatus(ObjectStatus.active);
			prjVarRef.setUpdatedBy("admin");
			prjVarRef.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
			return this.updateProjectVariableXref(prjVarRef);
		} else {
			prjVarRef = new SDTMProjectVariableXref();
			prjVarRef.setCreator("admin");
			prjVarRef.setUpdatedBy("admin");
			prjVarRef.setSdtmProject(project);
			prjVarRef.setSdtmVariable(var);
			prjVarRef.setCore("");
			prjVarRef.setOrderNumber(-1);
			return this.updateProjectVariableXref(prjVarRef);
		}
	}

}
