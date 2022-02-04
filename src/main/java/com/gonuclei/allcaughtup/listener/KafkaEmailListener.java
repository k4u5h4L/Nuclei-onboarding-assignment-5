package com.gonuclei.allcaughtup.listener;

import com.gonuclei.allcaughtup.constant.Constants;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

@Component
@Slf4j
public class KafkaEmailListener {

  @KafkaListener(topics = Constants.KAFKA_TOPIC_NAME, groupId = Constants.KAFKA_GROUP_NAME)
  void listener(String data) {
    log.info("Email listener service received: " + data);
  }

}
