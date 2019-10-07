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
			new Item("ABC", "Honor 5 watch", 600.0), new Item(null, "Alexa", 300.0));

	@Before
	public void setUp() {
		itemRepo.deleteAll().thenMany(Flux.fromIterable(items)).flatMap(itemRepo::save).doOnNext(item -> {
			System.out.println("Inserted item is :{}" + item);
		}).blockLast();
	}

	@Test
	public void getAllItems() {
		StepVerifier.create(itemRepo.findAll()).expectSubscription().expectNextCount(4).verifyComplete();
	}

	@Test
	public void getItemById() {
		StepVerifier.create(itemRepo.findById("ABC")).expectSubscription()
				.expectNextMatches(item -> item.getDesc().equals("Honor 5 watch")).verifyComplete();
	}

	@Test
	public void getItemByDesc() {
		StepVerifier.create(itemRepo.findByDesc("Alexa").log()).expectSubscription()
				.expectNextMatches(item -> item.getPrice() == 300.0).verifyComplete();

		StepVerifier.create(itemRepo.findByDesc("Alexa")).expectSubscription().expectNextCount(1).verifyComplete();
	}
}
