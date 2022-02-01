package com.gonuclei.allcaughtup.controller;

import com.gonuclei.allcaughtup.model.JwtRequest;
import com.gonuclei.allcaughtup.model.JwtResponse;
import com.gonuclei.allcaughtup.model.UserDto;
import com.gonuclei.allcaughtup.service.AuthenticationService;
import com.gonuclei.allcaughtup.service.JwtUserDetailsService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
@CrossOrigin
@AllArgsConstructor
public class JwtAuthenticationController {

  private JwtUserDetailsService userDetailsService;

  private AuthenticationService authenticationService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest)
      throws Exception {

    final String token = authenticationService.signIn(authenticationRequest.getEmail(),
        authenticationRequest.getPassword());

    return ResponseEntity.ok(new JwtResponse(token));
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<?> saveUser(@RequestBody UserDto user) throws Exception {
    return ResponseEntity.ok(userDetailsService.save(user));
  }

}
