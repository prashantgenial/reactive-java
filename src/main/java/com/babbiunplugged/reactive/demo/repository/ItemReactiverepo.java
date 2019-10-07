package com.babbiunplugged.reactive.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.babbiunplugged.reactive.demo.document.Item;

import reactor.core.publisher.Flux;

public interface ItemReactiverepo extends ReactiveMongoRepository<Item, String> {

	Flux<Item> findByDesc(String desc);

}
