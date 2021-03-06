package com.token.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/public")
public class UserController {

	@GetMapping("test")
	public String test1() {
		System.out.println("UserController -> test1");
		return "API Test";
	}

}
