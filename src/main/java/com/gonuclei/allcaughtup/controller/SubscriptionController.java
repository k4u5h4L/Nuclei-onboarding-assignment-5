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

  @RequestMapping(path = "/all", method = RequestMethod.GET)
  public List<Subscription> getAllSubscriptions() {
    return subscriptionService.getAllSubscriptions();
  }

  @RequestMapping(path = "/subscribed", method = RequestMethod.GET)
  public List<SubscribedUser> getSubscribedUserSubscriptions(HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.getSubscribedSubscriptions(authHeader);
  }

  @RequestMapping(path = "/subscribe/{subscriptionId}", method = RequestMethod.GET)
  public SubscribedUser subscribeUserToSubscription(HttpServletRequest request,
                                                    @PathVariable Long subscriptionId) {

    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.subscribeUserToSubscription(subscriptionId, authHeader);
  }

  @RequestMapping(path = "/cancel/{subscriptionId}", method = RequestMethod.GET)
  public SubscribedUser unsubscribeUserFromSubscription(HttpServletRequest request,
                                                        @PathVariable Long subscriptionId) {

    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.unsubscribeUserFromSubscription(subscriptionId, authHeader);
  }

  @RequestMapping(path = "/renew/{subscriptionId}", method = RequestMethod.GET)
  public SubscribedUser renewUserSubscription(HttpServletRequest request,
                                              @PathVariable Long subscriptionId) {

    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.renewUserSubscription(subscriptionId, authHeader);
  }

}
