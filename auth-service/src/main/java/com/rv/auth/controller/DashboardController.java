package com.rv.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DashboardController {
	
	
	
	 @GetMapping("/dashboard")
	    public String dashboard( ) {
	        return "Welcome ";
	    }

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user")
	public String userDashboard() {
	    return "USER dashboard";
	}

}
