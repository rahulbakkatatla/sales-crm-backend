package com.salescrm.salescrm.service.impl;

import com.salescrm.salescrm.domain.Lead;
import com.salescrm.salescrm.domain.LeadStatus;
import com.salescrm.salescrm.exception.BusinessRuleViolationException;
import com.salescrm.salescrm.exception.UnauthorizedAccessException;
import com.salescrm.salescrm.repository.LeadRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    // ================= CREATE LEAD =================

    public Lead createLead(Lead lead) {

        // LEAD_NA_01 — at least one contact method required
        if ((lead.getPhone() == null || lead.getPhone().isBlank()) &&
                (lead.getEmail() == null || lead.getEmail().isBlank())) {

            throw new BusinessRuleViolationException(
                    "Lead must have at least one contact method (phone or email)"
            );
        }

        // LEAD_NA_02 — owner is mandatory
        if (lead.getOwner() == null || lead.getOwner().isBlank()) {
            throw new BusinessRuleViolationException(
                    "Lead owner is required"
            );
        }

        // LEAD_NA_03 — duplicate prevention (service level)

        if (lead.getPhone() != null && !lead.getPhone().isBlank()) {
            if (leadRepository.findByPhone(lead.getPhone()).isPresent()) {
                throw new BusinessRuleViolationException(
                        "Lead already exists with this phone or email"
                );
            }
        }

        if (lead.getEmail() != null && !lead.getEmail().isBlank()) {
            if (leadRepository.findByEmail(lead.getEmail()).isPresent()) {
                throw new BusinessRuleViolationException(
                        "Lead already exists with this phone or email"
                );
            }
        }

        // Initial lifecycle state
        lead.setStatus(LeadStatus.NEW);

        // FINAL SAFETY NET — DB-level uniqueness (race-condition safe)
        try {
            return leadRepository.save(lead);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleViolationException(
                    "Lead already exists with this phone or email"
            );
        }
    }

    // ================= GET LEAD =================

    public Lead getLeadById(Long leadId, String requester) {

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Lead not found"
                ));

        // AUTHZ_NA_01 — ownership enforcement
        if (!lead.getOwner().equals(requester)) {
            throw new UnauthorizedAccessException(
                    "You do not own this lead"
            );
        }

        return lead;
    }

    // ================= UPDATE STATUS =================

    public Lead updateLeadStatus(Long leadId, LeadStatus newStatus, String requester) {

        Lead lead = getLeadById(leadId, requester);

        // LEAD_NA_04 — terminal state immutability
        if (lead.getStatus() == LeadStatus.CONVERTED ||
                lead.getStatus() == LeadStatus.LOST) {

            throw new BusinessRuleViolationException(
                    "Cannot update status of a converted or lost lead"
            );
        }

        LeadStatus currentStatus = lead.getStatus();

        // LEAD_NA_05 — lifecycle transition rules
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

    public void deleteLead(Long leadId, String requester) {

        Lead lead = getLeadById(leadId, requester);

        // LEAD_NA_06 — terminal leads cannot be deleted
        if (lead.getStatus() == LeadStatus.CONVERTED ||
                lead.getStatus() == LeadStatus.LOST) {

            throw new BusinessRuleViolationException(
                    "Cannot delete a converted or lost lead"
            );
        }

        lead.setDeleted(true);
        leadRepository.save(lead);
    }

}
