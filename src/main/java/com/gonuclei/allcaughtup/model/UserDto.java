package com.gonuclei.allcaughtup.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * UserDto model which is used to get the email and password, and to pass user data
 */
@Data
@AllArgsConstructor
public class UserDto {

  private String email;
  private String password;
}
