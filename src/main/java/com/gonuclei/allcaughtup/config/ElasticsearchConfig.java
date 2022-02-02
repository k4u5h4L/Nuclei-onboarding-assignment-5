package com.gonuclei.allcaughtup.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;

@Configuration
@EnableElasticsearchRepositories(queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND)
public class ElasticsearchConfig {

  @Bean
  RestHighLevelClient elasticsearchClient() {
    RestHighLevelClient client =
        new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    return client;
  }

  @Bean
  ElasticsearchRestTemplate elasticsearchTemplate() {
    return new ElasticsearchRestTemplate(elasticsearchClient());
  }
}
