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

NEVER ALLOW: Permanent deletion of a lead (handled by data integrity rules).



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

Authorization checks are enforced only after successful authentication.

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

Managers may modify leads only within their assigned team scope.



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

NEVER ALLOW: Activities on leads marked as CONVERTED.



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


---

## Design Freeze Notice

All business rules, authorization constraints, and data integrity guarantees
defined in this document are considered frozen as of the completion of Week 1.

All future low-level design, implementation, database modeling, and API design
must strictly adhere to these rules.

Any change to system behavior or permissions requires an explicit update to
this document before implementation.



## Low Level Design — Core Domain Entities & Responsibilities

This section identifies the core domain entities of SalesCRM and clearly
defines their responsibilities and boundaries.

The goal is to ensure that each entity has a single, well-defined purpose
and that no entity assumes responsibilities outside its scope.

---

### User

Represents a human actor interacting with the system.

Responsibilities:
- Holds identity and authentication credentials
- Has exactly one assigned role (Admin, Manager, or Salesperson)
- Owns an operational scope (self, team, or system-wide)
- Acts as the initiator of system actions

Explicitly NOT responsible for:
- Enforcing business rules
- Managing permissions of other users
- Directly modifying system configuration (unless Admin)

Notes:
- User lifecycle is controlled (ACTIVE → DEACTIVATED)
- Deactivated users retain historical ownership but lose access

---

### Role

Represents the authority level assigned to a user.

Responsibilities:
- Defines what actions a user is permitted to perform
- Determines data visibility scope
- Acts as the basis for authorization decisions

Explicitly NOT responsible for:
- User identity
- Business logic enforcement
- Direct data manipulation

Notes:
- Each user has exactly one role
- Roles are hierarchical and non-overlapping
- Role changes are strictly controlled by system rules

---

### Lead

Represents a potential customer in the sales pipeline.

Responsibilities:
- Stores contact and qualification information
- Maintains current sales status
- Acts as the central unit of sales work
- Owns associated sales activities

Explicitly NOT responsible for:
- Managing user permissions
- Storing customer-level data after conversion
- Enforcing authorization decisions

Notes:
- Lead lifecycle follows a controlled workflow
- Leads are immutable after terminal states (CONVERTED / LOST)
- Leads always have an owner

---

### Activity

Represents a sales action performed against a lead.

Responsibilities:
- Records calls, meetings, and follow-ups
- Captures timestamped sales effort
- Maintains historical traceability

Explicitly NOT responsible for:
- Existing independently of a lead
- Modifying lead ownership or status
- Enforcing business or permission rules

Notes:
- Activities are immutable once completed
- Activities cannot exist without a parent lead
- Activities reflect factual sales history

---

### Customer

Represents a successfully converted lead.

Responsibilities:
- Stores finalized customer identity
- Acts as a terminal business entity
- Serves as a reference for post-sale processes

Explicitly NOT responsible for:
- Managing lead-stage logic
- Accepting direct creation outside lead conversion
- Reverting back to lead state

Notes:
- Customers are created only via lead conversion
- Customer identity fields are stable
- Customers are never permanently deleted

---

## Entity Responsibility Guarantees

- Each entity has a single, clearly defined purpose
- No entity enforces business rules directly
- All rule enforcement occurs outside entities
- Entity boundaries prevent responsibility overlap

This separation ensures maintainability, clarity,
and predictable system behavior.


## Low Level Design — Entity Relationships & Lifecycles

This section defines how core domain entities in SalesCRM relate to each other
and how they evolve over time.

The goal is to ensure:
- Clear ownership
- Predictable state transitions
- No orphaned or ambiguous data states

---

## Entity Relationships

### User → Lead
Relationship:
- One User can own multiple Leads
- Each Lead has exactly one owning User at any time

Purpose:
- Ownership establishes accountability
- All sales actions are traceable to a responsible user

Constraints:
- Lead ownership changes are controlled by role rules
- Leads must always have an owner

---

### Lead → Activity
Relationship:
- One Lead can have multiple Activities
- Each Activity belongs to exactly one Lead

Purpose:
- Activities represent sales effort against a lead
- Ensures complete sales history per lead

Constraints:
- Activities cannot exist without a parent Lead
- Activity ownership is implicitly derived from Lead ownership

---

### Lead → Customer
Relationship:
- One Lead can convert into at most one Customer
- Each Customer originates from exactly one Lead

Purpose:
- Maintains full sales traceability
- Preserves lead-to-customer lineage

Constraints:
- Direct Customer creation is not allowed
- Conversion is a terminal, one-way operation

---

### User → Activity (Indirect)
Relationship:
- A User performs Activities via owned Leads

Purpose:
- Activities reflect user effort without duplicating ownership
- Avoids conflicting ownership models

Constraints:
- A User cannot perform activities on unowned Leads
- Activity records remain even after user deactivation

---

## Entity Lifecycles

### User Lifecycle
States:
- ACTIVE
- DEACTIVATED

Lifecycle Flow:
ACTIVE → DEACTIVATED

Rules:
- Deactivated users cannot authenticate or perform actions
- Historical ownership and activity records remain intact
- Deactivation requires reassignment of owned Leads

---

### Lead Lifecycle
States (conceptual):
- NEW
- IN_PROGRESS
- QUALIFIED
- CONVERTED (terminal)
- LOST (terminal)

Lifecycle Flow:
NEW → IN_PROGRESS → QUALIFIED → CONVERTED  
NEW → IN_PROGRESS → LOST

Rules:
- Stage skipping is not allowed
- Terminal states are immutable
- Activities are allowed only in non-terminal states

---

### Activity Lifecycle
States:
- CREATED
- COMPLETED (terminal)

Lifecycle Flow:
CREATED → COMPLETED

Rules:
- Completed activities are immutable
- Activities cannot be deleted
- Future-dated activities must be explicitly marked as follow-ups

---

### Customer Lifecycle
States:
- CREATED (terminal)

Lifecycle Flow:
CREATED

Rules:
- Customers are created only via Lead conversion
- Customer identity fields are stable
- Customers cannot revert to Leads

---

## Lifecycle Integrity Guarantees

- No entity exists without a valid parent relationship
- All terminal states are immutable
- State transitions are explicit and controlled
- Historical data is preserved across all lifecycles

This ensures that system behavior remains predictable,
auditable, and aligned with real-world sales processes.



## Low Level Design — Layered Architecture & Responsibility Boundaries

This section defines the internal structure of SalesCRM using a layered
architecture. Each layer has a clearly defined responsibility and strict
boundaries to prevent logic leakage and maintain system correctness.

The design ensures that business rules defined in Week 1 are enforced
consistently and centrally.

---

## Architectural Layers

SalesCRM follows a layered backend architecture consisting of:

1. API / Controller Layer
2. Service Layer
3. Repository / Data Access Layer

Each layer serves a specific purpose and must not assume responsibilities
belonging to another layer.

---

## API / Controller Layer

Purpose:
- Acts as the system’s entry point for external requests
- Handles request validation at a structural level
- Converts external input into internal commands

Responsibilities:
- Accept and parse incoming requests
- Validate request format (required fields, data types)
- Delegate execution to the appropriate service
- Translate service responses into API responses

Explicitly NOT responsible for:
- Enforcing business rules
- Performing authorization decisions
- Managing transactions
- Accessing data directly

Guarantees:
- Controllers remain thin and predictable
- No business logic duplication
- All decisions are deferred to services

---

## Service Layer

Purpose:
- Acts as the core decision-making layer
- Enforces all business, authorization, and lifecycle rules

Responsibilities:
- Enforce rules defined in Week 1
- Validate ownership and permissions
- Control entity state transitions
- Coordinate interactions between multiple entities
- Ensure atomic execution of business operations

Explicitly NOT responsible for:
- Handling HTTP or transport-level concerns
- Persisting data directly
- Exposing internal entities externally

Guarantees:
- Single source of truth for business logic
- Consistent rule enforcement across the system
- Predictable and testable behavior

---

## Repository / Data Access Layer

Purpose:
- Handles interaction with the persistence mechanism
- Abstracts data storage details from services

Responsibilities:
- Retrieve and store entities
- Execute data queries and persistence operations
- Maintain referential integrity at storage level

Explicitly NOT responsible for:
- Business rule enforcement
- Authorization checks
- Entity lifecycle decisions

Guarantees:
- Data access remains isolated
- Persistence logic does not affect business correctness
- Storage technology can change without impacting services

---

## Cross-Layer Rules

- Business rules must exist only in the Service layer
- Authorization checks must occur before any state modification
- Controllers must never call repositories directly
- Repositories must never contain conditional business logic

---

## Request Processing Flow (High-Level)

1. Request enters via Controller
2. Controller validates request structure
3. Controller delegates to Service
4. Service performs:
   - Authentication context verification
   - Authorization and ownership checks
   - Business rule validation
   - State transition decisions
5. Service interacts with Repository
6. Repository performs data access
7. Service returns result to Controller
8. Controller returns response

---

## Architectural Guarantees

- Clear separation of concerns
- Centralized rule enforcement
- Reduced risk of inconsistent behavior
- Easier testing and future scaling

This layered approach ensures that SalesCRM remains maintainable,
secure, and aligned with professional backend engineering practices.


## Low Level Design — Rule to Service Mapping

This section maps the business rules defined in Week 1
to the services responsible for enforcing them.

The goal is to ensure that:
- Every rule has a clear enforcement point
- No rule is duplicated across services
- No rule is left unenforced

This mapping guarantees that rules are enforced
consistently and predictably at runtime.

---

## Core Services Overview

SalesCRM defines the following core services:

- UserService
- AuthService
- AuthorizationService
- LeadService
- ActivityService
- CustomerService

Each service owns enforcement of rules related
to its primary domain.

---

## UserService

Responsible for:
- User lifecycle management
- Role assignment enforcement
- User deactivation handling

Enforces:
- User creation restrictions (USER_NA_01, USER_NA_02)
- Role modification rules (USER_NA_03, USER_NA_04)
- User deactivation constraints (USER_NA_05, USER_NA_06)
- Role misuse prevention related to user actions

Guarantees:
- No privilege escalation
- No orphaned ownership after deactivation
- Controlled role transitions

---

## AuthService

Responsible for:
- User authentication
- Session / token lifecycle management

Enforces:
- Authentication requirements for access (AUTH_NA_01)
- Credential validation (AUTH_NA_02)
- Deactivated user access blocking (AUTH_NA_03)
- Token expiration and logout behavior (AUTH_NA_04, AUTH_NA_05)
- Failed authentication attempt limits (AUTH_NA_07)

Guarantees:
- Every request is associated with a verified identity
- Authentication state is explicit and time-bound

---

## AuthorizationService

Responsible for:
- Permission checks
- Ownership and scope validation

Enforces:
- Role-based access rules (AUTHZ rules)
- Ownership-based data access
- Team-scope restrictions for Managers
- Prevention of cross-role misuse

Guarantees:
- No data access without explicit permission
- Consistent authorization decisions across services

---

## LeadService

Responsible for:
- Lead lifecycle management
- Lead ownership enforcement
- Lead status transitions

Enforces:
- Lead creation rules (LEAD_NA_01)
- Lead status workflow constraints (LEAD_NA_02)
- Ownership change restrictions (LEAD_NA_03)
- Duplicate lead prevention
- Terminal state immutability

Guarantees:
- Leads always follow valid sales workflow
- Lead ownership is always explicit
- No invalid or misleading lead states

---

## ActivityService

Responsible for:
- Activity creation and lifecycle
- Activity ownership validation

Enforces:
- Activity-to-lead association rules (ACT_NA_01)
- Activity ownership constraints (ACT_NA_02)
- Activity immutability (ACT_NA_03)
- Activity deletion prevention (ACT_NA_04)
- Activity timing constraints (ACT_NA_05)

Guarantees:
- Accurate and immutable sales history
- No fake or misleading activity records

---

## CustomerService

Responsible for:
- Lead-to-customer conversion
- Customer lifecycle enforcement

Enforces:
- Conversion eligibility rules (CUST_NA_01)
- Lead-origin-only customer creation (CUST_NA_02)
- Customer immutability constraints (CUST_NA_03)
- Referential integrity enforcement (CUST_NA_05)

Guarantees:
- Clean lead-to-customer lineage
- No orphaned or duplicate customers
- Stable customer identity

---

## Cross-Service Enforcement Rules

- Services may collaborate but must not bypass authorization checks
- AuthorizationService must be consulted before any state-changing operation
- No service directly accesses data owned by another service without validation
- Business rules must never be enforced in controllers or repositories

---

## Rule Enforcement Guarantees

- Every Week 1 rule maps to exactly one service
- No rule is enforced in multiple places
- Rule ownership is explicit and auditable

This mapping ensures that SalesCRM remains predictable,
secure, and easy to evolve as the system grows.



## Low Level Design — Error Handling & Failure Behavior

This section defines how SalesCRM behaves when invalid actions are attempted
or when business rules are violated.

The goal is to ensure:
- Predictable system behavior
- Clear feedback to clients
- Strong data consistency
- No partial or silent failures

Error handling is treated as a first-class design concern.

---

## Error Handling Principles

SalesCRM follows these core principles:

- Fail fast on invalid actions
- Never partially apply business operations
- Preserve system consistency at all times
- Provide clear, deterministic error outcomes

No error condition should leave the system
in an ambiguous or corrupted state.

---

## Rule Violation Handling

When a business rule is violated:

- The requested operation is rejected immediately
- No state changes are applied
- No data is persisted
- A clear error response is returned

Examples:
- Attempting to skip lead stages
- Unauthorized access to a lead
- Activity creation on invalid lead states
- Invalid role or permission usage

---

## Authorization & Authentication Failures

### Authentication Failures
Scenarios:
- Missing authentication
- Invalid credentials
- Expired session or token
- Deactivated user

Behavior:
- Request is rejected before reaching business logic
- No service-level processing occurs
- No data access is attempted

---

### Authorization Failures
Scenarios:
- Accessing resources outside permitted scope
- Modifying unowned data
- Performing admin actions without privileges

Behavior:
- Request is rejected at authorization check
- No business rule execution occurs
- No data mutation is allowed

---

## Data Integrity Failure Handling

Scenarios:
- Duplicate lead creation
- Orphaned relationship detection
- Invalid lifecycle transitions
- Conversion attempts from invalid states

Behavior:
- Entire operation is rolled back
- System remains in last valid state
- Referential integrity is preserved

---

## Idempotency & Consistency

Principles:
- Repeated invalid requests always produce the same result
- Repeated valid requests do not create duplicate data
- Terminal states cannot be modified or re-entered

Guarantees:
- No duplicate customers
- No duplicate activities from retries
- No accidental state corruption

---

## Error Categorization (Conceptual)

SalesCRM categorizes errors into:

- Authentication Errors (identity-related)
- Authorization Errors (permission-related)
- Business Rule Violations
- Data Integrity Violations

Each category results in a clear,
non-overlapping failure outcome.

---

## Observability & Debug Safety

Design guarantees:
- All rejected actions are traceable to a user identity
- Rule violations are detectable during debugging
- No silent ignores or hidden failures

Sensitive internal details are not exposed
through error responses.

---

## Failure Behavior Guarantees

- No partial writes
- No silent failures
- No inconsistent state transitions
- No data corruption under invalid input

This ensures that SalesCRM behaves predictably
even under incorrect or malicious usage.


## Low Level Design — Authorization & Ownership Flow

This section defines how authentication context, authorization checks,
and ownership validation are applied during request processing.

The goal is to ensure that:
- Every action is performed by a verified identity
- Every data access is permission-checked
- Ownership is enforced consistently
- No rule is bypassed due to flow gaps

---

## Request Processing Flow (Authorization-Aware)

Every incoming request that intends to read or modify protected data
follows the same high-level flow:

1. Authentication Context Resolution
2. Authorization Validation
3. Ownership Verification
4. Business Rule Enforcement
5. Data Access

No step may be skipped.

---

## Step 1 — Authentication Context Resolution

Purpose:
- Establish the identity of the requester

Behavior:
- Extract authenticated user context
- Verify user is active
- Attach user identity to request lifecycle

Guarantees:
- All downstream logic has access to a verified user
- Anonymous or deactivated users are blocked early

---

## Step 2 — Authorization Validation (Role-Based)

Purpose:
- Validate whether the user’s role permits the requested action

Behavior:
- Evaluate role against requested operation
- Enforce role hierarchy constraints
- Block unauthorized actions immediately

Examples:
- Salesperson attempting admin action → rejected
- Salesperson requesting reports → rejected
- Manager accessing outside team scope → rejected

Guarantees:
- Role-based boundaries are respected
- No unauthorized capability escalation

---

## Step 3 — Ownership Verification (Scope-Based)

Purpose:
- Enforce data ownership and scope constraints

Behavior:
- Validate that the user owns or is permitted to act on the target entity
- Apply scope rules:
  - Salesperson → own data only
  - Manager → team data only
  - Admin → system-wide scope

Examples:
- Salesperson editing unowned lead → rejected
- Manager modifying lead outside team → rejected

Guarantees:
- Ownership is always explicit
- No cross-user or cross-team data access

---

## Step 4 — Business Rule Enforcement

Purpose:
- Enforce domain-specific constraints

Behavior:
- Validate entity state transitions
- Enforce lifecycle rules
- Prevent terminal-state modifications

Examples:
- Skipping lead stages
- Activities on CONVERTED leads
- Duplicate customer creation

Guarantees:
- Business correctness is preserved
- State transitions are predictable and controlled

---

## Step 5 — Data Access

Purpose:
- Perform authorized and validated persistence operations

Behavior:
- Read or write data through repository layer
- Maintain referential integrity
- Ensure atomic execution

Guarantees:
- Data access occurs only after all validations pass
- No unauthorized or invalid writes reach persistence

---

## Ownership Model Summary

- Ownership is derived, not duplicated
- Lead ownership determines:
  - Activity permissions
  - Modification rights
- Customer ownership traces back to originating Lead
- Historical ownership is preserved even after user deactivation

---

## Authorization & Ownership Guarantees

- Authentication always precedes authorization
- Authorization always precedes ownership checks
- Ownership checks always precede state changes
- No direct data access without passing all validations

This flow ensures that SalesCRM enforces security,
ownership, and business correctness uniformly
across all system operations.


## Low Level Design — Review & Freeze (Week 2)

This section marks the completion and freeze of the Low Level Design (LLD)
for SalesCRM.

The purpose of this step is to validate that:
- All business rules have a clear enforcement point
- All entities have well-defined responsibilities
- No layer violates its boundaries
- The system structure is implementation-ready

No new design decisions are introduced at this stage.

---

## LLD Review Checklist

The following validations have been completed:

### Entity Design Validation
- Each core entity has a single, well-defined responsibility
- No entity enforces business rules directly
- Entity lifecycles are explicit and terminal states are immutable

### Relationship & Lifecycle Validation
- All relationships are ownership-safe and non-ambiguous
- No entity can exist without a valid parent where required
- Lead → Activity → Customer lineage is preserved end-to-end

### Layer Responsibility Validation
- Controllers are limited to request handling only
- All business rules are enforced in the Service layer
- Repositories contain no business or authorization logic

### Rule Enforcement Coverage
- Every Week 1 rule maps to exactly one service
- No rule is enforced in multiple places
- No rule is left without an enforcement owner

### Authorization & Ownership Flow Validation
- Authentication always precedes authorization
- Authorization always precedes ownership checks
- Ownership checks always precede state changes
- No request can bypass validation steps

### Failure Behavior Validation
- All rule violations fail fast
- No partial writes are possible
- System remains consistent under invalid operations

---

## LLD Stability Guarantees

With this design:

- Business rules are centralized and consistent
- Unauthorized access paths are structurally blocked
- Entity state transitions are predictable
- Future changes can be made without breaking core guarantees

This structure supports safe implementation,
incremental feature growth, and clear interview explanations.

---

## Low Level Design Freeze Notice

The Low Level Design defined in this document is considered frozen
as of the completion of Week 2.

All implementation work must adhere strictly to:
- Defined entities and responsibilities
- Layered architecture boundaries
- Rule-to-service enforcement mapping
- Authorization and ownership flow

Any structural or behavioral change requires
an explicit update to this document before implementation.








