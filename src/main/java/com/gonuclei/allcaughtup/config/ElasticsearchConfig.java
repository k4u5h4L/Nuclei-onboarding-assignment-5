package com.gonuclei.allcaughtup.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;

/**
 * Configuration for Elastic search
 */
@Configuration
//@EnableElasticsearchRepositories(queryLookupStrategy = QueryLookupStrategy.Key
// .CREATE_IF_NOT_FOUND)
@EnableElasticsearchRepositories(basePackages = "com.gonuclei.allcaughtup.repository.elasticsearch")
@ComponentScan(basePackages = {"com.gonuclei.allcaughtup.service"})
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

  @Override
  @Bean
  public RestHighLevelClient elasticsearchClient() {

    final ClientConfiguration clientConfiguration =
        ClientConfiguration.builder().connectedTo("localhost:9200").build();

    return RestClients.create(clientConfiguration).rest();
  }
}
