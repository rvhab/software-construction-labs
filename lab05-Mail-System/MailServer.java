package lab05;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class MailServer
{
    private HashMap<String, List<MailItem>> mailboxes;

    public MailServer()
    {
        mailboxes = new HashMap<String, List<MailItem>>();
    }

    public boolean registerUser(String name)
    {
        if (mailboxes.containsKey(name)) {
            return false;
        }
        mailboxes.put(name, new ArrayList<MailItem>());
        return true;
    }

    public boolean isRegistered(String name)
    {
        return mailboxes.containsKey(name);
    }

    public List<String> getUsers()
    {
        return new ArrayList<String>(new TreeSet<String>(mailboxes.keySet()));
    }

    public void post(MailItem item)
    {
        List<MailItem> box = mailboxes.get(item.getTo());
        if (box != null) {
            box.add(item);
        }
    }
    public int howManyMailItems(String who)
    {
        List<MailItem> box = mailboxes.get(who);
        return (box == null) ? 0 : box.size();
    }

    public MailItem getNextMailItem(String who)
    {
        List<MailItem> box = mailboxes.get(who);
        if (box == null || box.isEmpty()) {
            return null;
        }
        return box.remove(0);
    }
}