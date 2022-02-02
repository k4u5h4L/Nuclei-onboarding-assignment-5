package com.gonuclei.allcaughtup.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.gonuclei.allcaughtup.model.UserDto;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JwtAuthenticationControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  UserDto user;

  @BeforeEach
  void setUp() {
    user = new UserDto("authtestuser@mail.com", "password");
  }

  @Test
  @DisplayName("User is logged in successfully")
  void shouldLoginUser() {
    String uri = "/auth/login";

    webTestClient
        // make a post request to the uri
        .post().uri(uri)
        // set the content type to application-json
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        // configure the body of the POST request
        .body(Mono.just(user), UserDto.class)
        // execute the request
        .exchange()
        // expect status to be OK
        .expectStatus().isOk()
        // expect the headers
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        // expect the body to be not empty
        .expectBody().jsonPath("$.jwttoken").isNotEmpty();
  }

  @Test
  @DisplayName("User is created successfully")
  void shouldRegisterUser() {
    String uri = "/auth/register";

    webTestClient
        // make a post request to the uri
        .post().uri(uri)
        // set the content type to application-json
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        // configure the body of the POST request
        .body(Mono.just(user), UserDto.class)
        // execute the request
        .exchange()
        // expect status to be OK
        .expectStatus().isOk()
        // expect the headers
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        // expect the body to be not empty
        .expectBody().jsonPath("$.email").isNotEmpty()
        // expect the body to be of some value
        .jsonPath("$.email").isEqualTo(user.getEmail());
  }

  @Test
  @DisplayName("User is created successfully")
  void shouldNotAllowWithoutLogin() {
    String uri = "/api/subscription/subscribe/1";

    webTestClient
        // make a get request to the uri
        .get().uri(uri)
        // accept content type application-json
        .accept(MediaType.APPLICATION_JSON)
        // execute the request
        .exchange()
        // expect status to be OK
        .expectStatus().isUnauthorized();
  }
}