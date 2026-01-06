package com.salescrm.salescrm.domain;

public class Lead {
    private String name;
    private String phone;
    private String email;
    private LeadStatus status;

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }
}
