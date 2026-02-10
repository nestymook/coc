# Web UI Specification for Declaration of Relationship Form System

## User Roles
- **RA** — Relevant Authority
- **AA** — Approving Authority
- **RS** — Reporting Staff

## Form Statuses
- Draft
- Processing
- Returned
- Completed

## 1. Index Page (Login / Main Dashboard)

**Description**: The login page and landing page after successful actions.

### Layout
- **Left (Minority Width)**: Tree view (shown based on user's "View Records" permission)
  - **For RS** — Basic functions
    - **My Forms**
      - Draft
      - Processing
      - Returned
      - Completed
    - **For My Action**
      - **Processing**
        - By Month
        - By Department
        - By Form
      - **Completed**
        - By Month
        - By Department
        - By Form
  - **For RA and AA** — Audit Trail Function and Erasure Function

- **Right (Majority Width)**
  - **Top (Minority Height)**: Action buttons
    - **New Form** button
      - On click: Displays a list of selectable form types
      - Selection → navigates to **Create new form page**
  - **Bottom (Majority Height)**: Main content area
    - When selecting **My Forms** sub-nodes → show table filtered by selected status
      **Table columns**:
      - Status (String)
      - Code (String)
      - Title (String)
      - Date of Submission (Date)
      - Date of Last Action (Date)

      **Events**:
      - Double-click row → open corresponding form view/edit page (based on status)
      - Click column header → toggle sort (ASC → DESC → ASC)

    - When selecting **For My Action → Processing** or **Completed** sub-nodes → show table
      **Table columns**:
      - Department (String)
      - Staff Name (String)
      - Status (String)
      - Form (String)
      - Title (String)
      - Date of Submission (Date)
      - Date of Last Action (Date)

      **Events**:
      - Double-click row → open form view/edit
      - Sorting / Grouping depends on sub-node:
        - **By Department**: group & sort by department code
        - **By Month**: group & sort by Year + Month of submission date
        - **By Form**: group & sort by form type

## 2. Create New Form / Draft Page

**Description**: Page for RS to create new forms or edit forms in **Draft** status.

### Layout
- **Left**: Same tree as Index page
- **Right**:
  - **Top**: Action buttons
    - **Save as Draft**
      - Saves current data as Draft
      - Shows "Save." message on success
    - **Submit**
      - Confirmation: "Do you confirm the submission?"
      - On Yes: Submit form (Status → **Processing**)
      - Success message:
	  - Form routed to RA
	- **Close**
	  - Cancel and delete draft
    - Return to Index page

- **Form Content** (on load):
### Part 1: Introduction

### Part 2: Declaration of Relationship
- Text: *I hereby declare the following is my family members*

- **Table**:
| Name        | Relationship |
|-------------|--------------|
| (editable)  | (editable)   |

- **Other relevant Information**: Multiline textbox

- **Attach** button
- Opens file upload dialog
- On success: shows downloadable hyperlink

- **Checkbox**:  
*I hereby declare that the information entered above and supporting documents provided (if any) are true, accurate and complete.*

### Part 3: Comments
- **Refresh** button → reloads comment table
- **Table**:
| Description | Created by | Action Date |
|-------------|------------|-------------|
| ...         | ...        | ...         |

- Double-click row → popup showing reason/comment with Close button

- On load: show any existing RA comments (for returned forms)

## 3. Resubmission Form Page

**Description**: Opened when double-clicking a **Returned** form.

- Same layout as Create/Draft page
- **Top buttons**:
- **Resubmit**
- Confirmation: "Do you confirm the re-submission?"
- On Yes → Status → **Processing**
- Success message similar to Submit
- **Close** → back to Index (no changes)

- Form content identical to Create page
- Comments section shows return reason(s) from RA

## 4. Completed Forms Page

**Description**: Opened when double-clicking a **Completed** form.

- Same layout
- **Top buttons**:
- **Resubmit** (optional / conditional)
- **Close**

- Shows read-only form + comments (usually "instructions" type entries)

## 5. Processing Forms Page

**Description**: Opened when double-clicking a **Processing** form (mainly for RA/AA).

- Same layout
- **Top buttons** (RA-specific):
- **Return for Resubmission** (for non-recommended forms)
- Opens dialog:
- Multiline "Return reason"
- Buttons: **Return to reporting staff** | **Save as Draft** | **Cancel**
- **Recommended** — pass to AA with comment
- **Resubmit Recommended** — for previously returned but now re-recommending
- **Close**

- Form view + comments section