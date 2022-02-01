package com.gonuclei.allcaughtup.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.gonuclei.allcaughtup.model.Subscription;
import com.gonuclei.allcaughtup.model.UserDto;
import com.gonuclei.allcaughtup.repository.AppUserRepository;
import com.gonuclei.allcaughtup.repository.SubscriptionRepository;
import com.gonuclei.allcaughtup.service.AuthenticationService;
import com.gonuclei.allcaughtup.service.JwtUserDetailsService;
import com.gonuclei.allcaughtup.util.DatabaseSeedUtil;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubscriptionControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private AppUserRepository appUserRepository;

  @Autowired
  private SubscriptionRepository subscriptionRepository;

  @Autowired
  private JwtUserDetailsService userDetailsService;

  @Autowired
  private AuthenticationService authenticationService;

  private String jwtTokenHeader;
  private UserDto user;

  @BeforeEach
  void setUp() throws Exception {
    user = new UserDto("subscriptionuser@mail.com", "password");

    DatabaseSeedUtil.seedSubs(subscriptionRepository);

    if (appUserRepository.findByEmail(user.getEmail()).isEmpty()) {
      userDetailsService.save(user);
    }
    jwtTokenHeader = "Bearer " + authenticationService.signIn(user.getEmail(), user.getPassword());
  }

  @Test
  @DisplayName("User is able to get different subscriptions to choose from")
  void shouldReturnDifferentSubscriptions() {
    String uri = "/api/subscription/all";

    webTestClient
        // make a get request
        .get().uri(uri)
        // accept the content type application-json
        .accept(MediaType.APPLICATION_JSON)
        // set auth headers
        .header("Authorization", jwtTokenHeader)
        // execute the request
        .exchange()
        // expect the status to be OK
        .expectStatus().isOk()
        // expect the body to not be empty
        .expectBody()
        .consumeWith(response -> Assertions.assertThat(response.getResponseBody()).isNotNull());
  }

  @Test
  @DisplayName("User is able to subscribe to a subscription")
  void shouldSubscribeuserToThatSubscription() {
    Subscription subscription = subscriptionRepository.findAll().iterator().next();
    String uri = "/api/subscription/subscribe/" + subscription.getId();

    webTestClient
        // make a get request
        .get().uri(uri)
        // accept the content type application-json
        .accept(MediaType.APPLICATION_JSON)
        // set auth headers
        .header("Authorization", jwtTokenHeader)
        // execute the request
        .exchange()
        // expect the status to be OK
        .expectStatus().isOk()
        // expect the body to not be empty
        .expectBody()
        .consumeWith(response -> Assertions.assertThat(response.getResponseBody()).isNotNull());
  }
}