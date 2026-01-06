package com.salescrm.salescrm.controller;

import com.salescrm.salescrm.domain.Lead;
import com.salescrm.salescrm.service.impl.LeadService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public Lead createLead(@RequestBody Lead lead) {
        return leadService.createLead(lead);
    }
}
