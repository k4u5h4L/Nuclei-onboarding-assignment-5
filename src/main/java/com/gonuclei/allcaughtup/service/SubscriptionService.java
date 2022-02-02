package com.gonuclei.allcaughtup.service;

import com.gonuclei.allcaughtup.constant.Constants;
import com.gonuclei.allcaughtup.exception.AuthenticationFailureException;
import com.gonuclei.allcaughtup.exception.SubscriptionAlreadyExistsException;
import com.gonuclei.allcaughtup.exception.SubscriptionDoesNotExistException;
import com.gonuclei.allcaughtup.model.AppUser;
import com.gonuclei.allcaughtup.model.SubscribedUser;
import com.gonuclei.allcaughtup.model.Subscription;
import com.gonuclei.allcaughtup.repository.AppUserRepository;
import com.gonuclei.allcaughtup.repository.SubscribedUserRepository;
import com.gonuclei.allcaughtup.repository.SubscriptionRepository;
import com.gonuclei.allcaughtup.util.JwtTokenUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final SubscribedUserRepository subscribedUserRepository;
  private final AppUserRepository appUserRepository;

  private final JwtTokenUtil jwtTokenUtil;

  @Cacheable(value = "itemCache")
  public List<Subscription> getAllSubscriptions() {
    log.info("Returning list of subscriptions from DB");

    ArrayList<Subscription> result = new ArrayList<>();
    subscriptionRepository.findAll().forEach(result::add);

    return result;
  }

  public List<SubscribedUser> getSubscribedSubscriptions(String authHeader) {
    log.info("Returning list of subscriptions subscribed by user from DB");

    String email = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));

    if (appUserRepository.findByEmail(email).isEmpty()) {
      throw new AuthenticationFailureException(Constants.USER_NOT_FOUND_MESSAGE);
    }

    AppUser user = appUserRepository.findByEmail(email).get();

    ArrayList<SubscribedUser> result = new ArrayList<>();

    if (subscribedUserRepository.findAllByUserId(user.getId()).isPresent()) {
      result.addAll(subscribedUserRepository.findAllByUserId(user.getId()).get());
    }

    return result;
  }

  public SubscribedUser subscribeUserToSubscription(Long subscriptionId, String authHeader) {
    if (subscriptionRepository.findById(subscriptionId).isEmpty()) {
      throw new SubscriptionDoesNotExistException(Constants.SUBSCRIPTION_NOT_FOUND_MESSAGE);
    }

    Subscription subscription = subscriptionRepository.findById(subscriptionId).get();

    String email = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));

    if (appUserRepository.findByEmail(email).isEmpty()) {
      throw new AuthenticationFailureException(Constants.USER_NOT_FOUND_MESSAGE);
    }

    AppUser user = appUserRepository.findByEmail(email).get();

    if (subscribedUserRepository.findByUserIdAndSubscriptionId(user.getId(), subscriptionId)
        .isPresent()) {
      throw new SubscriptionAlreadyExistsException(Constants.SUBSCRIPTION_ALREADY_EXISTS_MESSAGE);
    }

    log.info("Subscribing the user to the subscription");

    return subscribedUserRepository.save(new SubscribedUser(user, subscription, LocalDate.now(),
        LocalDate.now().plus(subscription.getTimePeriod())));
  }

  public SubscribedUser unsubscribeUserFromSubscription(Long subscriptionId, String authHeader) {
    if (subscriptionRepository.findById(subscriptionId).isEmpty()) {
      throw new SubscriptionDoesNotExistException(Constants.SUBSCRIPTION_NOT_FOUND_MESSAGE);
    }

    Subscription subscription = subscriptionRepository.findById(subscriptionId).get();

    String email = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));

    if (appUserRepository.findByEmail(email).isEmpty()) {
      throw new AuthenticationFailureException(Constants.USER_NOT_FOUND_MESSAGE);
    }

    AppUser user = appUserRepository.findByEmail(email).get();

    if (subscribedUserRepository.findByUserIdAndSubscriptionId(user.getId(), subscriptionId)
        .isEmpty()) {
      throw new SubscriptionDoesNotExistException(Constants.USER_IS_NOT_SUBSCRIBED_MESSAGE);
    }

    log.info("Unsubscribing the user from the subscription");

    SubscribedUser su =
        subscribedUserRepository.findByUserIdAndSubscriptionId(user.getId(), subscriptionId).get();

    subscribedUserRepository.delete(su);

    return su;
  }

  public SubscribedUser renewUserSubscription(Long subscriptionId, String authHeader) {
    if (subscriptionRepository.findById(subscriptionId).isEmpty()) {
      throw new SubscriptionDoesNotExistException(Constants.SUBSCRIPTION_NOT_FOUND_MESSAGE);
    }

    Subscription subscription = subscriptionRepository.findById(subscriptionId).get();

    String email = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));

    if (appUserRepository.findByEmail(email).isEmpty()) {
      throw new AuthenticationFailureException(Constants.USER_NOT_FOUND_MESSAGE);
    }

    AppUser user = appUserRepository.findByEmail(email).get();

    if (subscribedUserRepository.findByUserIdAndSubscriptionId(user.getId(), subscriptionId)
        .isEmpty()) {
      throw new SubscriptionDoesNotExistException(Constants.USER_IS_NOT_SUBSCRIBED_MESSAGE);
    }

    log.info("Renewing the user's subscription");

    SubscribedUser subscribedUser =
        subscribedUserRepository.findByUserIdAndSubscriptionId(user.getId(), subscriptionId).get();

    subscribedUser.setEndDate(subscribedUser.getEndDate().plus(subscription.getTimePeriod()));

    return subscribedUserRepository.save(subscribedUser);
  }
}
