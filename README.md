# Voting System
================

This software was developed for voting at Colosseum-2015, the annual techfest held at College of Technology, Govind Ballabh Pant University of Agriculture and Technology.

Requirements:
* Java 1.6+
* MySQL

The GUI gets launched from the file Main.java. First a database structure is to be maintained. The database should contain the following relations:

* allevents(id, eventname, votes)
* centralevents(id, eventname, votes)
* branchevents(id, eventname, votes)
* college(id, centralevent, branchevent)
* external(id, centralevent, branchevent, preference)

In order to add/remove events, or modify the range of id numbers permitted, modifications are to be made in the appropriate text file in the "res" folder.
