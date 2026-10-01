package com.scentiva.modules.returns.repository;

import com.scentiva.modules.returns.model.ReturnRequest;
import com.scentiva.modules.returns.model.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnRequest, Long> {

    Optional<ReturnRequest> findByReturnNumberAndIsDeletedFalse(String returnNumber);

    @Query("SELECT r FROM ReturnRequest r LEFT JOIN FETCH r.items WHERE r.returnNumber = :returnNumber AND r.isDeleted = false")
    Optional<ReturnRequest> findByReturnNumberWithItems(@Param("returnNumber") String returnNumber);

    Page<ReturnRequest> findByCustomerIdAndIsDeletedFalse(Long customerId, Pageable pageable);

    Page<ReturnRequest> findByStatusAndIsDeletedFalse(ReturnStatus status, Pageable pageable);

    List<ReturnRequest> findByOrderIdAndIsDeletedFalse(Long orderId);

    Long countByStatusAndIsDeletedFalse(ReturnStatus status);
}
