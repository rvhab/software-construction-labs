package lab05;

import java.util.ArrayList;
import java.util.List;

public class MailClient
{
    private MailServer server;
    private String user;
    private List<MailItem> inbox;
    private List<MailItem> drafts;
    private List<MailItem> sent;
    private List<MailItem> bin;

    public MailClient(MailServer server, String user)
    {
        this.server = server;
        this.user = user;
        this.inbox = new ArrayList<MailItem>();
        this.drafts = new ArrayList<MailItem>();
        this.sent = new ArrayList<MailItem>();
        this.bin = new ArrayList<MailItem>();
    }

    public String getUser() { return user; }

    public List<MailItem> getFolder(Folder folder)
    {
        switch (folder) {
            case INBOX:  return inbox;
            case DRAFTS: return drafts;
            case SENT:   return sent;
            default:     return bin;
        }
    }
    public MailItem receiveNextMailItem()
    {
        MailItem item = server.getNextMailItem(user);
        if (item != null) {
            inbox.add(0, item);
        }
        return item;
    }

    public List<MailItem> receiveNewMail()
    {
        List<MailItem> arrived = new ArrayList<MailItem>();
        MailItem item = receiveNextMailItem();
        while (item != null) {
            arrived.add(item);
            item = receiveNextMailItem();
        }
        return arrived;
    }

    public String sendMailItem(String to, String subject, String message, MailItem draft)
    {
        if (to.isEmpty()) {
            return "Please enter a recipient.";
        }
        if (!server.isRegistered(to)) {
            return "There is no user named \"" + to + "\".";
        }
        if (message.isEmpty()) {
            return "The message is empty.";
        }
        if (subject.isEmpty()) {
            subject = "(no subject)";
        }
        MailItem item = new MailItem(user, to, subject, message);
        server.post(item);
        sent.add(0, item);
        if (draft != null) {
            drafts.remove(draft);
        }
        return null;
    }

    public MailItem saveDraft(String to, String subject, String message, MailItem oldDraft)
    {
        if (to.isEmpty() && subject.isEmpty() && message.isEmpty()) {
            return null;
        }
        if (oldDraft != null) {
            drafts.remove(oldDraft);
        }
        MailItem draft = new MailItem(user, to, subject.isEmpty() ? "(no subject)" : subject, message);
        drafts.add(0, draft);
        return draft;
    }

    public void deleteMailItem(MailItem item, Folder from)
    {
        if (from == Folder.BIN) {
            bin.remove(item);
        } else {
            getFolder(from).remove(item);
            item.setOrigin(from);
            bin.add(0, item);
        }
    }

    public void restoreMailItem(MailItem item)
    {
        bin.remove(item);
        getFolder(item.getOrigin()).add(0, item);
    }

    public void emptyBin()
    {
        bin.clear();
    }

    public int getUnreadCount()
    {
        int count = 0;
        for (MailItem m : inbox) {
            if (!m.isRead()) count++;
        }
        return count;
    }
}