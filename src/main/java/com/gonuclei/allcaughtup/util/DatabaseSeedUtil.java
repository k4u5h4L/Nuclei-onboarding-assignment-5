package com.gonuclei.allcaughtup.util;

import com.gonuclei.allcaughtup.model.AppUser;
import com.gonuclei.allcaughtup.model.SubscribedUser;
import com.gonuclei.allcaughtup.model.Subscription;
import com.gonuclei.allcaughtup.repository.AppUserRepository;
import com.gonuclei.allcaughtup.repository.SubscribedUserRepository;
import com.gonuclei.allcaughtup.repository.SubscriptionRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Util class which helps seed the database with initial values if database is empty
 */
@Slf4j
public class DatabaseSeedUtil {


  /**
   * Seeds all the tables in the database with initial values if not present
   *
   * @param subscriptionRepository   SubscriptionRepository repository to insert values
   * @param appUserRepository        AppUserRepository repository to insert values
   * @param subscribedUserRepository SubscribedUserRepository repository to insert values
   * @param bcryptEncoder            The encoder to use to hash the passwords
   */
  public static void seedAll(SubscriptionRepository subscriptionRepository,
                             AppUserRepository appUserRepository,
                             SubscribedUserRepository subscribedUserRepository,
                             PasswordEncoder bcryptEncoder) {
    Subscription netflix = new Subscription(200.00, Period.of(0, 2, 0), "Netflix",
        "A movie " + "streaming service billed every 2 months");
    Subscription prime = new Subscription(300.00, Period.of(1, 0, 0), "Amazon Prime",
        "A " + "movie streaming service billed every year");
    Subscription disney = new Subscription(250.00, Period.of(0, 1, 0), "Disney +",
        "A movie streaming service billed every month");
    if (subscriptionRepository.count() == 0) {
      subscriptionRepository.save(netflix);

      subscriptionRepository.save(prime);

      subscriptionRepository.save(disney);
    } else {
      log.info("Subscriptions already seeded");
    }

    AppUser user1 = new AppUser("kaushal.bhat@gonuclei.com", bcryptEncoder.encode("password"));
    AppUser user2 = new AppUser("devkauhere@gmail.com", bcryptEncoder.encode("password"));

    if (appUserRepository.count() == 0) {
      appUserRepository.save(user1);
      appUserRepository.save(user2);
    } else {
      log.info("AppUsers already seeded");
    }

    SubscribedUser sc1 = new SubscribedUser(user1, netflix, LocalDate.now(),
        LocalDate.now().plus(netflix.getTimePeriod()));

    SubscribedUser sc2 = new SubscribedUser(user1, prime, LocalDate.now(),
        LocalDate.now().plus(prime.getTimePeriod()));

    if (subscribedUserRepository.count() == 0) {
      subscribedUserRepository.save(sc1);
      subscribedUserRepository.save(sc2);
    } else {
      log.info("SubscribedUsers already seeded");
    }

    log.info("All items in database seeding finished");
  }

  /**
   * Seeds only the subscriptions table in the database
   *
   * @param subscriptionRepository SubscriptionRepository repository to insert values
   */
  public static void seedSubs(SubscriptionRepository subscriptionRepository) {
    if (subscriptionRepository.count() == 0) {
      Subscription netflix = new Subscription(200.00, Period.of(0, 2, 0), "Netflix",
          "A movie " + "streaming service billed every 2 months");
      subscriptionRepository.save(netflix);

      Subscription prime = new Subscription(300.00, Period.of(1, 0, 0), "Amazon Prime",
          "A " + "movie streaming service billed every year");

      subscriptionRepository.save(prime);

      subscriptionRepository.save(new Subscription(250.00, Period.of(0, 1, 0), "Disney +",
          "A movie streaming service billed every month"));

      log.info("Subscriptions seeded");
    } else {
      log.info("Subscriptions already seeded");
    }
  }
}
