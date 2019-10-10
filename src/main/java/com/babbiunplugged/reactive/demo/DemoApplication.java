package com.babbiunplugged.reactive.demo;

import static com.babbiunplugged.reactive.demo.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.babbiunplugged.reactive.demo.document.Item;
import com.babbiunplugged.reactive.demo.repository.ItemReactiverepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**Functional Interface
 * @author Prashant Gupta
 *
 */
@SpringBootApplication
public class DemoApplication implements CommandLineRunner{

	@Autowired
	private ItemReactiverepo itemRepo;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<Item> items = Arrays.asList(new Item(null, "Fitbit watch", 400.0), new Item(null, "Mi Band 4 watch", 450.50),
				new Item("ABC", "Honor 5 watch", 600.0), new Item(null, "Alexa", 300.0));
		
		itemRepo.deleteAll().thenMany(Flux.fromIterable(items).log("saved items:")).flatMap(itemRepo::save)
				.thenMany(itemRepo.findAll())
				.subscribe(item -> System.out.println("Item inserted from commandLineRunner:{}" + item));
	}

}

@Configuration
class WebConfiguration {
	Mono<ServerResponse> message(ServerRequest serverRequest){
		Mono<String> map = serverRequest.principal().map(p -> "Hello " + p.getName() + " !");
		return ServerResponse.ok().body(map, String.class);
	}
	
	Mono<ServerResponse> username(ServerRequest serverRequest) {
		Mono<UserDetails> map = serverRequest.principal()
				.map(p -> UserDetails.class.cast(Authentication.class.cast(p).getPrincipal()));
		return ServerResponse.ok().body(map, UserDetails.class);
	}

	@Bean
	RouterFunction<?> routes(){
		return route(GET("/message"), this::message).andRoute(GET("/user/{username}"), this::username);
	}
}

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		return http.authorizeExchange().pathMatchers("/admin").hasAuthority("ROLE_ADMIN")
				.pathMatchers(ITEM_FUNCTIONAL_END_POINT_V1).permitAll()
				.pathMatchers(ITEM_FUNCTIONAL_END_POINT_V1 + "/**").permitAll()
				.pathMatchers("/fun/runtimeexception").permitAll()
				.pathMatchers("/user/{username}")
				.access(new ReactiveAuthorizationManager<AuthorizationContext>() {

					@Override
					public Mono<AuthorizationDecision> check(Mono<Authentication> authentication,
							AuthorizationContext object) {

						return authentication.map(auth -> auth.getName().equals(object.getVariables().get("username")))
								.map(AuthorizationDecision::new);
					}
				}).anyExchange().authenticated().and().httpBasic().and().build();
	}

	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		UserDetails prashant = User.withUsername("prashant").password(passwordEncoder().encode("password"))
				.roles("USER").build();
		UserDetails amit = User.withUsername("amit").password(passwordEncoder().encode("password"))
				.roles("USER", "ADMIN").build();
		return new MapReactiveUserDetailsService(prashant, amit);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// return new BCryptPasswordEncoder(12);
		return NoOpPasswordEncoder.getInstance();
	}
}
