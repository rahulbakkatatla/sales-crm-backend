package com.salescrm.salescrm.service.impl;

import com.salescrm.salescrm.domain.Lead;
import com.salescrm.salescrm.domain.LeadStatus;
import org.springframework.stereotype.Service;

@Service
public class LeadService {

    public Lead createLead(Lead lead) {
        if ((lead.getPhone() == null || lead.getPhone().isBlank()) &&
                (lead.getEmail() == null || lead.getEmail().isBlank())) {

            throw new IllegalArgumentException(
                    "Lead must have at least one contact method (phone or email)"
            );
        }
        lead.setStatus(LeadStatus.NEW);
        return lead;
    }

    public Lead getLeadById(Long leadId, String requester) {
        // fake lead for now
        Lead lead = new Lead();
        lead.setOwner("rahul");

        if (!lead.getOwner().equals(requester)) {
            throw new IllegalArgumentException("Access denied");
        }

        return lead;
    }
}

