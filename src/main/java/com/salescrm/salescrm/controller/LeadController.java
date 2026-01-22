package com.salescrm.salescrm.controller;

import com.salescrm.salescrm.domain.Lead;
import com.salescrm.salescrm.domain.LeadStatus;
import com.salescrm.salescrm.dto.CreateLeadRequest;
import com.salescrm.salescrm.dto.LeadResponse;
import com.salescrm.salescrm.service.impl.LeadService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    // =========================
    // CREATE LEAD
    // =========================
    @PostMapping
    public LeadResponse createLead(@RequestBody CreateLeadRequest request,
                                   @RequestParam String requester) {

        // 1️⃣ Controller builds domain object (Lead)
        Lead lead = new Lead();
        lead.setName(request.getName());
        lead.setPhone(request.getPhone());
        lead.setEmail(request.getEmail());

        // 2️⃣ Owner is derived from request context (NOT from body)
        lead.setOwner(requester);

        // 3️⃣ Service enforces business rules + persistence
        Lead savedLead = leadService.createLead(lead);

        // 4️⃣ Convert domain → response DTO
        return new LeadResponse(
                savedLead.getId(),
                savedLead.getName(),
                savedLead.getStatus(),
                savedLead.getOwner()
        );
    }

    // =========================
    // GET LEAD
    // =========================
    @GetMapping("/{id}")
    public LeadResponse getLead(@PathVariable Long id,
                                @RequestParam String requester) {

        // 1️⃣ Service enforces ownership + deleted check
        Lead lead = leadService.getLeadById(id, requester);

        // 2️⃣ Convert domain → response DTO
        return new LeadResponse(
                lead.getId(),
                lead.getName(),
                lead.getStatus(),
                lead.getOwner()
        );
    }

    // =========================
    // UPDATE LEAD STATUS
    // =========================
    @PutMapping("/{id}/status")
    public LeadResponse updateLeadStatus(@PathVariable Long id,
                                         @RequestParam String requester,
                                         @RequestParam LeadStatus status) {

        // 1️⃣ Service enforces lifecycle + immutability
        Lead updatedLead = leadService.updateLeadStatus(id, status, requester);

        // 2️⃣ Convert domain → response DTO
        return new LeadResponse(
                updatedLead.getId(),
                updatedLead.getName(),
                updatedLead.getStatus(),
                updatedLead.getOwner()
        );
    }

    // =========================
    // SOFT DELETE LEAD
    // =========================
    @DeleteMapping("/{id}")
    public void deleteLead(@PathVariable Long id,
                           @RequestParam String requester) {

        // 1️⃣ Service performs soft delete + rule checks
        leadService.deleteLead(id, requester);
    }
}
