package com.babbiunplugged.reactive.demo.FluxAndMonoTest;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMono {

	String[] strArray = new String[] { "Prashant", "Ayaansh", "Vrinda", "Pratima" };
	List<String> ls = Arrays.asList(strArray);

	@Test
	public void flux1() {

		Flux<String> strFlux = Flux.just("Spring", "Spring boot", "Java reactive programming")
				.concatWith(Flux.error(new RuntimeException("Exception occured!!"))).log();
		// strFlux.subscribe(System.out::println);
		StepVerifier.create(strFlux).expectNext("Spring", "Spring boot", "Java reactive programming")
				.expectErrorMessage("Exception occured!!").verify();
	}

	@Test
	public void flux2() {

		// Flux<String> strFlux = Flux.fromIterable(ls).log();
		Flux<String> strFlux = Flux.fromArray(strArray).log();
		// strFlux.subscribe(System.out::println);
		StepVerifier.create(strFlux).expectNext("Prashant", "Ayaansh", "Vrinda", "Pratima")
				.verifyComplete();
	}
}
