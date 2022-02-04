package com.gonuclei.allcaughtup.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.gonuclei.allcaughtup.constant.Constants;
import com.gonuclei.allcaughtup.exception.AuthenticationFailureException;
import com.gonuclei.allcaughtup.model.AppUser;
import com.gonuclei.allcaughtup.model.Subscription;
import com.gonuclei.allcaughtup.model.UserDto;
import com.gonuclei.allcaughtup.repository.jpa.AppUserRepository;
import com.gonuclei.allcaughtup.repository.jpa.SubscribedUserRepository;
import com.gonuclei.allcaughtup.repository.jpa.SubscriptionRepository;
import com.gonuclei.allcaughtup.service.AuthenticationService;
import com.gonuclei.allcaughtup.service.JwtUserDetailsService;
import com.gonuclei.allcaughtup.service.SubscriptionService;
import com.gonuclei.allcaughtup.util.DatabaseSeedUtil;
import com.gonuclei.allcaughtup.util.JwtTokenUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubscriptionControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private AppUserRepository appUserRepository;

  @Autowired
  private SubscriptionRepository subscriptionRepository;

  @Autowired
  private SubscribedUserRepository subscribedUserRepository;

  @Autowired
  private JwtUserDetailsService userDetailsService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private SubscriptionService subscriptionService;

  @Autowired
  private AuthenticationService authenticationService;

  private String jwtTokenHeader = "";
  private UserDto userDto;
  private AppUser user;

  @BeforeEach
  void setUp() throws Exception {
    userDto = new UserDto("subscriptionuser@mail.com", "password");

    if (subscriptionRepository.count() == 0) {
      DatabaseSeedUtil.seedSubs(subscriptionRepository);
    }

    if (appUserRepository.findByEmail(userDto.getEmail()).isEmpty()) {
      user = userDetailsService.save(userDto);
    }

    if (jwtTokenHeader.isBlank() || !jwtTokenHeader.startsWith("Bearer ")) {
      jwtTokenHeader =
          "Bearer " + authenticationService.signIn(userDto.getEmail(), userDto.getPassword());
    }
  }

  @Test
  @DisplayName("User is able to get different subscriptions to choose from")
  @Order(1)
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
  @Order(2)
  void shouldSubscribeUserToThatSubscription() {
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

  @Test
  @DisplayName("User is able to cancel a current subscription")
  @Order(5)
  void shouldCancelCurrentSubscription() {
    Subscription subscription = subscriptionRepository.findAll().iterator().next();

    String email = jwtTokenUtil.getUsernameFromToken(jwtTokenHeader.substring(7));

    if (appUserRepository.findByEmail(email).isEmpty()) {
      throw new AuthenticationFailureException(Constants.USER_NOT_FOUND_MESSAGE);
    }

    AppUser appUser = appUserRepository.findByEmail(email).get();

    if (subscribedUserRepository.findAllByUserId(appUser.getId()).isEmpty()) {
      subscriptionService.subscribeUserToSubscription(subscription.getId(), jwtTokenHeader);
    }

    String uri = "/api/subscription/cancel/" + subscription.getId();

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
  @DisplayName("User is able to get subscriptions already subscribed")
  @Order(3)
  void shouldReturnDifferentSubscribedSubscriptions() {
    Subscription subscription = subscriptionRepository.findAll().iterator().next();

    String email = jwtTokenUtil.getUsernameFromToken(jwtTokenHeader.substring(7));

    if (appUserRepository.findByEmail(email).isEmpty()) {
      throw new AuthenticationFailureException(Constants.USER_NOT_FOUND_MESSAGE);
    }

    AppUser appUser = appUserRepository.findByEmail(email).get();

    if (subscribedUserRepository.findAllByUserId(appUser.getId()).isEmpty()) {
      subscriptionService.subscribeUserToSubscription(subscription.getId(), jwtTokenHeader);
    }

    String uri = "/api/subscription/subscribed";

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
  @DisplayName("User is able to renew subscriptions already subscribed")
  @Order(4)
  void shouldRenewSubscribedSubscriptions() {
    Subscription subscription = subscriptionRepository.findAll().iterator().next();

    String email = jwtTokenUtil.getUsernameFromToken(jwtTokenHeader.substring(7));

    if (appUserRepository.findByEmail(email).isEmpty()) {
      throw new AuthenticationFailureException(Constants.USER_NOT_FOUND_MESSAGE);
    }

    AppUser appUser = appUserRepository.findByEmail(email).get();

    if (subscribedUserRepository.findByUserIdAndSubscriptionId(appUser.getId(),
        subscription.getId()).isEmpty()) {
      subscriptionService.subscribeUserToSubscription(subscription.getId(), jwtTokenHeader);
    }

    String uri = "/api/subscription/renew/" + subscription.getId();

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