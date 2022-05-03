package org.lennardjones.investmanager.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

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
        var optionalDouble = jdbcProductRepository
                .getCurrentPriceByUserIdAndProductName(1L, "product1");
        Assertions.assertEquals(100, optionalDouble.orElseThrow());

        optionalDouble = jdbcProductRepository
                .getCurrentPriceByUserIdAndProductName(2L, "product2");
        Assertions.assertEquals(0, optionalDouble.orElseThrow());
    }

    @Test
    void saveTest() {
        Assertions.assertAll(
                () -> {
                    jdbcProductRepository.save(1L, "product3", 55);
                    var names = jdbcProductRepository.getAllNamesByUserId(1L);
                    Assertions.assertEquals("product1", names.get(0));
                    Assertions.assertEquals("product2", names.get(1));
                    Assertions.assertEquals("product3", names.get(2));
                    var currentPrice = jdbcProductRepository
                            .getCurrentPriceByUserIdAndProductName(1L, "product3");
                    Assertions.assertEquals(55, currentPrice.orElseThrow());
                },
                () -> {
                    jdbcProductRepository.save(2L, "product2", 99);
                    var names = jdbcProductRepository.getAllNamesByUserId(2L);
                    Assertions.assertEquals("product1", names.get(0));
                    Assertions.assertEquals("product2", names.get(1));
                    var currentPrice = jdbcProductRepository
                            .getCurrentPriceByUserIdAndProductName(2L, "product2");
                    Assertions.assertEquals(99, currentPrice.orElseThrow());
                }
        );
    }

    @Test
    void getAllNamesByUserIdTest() {
        var names = jdbcProductRepository.getAllNamesByUserId(2L);
        Assertions.assertEquals(2, names.size());
        Assertions.assertEquals("product1", names.get(0));
        Assertions.assertEquals("product2", names.get(1));
    }

    @Test
    void removeByUserIdAndProductNameTest() {
        jdbcProductRepository.removeByUserIdAndProductName(1L, "product2");

        var names = jdbcProductRepository.getAllNamesByUserId(1L);
        Assertions.assertEquals(1, names.size());
        Assertions.assertEquals("product1", names.get(0));
    }
}
