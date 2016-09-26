package com.openodm.impl.controller.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.openodm.impl.controller.SDTMProjectController;
import com.openodm.impl.controller.SDTMProjectDomainController;
import com.openodm.impl.controller.response.Breadcrumb;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainDataSet;
import com.openodm.impl.entity.sdtm.SDTMProjectLibrary;
import com.openodm.impl.entity.sdtm.SDTMVersion;
import com.openodm.impl.repository.ct.CTVersionRepository;
import com.openodm.impl.repository.ct.CodeListRepository;
import com.openodm.impl.repository.ct.ControlTerminologyRepository;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectDomainDataSetRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectLibraryRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectRepository;
import com.openodm.impl.repository.sdtm.SDTMVersionRepository;

@Controller
public class HomeController {
	private static final String SPECIAL_SEPARATOR = "$$$";
	@Autowired
	private ControlTerminologyRepository controlTerminologyRepository;
	@Autowired
	private CodeListRepository codeListRepository;
	@Autowired
	private CTVersionRepository ctVersionRepository;
	@Autowired
	private SDTMVersionRepository sdtmVersionRepository;
	@Autowired
	private SDTMProjectRepository sdtmProjectRepository;
	@Autowired
	private SDTMProjectLibraryRepository sdtmProjectLibraryRepository;
	@Autowired
	private SDTMDomainRepository sdtmDomainRepository;
	@Autowired
	private SDTMProjectDomainDataSetRepository sdtmProjectDomainDataSetRepository;
	@Autowired
	private SDTMProjectController sdtmProjectController;
	@Autowired
	private SDTMProjectDomainController sdtmProjectDomainController;
	ObjectMapper om = new ObjectMapper();

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String index(Map<String, Object> model) {
		return "index";
	}

	@RequestMapping(path = "/ct", method = RequestMethod.GET)
	public String listAllCTs(Map<String, Object> model) {
		model.put("title", "All CTs");
		model.put("selected", "ct");
		return "ct/list";
	}

	@RequestMapping(path = "/ct/new", method = RequestMethod.GET)
	public String newCT(Map<String, Object> model) {
		model.put("title", "Create new CT");
		model.put("selected", "ct");
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/ct", "All CTs")));
		model.put("ctVersions", ctVersionRepository.findAll());

		return "ct/new";
	}

	@RequestMapping(path = "/ct/{id}/codeList", method = RequestMethod.GET)
	public String getCodeListByCTId(@PathVariable Long id, Map<String, Object> model) {
		model.put("title", "Code List");
		model.put("selected", "ct");
		model.put("ctId", id);
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/ct", "All CTs")));
		return "ct/codeList";
	}

	@RequestMapping(path = "/ct/{id}/codeList/{codeListId}", method = RequestMethod.GET)
	public String getEnumeratedItemByCTIdAndCodeListId(@PathVariable Long id, @PathVariable Long codeListId, Map<String, Object> model) {
		ControlTerminology ct = controlTerminologyRepository.findOne(id);
		CodeList codeList = codeListRepository.findOne(codeListId);
		model.put("title", "Enumerated Item");
		model.put("selected", "ct");
		model.put("ctId", id);
		model.put("codeListId", codeListId);
		model.put("customized", false);
		model.put("extended", codeList == null ? false : StringUtils.equalsIgnoreCase(codeList.getCodeListExtensible(), "Yes"));
		addBreadCrumbs(id, model, ct);
		return "ct/enumeratedItemList";
	}

	private void addBreadCrumbs(Long id, Map<String, Object> model, ControlTerminology ct) {
		model.put("breadcrumbs",
				Arrays.asList(Breadcrumb.create("/ct", "All CTs"), Breadcrumb.create("/ct/" + id + "/codeList", ct == null ? "#" + id : ct.getName())));
	}

	@RequestMapping(path = "/ct/{id}/customizedCodeList/{codeListId}", method = RequestMethod.GET)
	public String getEnumeratedItemByCTIdAndCustomizedCodeListId(@PathVariable Long id, @PathVariable Long codeListId, Map<String, Object> model) {
		ControlTerminology ct = controlTerminologyRepository.findOne(id);
		model.put("title", "Enumerated Item");
		model.put("selected", "ct");
		model.put("ctId", id);
		model.put("codeListId", codeListId);
		model.put("customized", true);
		model.put("extended", false);
		addBreadCrumbs(id, model, ct);
		return "ct/enumeratedItemList";
	}

	@RequestMapping(path = "/ct/{id}/selectCodeList", method = RequestMethod.GET)
	public String searchCodeListByCTId(@PathVariable Long id, Map<String, Object> model) {
		ControlTerminology ct = controlTerminologyRepository.findOne(id);
		model.put("title", "Select Code List");
		model.put("selected", "ct");
		model.put("ctId", id);
		addBreadCrumbs(id, model, ct);
		return "ct/selectCodeList";
	}

	@RequestMapping(path = "/project", method = RequestMethod.GET)
	public String listAllProjects(Map<String, Object> model) {
		model.put("title", "All Projects");
		model.put("selected", "prj");
		return "project/list";
	}

	@RequestMapping(path = "/project/new", method = RequestMethod.GET)
	public String newProject(Map<String, Object> model) {
		model.put("title", "Create new Project");
		model.put("selected", "prj");
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/project", "All Projects")));
		List<SDTMVersion> versions = sdtmVersionRepository.findAll();
		Set<String> set = new HashSet<String>();
		List<SDTMVersion> result = new ArrayList<SDTMVersion>();
		for (SDTMVersion version : versions) {
			if (set.add(version.getOid())) {
				result.add(version);
			}
		}
		model.put("sdtmVersions", versions);

		return "project/new";
	}

	@RequestMapping(path = "/project/{id}/edit", method = RequestMethod.GET)
	public String editProject(@PathVariable Long id, Map<String, Object> model) {
		model.put("title", "Edit Project");
		model.put("selected", "prj");
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/project", "All Projects")));
		model.put("prjId", id);
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		if (prj != null) {
			List<SDTMProjectLibrary> libs = this.sdtmProjectLibraryRepository.findByProjectId(id);
			prj.setLibraries(libs);
			model.put("project", prj);
		}

		return "project/edit";
	}

	@RequestMapping(path = "/project/{id}", method = RequestMethod.GET)
	public String viewProject(@PathVariable Long id, Map<String, Object> model) {
		model.put("title", "View Project");
		model.put("selected", "prj");
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/project", "All Projects")));
		model.put("prjId", id);
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		if (prj != null) {
			List<SDTMProjectLibrary> libs = this.sdtmProjectLibraryRepository.findByProjectId(id);
			prj.setLibraries(libs);
			model.put("project", prj);
		}

		return "project/view";
	}

	@RequestMapping(path = "/project/{id}/toc", method = RequestMethod.GET)
	public String getProjectTOC(@PathVariable Long id, Map<String, Object> model) {
		model.put("title", "Project TOC");
		model.put("selected", "prj");
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/project", "All Projects")));
		model.put("prjId", id);
		return "project/toc";
	}

	@RequestMapping(path = "/project/{id}/domain/{domainId}/selectKeyVariable", method = RequestMethod.GET)
	public String selectKeyVariables(@PathVariable Long id, @PathVariable Long domainId, Map<String, Object> model) {
		model.put("title", "Select Key Variables");
		model.put("selected", "prj");
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		addBreadCrumbs(id, model, prj);
		model.put("prjId", id);
		model.put("domainId", domainId);
		return "project/selectKeyVariable";
	}

	private String getDomainName(Long domainId, SDTMDomain domain) {
		return domain == null ? "#" + domainId : domain.getName();
	}

	@RequestMapping(path = "/project/{id}/domain/{domainId}/variable", method = RequestMethod.GET)
	public String getProjectDomainVariablesKeyVariables(@PathVariable Long id, @PathVariable Long domainId, Map<String, Object> model) {
		model.put("title", "Variables For " + getDomainName(domainId, sdtmDomainRepository.findOne(domainId)));
		model.put("selected", "prj");
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		addBreadCrumbs(id, model, prj);
		model.put("prjId", id);
		model.put("domainId", domainId);
		return "project/domainVariable";
	}

	@RequestMapping(path = "/project/{id}/domain/{domainId}/dataSet", method = RequestMethod.GET)
	public String getProjectDomainDataSetList(@PathVariable Long id, @PathVariable Long domainId, Map<String, Object> model) {
		model.put("title", "Data Set For " + getDomainName(domainId, sdtmDomainRepository.findOne(domainId)));
		model.put("selected", "prj");
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		addBreadCrumbs(id, model, prj);
		model.put("prjId", id);
		model.put("domainId", domainId);
		return "project/domainDataSetList";
	}

	@RequestMapping(path = "/project/{id}/domain/{domainId}/dataSet/new", method = { RequestMethod.GET })
	public String createProjectDomainDataSet(@PathVariable Long id, @PathVariable Long domainId, Map<String, Object> model) {
		String domainName = getDomainName(domainId, sdtmDomainRepository.findOne(domainId));
		model.put("title", "Data Set For " + domainName);
		model.put("selected", "prj");
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		model.put("breadcrumbs",
				Arrays.asList(Breadcrumb.create("/project", "All Projects"),
						Breadcrumb.create("/project/" + id + "/toc", prj == null ? "#" + id : prj.getName()),
						Breadcrumb.create("/project/" + id + "/domain/" + domainId + "/dataSet", "dataSet")));
		model.put("prjId", id);
		model.put("domainId", domainId);
		addLibs(id, model, domainName);
		return "project/newDomainDataSet";
	}

	@RequestMapping(path = "/project/{id}/domain/{domainId}/dataSet/new", method = { RequestMethod.POST })
	public String createProjectDomainDataSetWithType(@PathVariable Long id, @PathVariable Long domainId, HttpServletRequest request,
			Map<String, Object> model) {
		String domainName = getDomainName(domainId, sdtmDomainRepository.findOne(domainId));
		model.put("title", "Data Set For " + domainName);
		model.put("selected", "prj");
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		model.put("breadcrumbs",
				Arrays.asList(Breadcrumb.create("/project", "All Projects"),
						Breadcrumb.create("/project/" + id + "/toc", prj == null ? "#" + id : prj.getName()),
						Breadcrumb.create("/project/" + id + "/domain/" + domainId + "/dataSet", "dataSet")));
		model.put("prjId", id);
		model.put("domainId", domainId);
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			model.put(entry.getKey(), entry.getValue()[0]);
		}
		buildMap(id, domainId, request.getParameter("joinType"), model, null);
		addLibs(id, model, domainName);
		return "project/newDomainDataSet";
	}

	@RequestMapping(path = "/project/{id}/domain/{domainId}/dataSet/{dataSetId}", method = { RequestMethod.GET })
	public String getProjectDomainDataSet(@PathVariable Long id, @PathVariable Long domainId, @PathVariable Long dataSetId, Map<String, Object> model) {
		String domainName = getDomainName(domainId, sdtmDomainRepository.findOne(domainId));
		model.put("title", "Data Set For " + domainName);
		model.put("selected", "prj");
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		model.put("breadcrumbs",
				Arrays.asList(Breadcrumb.create("/project", "All Projects"),
						Breadcrumb.create("/project/" + id + "/toc", prj == null ? "#" + id : prj.getName()),
						Breadcrumb.create("/project/" + id + "/domain/" + domainId + "/dataSet", "dataSet")));
		model.put("prjId", id);
		model.put("domainId", domainId);
		model.put("dataSetId", dataSetId);
		addLibs(id, model, domainName);
		SDTMProjectDomainDataSet dataSet = sdtmProjectDomainDataSetRepository.findOne(dataSetId);
		model.put("dataSet", dataSet);
		buildMap(id, domainId, dataSet.getJoinType(), model, dataSet);
		return "project/editDomainDataSet";
	}

	private void buildMap(Long id, Long domainId, String joinType, Map<String, Object> model, SDTMProjectDomainDataSet dataSet) {
		if (StringUtils.equalsIgnoreCase(joinType, "set")) {
			List<String> names = new ArrayList<>();
			if (dataSet != null) {
				String metaData = dataSet.getMetaData();
				try {
					JsonNode jsonNode = om.reader().readTree(metaData);
					ArrayNode dataSetArrayNode = (ArrayNode) jsonNode.get("dataSets");
					if (dataSetArrayNode != null && dataSetArrayNode.size() > 0) {
						for (int i = 0; i < dataSetArrayNode.size(); i++) {
							JsonNode dataSetNode = dataSetArrayNode.get(i);
							JsonNode libIdNode = dataSetNode.get("libId");
							String dataSetStr = dataSetNode.get("dataSet").asText();
							if (libIdNode != null && !libIdNode.isNull()) {
								Map<String, Object> m = new HashMap<>();
								names.add(libIdNode.asText() + SPECIAL_SEPARATOR + dataSetStr);
							} else {
								names.add(SPECIAL_SEPARATOR + dataSetStr);
							}
						}

					}
				} catch (IOException e) {
				}
			}
			Map<String, String> map = new LinkedHashMap<>();
			Map<String, String> selectedMap = new LinkedHashMap<>();
			List<SDTMProjectLibrary> libs = sdtmProjectLibraryRepository.findByProjectId(id);
			for (SDTMProjectLibrary lib : libs) {
				List<Map<String, Object>> dataSets = sdtmProjectController.listProjectLibrariesDataSet(id, lib);
				if (!CollectionUtils.isEmpty(dataSets)) {
					map.put(lib.getName(), buildOptions(dataSets, lib, null).toString());
					if (!CollectionUtils.isEmpty(names)) {
						List<Map<String, Object>> selectedDataSets = new ArrayList<>();
						for (Map<String, Object> dataSetToUse : dataSets) {
							if (names.contains(lib.getId() + SPECIAL_SEPARATOR + dataSetToUse.get("name"))) {
								selectedDataSets.add(dataSetToUse);
							}
						}
						if (!CollectionUtils.isEmpty(selectedDataSets)) {
							selectedMap.put(lib.getName(), buildOptions(selectedDataSets, lib, null).toString());
						}
					}
				}
			}
			List<Map<String, Object>> dataSets = this.sdtmProjectDomainController.listProjectDomainDataSetShort(id, domainId);
			if (!CollectionUtils.isEmpty(dataSets)) {
				SDTMProjectLibrary lib = new SDTMProjectLibrary();
				lib.setName("WORK");
				map.put(lib.getName(), buildOptions(dataSets, lib, dataSet).toString());
				if (!CollectionUtils.isEmpty(names)) {
					List<Map<String, Object>> selectedDataSets = new ArrayList<>();
					for (Map<String, Object> dataSetToUse : dataSets) {
						if (names.contains(SPECIAL_SEPARATOR + dataSetToUse.get("name"))) {
							selectedDataSets.add(dataSetToUse);
						}
					}
					if (!CollectionUtils.isEmpty(selectedDataSets)) {
						selectedMap.put(lib.getName(), buildOptions(selectedDataSets, lib, dataSet).toString());
					}
				}

			}
			if (!CollectionUtils.isEmpty(map)) {
				model.put("availableDataSetMap", map);
			}
			if (!CollectionUtils.isEmpty(selectedMap)) {
				model.put("selectedDataSetMap", selectedMap);
			}
		}
	}

	private StringBuilder buildOptions(List<Map<String, Object>> dataSets, SDTMProjectLibrary lib, SDTMProjectDomainDataSet existingDataSet) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> dataSet : dataSets) {
			if (lib.getId() < 0 && existingDataSet.getId().equals(dataSet.get("name"))) {
				continue;
			}
			sb.append("<option value=\"").append(dataSet.get("name")).append("\"");
			if (lib != null) {
				if (lib.getId() != null && lib.getId() > 0) {
					sb.append(" LibraryId=\"").append(lib.getId()).append("\"");
				}
				if (lib.getName() != null) {
					sb.append(" LibraryName=\"").append(lib.getName()).append("\"");
				}
			}
			sb.append(">").append(dataSet.get("label")).append("</option>");
		}
		return sb;
	}

	private void addLibs(Long id, Map<String, Object> model, String domainName) {
		List<SDTMProjectLibrary> libs = this.sdtmProjectLibraryRepository.findByProjectId(id);
		SDTMProjectLibrary workLib = new SDTMProjectLibrary();
		workLib.setName("WORK");
		libs.add(workLib);
		model.put("libs", libs);
	}

	private void addBreadCrumbs(Long id, Map<String, Object> model, SDTMProject prj) {
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/project", "All Projects"),
				Breadcrumb.create("/project/" + id + "/toc", prj == null ? "#" + id : prj.getName())));
	}

	@RequestMapping(path = "/project/{id}/domain/{domainId}/variable/{variableId}/selectCodeList", method = RequestMethod.GET)
	public String selectCodelist(@PathVariable Long id, @PathVariable Long domainId, @PathVariable Long variableId, Map<String, Object> model) {
		model.put("title", "Select Code List");
		model.put("selected", "prj");
		SDTMProject prj = sdtmProjectRepository.findOne(id);
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/project", "All Projects"),
				Breadcrumb.create("/project/" + id + "/toc", prj == null ? "#" + id : prj.getName()),
				Breadcrumb.create("/project/" + id + "/domain/" + domainId + "/variable", getDomainName(domainId, sdtmDomainRepository.findOne(domainId)))));
		model.put("prjId", id);
		model.put("variableId", variableId);
		model.put("domainId", domainId);
		return "project/selectDomainVariableCodeList";
	}

}
