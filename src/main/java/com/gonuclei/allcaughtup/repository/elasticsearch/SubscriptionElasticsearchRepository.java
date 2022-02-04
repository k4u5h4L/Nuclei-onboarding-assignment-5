package com.gonuclei.allcaughtup.repository.elasticsearch;

import com.gonuclei.allcaughtup.model.Subscription;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SubscriptionElasticsearchRepository
    extends ElasticsearchRepository<Subscription, Long> {

}
