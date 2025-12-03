package com.huerto.repository;

import com.huerto.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByUserId(Long userId);
    List<Sale> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.createdAt BETWEEN :start AND :end")
    BigDecimal sumTotalsBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.createdAt BETWEEN :start AND :end")
    Long countSalesBetween(LocalDateTime start, LocalDateTime end);
}

