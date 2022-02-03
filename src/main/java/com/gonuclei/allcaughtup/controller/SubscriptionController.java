package com.gonuclei.allcaughtup.controller;

import com.gonuclei.allcaughtup.model.SubscribedUser;
import com.gonuclei.allcaughtup.model.Subscription;
import com.gonuclei.allcaughtup.service.SubscriptionService;

import lombok.AllArgsConstructor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/subscription")
@AllArgsConstructor
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  /**
   * Function to handle response of all the subscriptions in the database/cache
   *
   * @return List of subscriptions currently in the database/cache
   */
  @RequestMapping(path = "/all", method = RequestMethod.GET)
  public List<Subscription> getAllSubscriptions() {
    return subscriptionService.getAllSubscriptions();
  }

  /**
   * Function to handle response of all the subscriptions which the user is currently subscribed to
   *
   * @param request Request object which contain information like Headers
   * @return List of all subscriptions with start and end date
   */
  @RequestMapping(path = "/subscribed", method = RequestMethod.GET)
  public List<SubscribedUser> getSubscribedUserSubscriptions(HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.getSubscribedSubscriptions(authHeader);
  }

  /**
   * Function to handle the subscribe event, i.e. when the user wants to subscribe to a
   * particular subscription
   *
   * @param request        Request object which contain information like Headers
   * @param subscriptionId ID of the subscription he/she wants to subscribe to
   * @return The subscription which has start and end date
   */
  @RequestMapping(path = "/subscribe/{subscriptionId}", method = RequestMethod.GET)
  public SubscribedUser subscribeUserToSubscription(HttpServletRequest request,
                                                    @PathVariable Long subscriptionId) {

    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.subscribeUserToSubscription(subscriptionId, authHeader);
  }

  /**
   * Function to handle the unsubscribe event, i.e. when the user wants to unsubscribe from a
   * particular subscription
   *
   * @param request        Request object which contain information like Headers
   * @param subscriptionId ID of the subscription he/she wants to unsubscribe from
   * @return The unsubscribed subscription which has start and end date
   */
  @RequestMapping(path = "/cancel/{subscriptionId}", method = RequestMethod.GET)
  public SubscribedUser unsubscribeUserFromSubscription(HttpServletRequest request,
                                                        @PathVariable Long subscriptionId) {

    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.unsubscribeUserFromSubscription(subscriptionId, authHeader);
  }

  /**
   * Function to handle the renew event, i.e. when the user wants to renew his/her current
   * subscription
   *
   * @param request        Request object which contain information like Headers
   * @param subscriptionId ID of the subscription he/she wants to renew
   * @return The renewed subscription which has start and a new end date
   */
  @RequestMapping(path = "/renew/{subscriptionId}", method = RequestMethod.GET)
  public SubscribedUser renewUserSubscription(HttpServletRequest request,
                                              @PathVariable Long subscriptionId) {

    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.renewUserSubscription(subscriptionId, authHeader);
  }

}
