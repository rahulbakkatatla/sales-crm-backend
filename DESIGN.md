# SalesCRM - System Design Notes

## System Overview

SalesCRM is a backend system used by sales teams to manage leads,
track activities, and monitor performance while enforcing role-based access
and data integrity.

## Actors & Hierarchy

The system supports the following roles:

- Admin
- Manager
- Salesperson

Hierarchy:
Admin → Manager → Salesperson

## Responsibilities by Role

### Admin
- Manages users and roles
- Has visibility into all data
- Configures system-level settings

### Manager
- Manages a team of salespersons
- Assigns leads
- Reviews team performance

### Salesperson
- Works on assigned leads
- Logs sales activities
- Updates lead status

## Lead Rules

### Rule ID: LEAD_NA_01
Area: Lead Creation  
Scenario: Creating a new lead  
Triggered by: Salesperson / Manager  

Rule Type: NEVER ALLOW  

Rule Statement:
-The System should not allow creation of a lead unless atleast one valid contact method is provided. Contact methods - Phone Number, Email Address, Office Address.

Why this rule exists:
- Sales team without having a contact cannot act on a lead.
- Leads without a minimum contact method leads to waste sales effort.

What could break if this rule is missing:
- Fake or incomplete leads get created  
- Salespeople waste time on unusable leads  
- Reports show inflated lead counts with no real value  
- Trust in the CRM data decreases

### Rule ID: LEAD_NA_02
Area: Lead Status Management
Scenario: Updating lead status
Triggered by: Salesperson

Rule Type: NEVER ALLOW

Rule Statement:
- The system should not allow a lead to skip sales stages directly (for example, NEW → CONVERTED).

Why this rule exists:
- Real sales follows a step-by-step process.
- Skipping stages hides actual sales activity and effort.

What could break if this rule is missing:
- Fake or premature conversions
- Managers lose visibility into true sales progress
- Performance reports become misleading
- Sales discipline is lost across the team

### Rule ID: LEAD_NA_03

Area: Lead Ownership
Scenario: Changing lead owner
Triggered by: Salesperson

Rule Type: NEVER ALLOW

Rule Statement:
- A salesperson should not be allowed to change the ownership of a lead.

Why this rule exists:
- Lead ownership reflects responsibility assigned by management.
- Allowing self-assignment leads to misuse and conflicts.

What could break if this rule is missing:
- Salespeople may steal high-quality leads
- Managers lose control over lead distribution
- Accountability for follow-ups becomes unclear
- Internal disputes within sales teams increase

🟨 ADDITIONAL LEAD RULES 

NEVER ALLOW: Manual setting of initial lead status during creation.

NEVER ALLOW: Updating a lead once it is marked as CONVERTED or LOST.

NEVER ALLOW: Creation of duplicate leads with the same primary contact identifier.

NEVER ALLOW: Deleting a lead permanently from the system.
