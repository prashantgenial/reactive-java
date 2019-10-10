package com.babbiunplugged.reactive.demo.controller;

import static com.babbiunplugged.reactive.demo.constants.ItemConstants.ITEM_END_POINT_V1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.babbiunplugged.reactive.demo.document.Item;
import com.babbiunplugged.reactive.demo.repository.ItemReactiverepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ItemController {

	Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemReactiverepo itemRepo;

	/*
	 * @ExceptionHandler(RuntimeException.class) public ResponseEntity<String>
	 * handleRuntimeException(RuntimeException ex){
	 * log.error("Exception caught in handleRuntimeException :  {} " , ex); return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage())
	 * ; }
	 */

	@GetMapping(ITEM_END_POINT_V1)
	public Flux<Item> findAll() {
		return itemRepo.findAll();
	}

	@GetMapping(ITEM_END_POINT_V1 + "/{id}")
	public Mono<ResponseEntity<Item>> findById(@PathVariable String id) {
		return itemRepo.findById(id).map(item -> new ResponseEntity<>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping(ITEM_END_POINT_V1)
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Item> create(@RequestBody Item item) {
		return itemRepo.save(item);
	}

	@DeleteMapping(ITEM_END_POINT_V1 + "/{id}")
	public Mono<Void> deleteById(@PathVariable String id) {
		return itemRepo.deleteById(id);
	}

	@GetMapping(ITEM_END_POINT_V1 + "/runtimeException")
	public Flux<Item> runtimeException() {

		return itemRepo.findAll()
				.concatWith(Mono.error(new RuntimeException("RuntimeException Occurred.")));
	}

	@PutMapping(ITEM_END_POINT_V1 + "/{id}")
	public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {

		return itemRepo.findById(id).flatMap(currentItem -> {

			currentItem.setPrice(item.getPrice());
			currentItem.setDesc(item.getDesc());
			return itemRepo.save(currentItem);
		}).map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}
}
