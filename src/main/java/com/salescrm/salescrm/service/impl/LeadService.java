package com.salescrm.salescrm.service.impl;

import com.salescrm.salescrm.repository.LeadRepository;
import com.salescrm.salescrm.domain.Lead;
import com.salescrm.salescrm.domain.LeadStatus;
import com.salescrm.salescrm.exception.BusinessRuleViolationException;
import com.salescrm.salescrm.exception.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class LeadService {
    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }


    public Lead createLead(Lead lead) {

        // LEAD_NA_01 — at least one contact method required
        if ((lead.getPhone() == null || lead.getPhone().isBlank()) &&
                (lead.getEmail() == null || lead.getEmail().isBlank())) {

            throw new BusinessRuleViolationException(
                    "Lead must have at least one contact method (phone or email)"
            );
        }

        // LEAD_NA_02 — Duplicate prevention (phone/email uniqueness)

        // Check duplicate by phone
        if (lead.getPhone() != null && !lead.getPhone().isBlank()) {
            boolean phoneExists = leadRepository.findByPhone(lead.getPhone()).isPresent();
            if (phoneExists) {
                throw new BusinessRuleViolationException(
                        "Lead already exists with this phone or email"
                );
            }
        }

        // Check duplicate by email
        if (lead.getEmail() != null && !lead.getEmail().isBlank()) {
            boolean emailExists = leadRepository.findByEmail(lead.getEmail()).isPresent();
            if (emailExists) {
                throw new BusinessRuleViolationException(
                        "Lead already exists with this phone or email"
                );
            }
        }

        lead.setStatus(LeadStatus.NEW);

        // FINAL SAFETY NET — DB uniqueness handling
        try {
            return leadRepository.save(lead);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new BusinessRuleViolationException(
                    "Lead already exists with this phone or email"
            );
        }
    }



    public Lead getLeadById(Long leadId, String requester) {

        // Temporary in-memory lead (no DB yet)
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Lead not found"
                ));


        // AUTHZ_NA_01 — ownership check
        if (!lead.getOwner().equals(requester)) {
            throw new UnauthorizedAccessException(
                    "You do not own this lead"
            );
        }

        return lead;
    }

    public Lead updateLeadStatus(Long leadId, LeadStatus newStatus, String requester) {

        Lead lead = getLeadById(leadId, requester);

        // Terminal state immutability
        if (lead.getStatus() == LeadStatus.CONVERTED ||
                lead.getStatus() == LeadStatus.LOST) {

            throw new BusinessRuleViolationException(
                    "Cannot update status of a converted or lost lead"
            );
        }

        LeadStatus currentStatus = lead.getStatus();

        // Enforce lifecycle transitions
        switch (currentStatus) {

            case NEW:
                if (newStatus != LeadStatus.CONTACTED) {
                    throw new BusinessRuleViolationException("Invalid status transition");
                }
                break;

            case CONTACTED:
                if (newStatus != LeadStatus.QUALIFIED) {
                    throw new BusinessRuleViolationException("Invalid status transition");
                }
                break;

            case QUALIFIED:
                if (newStatus != LeadStatus.CONVERTED &&
                        newStatus != LeadStatus.LOST) {
                    throw new BusinessRuleViolationException("Invalid status transition");
                }
                break;

            default:
                throw new BusinessRuleViolationException("Invalid status transition");
        }


        lead.setStatus(newStatus);
        return leadRepository.save(lead);

    }
}
