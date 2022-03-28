package com.gonuclei.allcaughtup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

/**
 * JwtResponse model which is used to send the JWT token to the user on successful authentication
 */
@Data
@AllArgsConstructor
public class JwtResponse implements Serializable {

  private static final long serialVersionUID = -8091879091924046844L;
  private final String jwttoken;
}