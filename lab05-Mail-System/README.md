# Lab 05: Mail System

A mail system built with Java Swing. Several users can register, sign in,
and exchange mail through one shared server, all in a single window.

## Classes

- Folder.java: an enum that holds the four folder names (Inbox, Drafts, Sent and Bin).
- MailItem.java: stores the contents of one mail, such as the sender, recipient, subject, message and time.
- MailServer.java: acts as the post office. It keeps a mailbox for each user and delivers mail.
- MailClient.java: sends and receives mail for one user and manages that user's folders.
- MailGUI.java: the graphical user interface, which runs in a single window.

## Features

- Register and sign in with a username
- Switch between signed-in accounts in the same window
- Compose, send and reply to mail
- Save and edit drafts
- Delete mail to the Bin, restore it, or empty the Bin
- Unread counter and automatic mail checking

## How to run

1. Put all the .java files in a package named lab05.
2. Run MailGUI.java.
3. Register two users, add both accounts, and send a mail between them.
