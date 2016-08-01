package com.openodm.impl.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String index(Map<String, Object> model) {
		return "index";
	}

	@RequestMapping(path = "/ct/list", method = RequestMethod.GET)
	public String listAllCTs(Map<String, Object> model) {
		model.put("title", "All CTs");
		model.put("selected", "ct");
		return "ct/list";
	}

	@RequestMapping(path = "/ct/{id}", method = RequestMethod.GET)
	public String getCTById(@PathVariable Long id, Map<String, Object> model) {
		model.put("title", "CT");
		model.put("selected", "ct");
		model.put("id", id);
		return "ct/codeList";
	}

}
