package com.epam.testtask;

import com.epam.testtask.consumer.TestMessageConsumer;
import com.epam.testtask.model.dto.ProductDTO;
import com.epam.testtask.utils.csv.processing.MessageCounter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ComponentScan
@CamelSpringBootTest
public class CsvToActiveMqTest {

    public static CountDownLatch latch;

    @Value("${csv.file.uri}")
    private String fileUri;

    @Autowired
    CamelContext camelContext;

    private Path path;

    private Faker faker = new Faker();

    @BeforeEach
    public void prepare() throws Exception {
        path = Paths.get(fileUri + "test.csv");
        MessageCounter.resetCount();
    }




    @Test
    public void sendMessageCountTest() throws Exception {
        Integer messages = 50;
        for (int i = 1; i <= messages; i++) {
            String s = String.format("%s,%s,%s,%s,%s,%s\n", faker.internet().uuid(),
                    faker.idNumber().ssnValid(),
                    faker.app().name(),
                    faker.number().randomDouble(2, 10, 500),
                    faker.number().randomDouble(2, 10, 500),
                    faker.number().numberBetween(50, 1000));
            Files.write(path, s.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        latch = new CountDownLatch(messages);
        latch.await(4000, TimeUnit.SECONDS);

        assertEquals (MessageCounter.getCount(), messages.longValue());
    }

    @Test
    public void equalsMessagesTest() throws Exception {
        Path path = Paths.get(fileUri + "test.csv");
        String csvRow = String.format("%s,%s,%s,%s,%s,%s\n", faker.internet().uuid(),
                faker.idNumber().ssnValid(),
                faker.app().name(),
                faker.number().randomDouble(2, 10, 500),
                faker.number().randomDouble(2, 10, 500),
                faker.number().numberBetween(50, 1000));

        Files.write(path, csvRow.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);


        latch = new CountDownLatch(1);
        latch.await(40, TimeUnit.SECONDS);

        ObjectMapper objectMapper = new ObjectMapper();
        ProductDTO productFromConsumer = objectMapper.readValue(TestMessageConsumer.getLastStr(), ProductDTO.class);
        String productJsonToCsvFormat = String.format("%s,%s,%s,%s,%s,%s\n", productFromConsumer.getUniqueID(),
                productFromConsumer.getProductCode(),
                productFromConsumer.getProductName(),
                productFromConsumer.getPriceWholesale(),
                productFromConsumer.getPriceRetail(),
                productFromConsumer.getInStock());

        assertEquals(productJsonToCsvFormat, csvRow);
    }
}
