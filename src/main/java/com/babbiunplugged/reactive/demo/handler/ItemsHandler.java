package com.babbiunplugged.reactive.demo.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

//import com.babbiunplugged.document.ItemCapped;
import com.babbiunplugged.reactive.demo.document.Item;
//import com.babbiunplugged.repository.ItemReactiveCappedRepository;
//import com.babbiunplugged.repository.ItemReactiveRepository;
import com.babbiunplugged.reactive.demo.repository.ItemReactiverepo;

import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

    @Autowired
	ItemReactiverepo itemReactiveRepository;

//    @Autowired
//    ItemReactiveCappedRepository itemReactiveCappedRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);

    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);
		return itemMono
				.flatMap(item -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(item)))
				.switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {

        Mono<Item> itemTobeInserted = serverRequest.bodyToMono(Item.class);

        return itemTobeInserted.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item), Item.class));

    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");
        Mono<Void> deleteItem = itemReactiveRepository.deleteById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");

        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap((item) -> {

                    Mono<Item> itemMono = itemReactiveRepository.findById(id)
                            .flatMap(currentItem -> {
                                currentItem.setDesc(item.getDesc());
                                currentItem.setPrice(item.getPrice());
                                return itemReactiveRepository.save(currentItem);

                            });
                    return itemMono;
                });
		return updatedItem
				.flatMap(item -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(item)))
				.switchIfEmpty(notFound);


    }

//    public Mono<ServerResponse> itemsStream(ServerRequest serverRequest){
//
//        return  ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_STREAM_JSON)
//                .body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);
//    }

    public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){

        throw new RuntimeException("RuntimeException Occurred");
    }


}
