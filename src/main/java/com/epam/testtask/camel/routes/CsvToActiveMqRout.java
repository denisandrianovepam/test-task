package com.epam.testtask.camel.routes;

import com.epam.testtask.model.dto.ProductDTO;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;


@Component
public class CsvToActiveMqRout extends RouteBuilder {

    DataFormat productDataFormat = new BindyCsvDataFormat(ProductDTO.class);


    @Override
    public void configure() throws Exception {
        from("file:{{csv.file.uri}}")
                .split(body().tokenize("\n"))
                .streaming()
                .routeId("loadCsv")
                .to("seda:transform");

        // seda используется для паралельного многопоточного преобразования форматов и отправки в очередь
        from("seda:transform?concurrentConsumers={{csv.activemq.concurrent-threads}}")
                .routeId("transformToJsonAndSendToActiveMq")
                .unmarshal(productDataFormat)
                .split(body()).streaming()
                .marshal().json()
                .to("activemq:{{jms.queue.name}}?jmsMessageType={{csv.activemq.message-type}}")
                .bean("messageCounter", "increment");


    }
}
