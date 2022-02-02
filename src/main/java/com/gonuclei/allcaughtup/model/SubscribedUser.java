package com.gonuclei.allcaughtup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor
public class SubscribedUser {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JsonIgnore
  private AppUser user;

  @JoinColumn(name = "subscription_id", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private Subscription subscription;

  private LocalDate startDate;
  private LocalDate endDate;

  public SubscribedUser(final AppUser user, final Subscription subscription,
                        final LocalDate startDate, final LocalDate endDate) {
    this.user = user;
    this.subscription = subscription;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SubscribedUser)) return false;
    SubscribedUser that = (SubscribedUser) o;
    return Objects.equals(user.getEmail(), that.user.getEmail()) && Objects.equals(
        user.getPassword(), that.user.getPassword()) && Objects.equals(subscription.getName(),
        that.subscription.getName()) && Objects.equals(subscription.getPrice(),
        that.subscription.getPrice()) && Objects.equals(subscription.getTimePeriod(),
        that.subscription.getTimePeriod()) && Objects.equals(subscription.getAbout(),
        that.subscription.getAbout()) && Objects.equals(startDate, that.startDate)
        && Objects.equals(endDate, that.endDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user.getEmail(), subscription.getName(), startDate, endDate);
  }
}
