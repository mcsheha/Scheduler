README.txt

Scheduler Application version 1.0
C195 - Advanced Java Concepts
By: Michael Shehadeh



I. Login
-----------
Username: admin
Password: admin

or

Username: Mike
Password: 1234

(case sensitve)

* Uncomment 'bypassLogin();' in MainApp.java start() to bypass the login process




II. Considerations
---------------------
- All times are stored in database in UTC and converted to system default when presented to user
- Second Language is French, uncommet 'Locale.setDefault(new Locale("fr", "FR"));' for testing.
- Login Log is saved as: src/scheduler/LoginLog.txt. File will be generated if doesn't exist, appended to if it already exists.




III. Reporting
----------------
- 'Number of Appointment Types by Month' is based on 'description' field of DB.
- 'Schedule for Consulatant' will show all appointments for selected consultant.
- 'Print' button will print the selected report to console



