package com.example.amazon.dynamodb.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping
public class IndexController {

	@GetMapping
	public RedirectView index() {
		return new RedirectView("/dynamodb");
	}

}
