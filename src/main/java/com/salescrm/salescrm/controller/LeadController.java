package com.salescrm.salescrm.controller;

import com.salescrm.salescrm.domain.Lead;
import com.salescrm.salescrm.domain.LeadStatus;
import com.salescrm.salescrm.service.impl.LeadService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    // ✅ 1. CREATE LEAD
    @PostMapping
    public Lead createLead(@RequestBody Lead lead) {
        return leadService.createLead(lead);
    }

    // ✅ 2. GET LEAD (ownership check)
    @GetMapping("/{id}")
    public Lead getLead(
            @PathVariable Long id,
            @RequestParam String requester
    ) {
        return leadService.getLeadById(id, requester);
    }

    // ✅ 3. UPDATE LEAD STATUS (DAY 4)
    @PutMapping("/{id}/status")
    public Lead updateLeadStatus(
            @PathVariable Long id,
            @RequestParam LeadStatus status,
            @RequestParam String requester
    ) {
        return leadService.updateLeadStatus(id, status, requester);
    }
}

