package com.openodm.impl.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.epam.parso.SasFileReader;
import com.epam.parso.impl.SasFileReaderImpl;
import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.ct.EnumeratedItem;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainXref;
import com.openodm.impl.entity.sdtm.SDTMProjectLibrary;
import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;
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

@RestController
@SuppressWarnings("unchecked")
public class SDTMProjectController {
	private static final Logger LOG = LoggerFactory.getLogger(SDTMProjectController.class);
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

	@RequestMapping(value = "/sdtm/v1/project/{id}/library", method = RequestMethod.GET)
	public List<SDTMProjectLibrary> listProjectLibraries(@PathVariable("id") Long id) {
		List<SDTMProjectLibrary> libs = sdtmProjectLibraryRepository.findByProjectId(id);
		for (SDTMProjectLibrary library : libs) {
			List<Map<String, Object>> dataSetList = new ArrayList<>();
			if (library != null) {
				Path folder = Paths.get(library.getPath());
				if (Files.exists(folder) && folder.toFile().isDirectory()) {
					try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
						for (Path path : directoryStream) {
							Map<String, Object> map = new HashMap<String, Object>();
							dataSetList.add(map);
							Path fileName = path.getFileName();
							if (fileName.toString().lastIndexOf(".") > -1) {
								map.put("name", fileName.toString().substring(0, fileName.toString().lastIndexOf(".")));
							} else {
								map.put("name", fileName);
							}
							try (InputStream is = new FileInputStream(path.toFile())) {
								SasFileReader sasFileReader = new SasFileReaderImpl(is);
								map.put("columnList", sasFileReader.getColumns());
							} catch (FileNotFoundException e) {
								LOG.error("Got Exception during reading file, folder={}", folder, e);
							} catch (IOException e) {
								LOG.error("Got Exception during reading file, folder={}", folder, e);
							}

						}
					} catch (IOException ex) {
					}
				}
			}
			library.setDataSetList(dataSetList);

		}
		return libs;
	}

}
