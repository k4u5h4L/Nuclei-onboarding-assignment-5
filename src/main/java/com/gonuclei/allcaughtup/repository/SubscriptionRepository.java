package com.gonuclei.allcaughtup.repository;

import com.gonuclei.allcaughtup.model.Subscription;

import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

  public static final String HASH_KEY = "Subscription";
}
