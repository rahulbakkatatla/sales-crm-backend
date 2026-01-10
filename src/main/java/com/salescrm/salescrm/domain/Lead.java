package com.salescrm.salescrm.domain;

public class Lead {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private LeadStatus status;
    private String owner;

    // ===== GETTERS =====
    public Long getId() {
        return id;
    }

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

    public String getOwner() {
        return owner;
    }

    // ===== SETTERS =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
