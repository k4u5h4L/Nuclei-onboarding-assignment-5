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

@Slf4j
public class DatabaseSeedUtil {


  public static void seedAll(SubscriptionRepository subscriptionRepository,
                             AppUserRepository appUserRepository,
                             SubscribedUserRepository subscribedUserRepository,
                             PasswordEncoder bcryptEncoder) {
    Subscription netflix = new Subscription(200.00, Period.of(0, 2, 0), "Netflix",
        "A movie " + "streaming service billed every 2 months");
    subscriptionRepository.save(netflix);

    Subscription prime = new Subscription(300.00, Period.of(1, 0, 0), "Amazon Prime",
        "A " + "movie streaming service billed every year");

    subscriptionRepository.save(prime);

    subscriptionRepository.save(new Subscription(250.00, Period.of(0, 1, 0), "Disney +",
        "A movie streaming service billed every month"));

    appUserRepository.save(new AppUser("admin", bcryptEncoder.encode("admin-password")));

    AppUser user1 = new AppUser("user1", bcryptEncoder.encode("test123"));
    appUserRepository.save(user1);

    SubscribedUser sc1 = new SubscribedUser(user1, netflix, LocalDate.now(),
        LocalDate.now().plus(netflix.getTimePeriod()));

    SubscribedUser sc2 = new SubscribedUser(user1, prime, LocalDate.now(),
        LocalDate.now().plus(prime.getTimePeriod()));

    subscribedUserRepository.save(sc1);
    subscribedUserRepository.save(sc2);

    log.info("All items in database seeded");
  }

  public static void seedSubs(SubscriptionRepository subscriptionRepository) {
    Subscription netflix = new Subscription(200.00, Period.of(0, 2, 0), "Netflix",
        "A movie " + "streaming service billed every 2 months");
    subscriptionRepository.save(netflix);

    Subscription prime = new Subscription(300.00, Period.of(1, 0, 0), "Amazon Prime",
        "A " + "movie streaming service billed every year");

    subscriptionRepository.save(prime);

    subscriptionRepository.save(new Subscription(250.00, Period.of(0, 1, 0), "Disney +",
        "A movie streaming service billed every month"));

    log.info("Subscriptions seeded");
  }
}
