# System Specification for Form Management Application

This specification outlines the requirements for building a web application using Vue.js for the frontend and Spring Boot for the backend. The application manages "Declaration of Relationship" forms with role-based access control (RBAC) for three roles: RS (Reporting Staff), RA (Relevant Authority), and AA (Approving Authority). Forms have statuses: Draft, Processing, Returned, Completed.

The system supports user authentication, form creation/submission/resubmission, auditing, file attachments, comments, and role-specific actions. Assume a relational database (e.g., PostgreSQL or MySQL) for persistence. Use JWT for authentication.

## 1. System Overview

### Functional Requirements
- **Authentication**: Users log in with username/password. Roles determine UI elements and actions.
- **Index Page**: Dashboard with tree menu (left sidebar) and dynamic tables/buttons (right panel) based on selection.
- **Form Pages**: Dedicated views for creating/editing forms in different statuses, with role-specific buttons.
- **Form Structure**: "Declaration of Relationship" form includes:
  - Part 1: Static introduction text.
  - Part 2: Declaration table (family members: Name, Relationship), multiline textbox for other info, file attachment button (upload and display hyperlink), checkbox for declaration.
  - Part 3: Comments table (Description, Created By, Action Date) with refresh button and double-click for details.
- **Actions**:
  - RS: Create, save draft, submit, resubmit.
  - RA: Review, return for resubmission (with reason), recommend to AA.
  - AA: Final approval (implied in completed status, but extend if needed).
- **Sorting/Grouping**: In tables, support sorting (ASC/DESC on column click) and grouping (by Department, Month, Form).
- **Events**: Double-click rows to navigate, confirm dialogs for submissions, file uploads.
- **Permissions**: Check "View Records" permission to show/hide tree nodes.

### Non-Functional Requirements
- **Tech Stack**:
  - Frontend: Vue 3 (with Composition API), Vue Router, Pinia (for state management), Axios (for API calls), Element Plus or Vuetify for UI components (tables, trees, dialogs, file uploads).
  - Backend: Spring Boot 3.x, Spring Security (JWT), Spring Data JPA, Hibernate, Lombok for boilerplate reduction.
  - Database: PostgreSQL (entities for Users, Forms, FamilyMembers, Attachments, Comments).
  - File Storage: Store attachments in database as BLOB or on filesystem (e.g., /uploads folder).
- **Security**: Role-based access, validate inputs, prevent XSS/SQL injection.
- **Performance**: Paginate tables if data grows large.
- **Error Handling**: Show user-friendly messages for failures.
- **Deployment**: Assume Docker for containerization.

## 2. Database Schema

Use JPA entities. Key tables:

- **User** (id: Long PK, username: String unique, password: String hashed, role: Enum[RS, RA, AA], position: String, department: String, canViewRecords: Boolean)
- **Form** (id: Long PK, code: String unique, title: String, status: Enum[DRAFT, PROCESSING, RETURNED, COMPLETED], submissionDate: Date, lastActionDate: Date, otherInfo: Text, declarationChecked: Boolean, creatorId: Long FK to User, raId: Long FK to User (Relevant Authority), aaId: Long FK to User (Approving Authority), isRecommended: Boolean, returnReason: Text)
- **FamilyMember** (id: Long PK, formId: Long FK, name: String, relationship: String)
- **Attachment** (id: Long PK, formId: Long FK, filename: String, content: Byte[] or path: String)
- **Comment** (id: Long PK, formId: Long FK, description: String, createdById: Long FK to User, actionDate: Date, type: Enum[RETURNED, REVIEWED, REJECTED, INSTRUCTIONS])

Relationships:
- Form 1:N FamilyMember
- Form 1:N Attachment
- Form 1:N Comment
- Form N:1 User (creator, ra, aa)

## 3. Backend (Spring Boot)

### Project Structure
- Controllers: REST endpoints.
- Services: Business logic.
- Repositories: JPA interfaces.
- Security: JWT filter, role annotations (@PreAuthorize).
- Config: CORS, JWT utils.

### API Endpoints
Base URL: /api

#### Authentication
- POST /auth/login: {username, password} → {token, userDetails}
- GET /auth/me: → Current user info (with roles)

#### Forms
- GET /forms/my: Params(status: String optional) → List of user's forms (for "My Forms" tree nodes). Return DTO with code, title, status, submissionDate, lastActionDate.
- GET /forms/action: Params(status: String[PROCESSING|COMPLETED], sortBy: String[DEPARTMENT|MONTH|FORM]) → List for "For My Action". DTO with department, staffName, status, formType (hardcode "Declaration of Relationship"), title, dates. Group and sort as specified.
- POST /forms/new: Body(FormDTO with familyMembers list, otherInfo, attachments multipart) → Create draft. Assign code (e.g., auto-generate UUID or sequential).
- PUT /forms/{id}/draft: Body(updated FormDTO) → Save as draft.
- PUT /forms/{id}/submit: Body(updated FormDTO) → Submit to Processing, assign RA/AA (logic: auto-assign based on department or config).
- PUT /forms/{id}/resubmit: Body(updated FormDTO) → Resubmit from Returned to Processing.
- PUT /forms/{id}/return: Body({reason: String}) → RA only, set to Returned, add Comment.
- PUT /forms/{id}/recommend: Body({reason: String optional}) → RA only, set isRecommended=true, forward to AA (update status if needed).
- PUT /forms/{id}/resubmit-recommend: Similar to recommend, clear return flags.
- GET /forms/{id}: → Full FormDTO (with familyMembers, attachments hyperlinks, comments).
- DELETE /forms/{id}: → Delete draft.

#### Comments
- GET /forms/{id}/comments: → List of CommentDTO.
- POST /forms/{id}/comments: Body({description: String}) → Add comment (auto-set createdBy, date).

#### Attachments
- POST /forms/{id}/attachments: Multipart file → Upload, return hyperlink (e.g., /api/attachments/{attachId}).
- GET /attachments/{id}: → Download file.

#### Users
- GET /users: Admin only, list users (for assigning RA/AA if manual).

Use DTOs to avoid exposing entities. Handle sorting/grouping in service layer (e.g., use Java streams or JPQL).

### Security
- Configure Spring Security: JWT authentication.
- @PreAuthorize("hasRole('RS')") on RS endpoints, etc.
- Validate form ownership/status before actions.

## 4. Frontend (Vue.js)

### Project Structure
- src/
  - components/ : Reusable (FormTable.vue, CommentsTable.vue, TreeMenu.vue, UploadButton.vue)
  - views/ : Pages (Index.vue, CreateForm.vue, ResubmitForm.vue, CompletedForm.vue, ProcessingForm.vue)
  - store/ : Pinia stores (auth, forms)
  - router/ : Routes
  - api/ : Axios instance with JWT interceptor

### Routes
- / : Index (redirect after login)
- /login : Login form
- /forms/new : CreateForm
- /forms/{id}/draft : CreateForm (load draft)
- /forms/{id}/resubmit : ResubmitForm
- /forms/{id}/completed : CompletedForm (read-only, with resubmit button)
- /forms/{id}/processing : ProcessingForm (RA actions)

Guard routes with auth/roles using beforeEnter.

### State Management (Pinia)
- authStore: token, user (role, canViewRecords)
- formsStore: currentForm, formsList, actionsList

### Components
- **TreeMenu**: Vue Tree component. Dynamically build nodes based on role:
  - For RS: Basic functions → My Forms (sub: Draft, Processing, Returned, Completed)
  - For RA/AA: Add Audit Trail/Erasure (placeholders if not detailed).
  - Emit selection event to load right panel.
- **FormTable**: Data table with columns as specified. Support sorting (ASC/DESC on header click), double-click row to navigate (e.g., router.push('/forms/{id}/{status}')).
- **CommentsTable**: Table with refresh button (API call), double-click for modal with details.
- **FormEditor**: Core component for form parts:
  - Part 1: Static text.
  - Part 2: Editable table (add/remove rows for family members), multiline textarea, upload button (axios post multipart), checkbox.
  - Part 3: CommentsTable.
- **Buttons**: Role/status-based (e.g., v-if="user.role === 'RS' && form.status === 'DRAFT'").
- **Dialogs**: Use modal for confirms, reasons (v-dialog).

### Page Details

#### 1. Index Page (Index.vue)
- Layout: Flex row.
- Left (30% width): TreeMenu.
- Right (70% width):
  - Top (20% height): Buttons (e.g., New Form → list selectable forms, but since only one type, direct to /forms/new).
  - Bottom: Conditional table based on tree selection.
    - My Forms subnodes: FormTable with status filter.
    - For My Action: FormTable with grouping/sortBy param in API call.

#### 2. Create New Form / Draft Page (CreateForm.vue)
- Layout: Similar, left TreeMenu.
- Right:
  - Top: Buttons (Save Draft: API PUT draft, Submit: Confirm dialog → PUT submit, Close: Delete/Back).
  - FormEditor (editable).
- On mounted: If id, load form data.

#### 3. Resubmission Form Page (ResubmitForm.vue)
- Similar to CreateForm.
- Buttons: Resubmit (confirm → PUT resubmit), Close.
- Load form, show comments (highlight return reasons).

#### 4. Completed Forms Page (CompletedForm.vue)
- Similar.
- Buttons: Resubmit (if allowed, confirm → PUT resubmit), Close.
- FormEditor read-only.
- Comments with completion reasons.

#### 5. Processing Forms Page (ProcessingForm.vue)
- Similar.
- Buttons (RA only): Return for Resubmission (dialog for reason → PUT return), Recommend (confirm + optional reason → PUT recommend), Resubmit Recommended (similar), Close.
- FormEditor read-only except comments.

### Events and Interactions
- Double-click: Use @dblclick on table rows.
- Sorting: Use table sort prop.
- File Upload: <input type="file"> or UI component, post to API.
- Messages: Use toast/snackbar for success/errors.
- On Load: Use async mounted() with API calls.

This specification provides a complete blueprint. Implement iteratively: Start with backend entities/APIs, then frontend auth/pages. Test role-based flows end-to-end.