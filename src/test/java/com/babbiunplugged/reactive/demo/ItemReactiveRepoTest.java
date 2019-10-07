package com.babbiunplugged.reactive.demo;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.babbiunplugged.reactive.demo.document.Item;
import com.babbiunplugged.reactive.demo.repository.ItemReactiverepo;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepoTest {

	@Autowired
	private ItemReactiverepo itemRepo;

	List<Item> items = Arrays.asList(new Item(null, "Fitbit watch", 400.0), new Item(null, "Mi Band 4 watch", 450.50),
			new Item(null, "Honor 5 watch", 600.0));

	@Before
	public void setUp() {
		itemRepo.deleteAll().thenMany(Flux.fromIterable(items)).flatMap(itemRepo::save).doOnNext(item -> {
			System.out.println("Inserted item is :{}" + item);
		}).blockLast();
	}

	@Test
	public void getAllItems() {
		StepVerifier.create(itemRepo.findAll()).expectSubscription().expectNextCount(3).verifyComplete();
	}
}
