package com.tenpo.challenge.repository;

import com.tenpo.challenge.model.ApiCallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiCallHistoryRepository extends JpaRepository<ApiCallHistory, Long> {
    Page<ApiCallHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);
}