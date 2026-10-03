package lab05;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MailItem
{
    private String from;
    private String to;
    private String subject;
    private String message;
    private String time;
    private boolean read;
    private Folder origin = Folder.INBOX; 

    public MailItem(String from, String to, String subject, String message)
    {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        this.read = false;
    }

    public String getFrom()    { return from; }
    public String getTo()      { return to; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public String getTime()    { return time; }
    public boolean isRead()    { return read; }
    public void markRead()     { this.read = true; }
    public Folder getOrigin()  { return origin; }
    public void setOrigin(Folder origin) { this.origin = origin; }

    /** Text version of the mail (used to show / print it). */
    public String show()
    {
        return "From: " + from + "\nTo: " + to + "\nDate: " + time
             + "\nSubject: " + subject + "\n\n" + message;
    }
}