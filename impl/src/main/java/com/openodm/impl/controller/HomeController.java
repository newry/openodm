package com.openodm.impl.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
	@Value("${application.message:Hello World}")
	private String message = "Hello World";

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String index(Map<String, Object> model) {
		model.put("time", new Date());
		model.put("message", this.message);
		return "index";
	}

}
