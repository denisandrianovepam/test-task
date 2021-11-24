package com.epam.testtask.utils.csv.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Используется для подчета отправленных строк и вывода в консоль
 */
@Component("messageCounter")
public class MessageCounter {

    private static AtomicLong count = new AtomicLong();

    private Logger logger = LoggerFactory.getLogger(MessageCounter.class);

    public void increment() {
        logger.info(String.format("В очередь отправлено: %s сообщений", count.incrementAndGet()));
    }

    public static Long getCount() {
        return count.get();
    }

    public static void resetCount(){count.set(0);}

}
