package com.babbiunplugged.reactive.demo.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class GreetService {

	/**
	 * Method level security implementation by @PreAuthorize
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<String> greet() {
		return Mono.just("Hello from service!");
	}
}
