package org.lennardjones.investmanager.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

/**
 * This class is a Spring JDBC implementation of
 * {@link org.lennardjones.investmanager.repository.ProductRepository product repository}.
 *
 * @since 1.1
 * @author lennardjones
 */
@Repository
public class JdbcProductRepository implements ProductRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Double> getCurrentPriceByUserIdAndProductName(Long userId, String productName) {
        var sql = "SELECT current_price FROM product WHERE owner_id = ? AND name = ?";
        RowMapper<Double> rowMapper = (ResultSet rs, int rowNum) -> rs.getDouble("current_price");
        var currentPriceList = jdbcTemplate.query(sql, rowMapper, userId, productName);
        return currentPriceList.isEmpty() ? Optional.empty() : Optional.of(currentPriceList.get(0));
    }

    @Override
    public void save(Long userId, String productName, double currentPrice) {
        removeByUserIdAndProductName(userId, productName);
        var sql = "INSERT INTO product VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, userId, productName, currentPrice);
    }

    @Override
    public List<String> getAllNamesByUserId(Long userId) {
        var sql = "SELECT name FROM product WHERE owner_id = ?";
        RowMapper<String> rowMapper = (ResultSet rs, int rowNum) -> rs.getString("name");
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public void removeByUserIdAndProductName(Long userId, String productName) {
        var sql = "DELETE FROM product WHERE owner_id = ? AND name = ?";
        jdbcTemplate.update(sql, userId, productName);
    }
}
