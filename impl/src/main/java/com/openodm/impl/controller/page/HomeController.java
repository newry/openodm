package com.openodm.impl.controller.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openodm.impl.controller.response.Breadcrumb;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectLibrary;
import com.openodm.impl.entity.sdtm.SDTMVersion;
import com.openodm.impl.repository.ct.CTVersionRepository;
import com.openodm.impl.repository.ct.CodeListRepository;
import com.openodm.impl.repository.ct.ControlTerminologyRepository;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectLibraryRepository;
import com.openodm.impl.repository.sdtm.SDTMProjectRepository;
import com.openodm.impl.repository.sdtm.SDTMVersionRepository;

@Controller
public class HomeController {
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

	@RequestMapping(path = "/project/{id}", method = RequestMethod.GET)
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
		model.put("title", "Key Variables for " + getDomainName(domainId, sdtmDomainRepository.findOne(domainId)));
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

	private void addBreadCrumbs(Long id, Map<String, Object> model, SDTMProject prj) {
		model.put("breadcrumbs", Arrays.asList(Breadcrumb.create("/project", "All Projects"),
				Breadcrumb.create("/project/" + id + "/toc", prj == null ? "#" + id : prj.getName())));
	}

}
