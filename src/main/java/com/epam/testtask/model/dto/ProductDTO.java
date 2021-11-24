package com.epam.testtask.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@Getter
@Setter
@NoArgsConstructor
@ToString
@CsvRecord(separator = ",", crlf = "UNIX")
public class ProductDTO {

    @DataField(pos = 1, required = true)
    private String uniqueID;

    @DataField(pos = 2, required = true)
    private String productCode;

    @DataField(pos = 3, required = true)
    private String productName;

    @DataField(pos = 4, required = true)
    private Double priceWholesale;

    @DataField(pos = 5, required = true)
    private Double priceRetail;

    @DataField(pos = 6, required = true)
    private Integer inStock;
}
