package com.gonuclei.allcaughtup;

import com.gonuclei.allcaughtup.model.AppUser;
import com.gonuclei.allcaughtup.model.SubscribedUser;
import com.gonuclei.allcaughtup.model.Subscription;
import com.gonuclei.allcaughtup.repository.AppUserRepository;
import com.gonuclei.allcaughtup.repository.SubscribedUserRepository;
import com.gonuclei.allcaughtup.repository.SubscriptionRepository;
import com.gonuclei.allcaughtup.util.DatabaseSeedUtil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
public class AllCaughtUpApplication {

  @Autowired
  private PasswordEncoder bcryptEncoder;

  public static void main(String[] args) {
    SpringApplication.run(AllCaughtUpApplication.class, args);
  }

  @Bean
  public CommandLineRunner runner(SubscriptionRepository subscriptionRepository,
                                  AppUserRepository appUserRepository,
                                  SubscribedUserRepository subscribedUserRepository) {
    return (args) -> {
      DatabaseSeedUtil.seedSubs(subscriptionRepository);
    };
  }
}
