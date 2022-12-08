package com.gonuclei.allcaughtup.repository.elasticsearch;

import com.gonuclei.allcaughtup.model.Subscription;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SubscriptionElasticsearchRepository
    extends ElasticsearchRepository<Subscription, Long> {

  List<Subscription> findByNameLikeAndAboutLikeAndPriceLessThan(String name, String about,
                                                                double priceLessThan);

  List<Subscription> findByNameLikeOrAboutLikeOrPriceLessThan(String name, String about,
                                                              double priceLessThan);
}
