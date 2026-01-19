package com.salescrm.salescrm.repository;

import com.salescrm.salescrm.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    Optional<Lead> findByPhone(String phone);

    Optional<Lead> findByEmail(String email);
}
