package com.gonuclei.allcaughtup.repository.jpa;

import com.gonuclei.allcaughtup.model.Subscription;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

}
