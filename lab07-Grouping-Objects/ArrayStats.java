package lab07;

import java.util.ArrayList;
import java.util.List;

public class ArrayStats
{
    /** The largest value, or null if the array is empty. */
    public static Integer largest(int[] a)
    {
        return lastState(a)[0];
    }

    /** The second largest (different) value, or null if there is none. */
    public static Integer secondLargest(int[] a)
    {
        return lastState(a)[1];
    }

    /** The smallest value, or null if the array is empty. */
    public static Integer smallest(int[] a)
    {
        return lastState(a)[2];
    }

    /** The second smallest (different) value, or null if there is none. */
    public static Integer secondSmallest(int[] a)
    {
        return lastState(a)[3];
    }

    /** All four answers at once: {largest, 2nd largest, smallest, 2nd smallest}. */
    public static Integer[] analyse(int[] a)
    {
        return lastState(a);
    }

    /**
     * What the four trackers hold after each number is read.
     * Each row is {largest, 2nd largest, smallest, 2nd smallest} (null = not set yet).
     */
    public static List<Integer[]> states(int[] a)
    {
        List<Integer[]> rows = new ArrayList<Integer[]>();
        Integer big = null, big2 = null, small = null, small2 = null;

        for (int i = 0; i < a.length; i++) {
            int n = a[i];

            // track the two biggest values
            if (big == null || n > big) {
                big2 = big;
                big = n;
            } else if (n != big && (big2 == null || n > big2)) {
                big2 = n;
            }

            // track the two smallest values
            if (small == null || n < small) {
                small2 = small;
                small = n;
            } else if (n != small && (small2 == null || n < small2)) {
                small2 = n;
            }

            rows.add(new Integer[] { big, big2, small, small2 });
        }
        return rows;
    }

    private static Integer[] lastState(int[] a)
    {
        if (a.length == 0) {
            return new Integer[] { null, null, null, null };
        }
        List<Integer[]> rows = states(a);
        return rows.get(rows.size() - 1);
    }

    /**
     * Turns text like "12, 45 7 89" into an int array.
     * Throws IllegalArgumentException with a clear message if the text is not valid.
     */
    public static int[] parse(String text)
    {
        String t = text.trim();
        if (t.isEmpty()) {
            throw new IllegalArgumentException("Enter some numbers first.");
        }
        List<Integer> values = new ArrayList<Integer>();
        for (String token : t.split("[,\\s]+")) {
            if (token.isEmpty()) {
                continue;
            }
            try {
                values.add(Integer.parseInt(token));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("\"" + token + "\" is not a valid whole number.");
            }
        }
        int[] a = new int[values.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = values.get(i);
        }
        return a;
    }
}