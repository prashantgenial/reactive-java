package com.babbiunplugged.reactive.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.babbiunplugged.reactive.demo.service.GreetService;

import reactor.core.publisher.Mono;

@RestController
public class GreetController {

	@Autowired
	private GreetService greetService;

	@GetMapping("/greetService")
	public Mono<String> greetService() {
		return greetService.greet();
	}

	@GetMapping("/")
	public Mono<String> greet(Mono<Principal> principal) {
		return principal.map(Principal::getName).map(name -> String.format("Hello, %s", name));
	}

	@GetMapping("/admin")
	public Mono<String> greetAdmin(Mono<Principal> principal) {
		return principal.map(Principal::getName).map(name -> String.format("Admin access: %s", name));
	}
}
