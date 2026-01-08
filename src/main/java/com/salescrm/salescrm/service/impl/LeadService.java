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
        Lead lead = new Lead();
        lead.setOwner("rahul");
        lead.setStatus(LeadStatus.NEW); // ⭐ ADD THIS

        if (!lead.getOwner().equals(requester)) {
            throw new IllegalArgumentException("Access denied");
        }

        return lead;
    }


    public Lead updateLeadStatus(Long leadId, LeadStatus newStatus, String requester){
        Lead lead = getLeadById(leadId, requester);

        if (lead.getStatus() == LeadStatus.CONVERTED ||
                lead.getStatus() == LeadStatus.LOST) {

            throw new IllegalArgumentException(
                    "Cannot update status of a converted or lost lead"
            );
        }

        LeadStatus currentStatus = lead.getStatus();

        switch (currentStatus) {

            case NEW:
                if (newStatus != LeadStatus.CONTACTED) {
                    throw new IllegalArgumentException("Invalid status transition");
                }
                break;

            case CONTACTED:
                if (newStatus != LeadStatus.QUALIFIED) {
                    throw new IllegalArgumentException("Invalid status transition");
                }
                break;

            case QUALIFIED:
                if (newStatus != LeadStatus.CONVERTED &&
                        newStatus != LeadStatus.LOST) {
                    throw new IllegalArgumentException("Invalid status transition");
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid status transition");
        }

        lead.setStatus(newStatus);
        return lead;



    }

}


