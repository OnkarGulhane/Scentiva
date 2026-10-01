package com.scentiva.modules.catalog.repository;

import com.scentiva.modules.catalog.model.OlfactoryPyramid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OlfactoryPyramidRepository extends JpaRepository<OlfactoryPyramid, Long> {

    Optional<OlfactoryPyramid> findByProductIdAndIsDeletedFalse(Long productId);
}
