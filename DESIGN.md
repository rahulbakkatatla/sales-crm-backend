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


## User & Role Rules

This section defines strict rules governing user creation, role assignment,
deactivation, and misuse prevention. These rules ensure that authority,
access, and responsibility are clearly controlled across the system.

---

### Rule ID: USER_NA_01
Area: User Creation  
Scenario: Creating a new user  
Triggered by: Salesperson  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow a Salesperson to create new users.

Why this rule exists:
- Salespersons are end users, not system administrators.
- Allowing them to create users introduces security and misuse risks.

What could break if this rule is missing:
- Unauthorized users gain system access  
- Fake or duplicate accounts are created  
- Loss of control over system entry points  
- Increased security vulnerabilities  

---

### Rule ID: USER_NA_02
Area: User Creation  
Scenario: Creating a new user  
Triggered by: Manager  

Rule Type: NEVER ALLOW  

Rule Statement:
- A Manager should not be allowed to create Admin users.

Why this rule exists:
- Admin roles have full system control.
- Admin creation must remain a tightly controlled action.

What could break if this rule is missing:
- Privilege escalation  
- Abuse of system-wide access  
- Loss of trust in role hierarchy  
- Irreversible security damage  

---

### Rule ID: USER_NA_03
Area: Role Assignment  
Scenario: Assigning or modifying roles  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- A user should not be allowed to assign or modify their own role.

Why this rule exists:
- Self-promotion breaks authority boundaries.
- Role changes must be externally controlled.

What could break if this rule is missing:
- Users escalate privileges silently  
- Salespersons become Managers or Admins  
- Audit trails lose meaning  
- System authority collapses  

---

### Rule ID: USER_NA_04
Area: Role Assignment  
Scenario: Assigning roles  
Triggered by: Manager  

Rule Type: NEVER ALLOW  

Rule Statement:
- A Manager should not be allowed to assign or modify Admin roles.

Why this rule exists:
- Managers operate within a limited organizational scope.
- Admin privileges exceed managerial authority.

What could break if this rule is missing:
- Chain-of-command violations  
- Confusion in responsibility ownership  
- Increased insider threats  

---

### Rule ID: USER_NA_05
Area: User Deactivation  
Scenario: Deactivating a user  
Triggered by: Salesperson  

Rule Type: NEVER ALLOW  

Rule Statement:
- A Salesperson should not be allowed to deactivate any user, including themselves.

Why this rule exists:
- Deactivation affects system continuity and reporting.
- Such actions require administrative oversight.

What could break if this rule is missing:
- Salespeople disable accounts to avoid accountability  
- Team data becomes incomplete  
- Historical activity tracking breaks  

---

### Rule ID: USER_NA_06
Area: User Deactivation  
Scenario: Deactivating a user with owned data  
Triggered by: Admin / Manager  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow deactivation of a user without reassigning their owned leads and responsibilities.

Why this rule exists:
- Business data must always have an owner.
- Orphaned leads result in missed follow-ups and revenue loss.

What could break if this rule is missing:
- Leads become unmanaged  
- Sales activities stop unexpectedly  
- Reporting accuracy degrades  
- Revenue opportunities are lost  

---

### Rule ID: USER_NA_07
Area: Role Misuse Prevention  
Scenario: Accessing data beyond assigned scope  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- Users should not be allowed to access data outside their permitted role scope.

Why this rule exists:
- Each role has a defined responsibility boundary.
- Access must match operational needs.

What could break if this rule is missing:
- Salespersons view other teams’ leads  
- Managers access unrelated departments  
- Data privacy violations occur  
- Internal trust is damaged  

---

### Rule ID: USER_NA_08
Area: Role Misuse Prevention  
Scenario: Performing admin-level actions  
Triggered by: Non-Admin users  

Rule Type: NEVER ALLOW  

Rule Statement:
- Non-Admin users should not be allowed to perform system-level configuration or administrative actions.

Why this rule exists:
- System configuration impacts all users.
- Only Admins have the authority and accountability.

What could break if this rule is missing:
- System instability  
- Misconfigured permissions  
- Untraceable system changes  

---

### ADDITIONAL USER & ROLE RULES

NEVER ALLOW: Creation of users without an explicitly assigned role.

NEVER ALLOW: Changing a user’s role after deactivation.

NEVER ALLOW: Permanent deletion of user accounts from the system.

NEVER ALLOW: Assignment of multiple roles to a single user.

NEVER ALLOW: Reactivation of a user without administrative approval.



## Authentication Rules

This section defines how users prove their identity to the system.
Authentication rules ensure that only valid, verified users can access
the system and that identity-related actions are secure, predictable,
and abuse-resistant.

Authentication answers one question only:
"Who are you?"

Authorization (what you can do) is handled separately.

---

### Rule ID: AUTH_NA_01
Area: System Access  
Scenario: Accessing protected system resources  
Triggered by: Any request  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow access to any protected resource without successful authentication.

Why this rule exists:
- All system actions must be traceable to a verified user.
- Anonymous access leads to security and data integrity risks.

What could break if this rule is missing:
- Unauthorized data access  
- Actions without accountability  
- Data leaks and misuse  
- Inability to audit user actions  

---

### Rule ID: AUTH_NA_02
Area: Login  
Scenario: Attempting login with invalid credentials  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow login with invalid or incorrect credentials.

Why this rule exists:
- Authentication relies on trust in identity verification.
- Accepting invalid credentials breaks system security.

What could break if this rule is missing:
- Unauthorized users gain access  
- Brute-force or guessing attacks succeed  
- Loss of trust in authentication system  

---

### Rule ID: AUTH_NA_03
Area: Account Status  
Scenario: Login attempt by deactivated user  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow deactivated users to authenticate.

Why this rule exists:
- Deactivation represents intentional removal of access.
- Allowing login defeats administrative control.

What could break if this rule is missing:
- Former employees regain access  
- Compliance violations  
- Security incidents involving inactive users  

---

### Rule ID: AUTH_NA_04
Area: Session / Token Validity  
Scenario: Accessing the system with an expired session or token  
Triggered by: Any request  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not accept expired authentication sessions or tokens.

Why this rule exists:
- Authentication must be time-bound.
- Long-lived sessions increase security risk.

What could break if this rule is missing:
- Stolen tokens remain usable  
- Users stay logged in indefinitely  
- Increased risk of unauthorized access  

---

### Rule ID: AUTH_NA_05
Area: Logout  
Scenario: Performing actions after logout  
Triggered by: Any request  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow authenticated actions after a user has logged out.

Why this rule exists:
- Logout represents explicit termination of access.
- Continued access violates user intent and security expectations.

What could break if this rule is missing:
- Users unknowingly perform actions  
- Shared devices become security risks  
- Session misuse occurs  

---

### Rule ID: AUTH_NA_06
Area: Multiple Concurrent Sessions  
Scenario: Logging in from multiple devices or locations  
Triggered by: Any user  

Rule Type: NEVER ALLOW (Policy-based)  

Rule Statement:
- The system should not allow unrestricted concurrent active sessions without explicit policy support.

Why this rule exists:
- Concurrent sessions increase attack surface.
- Session control improves security and traceability.

What could break if this rule is missing:
- Session hijacking becomes harder to detect  
- Users lose visibility into active logins  
- Security incidents go unnoticed  

---

### Rule ID: AUTH_NA_07
Area: Authentication Attempts  
Scenario: Repeated failed login attempts  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow unlimited failed authentication attempts.

Why this rule exists:
- Prevents brute-force attacks.
- Protects user accounts from compromise.

What could break if this rule is missing:
- Password guessing attacks succeed  
- Accounts are compromised silently  
- System security reputation degrades  

---

### ADDITIONAL AUTHENTICATION RULES

NEVER ALLOW: Authentication without a verified user identity.

NEVER ALLOW: Use of authentication tokens across different users.

NEVER ALLOW: Silent re-authentication without user awareness.

NEVER ALLOW: Access to authentication-sensitive endpoints over insecure channels.

NEVER ALLOW: Reuse of invalidated or revoked authentication credentials.


## Authorization Rules

Authorization defines what an authenticated user is allowed to do
and what data they are permitted to access.

Authorization answers one question only:
"What are you allowed to do?"

Authorization rules are strictly role-based and ownership-aware.

---

### Rule ID: AUTHZ_NA_01
Area: Lead Access  
Scenario: Viewing leads  
Triggered by: Salesperson  

Rule Type: NEVER ALLOW  

Rule Statement:
- A Salesperson should not be allowed to view leads that are not assigned to them.

Why this rule exists:
- Sales data is sensitive and ownership-based.
- Salespersons should only access leads they are responsible for.

What could break if this rule is missing:
- Exposure of other salespersons’ leads  
- Internal competition and misuse  
- Data privacy violations  
- Loss of trust within sales teams  

---

### Rule ID: AUTHZ_NA_02
Area: Lead Modification  
Scenario: Updating lead details  
Triggered by: Salesperson  

Rule Type: NEVER ALLOW  

Rule Statement:
- A Salesperson should not be allowed to modify leads not owned by them.

Why this rule exists:
- Ownership implies responsibility.
- Unauthorized edits corrupt data reliability.

What could break if this rule is missing:
- Leads modified without accountability  
- Conflicting sales actions  
- Reporting inaccuracies  

---

### Rule ID: AUTHZ_NA_03
Area: Manager Scope  
Scenario: Accessing leads  
Triggered by: Manager  

Rule Type: NEVER ALLOW  

Rule Statement:
- A Manager should not be allowed to access leads outside their assigned team.

Why this rule exists:
- Managers operate within a defined organizational boundary.
- Cross-team access violates data isolation.

What could break if this rule is missing:
- Unauthorized visibility across teams  
- Data leakage between departments  
- Organizational boundary violations  

---

### Rule ID: AUTHZ_NA_04
Area: Administrative Privileges  
Scenario: Performing system-level actions  
Triggered by: Non-Admin user  

Rule Type: NEVER ALLOW  

Rule Statement:
- Non-Admin users should not be allowed to perform system-level administrative actions.

Why this rule exists:
- System-level changes affect all users.
- Only Admins have the authority and accountability.

What could break if this rule is missing:
- System misconfiguration  
- Unauthorized privilege usage  
- Untraceable system changes  

---

### Rule ID: AUTHZ_NA_05
Area: Report Access  
Scenario: Viewing performance reports  
Triggered by: Salesperson  

Rule Type: NEVER ALLOW  

Rule Statement:
- A Salesperson should not be allowed to view aggregated team or organization-wide performance reports.

Why this rule exists:
- Reports contain sensitive performance data.
- Visibility must align with responsibility.

What could break if this rule is missing:
- Internal performance conflicts  
- Misinterpretation of metrics  
- Data misuse  

---

### ADDITIONAL AUTHORIZATION RULES

NEVER ALLOW: Access to any resource without explicit permission checks.

NEVER ALLOW: Role-based access decisions in controllers.

NEVER ALLOW: Bypassing authorization using indirect references (IDs).

NEVER ALLOW: Authorization logic duplication across layers.


## Activity Rules

Activities represent sales actions such as calls, meetings,
and follow-ups performed against leads.

Activities exist only in the context of a lead.

---

### Rule ID: ACT_NA_01
Area: Activity Creation  
Scenario: Creating an activity  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow creation of an activity without an associated lead.

Why this rule exists:
- Activities have no meaning without a lead.
- Ensures traceable sales actions.

What could break if this rule is missing:
- Orphan activities  
- Broken sales history  
- Invalid reporting  

---

### Rule ID: ACT_NA_02
Area: Activity Ownership  
Scenario: Creating or editing activities  
Triggered by: Salesperson  

Rule Type: NEVER ALLOW  

Rule Statement:
- A Salesperson should not be allowed to create or edit activities for leads they do not own.

Why this rule exists:
- Activities represent work done by the owner.
- Prevents false or misleading activity logs.

What could break if this rule is missing:
- Fake activity entries  
- Inflated performance metrics  
- Accountability loss  

---

### Rule ID: ACT_NA_03
Area: Activity Modification  
Scenario: Editing completed activities  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- Completed activities should not be editable.

Why this rule exists:
- Activities are historical records.
- Editing breaks audit integrity.

What could break if this rule is missing:
- Manipulated sales history  
- Invalid performance evaluation  
- Audit trail corruption  

---

### Rule ID: ACT_NA_04
Area: Activity Deletion  
Scenario: Deleting activities  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- Activities should not be permanently deleted from the system.

Why this rule exists:
- Activities provide historical accountability.
- Deletion causes loss of sales traceability.

What could break if this rule is missing:
- Incomplete sales timelines  
- Disputed sales actions  
- Loss of audit confidence  

---

### Rule ID: ACT_NA_05
Area: Activity Timing  
Scenario: Logging future-dated activities  
Triggered by: Salesperson  

Rule Type: NEVER ALLOW  

Rule Statement:
- Activities should not be logged for future dates unless explicitly marked as planned follow-ups.

Why this rule exists:
- Prevents false activity inflation.
- Maintains accurate timelines.

What could break if this rule is missing:
- Fake productivity signals  
- Misleading activity counts  

---

### ADDITIONAL ACTIVITY RULES

NEVER ALLOW: Activities on leads marked as LOST.

NEVER ALLOW: Activities on leads not yet assigned.

NEVER ALLOW: Editing activity ownership after creation.



## Customer & Data Integrity Rules

Customers represent successfully converted leads.
Data integrity rules ensure consistency, traceability,
and correctness across the system lifecycle.

---

### Rule ID: CUST_NA_01
Area: Lead Conversion  
Scenario: Converting a lead to a customer  
Triggered by: Salesperson  

Rule Type: NEVER ALLOW  

Rule Statement:
- A lead should not be converted to a customer unless it is in an eligible terminal sales stage.

Why this rule exists:
- Conversion reflects successful sales completion.
- Prevents premature customer creation.

What could break if this rule is missing:
- Fake customer records  
- Inaccurate revenue tracking  
- Misleading business metrics  

---

### Rule ID: CUST_NA_02
Area: Customer Creation  
Scenario: Creating customer records  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- Customers should not be created directly without originating from a lead.

Why this rule exists:
- Ensures complete sales traceability.
- Maintains lead-to-customer lineage.

What could break if this rule is missing:
- Orphan customer records  
- Loss of sales history  
- Broken analytics  

---

### Rule ID: CUST_NA_03
Area: Customer Modification  
Scenario: Editing customer data  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- Critical customer identity fields should not be modified after creation.

Why this rule exists:
- Customers represent finalized business entities.
- Identity consistency is mandatory.

What could break if this rule is missing:
- Duplicate customers  
- Broken integrations  
- Reporting inconsistencies  

---

### Rule ID: CUST_NA_04
Area: Data Deletion  
Scenario: Deleting customers or leads  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- Customers and leads should not be permanently deleted from the system.

Why this rule exists:
- Business systems must retain historical records.
- Deletion causes irreversible data loss.

What could break if this rule is missing:
- Compliance violations  
- Audit failures  
- Data trust erosion  

---

### Rule ID: CUST_NA_05
Area: Referential Integrity  
Scenario: Removing linked data  
Triggered by: Any user  

Rule Type: NEVER ALLOW  

Rule Statement:
- The system should not allow orphaned references between leads, activities, and customers.

Why this rule exists:
- Data relationships must remain consistent.
- Orphan records indicate system corruption.

What could break if this rule is missing:
- Broken queries  
- Invalid reports  
- System instability  

---

### ADDITIONAL DATA INTEGRITY RULES

NEVER ALLOW: Duplicate customers from the same primary lead.

NEVER ALLOW: Conversion rollback after customer creation.

NEVER ALLOW: Data modification that breaks historical consistency.



