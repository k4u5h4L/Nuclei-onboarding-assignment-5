package com.gonuclei.allcaughtup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"subscribedUsers"})
@ToString(exclude = {"subscribedUsers"})
public class Subscription implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Double price;
  private Period timePeriod;
  private String name;
  private String about;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonIgnore
  private List<SubscribedUser> subscribedUsers = new ArrayList<>();

  public Subscription(final Double price, final Period timePeriod, final String name,
                      final String about) {
    this.price = price;
    this.timePeriod = timePeriod;
    this.name = name;
    this.about = about;
  }
}
