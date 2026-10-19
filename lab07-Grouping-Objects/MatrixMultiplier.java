package lab07;

/**
 * MatrixMultiplier - multiplies two matrices using three nested loops.
 *
 * Rule: the number of COLUMNS in the first matrix must equal the number of
 * ROWS in the second matrix. The result has the ROWS of the first matrix
 * and the COLUMNS of the second matrix.
 */
public class MatrixMultiplier
{
    /** True if a and b can be multiplied (columns of a = rows of b). */
    public static boolean canMultiply(int[][] a, int[][] b)
    {
        return a.length > 0 && b.length > 0 && a[0].length == b.length;
    }

    /**
     * Multiplies a by b. Throws IllegalArgumentException if the sizes do not match.
     * The result is stored in long values so large products do not overflow.
     */
    public static long[][] multiply(int[][] a, int[][] b)
    {
        // 1. input validation
        if (!canMultiply(a, b)) {
            throw new IllegalArgumentException("The number of columns in the first matrix ("
                    + (a.length == 0 ? 0 : a[0].length) + ") must equal the number of rows in the second matrix ("
                    + b.length + ").");
        }

        // 2. result matrix: rows of a x columns of b
        int rows = a.length;
        int cols = b[0].length;
        int inner = b.length;
        long[][] result = new long[rows][cols];

        // 3. three nested loops
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < inner; k++) {
                    result[i][j] += (long) a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    /** Explains how one result element (row, col) is calculated. Row and col start at 0. */
    public static String explain(int[][] a, int[][] b, int row, int col)
    {
        StringBuilder products = new StringBuilder();
        StringBuilder values = new StringBuilder();
        long sum = 0;
        for (int k = 0; k < b.length; k++) {
            long p = (long) a[row][k] * b[k][col];
            sum += p;
            if (k > 0) {
                products.append(" + ");
                values.append(" + ");
            }
            products.append(num(a[row][k])).append(" x ").append(num(b[k][col]));
            values.append(num(p));
        }
        return "Result element (row " + (row + 1) + ", column " + (col + 1) + ")\n"
             + "= row " + (row + 1) + " of A multiplied by column " + (col + 1) + " of B\n\n"
             + "= " + products + "\n"
             + "= " + values + "\n"
             + "= " + sum;
    }

    private static String num(long n)
    {
        return n < 0 ? "(" + n + ")" : String.valueOf(n);
    }
}