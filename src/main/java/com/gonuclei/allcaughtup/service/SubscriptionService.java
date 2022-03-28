package com.gonuclei.allcaughtup.service;

import static org.elasticsearch.index.query.QueryBuilders.wrapperQuery;

import com.gonuclei.allcaughtup.constant.Constants;
import com.gonuclei.allcaughtup.exception.AuthenticationFailureException;
import com.gonuclei.allcaughtup.exception.InvalidOrderException;
import com.gonuclei.allcaughtup.exception.SubscriptionAlreadyExistsException;
import com.gonuclei.allcaughtup.exception.SubscriptionDoesNotExistException;
import com.gonuclei.allcaughtup.model.AppUser;
import com.gonuclei.allcaughtup.model.SubscribedUser;
import com.gonuclei.allcaughtup.model.Subscription;
import com.gonuclei.allcaughtup.repository.elasticsearch.SubscriptionElasticsearchRepository;
import com.gonuclei.allcaughtup.repository.jpa.AppUserRepository;
import com.gonuclei.allcaughtup.repository.jpa.SubscribedUserRepository;
import com.gonuclei.allcaughtup.repository.jpa.SubscriptionRepository;
import com.gonuclei.allcaughtup.util.JwtTokenUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.json.CDL;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final SubscribedUserRepository subscribedUserRepository;
  private final AppUserRepository appUserRepository;
  private final SubscriptionElasticsearchRepository subscriptionElasticsearchRepository;
  private final ElasticsearchOperations elasticsearchOperations;

  private final JwtTokenUtil jwtTokenUtil;

  private KafkaTemplate<String, String> kafkaTemplate;

  /**
   * Function which handles returning all the subscriptions in the database. Also caches the
   * response for future requests
   *
   * @return List of subscriptions in the database
   */
  @Cacheable(value = "itemCache")
  public List<Subscription> getAllSubscriptions(String name, String about, double priceLessThan) {
    log.info("Returning list of subscriptions from DB");

    ArrayList<Subscription> result = new ArrayList<>();

    if (name.isBlank() && about.isBlank() && priceLessThan == 0) {
      subscriptionElasticsearchRepository.findAll().forEach(result::add);
    } else {
      result.addAll(
          subscriptionElasticsearchRepository.findByNameLikeAndAboutLikeAndPriceLessThan(name,
              about, priceLessThan));
    }

    return result;
  }

  /**
   * FUnction which handles searching og subscriptions using elastic search
   *
   * @param query     Elastic search DSL query
   * @param sortField Field to sort the subscriptions by
   * @param sortOrder Order to sort the subscriptions in
   * @return List of Subscriptions matched
   */
  public List<Subscription> searchSubscription(String query, String sortField, String sortOrder) {
    if (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")) {
      throw new InvalidOrderException(Constants.INVALID_ORDER_MESSAGE);
    }

    Query searchQuery = new NativeSearchQueryBuilder()
        // with the 'query' part of the request
        .withQuery(wrapperQuery(query))
        // with sorting
        .withSorts(
            SortBuilders.fieldSort(sortField).order(SortOrder.fromString(sortOrder.toUpperCase())))
        // build the search query
        .build();
    SearchHits<Subscription> hits = elasticsearchOperations.search(searchQuery, Subscription.class);

    ArrayList<Subscription> results = new ArrayList<>();

    for (SearchHit<Subscription> hit : hits) {
      results.add(hit.getContent());
    }

    log.info("Returning search results");

    return results;
  }

  /**
   * Function which handles adding of a new subscription to the database, elastic search and
   * redis cache
   *
   * @param subscription Subscription object to be saved
   * @return List of subscriptions in the database
   */
  @CachePut(value = "itemCache")
  public Subscription addSubscription(Subscription subscription) {
    log.info("Subscription " + subscription.getName() + " is being added to DB and elastic search");

    Subscription sub = subscriptionRepository.save(subscription);
    subscriptionElasticsearchRepository.save(sub);
    return sub;
  }

  /**
   * Function to handle response of all the subscriptions which the user is currently subscribed to
   *
   * @param authHeader The Header with the JWT token in it
   * @return List of subscriptions which start and end date
   */
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

  /**
   * Function to handle the subscribe event, i.e. when the user wants to subscribe to a
   * particular subscription
   *
   * @param subscriptionId The ID of the subscription user wants to subscribe to
   * @param authHeader     The Header with the JWT token in it
   * @return The subscription which has start and end date
   */
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

  /**
   * Function to handle the unsubscribe event, i.e. when the user wants to unsubscribe from a
   * particular subscription
   *
   * @param subscriptionId ID of the subscription he/she wants to unsubscribe from
   * @param authHeader     The Header with the JWT token in it
   * @return The unsubscribed subscription which has start and end date
   */
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

  /**
   * Function to handle the renew event, i.e. when the user wants to renew his/her current
   * subscription
   *
   * @param subscriptionId ID of the subscription he/she wants to renew
   * @param authHeader     The Header with the JWT token in it
   * @return The renewed subscription which has start and a new end date
   */
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

  public String sendEmail(String email) {
    kafkaTemplate.send(Constants.KAFKA_TOPIC_NAME, email);

    return "Successfully sent message to Kafka";
  }
}
