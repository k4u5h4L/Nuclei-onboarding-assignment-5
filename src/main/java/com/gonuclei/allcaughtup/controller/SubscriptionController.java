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
  public List<Subscription> getSubscriptions() {
    return subscriptionService.getAllSubscriptions();
  }

  @RequestMapping(path = "/subscribe/{subscriptionId}", method = RequestMethod.GET)
  public SubscribedUser subscribeUser(HttpServletRequest request,
                                      @PathVariable Long subscriptionId) {

    final String authHeader = request.getHeader("Authorization");

    return subscriptionService.subscribeUserToSubscription(subscriptionId, authHeader);
  }

}
