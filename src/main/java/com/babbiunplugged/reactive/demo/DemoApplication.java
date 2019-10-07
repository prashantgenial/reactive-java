package com.babbiunplugged.reactive.demo;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

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

import reactor.core.publisher.Mono;

/**Functional Interface
 * @author Prashant Gupta
 *
 */
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
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
