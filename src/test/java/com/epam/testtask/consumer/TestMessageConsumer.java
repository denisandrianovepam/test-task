package com.epam.testtask.consumer;


import com.epam.testtask.CsvToActiveMqTest;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Тестовый consumer для получения сообщения из EmbeddedActiveMq
 * Необходим исключительно для тестов
 */
@Component
public class TestMessageConsumer implements MessageListener {

    private static Logger log = LoggerFactory.getLogger(TestMessageConsumer.class);
    @Getter
    private static String lastStr;

    @Override
    @SneakyThrows

    public void onMessage(Message message) {
        // TODO: 24.11.2021 Добавить описание для других типов сообщений
        lastStr = ((ActiveMQTextMessage) message).getText();
        CsvToActiveMqTest.latch.countDown();
    }

}
