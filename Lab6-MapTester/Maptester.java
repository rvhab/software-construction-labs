package lab06;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Maptester
{
    private HashMap<String, String> phoneBook;

    public Maptester()
    {
        phoneBook = new HashMap<String, String>();
    }

    //the two main methods
    public String enterNumber(String name, String number)
    {
        return phoneBook.put(name, number);
    }
    public String lookupNumber(String name)
    {
        return phoneBook.get(name);
    }

    //extra operations
    public String removeEntry(String name)
    {
        return phoneBook.remove(name);
    }
    public List<String> getNamesWithNumber(String number)
    {
        List<String> names = new ArrayList<String>();
        for (Map.Entry<String, String> e : phoneBook.entrySet()) {
            if (e.getValue().equals(number)) {
                names.add(e.getKey());
            }
        }
        Collections.sort(names);
        return names;
    }
    public int size()
    {
        return phoneBook.size();
    }
    public Map<String, String> getEntries()
    {
        return new TreeMap<String, String>(phoneBook);
    }
    public static String validateName(String raw)
    {
        if (raw.trim().isEmpty()) {
            return "Name is required.";
        }
        return null;
    }
    public static String validateNumber(String raw)
    {
        String number = raw.trim();
        if (number.isEmpty()) {
            return "Phone number is required.";
        }
        if (!number.matches("[0-9 ()+*#.\\-]+") || !number.matches(".*[0-9].*")) {
            return "Use digits only (spaces, - ( ) + * # are allowed).";
        }
        return null;
    }
    public static String cleanName(String raw)
    {
        return raw.trim().replaceAll("\\s+", " ");
    }
    public static String cleanNumber(String raw)
    {
        return raw.trim().replaceAll("\\s+", " ");
    }
}