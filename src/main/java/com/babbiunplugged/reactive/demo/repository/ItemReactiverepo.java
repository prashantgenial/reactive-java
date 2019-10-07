package com.babbiunplugged.reactive.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.babbiunplugged.reactive.demo.document.Item;

public interface ItemReactiverepo extends ReactiveMongoRepository<Item, String> {

}
