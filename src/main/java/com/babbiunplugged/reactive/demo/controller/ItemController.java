package com.babbiunplugged.reactive.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.babbiunplugged.reactive.demo.document.Item;
import com.babbiunplugged.reactive.demo.repository.ItemReactiverepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ItemController {

	@Autowired
	private ItemReactiverepo itemRepo;

	@GetMapping("/items")
	public Flux<Item> findAll() {
		return itemRepo.findAll();
	}

	@GetMapping("/items/{id}")
	public Mono<ResponseEntity<Item>> findById(@PathVariable String id) {
		return itemRepo.findById(id).map(item -> new ResponseEntity<>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/items")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Item> create(@RequestBody Item item) {
		return itemRepo.save(item);
	}

	@DeleteMapping("/items/{id}")
	public Mono<Void> deleteById(@PathVariable String id) {
		return itemRepo.deleteById(id);
	}
}
