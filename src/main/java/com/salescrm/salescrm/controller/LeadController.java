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

    // 🔹 Create Lead
    @PostMapping
    public Lead createLead(@RequestBody Lead lead) {
        return leadService.createLead(lead);
    }

    // 🔹 Get Lead (ownership enforced)
    @GetMapping("/{id}")
    public Lead getLead(@PathVariable Long id,
                        @RequestParam String requester) {

        return leadService.getLeadById(id, requester);
    }

    // 🔹 Update Lead Status (lifecycle + ownership enforced)
    @PutMapping("/{id}/status")
    public Lead updateLeadStatus(@PathVariable Long id,
                                 @RequestParam String requester,
                                 @RequestParam LeadStatus status) {

        return leadService.updateLeadStatus(id, status, requester);
    }
}

