package org.lennardjones.investmanager.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.junit.jupiter.api.Assertions.*;

class JdbcProductRepositoryTests {
    EmbeddedDatabase embeddedDataSource;
    JdbcTemplate jdbcTemplate;
    JdbcProductRepository jdbcProductRepository;

    @BeforeEach
    void setup() {
        embeddedDataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .build();

        jdbcTemplate = new JdbcTemplate(embeddedDataSource);
        jdbcProductRepository = new JdbcProductRepository(jdbcTemplate);
    }

    @AfterEach
    void shutdown() {
        embeddedDataSource.shutdown();
    }

    @Test
    void getCurrentPriceByUserIdAndProductNameTest() {
        assertAll(
                () -> {
                    //given
                    var userId = 1L;
                    var productName = "product1";

                    //when
                    var optionalDouble = jdbcProductRepository
                            .getCurrentPriceByUserIdAndProductName(userId, productName);

                    //then
                    assertEquals(100, optionalDouble.orElseThrow());
                },
                () -> {
                    //given
                    var userId = 2L;
                    var productName = "product2";

                    //when
                    var optionalDouble = jdbcProductRepository
                            .getCurrentPriceByUserIdAndProductName(userId, productName);

                    //then
                    assertEquals(0, optionalDouble.orElseThrow());
                }
        );
    }

    @Test
    void saveTest() {
        assertAll(
                () -> {
                    //given
                    var userId = 1L;
                    var productName = "product3";
                    var currentPrice = 55;

                    //when
                    jdbcProductRepository.save(userId, productName, currentPrice);

                    //then
                    var names = jdbcProductRepository.getAllNamesByUserId(userId);
                    assertEquals("product1", names.get(0));
                    assertEquals("product2", names.get(1));
                    assertEquals(productName, names.get(2));
                    var realCurrentPrice = jdbcProductRepository
                            .getCurrentPriceByUserIdAndProductName(userId, productName)
                            .orElseThrow();
                    assertEquals(realCurrentPrice, realCurrentPrice);
                },
                () -> {
                    //given
                    var userId = 2L;
                    var productName = "product2";
                    var currentPrice = 99;

                    //when
                    jdbcProductRepository.save(userId, productName, currentPrice);

                    //then
                    var names = jdbcProductRepository.getAllNamesByUserId(userId);
                    assertEquals("product1", names.get(0));
                    assertEquals(productName, names.get(1));
                    var realCurrentPrice = jdbcProductRepository
                            .getCurrentPriceByUserIdAndProductName(userId, productName)
                            .orElseThrow();
                    assertEquals(currentPrice, realCurrentPrice);
                }
        );
    }

    @Test
    void getAllNamesByUserIdTest() {
        //given
        var userId = 2L;

        //when
        var names = jdbcProductRepository.getAllNamesByUserId(userId);

        //then
        assertEquals(2, names.size());
        assertEquals("product1", names.get(0));
        assertEquals("product2", names.get(1));
    }

    @Test
    void removeByUserIdAndProductNameTest() {
        //given
        var userId = 1L;
        var productName = "product2";

        //when
        jdbcProductRepository.removeByUserIdAndProductName(userId, productName);

        //then
        var names = jdbcProductRepository.getAllNamesByUserId(userId);
        assertEquals(1, names.size());
        assertEquals("product1", names.get(0));
    }
}
