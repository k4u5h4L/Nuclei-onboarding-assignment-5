package com.gonuclei.allcaughtup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * JwtRequest model which is used to get username and password from request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest implements Serializable {

  private static final long serialVersionUID = 5926468583005150707L;

  private String email;
  private String password;
}
