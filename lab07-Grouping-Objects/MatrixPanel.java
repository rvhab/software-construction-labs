package lab07;

import static lab07.Ui.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeListener;

/**
 * MatrixPanel - the screen for Lab 07, Task 2 (matrix multiplication).
 * Choose the size of Matrix A and Matrix B, type the numbers, and press Multiply.
 * Click any number in the result to see exactly how it was calculated.
 */
public class MatrixPanel extends JPanel implements TaskPanel
{

    private static final int MAX_SIZE = 5;

    private MatrixInput matA;
    private MatrixInput matB;
    private JTextArea message = wrapText(2, 10);
    private JLabel resultDims = new JLabel(" ");
    private JPanel resultGrid = new JPanel();
    private JPanel resultHolder = new JPanel(new GridBagLayout());
    private JTextArea explanation = new JTextArea();
    private ResultCell[][] resultCells = new ResultCell[0][0];
    private int[][] lastA;
    private int[][] lastB;

    public MatrixPanel()
    {
        setBackground(BG);
        setLayout(new BorderLayout());

        JPanel body = new ScrollBody();
        body.setLayout(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(18, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;

        // ---- row 0: Matrix A  x  Matrix B
        matA = new MatrixInput("A", 2, 3, this::inputsChanged);
        matB = new MatrixInput("B", 3, 2, this::inputsChanged);
        JLabel times = new JLabel("\u00D7", SwingConstants.CENTER);
        times.setFont(font(Font.BOLD, 34));
        times.setForeground(MUTED);
        times.setBorder(new EmptyBorder(0, 14, 0, 14));

        g.gridy = 0; g.weighty = 0; g.insets = new Insets(0, 0, 14, 0);
        g.gridx = 0; g.weightx = 1;
        body.add(matA, g);
        g.gridx = 1; g.weightx = 0;
        body.add(times, g);
        g.gridx = 2; g.weightx = 1;
        body.add(matB, g);

        // ---- row 1: actions
        g.gridy = 1; g.gridx = 0; g.gridwidth = 3; g.weightx = 1;
        body.add(buildActions(), g);

        // ---- row 2: result and explanation
        JPanel bottom = new JPanel(new GridLayout(1, 2, 14, 0));
        bottom.setOpaque(false);
        bottom.add(buildResultCard());
        bottom.add(buildExplanationCard());
        g.gridy = 2; g.weighty = 1; g.insets = new Insets(0, 0, 0, 0);
        body.add(bottom, g);

        add(scrollPage(body), BorderLayout.CENTER);

        inputsChanged();
    }

    public String title()    { return "Matrix multiplication"; }

    public String subtitle() { return "Columns of A must equal rows of B. The result has the rows of A and the columns of B"; }

    // ================================================================ layout

    private JComponent buildActions()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(14, 20, 14, 20));

        RoundButton multiply = new RoundButton("Multiply", Kind.PRIMARY);
        RoundButton random = new RoundButton("Random numbers", Kind.SECONDARY);
        RoundButton clear = new RoundButton("Clear", Kind.SECONDARY);
        multiply.addActionListener(e -> multiply());
        random.addActionListener(e -> randomNumbers());
        clear.addActionListener(e -> clearAll());

        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setOpaque(false);
        buttons.add(multiply);
        buttons.add(random);
        buttons.add(clear);

        message.setFont(font(Font.BOLD, 13));

        JPanel buttonRow = new JPanel(new BorderLayout());
        buttonRow.setOpaque(false);
        buttonRow.add(buttons, BorderLayout.WEST);

        card.add(buttonRow, BorderLayout.NORTH);
        card.add(message, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildResultCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 10));

        resultDims.setFont(font(Font.PLAIN, 12));
        resultDims.setForeground(MUTED);
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(sectionTitle("Result"), BorderLayout.WEST);
        head.add(resultDims, BorderLayout.EAST);

        resultGrid.setOpaque(false);
        resultHolder.setOpaque(false);
        resultHolder.add(resultGrid);

        card.add(head, BorderLayout.NORTH);
        card.add(resultHolder, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildExplanationCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 10));

        explanation.setEditable(false);
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setRows(9);
        explanation.setColumns(24);
        explanation.setFont(new Font("Consolas", Font.PLAIN, 15));
        explanation.setForeground(TEXT);
        explanation.setBackground(new Color(0xF7FAFC));
        explanation.setBorder(new EmptyBorder(12, 14, 12, 14));
        JScrollPane scroll = new JScrollPane(explanation);
        scroll.setBorder(new LineBorder(BORDER));

        card.add(sectionTitle("How it was calculated"), BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ============================================================== actions

    /** Reads both matrices, checks them, multiplies and shows the result. */
    private void multiply()
    {
        matA.clearMarks();
        matB.clearMarks();

        // 1. the sizes must match (columns of A = rows of B)
        if (matA.colCount() != matB.rowCount()) {
            showError("Cannot multiply: Matrix A has " + matA.colCount() + " columns but Matrix B has "
                    + matB.rowCount() + " rows. They must be equal.");
            return;
        }

        // 2. every box must hold a whole number
        int[][] a, b;
        try {
            a = matA.read();
            b = matB.read();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        // 3. multiply
        long[][] c = MatrixMultiplier.multiply(a, b);
        lastA = a;
        lastB = b;
        showResult(c);
        selectResult(0, 0);
        setMessage("Done. The result is a " + c.length + " x " + c[0].length + " matrix. "
                 + "Click a number in the result to see how it was calculated.", SUCCESS);
    }

    private void randomNumbers()
    {
        Random rnd = new Random();
        matA.fillRandom(rnd);
        matB.fillRandom(rnd);
        inputsChanged();
    }

    private void clearAll()
    {
        matA.clearCells();
        matB.clearCells();
        inputsChanged();
    }

    /** Called when a size changes or the boxes are cleared or filled. */
    private void inputsChanged()
    {
        matA.clearMarks();
        matB.clearMarks();
        matA.highlight(-1, -1);
        matB.highlight(-1, -1);
        clearResult();
        if (matA.colCount() != matB.rowCount()) {
            setMessage("Not possible yet: Matrix A has " + matA.colCount() + " columns but Matrix B has "
                     + matB.rowCount() + " rows. They must be equal.", WARN);
        } else {
            setMessage("A is " + matA.rowCount() + " x " + matA.colCount() + " and B is "
                     + matB.rowCount() + " x " + matB.colCount() + ", so the result will be "
                     + matA.rowCount() + " x " + matB.colCount() + ".", MUTED);
        }
    }

    // =============================================================== helpers

    private void showResult(long[][] c)
    {
        int rows = c.length;
        int cols = c[0].length;
        resultGrid.removeAll();
        resultGrid.setLayout(new GridLayout(rows, cols, 6, 6));
        resultCells = new ResultCell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                final int r = i, col = j;
                ResultCell cell = new ResultCell(String.valueOf(c[i][j]));
                cell.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { selectResult(r, col); }
                });
                resultCells[i][j] = cell;
                resultGrid.add(cell);
            }
        }
        resultDims.setText(rows + " x " + cols);
        resultGrid.revalidate();
        resultGrid.repaint();
        resultHolder.revalidate();
        resultHolder.repaint();
    }

    /** Highlights one result element and explains it. */
    private void selectResult(int row, int col)
    {
        for (int i = 0; i < resultCells.length; i++) {
            for (int j = 0; j < resultCells[i].length; j++) {
                resultCells[i][j].setSelected(i == row && j == col);
            }
        }
        matA.highlight(row, -1);
        matB.highlight(-1, col);
        explanation.setText(MatrixMultiplier.explain(lastA, lastB, row, col));
        explanation.setCaretPosition(0);
    }

    private void clearResult()
    {
        resultGrid.removeAll();
        resultCells = new ResultCell[0][0];
        resultDims.setText(" ");
        explanation.setText("Press Multiply, then click any number in the result "
                          + "to see how it was calculated.");
        resultGrid.revalidate();
        resultGrid.repaint();
    }

    private void showError(String text)
    {
        clearResult();
        matA.highlight(-1, -1);
        matB.highlight(-1, -1);
        setMessage(text, DANGER);
    }

    private void setMessage(String text, Color c)
    {
        message.setForeground(c);
        message.setText(text);
    }

    // =============================================================== styling

    private static Border fieldBorder(Color c)
    {
        return new CompoundBorder(new LineBorder(c, 1, true), new EmptyBorder(6, 4, 6, 4));
    }

    /** One matrix: title, row and column size pickers, and a grid of boxes to type in. */
    private static class MatrixInput extends Card
    {
        private final String name;
        private final JSpinner rows;
        private final JSpinner cols;
        private final JPanel grid = new JPanel();
        private JTextField[][] cells = new JTextField[0][0];

        MatrixInput(String name, int r, int c, Runnable onChange)
        {
            this.name = name;
            this.rows = spinner(r);
            this.cols = spinner(c);
            setLayout(new BorderLayout(0, 12));

            JLabel title = sectionTitle("Matrix " + name);

            JPanel sizes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            sizes.setOpaque(false);
            sizes.add(small("Rows"));
            sizes.add(rows);
            sizes.add(small("Columns"));
            sizes.add(cols);

            JPanel head = new JPanel(new BorderLayout());
            head.setOpaque(false);
            head.add(title, BorderLayout.WEST);
            head.add(sizes, BorderLayout.EAST);

            grid.setOpaque(false);
            JPanel holder = new JPanel(new GridBagLayout());
            holder.setOpaque(false);
            holder.add(grid);

            add(head, BorderLayout.NORTH);
            add(holder, BorderLayout.CENTER);

            ChangeListener changed = e -> {
                rebuild();
                onChange.run();
            };
            rows.addChangeListener(changed);
            cols.addChangeListener(changed);
            rebuild();
        }

        private static JSpinner spinner(int value)
        {
            JSpinner s = new JSpinner(new SpinnerNumberModel(value, 1, MAX_SIZE, 1));
            s.setFont(font(Font.BOLD, 13));
            s.setPreferredSize(new Dimension(58, 30));
            JTextField tf = ((JSpinner.DefaultEditor) s.getEditor()).getTextField();
            tf.setEditable(false);
            tf.setBackground(Color.WHITE);
            tf.setHorizontalAlignment(SwingConstants.CENTER);
            return s;
        }

        private static JLabel small(String text)
        {
            JLabel l = new JLabel(text);
            l.setFont(font(Font.BOLD, 11));
            l.setForeground(MUTED);
            return l;
        }

        int rowCount() { return (Integer) rows.getValue(); }
        int colCount() { return (Integer) cols.getValue(); }

        /** Rebuilds the grid for the chosen size and keeps numbers that still fit. */
        private void rebuild()
        {
            String[][] old = new String[cells.length][];
            for (int i = 0; i < cells.length; i++) {
                old[i] = new String[cells[i].length];
                for (int j = 0; j < cells[i].length; j++) {
                    old[i][j] = cells[i][j].getText();
                }
            }
            int r = rowCount();
            int c = colCount();
            grid.removeAll();
            grid.setLayout(new GridLayout(r, c, 6, 6));
            cells = new JTextField[r][c];
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    JTextField f = makeCell();
                    if (i < old.length && j < old[i].length) {
                        f.setText(old[i][j]);
                    }
                    cells[i][j] = f;
                    grid.add(f);
                }
            }
            grid.revalidate();
            grid.repaint();
        }

        private JTextField makeCell()
        {
            JTextField f = new JTextField();
            f.setPreferredSize(new Dimension(60, 38));
            f.setMinimumSize(new Dimension(60, 38));
            f.setHorizontalAlignment(SwingConstants.CENTER);
            f.setFont(font(Font.BOLD, 15));
            f.setForeground(TEXT);
            f.setCaretColor(TEXT);
            f.setBorder(fieldBorder(BORDER));
            f.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (!Boolean.TRUE.equals(f.getClientProperty("invalid"))) f.setBorder(fieldBorder(PRIMARY));
                }
                @Override public void focusLost(FocusEvent e) {
                    if (!Boolean.TRUE.equals(f.getClientProperty("invalid"))) f.setBorder(fieldBorder(BORDER));
                }
            });
            f.addKeyListener(new KeyAdapter() {
                @Override public void keyTyped(KeyEvent e) {
                    if (Boolean.TRUE.equals(f.getClientProperty("invalid"))) {
                        f.putClientProperty("invalid", false);
                        f.setBorder(fieldBorder(PRIMARY));
                    }
                }
            });
            return f;
        }

        /** Reads all boxes as whole numbers. Marks the bad box red and throws if one is wrong. */
        int[][] read()
        {
            int r = cells.length;
            int c = cells[0].length;
            int[][] m = new int[r][c];
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    String t = cells[i][j].getText().trim();
                    if (t.isEmpty()) {
                        mark(cells[i][j]);
                        throw new IllegalArgumentException("Matrix " + name + ": row " + (i + 1)
                                + ", column " + (j + 1) + " is empty.");
                    }
                    try {
                        m[i][j] = Integer.parseInt(t);
                    } catch (NumberFormatException e) {
                        mark(cells[i][j]);
                        throw new IllegalArgumentException("Matrix " + name + ": \"" + t + "\" in row "
                                + (i + 1) + ", column " + (j + 1) + " is not a whole number.");
                    }
                }
            }
            return m;
        }

        private void mark(JTextField f)
        {
            f.putClientProperty("invalid", true);
            f.setBorder(fieldBorder(DANGER));
            f.requestFocusInWindow();
        }

        void clearMarks()
        {
            for (JTextField[] row : cells) {
                for (JTextField f : row) {
                    f.putClientProperty("invalid", false);
                    f.setBorder(fieldBorder(f.hasFocus() ? PRIMARY : BORDER));
                }
            }
        }

        void clearCells()
        {
            for (JTextField[] row : cells) {
                for (JTextField f : row) f.setText("");
            }
        }

        void fillRandom(Random rnd)
        {
            for (JTextField[] row : cells) {
                for (JTextField f : row) f.setText(String.valueOf(rnd.nextInt(9) + 1));
            }
        }

        /** Colours one row and/or one column of boxes (-1 means none). */
        void highlight(int row, int col)
        {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    boolean on = (i == row) || (j == col);
                    cells[i][j].setBackground(on ? HILITE : Color.WHITE);
                }
            }
        }
    }

    /** One number of the result, drawn as a rounded box that can be clicked. */
    private static class ResultCell extends JComponent
    {
        private final String text;
        private boolean selected = false;
        private boolean over = false;

        ResultCell(String text)
        {
            this.text = text;
            setFont(font(Font.BOLD, 15));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            FontMetrics fm = getFontMetrics(getFont());
            Dimension size = new Dimension(Math.max(60, fm.stringWidth(text) + 24), 38);
            setPreferredSize(size);
            setMinimumSize(size);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { over = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { over = false; repaint(); }
            });
        }

        void setSelected(boolean s)
        {
            selected = s;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(selected ? PRIMARY : (over ? new Color(0xDCE6F0) : PLAIN_BG));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(selected ? Color.WHITE : TEXT);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                          (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}