package lab07;

/**
 * ArrayEquality - tests whether two arrays are equal.
 * Two arrays are equal only if they have the same length AND every pair of
 * elements at the same position is the same.
 */
public class ArrayEquality
{
    /** True only if both arrays have the same length and all elements match. */
    public static boolean areEqual(int[] a, int[] b)
    {
        // 1. check lengths first: different lengths can never be equal
        if (a.length != b.length) {
            return false;
        }

        // 2. compare the elements one by one
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        // 3. nothing was different
        return true;
    }

    /**
     * Index of the first position where the arrays differ, or -1 if every
     * position they share matches.
     */
    public static int firstDifference(int[] a, int[] b)
    {
        int shared = Math.min(a.length, b.length);
        for (int i = 0; i < shared; i++) {
            if (a[i] != b[i]) {
                return i;
            }
        }
        return -1;
    }
}