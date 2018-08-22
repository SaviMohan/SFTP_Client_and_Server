NAME: Saviduruu Mohan	UPI:smoh944	ID: 860579101
README for cs725 Assignment 1

***********HOW THE CODE WORKS*******************************************************************************************
The code consists of two classes, Client.java and Server.java, which are resposible for client side and server side functionality
respectively. Once the Server class is started it will run continuously and wait for a connection from the Client class.
Then the Client class is started and will connect to the server. On connection with the server, it should send a message
"+localhost SFTP Service" to the Client. The user can enter commands on the console for the Client and these will be 
sent by the Client class to the Server class to be processed. In both the SFTPClient and SFTPServer folders (inside src folder) 
there exists a public directory where files sent and received between client and server are stored. The public directory of 
the server contains folders for each user. A logged in user can access (via CDIR) without credentials their own folder, but needs credentials to access
another user's folder.  
************************************************************************************************************************

****HOW TO RUN********************************************************************************************************
-Open eclipse on a Windows computer
-import project "Assignment1SFTPClientServer"
-In the package explorer, right click on Server.java, select "Run as", then select "1 Java Application"
-Then again the package explorer, right click on Client.java, select "Run as", then select "1 Java Application" 
NOTE: make sure to run server class FIRST, then client class SECOND
In the console for Client.java you should now see the message: "+localhost SFTP Service"
***********************************************************************************************************************
PLEASE RUN THE TEST CASES FOR EACH COMMAND IN THE ORDER LISTED BELOW
!!!!NOTE: The RETR and STOR commands are automated (i.e you only need to enter the initial STOR or RETR command and the program will take care of the rest)

!REFER TO PICTURES IN project folder for example test case outputs!

IMPORTANT: In the test cases, the lines that start with "CLIENT:" represent the lines that need to be manually typed in by the user.
Although when typing in a line only type in whats specified inside the speech "" marks 
All other lines are produced by the program.

!!!!!!!!!!!!!!!!!!!!close server side first followed by client size when done with testin!!!!!!!!!!!!!!!!!


****ALL 11 RFC913 COMMANDS HAVE BEEN IMPLEMENTED********************************************************************************************
*All commands from TYPE onwards(no. 4 onwards) will only work if you are logged in (as specified in protocol), otherwise they will simply abort*

1. USER
Server takes in a user name from the client and determines if its valid.
If its valid, it either logs the user in if they don't need to enter an account or password, or it tells them 
to send the account and password. 

Test Case - succes - login with user, who doesn't need an account or password, so logged in straight away 
"""
CLIENT:"USER master"
FROM SERVER: !master logged in
"""

Test Case - success - login with user, who needs to also enter account and password 
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
"""

Test Case - fail - invalid user login
"""
CLIENT:"USER lol"
FROM SERVER: -Invalid user-id, try again
"""

2. ACCT
Server takes in a account name from the client and determines if its valid. If its valid, it either 
logs the user in if their other credentials 
are validated or tells the user what other credentials need to be validated. 

Test Case - fail case - user tries to input account name before entering valid user id
"""
CLIENT:"ACCT svr"
FROM SERVER: -Please enter userID first
"""
	 
Test Case - fail case - user enters invalid id
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT jjj"
FROM SERVER: -Invalid account
"""

Test Case - success case - user enters account and is prompted to now enter password
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
"""

Test Case - success case - user enters password first, before entering account and is then logged in
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"PASS 999"
FROM SERVER: +Password valid, send account
CLIENT:"ACCT svr"
FROM SERVER: !Account valid, logged-in
"""

3. PASS
Server takes in a password from the client and checks if it is valid. It then logs the user in if their other credentials 
are validated or tells the client what other credentials it needs to verify.
	 
Test Case - fail case - user tries to input password before entering valid user id
"""
CLIENT:"PASS 999"
FROM SERVER: -Please enter userID first
"""	 

Test Case (incorrect password (fail case), followed by correct password (success case))
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 456"
FROM SERVER: -Wrong password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
"""	 

Test Case - success case - user enters password and is then prompted to enter account.
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"PASS 999"
FROM SERVER: +Password valid, send account
"""	 


4. TYPE
Server sets the file transmission mode to ASCII, Binary or Continuous depending on the client message.
In our case Binary and Continuous are the same (Each transmitted byte is the next byte of the file), 
but ASCII mode is different in that itinvolves swapping out file line endings with CRLF
	 
Test Case - all available TYPE values are shown (success case), followed by fail case where user enters invalid TYPE value.
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"TYPE A"
FROM SERVER: +Using Ascii mode
CLIENT:"TYPE B"
FROM SERVER: +Using Binary mode
CLIENT:"TYPE C"
FROM SERVER: +Using Continuous mode
CLIENT:"TYPE D"
FROM SERVER: -Type not valid
"""

Test Case - success case - Shows that files can be successfully retrieved from Server for all 3 TYPE values
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in (now logged into server's public directory, observe files fileB.txt, fileJ.txt and fileK.txt, also observe in client's public directory, only file there is fileA.txt)
CLIENT:"TYPE A"
FROM SERVER: +Using Ascii mode
CLIENT:"RETR fileB.txt"
FROM SERVER: 100
TO SERVER: SEND
FILE RECEIVED
CLIENT:"TYPE B"
FROM SERVER: 
+Using Binary mode
CLIENT:"RETR fileJ.txt"
FROM SERVER: 11
TO SERVER: SEND
FILE RECEIVED
CLIENT:"TYPE C"
FROM SERVER: +Using Continuous mode
CLIENT:"RETR fileK.txt"
FROM SERVER: 14
TO SERVER: SEND
FILE RECEIVED (Notice the newly received fileB.txt, fileJ.txt and fileK.txt in the client folder's public directory)
""" (After this test case, delete the newly received fileB.txt, fileJ.txt and fileK.txt from the SFTPClient folder's public directory)

5. LIST
Server lists the file contents of the specified directory from the client.
If no directory is specified, server returns the file contents of the current directory.

Test Case - This test case demonstrates that the LIST cmd will return the right results for different directories and when no directory is 
specified.
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"LIST F"
FROM SERVER: +src/SFTPServer/public											SUCCESS CASE: when no directory is specified, returns the file names in current directory
fileB.txt
fileC.txt
fileJ.txt
fileK.txt

CLIENT:"LIST V"																SUCCESS CASE: when no directory is specified, returns the file names and details in current directory
FROM SERVER: +src/SFTPServer/public
fileB.txt  Size: 100 bytes  Last Modified: 2018-08-21T02:30:59.445  
fileC.txt  Size: 65 bytes  Last Modified: 2018-08-22T14:22:41.800  
fileJ.txt  Size: 11 bytes  Last Modified: 2018-08-22T20:28:30.678  
fileK.txt  Size: 14 bytes  Last Modified: 2018-08-22T20:28:57.731  

CLIENT:"LIST"																FAIL CASE: LIST cmd must specify either F or V
FROM SERVER: -Error processing your message
CLIENT:"LIST B"																FAIL CASE: LIST cmd must specify either F or V
FROM SERVER: -invalid parameters for LIST command
CLIENT:"LIST F src/SFTPServer/public/invis"									FAIL CASE: LIST cmd must specify valid directory
FROM SERVER: -invalid file directory
CLIENT:"LIST F src/SFTPServer/public/bob"									SUCCESS CASE: when directory is specified, returns the file names in that directory
FROM SERVER: +src/SFTPServer/public/bob
fileD.txt
fileE.txt

CLIENT:"LIST V src/SFTPServer/public/bob"									SUCCESS CASE: when directory is specified, returns the file names and details in that directory
FROM SERVER: +src/SFTPServer/public/bob
fileD.txt  Size: 46 bytes  Last Modified: 2018-08-22T14:23:57.627  
fileE.txt  Size: 37 bytes  Last Modified: 2018-08-22T14:25:16.021  
"""




6. RETR
The Server automates the RETR sequence.
(i.e. client user only needs to enter the initial RETR command and the server and client classes will take care of the rest)
First the server extracts the file name of the file to be sent from the client message. If the requested file 
exists in the current directory then it sends the file size back to the client. If it receives the SEND command
from the client then it will send the file to the client. 

Test Case 
-Before starting this test case, ensure fileA.txt is the only file in the client folder's public/ directory
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"RETR fileZ.txt"
FROM SERVER: -Specified file does not exist in current directory, RETR aborted		FAIL CASE: valid file must be specified
CLIENT:"RETR fileB.txt"											
FROM SERVER: 100
TO SERVER: SEND
FILE RECEIVED																		SUCCESS CASE: Notice fileB.txt now in client's public directory
CLIENT:"RETR fileB.txt"
FROM SERVER: 100
TO SERVER: SEND
FILE RECEIVED																		SUCCESS CASE: Notice n_fileB.txt (new generation) now in client's public directory
"""	 (You should now have two new files in client folder's public directory, fileB.txt and n_fileB.txt, delete these 2 files after this test case is run)

7. STOR
The server automates the STOR sequence.
(i.e. client user only needs to enter the initial STOR command and the server and client classes will take care of the rest)
First the server extracts the file name of the file to be stored, then storage specification (NEW, OLD, APP).
Then it receives the file size from the client and determines if there is enough space.
If there is then it receives the file from the client and stores it in the current directory
	 
Test Case 
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"STOR fileX.txt"
No such file exists in client's directory										FAIL CASE: valid file must be specified to be sent
CLIENT:"STOR NEW fileX.txt"
No such file exists in client's directory										FAIL CASE: valid file must be specified to be sent
CLIENT:"STOR MMM fileA.txt"
FROM SERVER: -Invalid file parameters, STOR command sequence aborted			FAIL CASE: valid file parameters must be specified
CLIENT:"STOR NEW fileA.txt"
FROM SERVER: +File does not exist, will create new file
TO SERVER:SIZE 58
FROM SERVER: +ok, waiting for file
Sending File
FROM SERVER: +Saved fileA.txt													SUCCESS CASE: notice fileA.txt now in server's public directory
CLIENT:"STOR NEW fileA.txt"
FROM SERVER: +File exists, will create new generation of file
TO SERVER:SIZE 58
FROM SERVER: +ok, waiting for file
Sending File
FROM SERVER: +Saved n_fileA.txt													SUCCESS CASE: notice new generation of fileA.txt (n_fileA.txt) in server's public directory due to NEW parameter
CLIENT:"STOR APP fileA.txt"
FROM SERVER: +Will append to file
TO SERVER:SIZE 58
FROM SERVER: +ok, waiting for file
Sending File
FROM SERVER: +Saved fileA.txt													SUCESS CASE: Notice how fileA.txt in server's public directory now has 3 new lines appended to it (6 in total) due to APP parameter
CLIENT:"STOR OLD fileA.txt"
FROM SERVER: +Will write over old file
TO SERVER:SIZE 58
FROM SERVER: +ok, waiting for file
Sending File
FROM SERVER: +Saved fileA.txt													SUCCESS CASE: Notice how fileA.txt in server's public directory has had its contents overwritten, and is now back to 3 lines
""" (At the end of this test case you should have two new files in server folder's public directory, fileA.txt and n_fileA.txt, delete these two files)
	 
8. NAME
The server gets the name of the file
to rename from the client message. If that file exists in the current directory
then it requests from the client what the new name of the file should be, and then 
renames it accordingly.
	 
Test Case - Success (fileJ.txt in server public/ directory renamed to fileM.txt)
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"NAME fileJ.txt"
FROM SERVER: +File exists, send TOBE
CLIENT:"TOBE fileM.txt"
FROM SERVER: +fileJ.txt renamed to fileM.txt
"""

Test Case - Fail (NAME command fails as there is no file in server public/ directory called ran.txt)
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"NAME ran.txt"
FROM SERVER: -Can't find ran.txt, NAME command is aborted, don't send TOBE.

"""

9. KILL
Server deletes the specified file from the client message if it exists in the current directory

Test Case - Success
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in (You're now logged in to the default location (the public/ directory in the server folder). Observe that there is a fileK.txt file)
CLIENT:"KILL fileK.txt"
FROM SERVER: +fileK.txt deleted (If you look in public/ directory in the server folder fileK.txt should now have gone)
"""
Test Case - Fail (file to delete does not exist)
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"KILL ran.txt"
FROM SERVER: -Not deleted because specified file does not exist in directory
"""

10. CDIR
Server extract the directory to change the current directory to from the client message.
Then it determines if this is a valid directory. Then it checks whether the currently logged in user has 
permission to access the folder. If they do, then it changes the directory, otherwise it requests that the client
enters the account/password credentials for that folder, then if that is successful it will change the directory.
	 
Test Case 
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"CDIR src/SFTPServer/public/invis"
FROM SERVER: -Can't connect to directory because: invalid file directory			FAIL CASE: can't change directory to an invalid directory
CLIENT:"CDIR src/SFTPServer/public/bob"
FROM SERVER: !Changed working dir to src/SFTPServer/public/bob						SUCCESS CASE: Since you are logged in as bob, you can switch directory to his folder, the LIST cmd now returns the contents of bob's folder
CLIENT:"LIST F"
FROM SERVER: +src/SFTPServer/public/bob
fileD.txt
fileE.txt

CLIENT:"CDIR src/SFTPServer/public/marvin"											SUCCESS CASE: In order to switch to marvin's folder you need to specify his credentials
FROM SERVER: +directory ok, but need credentials to verify, send account/password
CLIENT:"ACCT andr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 42"
FROM SERVER: !Changed working dir to src/SFTPServer/public/marvin
CLIENT:"LIST F"
FROM SERVER: +src/SFTPServer/public/marvin
fileF.txt
fileG.txt

CLIENT:"CDIR src/SFTPServer/public/mc117"											FAIL CASE: In order to switch to mc117's folder you need to specify his credentials, however the wrong account is entered and thus the CDIR sequence is aborted
FROM SERVER: +directory ok, but need credentials to verify, send account/password
CLIENT:"ACCT mmm"
FROM SERVER: -Invalid account
"""

11. DONE
Server closes the socket that is connected with the client and ends the connection

Test Case - Success (No more messages can be sent by client after connection closed)
"""
CLIENT:"USER bob"
FROM SERVER: +User-id valid, send account and/or password
CLIENT:"ACCT svr"
FROM SERVER: +Account valid, send password
CLIENT:"PASS 999"
FROM SERVER: !Password valid, logged-in
CLIENT:"DONE"
FROM SERVER: +localhost connection closing
Client side connection closed
"""

Test Case - Fail (Due to not logging in first)
"""
CLIENT:"DONE"
FROM SERVER: -Please Login first
"""



