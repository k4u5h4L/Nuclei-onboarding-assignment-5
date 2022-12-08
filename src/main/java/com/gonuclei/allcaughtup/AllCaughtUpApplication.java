package com.gonuclei.allcaughtup;

import com.gonuclei.allcaughtup.repository.elasticsearch.SubscriptionElasticsearchRepository;
import com.gonuclei.allcaughtup.repository.jpa.AppUserRepository;
import com.gonuclei.allcaughtup.repository.jpa.SubscribedUserRepository;
import com.gonuclei.allcaughtup.repository.jpa.SubscriptionRepository;
import com.gonuclei.allcaughtup.util.DatabaseSeedUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories("com.gonuclei.allcaughtup.repository.jpa")
@EnableElasticsearchRepositories("com.gonuclei.allcaughtup.repository.elasticsearch")
public class AllCaughtUpApplication {

  @Autowired
  private PasswordEncoder bcryptEncoder;

  /**
   * Starting point of the application
   *
   * @param args command line arguements
   */
  public static void main(String[] args) {
    SpringApplication.run(AllCaughtUpApplication.class, args);
  }

  @Bean
  public CommandLineRunner runner(SubscriptionRepository subscriptionRepository,
                                  AppUserRepository appUserRepository,
                                  SubscribedUserRepository subscribedUserRepository,
                                  SubscriptionElasticsearchRepository subscriptionElasticsearchRepository) {
    return (args) -> {
      DatabaseSeedUtil.seedAll(subscriptionRepository, appUserRepository, subscribedUserRepository,
          bcryptEncoder);

      DatabaseSeedUtil.seedElasticsearchSubscriptions(subscriptionRepository,
          subscriptionElasticsearchRepository);
    };
  }
}
