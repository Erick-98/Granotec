package com.granotec.inventory_api.ov;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvRepository extends JpaRepository<Ov, Integer>, JpaSpecificationExecutor<Ov> {
    Page<Ov> findByCustomerId(Long customerId, Pageable pageable);
    long countByCustomerId(Long customerId);
    List<Ov> findByCustomerId(Long customerId);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Ov o WHERE o.customer.id = :customerId")
    BigDecimal sumTotalByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT MAX(o.fecha) FROM Ov o WHERE o.customer.id = :customerId")
    LocalDate maxFechaByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(o) FROM Ov o WHERE o.fecha BETWEEN :from AND :to")
    long countByFechaBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Ov o WHERE o.fecha BETWEEN :from AND :to")
    BigDecimal sumTotalByFechaBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT COUNT(o) FROM Ov o WHERE o.customer.id = :customerId AND o.fecha BETWEEN :from AND :to")
    long countByCustomerIdAndFechaBetween(@Param("customerId") Long customerId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT COALESCE(SUM(o.total),0) FROM Ov o WHERE o.customer.id = :customerId AND o.fecha BETWEEN :from AND :to")
    BigDecimal sumTotalByCustomerIdAndFechaBetween(@Param("customerId") Long customerId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    // --- Methods for Salesperson endpoints ---
    Page<Ov> findBySalespersonId(Integer salespersonId, Pageable pageable);

    long countBySalespersonId(Integer salespersonId);

    @Query("SELECT COALESCE(SUM(o.total),0) FROM Ov o WHERE o.salesperson.id = :salespersonId")
    BigDecimal sumTotalBySalespersonId(@Param("salespersonId") Integer salespersonId);

    @Query("SELECT MAX(o.fecha) FROM Ov o WHERE o.salesperson.id = :salespersonId")
    LocalDate maxFechaBySalespersonId(@Param("salespersonId") Integer salespersonId);
}
