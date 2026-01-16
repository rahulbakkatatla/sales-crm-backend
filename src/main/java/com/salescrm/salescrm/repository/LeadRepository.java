package com.salescrm.salescrm.repository;

import com.salescrm.salescrm.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, Long> {
}


