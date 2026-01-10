package com.salescrm.salescrm.dto;

import com.salescrm.salescrm.domain.LeadStatus;

public class LeadResponse {

    private Long id;
    private String name;
    private LeadStatus status;
    private String owner;

    public LeadResponse(Long id, String name, LeadStatus status, String owner) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public String getOwner() {
        return owner;
    }
}
