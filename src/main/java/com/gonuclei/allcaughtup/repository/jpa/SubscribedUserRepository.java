package com.gonuclei.allcaughtup.repository.jpa;

import com.gonuclei.allcaughtup.model.SubscribedUser;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface SubscribedUserRepository extends CrudRepository<SubscribedUser, Long> {

  Optional<List<SubscribedUser>> findAllByUserId(Long userId);

  Optional<List<SubscribedUser>> findAllBySubscriptionId(Long subscriptionId);

  Optional<SubscribedUser> findByUserIdAndSubscriptionId(Long userId, Long subscriptionId);
}
