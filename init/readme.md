Create a web ui with the following user interface


Roles:

RA
AA
RS

Form Status:
Draft
Processing
Returned
Completed

1. Index page
	Description: The login page where the user first login and after any successful actions.
    Left (Minority Width\): a tree that including the folloiwing items, according to whether this user can "View Records":
		(For RS) Basic functions
		Top node: My Forms
		    Sub node: Draft
			Sub node: Processing
			Sub node: Returned
			Sub node: Completed
		Top node: For My Action
			Sub node: Processing
				Sub Sub node: By Month
				Sub Sub node: By Department
				Sub Sub node: By Form
			Sub node: Completed
				Sub node: By Month
				Sub node: By Department
				Sub node: By Form	
		(For RA and AA) Audit Trail Function and Erasure Function
	Right (Majority Width)
		Top (Minority Height):  Display buttons available for action
			Button: New Form
				Event: After click the button, a list of Forms selectable for the RS is listed for highlight and mouse click for selection
					Click Event: Go to Create new form page
		Bottom (Rest of the height): 
			If check My Forms sub nodes, display the following table:
				Table: with given Form Status for the current user
					Column: Status (String)
					Column: Code (String)
					Column: Title (String)
					Column: Date of Submission (Date)
					Column: Date of Last Action (Date)
					Event: If double click any records, display corresponding pages depending on the Form Status
					Event: If click the header once, will be sorted ASC, click again then sorted DESC
			If check **"For My Action"** --> **"Processing"** or **"Completed"** sub nodes, display the following table:
				Table: with given Form Status for the current user
					Column: Department (String)
					Column: Staff Name (String)
					Column: Status (String)
					Column: Form (String)
					Column: Title (String)
					Column: Date of Submission (Date)
					Column: Date of Last Action (Date)
					Event: If double click any records, display corresponding pages depending on the Form Status
					Sorting Order: depends on user select "By Department", "By Month", "By Form"
						"By Department" sort by and group by department code
						"By Month": sort by and group by Year + Month of the submission date
						"By Form": sort by and group by form type
2. Create new form page / draft page
	Description: The page where the RS user create new forms or Form Status "Draft"
	Left (Minority Width): a tree that including the following items, according to whether this user can "View Records" as in Index Page
	Right (Majority Width)
		Top (Minority Height):  Display buttons available for action
			Button: Save as Draft
				Event: After click the button, save the filled information as draft (Status: Draft), display "Save." if complete.
			Button: Submit
				Event: Display a "Do you confirm the submission?" message, if Yes, save and submit this creation (Status: Processing). If save success, display message "Submitted!\n Responsibile Relavant Authority: <User Name> [<Position>]\n Responsibile Approving Authority: <User Name> [<Position>]". The form is submitted to RA.
			Button: Close
				Event: Cancel and delete this creation, and then go back to Index Page
			On Load: Load the predefined form details
				Declaration of Relationship Form as follows:
				Part 1: Introduction
				Part 2: Declaration of Relationship
					Text: I hereby declare the following is my family members
					Table:
						Column: Name (String)
						Column: Relationship (String)
					Textbox: Other relevant Information (Multiline text box)
					Button: Attach (if applicable)
						Event: Open a file upload window for upload files. Upon successful upload, show the hyperlink for download
					Checkbox: I hereby declare that the information entered above and supporting documents provided (if any) are true, accurate and complete.
				Part 3: Comments
					Button: Display buttons available for action
						Refresh: 
							Event: Reload the table with user comments
					Table: 
						Column: Description (Text)
						Column: Created by (Text)
						Column: Action Date (Date)
						Event: On double click the record, popup a message box to show the reason for resubmission with close button
					On Load: Load the data with any comments given by RA for the reason for resubmission
						

					
					
3. Resubmission Form Page
	Description: The page where user double clicked form with Form Status "Returned"
	Left (Minority Width): a tree that including the following items, according to whether this user can "View Records" as in Index Page
	Right (Majority Width)
		Top (Minority Height):  Display buttons available for action
			Resubmit: Re-submit the form
				Event: Display a "Do you confirm the re-submission?" message, if Yes, save and submit this creation (Status: Processing). If save success, display message "Re-submitted!\n Responsibile Relavant Authority: <User Name> [<Position>]\n Responsibile Approving Authority: <User Name> [<Position>]". The form is submitted to RA.
			Button: Close
				Event: Go back to Index Page with no change
			On Load: Load the predefined form details
				Declaration of Relationship Form as follows:
				Part 1: Introduction
				Part 2: Declaration of Relationship
					Text: I hereby declare the following is my family members
					Table:
					Column: Name (String)
					Column: Relationship (String)
					Textbox: Other relevant Information (Multiline text box)
					Button: Attach (if applicable)
						Event: Open a file upload window for upload files. Upon successful upload, show the hyperlink for download
					Checkbox: I hereby declare that the information entered above and supporting documents provided (if any) are true, accurate and complete.
				Part 3: Comments
					Button: Display buttons available for action
						Refresh: 
							Event: Reload the table with user comments
					Table: 
						Column: Description (Text)
							For returned forms (Forms Status "Returned"), there will be a record with description returned
							For reviewed forms (Forms Status "Completed"), there will be a record with description instructions
							For rejected forms (Forms Status "Returned"), there will be a record with description rejected
						Column: Created by (Text)
						Column: Action Date (Date)
						Event: On double click the record, popup a message box to show the reason for resubmission with close button
					On Load: Load the data with any comments given by RA for the reason for resubmission

4. Completed Forms Page
	Description: The page where user double clicked form with Form Status "Completed"
	Left (Minority Width): a tree that including the following items, according to whether this user can "View Records" as in Index Page
	Right (Majority Width)
		Top (Minority Height):  Display buttons available for action
			Resubmit: Re-submit the form
				Event: Display a "Do you confirm the re-submission?" message, if Yes, save and submit this creation (Status: Processing). If save success, display message "Re-submitted!\n Responsibile Relavant Authority: <User Name> [<Position>]\n Responsibile Approving Authority: <User Name> [<Position>]". The form is submitted to RA.
			Button: Close
				Event: Go back to Index Page with no change
			On Load: Load the predefined form details
				Declaration of Relationship Form as follows:
				Part 1: Introduction
				Part 2: Declaration of Relationship
					Text: I hereby declare the following is my family members
					Table:
					Column: Name (String)
					Column: Relationship (String)
					Textbox: Other relevant Information (Multiline text box)
					Button: Attach (if applicable)
						Event: Open a file upload window for upload files. Upon successful upload, show the hyperlink for download
					Checkbox: I hereby declare that the information entered above and supporting documents provided (if any) are true, accurate and complete.
				Part 3: Comments
					Button: Display buttons available for action
						Refresh: 
							Event: Reload the table with user comments
					Table: 
						Column: Description (Text)
							For reviewed forms (Forms Status "Completed"), there will be a record with description instructions
							For rejected forms (Forms Status "Returned"), there will be a record with description rejected
						Column: Created by (Text)
						Column: Action Date (Date)
						Event: On double click the record, popup a message box to show the reason for completion with close button
					On Load: Load the data with any comments given by RA for the reason for completion
					
4. Processing Forms Page
	Description: The page where user double clicked form with Form Status "Processing"
	Left (Minority Width): a tree that including the following items, according to whether this user can "View Records" as in Index Page
	Right (Majority Width)
		Top (Minority Height):  Display buttons available for action
			Return for Resubmission: Return the form to RS with reason (for RA only and the form is not recommended)
				Event: Display a dialog box for input return reason. 
					Dialog box: Return reason (multiline text). If previously entered the reason, display the text.
						Button: Return to reporting staff
							Event: save and return this creation (Status: Processing). If reason, display message "Do you confirm?". The form is submitted to RS. Close the dialog box.
							Event: after the returned
						Button: Save as Draft
							Event: just save the text for next time display. Close the dialog box.
						Button: Cancel
							Event: Close the dialog box without save the text
			Recommended: Pass the form to AA with reason (for RA only and the form is not recommended)
				Event: Display a "Confirm recommend?" message, if Yes, display a message box with multiline text box for input the reason, save and return this creation (Status: Processing and mark is recommended). If save success, display message "Recommended!". The form is submitted to RS.
			Resubmit Recommended: Pass the form to AA with reason (for RA only and the form is not recommended but has returned)
				Event: Display a "Confirm resubmit recommend?" message, if Yes, save and return this creation (Status: Processing and mark is recommended and clear returned). If save success, display message "Resubmit Recommended!". The form is submitted to RS.
			Button: Close
				Event: Go back to Index Page with no change
			On Load: Load the predefined form details
				Declaration of Relationship Form as follows:
				Part 1: Introduction
				Part 2: Declaration of Relationship
					Text: I hereby declare the following is my family members
					Table:
					Column: Name (String)
					Column: Relationship (String)
					Textbox: Other relevant Information (Multiline text box)
					Button: Attach (if applicable)
						Event: Open a file upload window for upload files. Upon successful upload, show the hyperlink for download
					Checkbox: I hereby declare that the information entered above and supporting documents provided (if any) are true, accurate and complete.
				Part 3: Comments
					Button: Display buttons available for action
						Refresh: 
							Event: Reload the table with user comments
					Table: 
						Column: Description (Text)
							For reviewed forms (Forms Status "Completed"), there will be a record with description instructions
							For rejected forms (Forms Status "Returned"), there will be a record with description rejected
						Column: Created by (Text)
						Column: Action Date (Date)
						Event: On double click the record, popup a message box to show the reason for completion with close button
					On Load: Load the data with any comments given by RA for the reason for completion
	
??? How RA can return the form?