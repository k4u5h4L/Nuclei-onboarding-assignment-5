package com.gonuclei.allcaughtup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 * AppUser model which is used for authentication
 */
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"subscriptions"})
@ToString(exclude = {"subscriptions"})
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  @Column(unique = true)
  private String email;

  @Column
  @JsonIgnore
  private String password;

  @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonIgnore
  private List<SubscribedUser> subscriptions = new ArrayList<>();

  public AppUser(final String email, final String password) {
    this.email = email;
    this.password = password;
  }
}
