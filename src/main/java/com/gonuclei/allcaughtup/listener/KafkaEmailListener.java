package com.gonuclei.allcaughtup.listener;

import com.gonuclei.allcaughtup.constant.Constants;
import com.gonuclei.allcaughtup.model.AppUser;
import com.gonuclei.allcaughtup.model.SubscribedUser;
import com.gonuclei.allcaughtup.model.Subscription;
import com.gonuclei.allcaughtup.repository.elasticsearch.SubscriptionElasticsearchRepository;
import com.gonuclei.allcaughtup.repository.jpa.AppUserRepository;
import com.gonuclei.allcaughtup.repository.jpa.SubscribedUserRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

@Component
@Slf4j
public class KafkaEmailListener {

  @Autowired
  private AppUserRepository appUserRepository;

  @Autowired
  private SubscribedUserRepository subscribedUserRepository;

  @Autowired
  private JavaMailSender emailSender;

  @Value("${spring.mail.username}")
  private String mailUsername;

  @KafkaListener(topics = Constants.KAFKA_TOPIC_NAME, groupId = Constants.KAFKA_GROUP_NAME)
  void listener(String email) {
    log.info("Email listener service received email: " + email);

    if (appUserRepository.findByEmail(email).isEmpty()) {
      throw new UsernameNotFoundException(Constants.USER_NOT_FOUND_MESSAGE);
    }

    AppUser user = appUserRepository.findByEmail(email).get();

    if (subscribedUserRepository.findAllByUserId(user.getId()).isPresent()) {
      SubscribedUser sc = subscribedUserRepository.findAllByUserId(user.getId()).get().get(0);

      SimpleMailMessage message = new SimpleMailMessage();

      message.setFrom(mailUsername);
      message.setTo(email);
      message.setSubject("Subscription that you had subscribed to");
      message.setText(sc.getSubscription().getName() + "\n\n\n" + sc.getSubscription().getAbout());
      emailSender.send(message);

      log.info("Email successfully sent to email: " + email);
    } else {
      log.warn("No subscriptions from user " + user.getEmail() + " were found");
    }
  }

}
