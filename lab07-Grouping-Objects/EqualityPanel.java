package lab07;

import static lab07.Ui.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * EqualityPanel - the screen for Lab 07, Task 3.
 * Type two arrays and the program tells you whether they are equal. It checks the
 * lengths first, then compares the elements one by one and shows every comparison.
 */
public class EqualityPanel extends JPanel implements TaskPanel
{
    private static final Color BAD_ROW = new Color(0xFDECEA);

    private enum State { IDLE, EQUAL, DIFFERENT }

    private JTextField fieldA = new JTextField();
    private JTextField fieldB = new JTextField();
    private JLabel message = new JLabel(" ");
    private JLabel verdict = new JLabel("Compare two arrays to see the result");
    private JTextArea reason = wrapText(2, 10);
    private Badge badge = new Badge();
    private DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Index", "Array A", "Array B", "Result" }, 0)
    {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private JTable table;

    public EqualityPanel()
    {
        setBackground(BG);
        setLayout(new BorderLayout());

        JPanel body = new ScrollBody();
        body.setLayout(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(18, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.BOTH;

        g.gridy = 0; g.weighty = 0; g.insets = new Insets(0, 0, 14, 0);
        body.add(buildInputCard(), g);
        g.gridy = 1;
        body.add(buildVerdictCard(), g);
        g.gridy = 2; g.weighty = 1; g.insets = new Insets(0, 0, 0, 0);
        body.add(buildTableCard(), g);
        add(scrollPage(body), BorderLayout.CENTER);

        showState(State.IDLE, "Compare two arrays to see the result", " ");
    }

    public String title()    { return "Array equality test"; }

    public String subtitle() { return "Check the lengths first, then compare the elements one by one"; }

    // ================================================================ layout

    private JComponent buildInputCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 12));
        card.add(sectionTitle("Enter two arrays"), BorderLayout.NORTH);

        setupField(fieldA);
        setupField(fieldB);
        fieldA.addActionListener(e -> compare());
        fieldB.addActionListener(e -> compare());

        JPanel rows = new JPanel(new GridBagLayout());
        rows.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 0, 10, 12);
        g.anchor = GridBagConstraints.WEST;
        g.gridy = 0; g.gridx = 0; g.weightx = 0;
        rows.add(fieldLabel("Array A"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(0, 0, 10, 0);
        rows.add(fieldA, g);
        g.gridy = 1; g.gridx = 0; g.weightx = 0; g.fill = GridBagConstraints.NONE; g.insets = new Insets(0, 0, 0, 12);
        rows.add(fieldLabel("Array B"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(0, 0, 0, 0);
        rows.add(fieldB, g);

        RoundButton compare = new RoundButton("Compare", Kind.PRIMARY);
        RoundButton same = new RoundButton("Equal pair", Kind.SECONDARY);
        RoundButton diff = new RoundButton("Different pair", Kind.SECONDARY);
        RoundButton clear = new RoundButton("Clear", Kind.SECONDARY);
        compare.addActionListener(e -> compare());
        same.addActionListener(e -> example("4, 8, 15, 16, 23, 42", "4, 8, 15, 16, 23, 42"));
        diff.addActionListener(e -> example("4, 8, 15, 16, 23, 42", "4, 8, 15, 17, 23, 42"));
        clear.addActionListener(e -> clearAll());

        JPanel buttons = new JPanel(new GridLayout(1, 4, 8, 0));
        buttons.setOpaque(false);
        buttons.add(compare);
        buttons.add(same);
        buttons.add(diff);
        buttons.add(clear);
        JPanel buttonRow = new JPanel(new BorderLayout());
        buttonRow.setOpaque(false);
        buttonRow.add(buttons, BorderLayout.WEST);

        JLabel hint = new JLabel("Whole numbers separated by spaces or commas, for example: 4, 8, 15, 16");
        hint.setFont(font(Font.PLAIN, 12));
        hint.setForeground(MUTED);
        message.setFont(font(Font.BOLD, 12));

        JPanel notes = new JPanel(new GridLayout(2, 1, 0, 4));
        notes.setOpaque(false);
        notes.add(hint);
        notes.add(message);

        JPanel middle = new JPanel(new BorderLayout(0, 12));
        middle.setOpaque(false);
        middle.add(rows, BorderLayout.NORTH);
        middle.add(buttonRow, BorderLayout.CENTER);
        middle.add(notes, BorderLayout.SOUTH);
        card.add(middle, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildVerdictCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(18, 0));
        card.setBorder(new EmptyBorder(16, 22, 16, 22));

        verdict.setFont(font(Font.BOLD, 20));
        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.setOpaque(false);
        text.add(verdict, BorderLayout.NORTH);
        text.add(reason, BorderLayout.CENTER);

        card.add(badge, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildTableCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 10));

        JLabel sub = new JLabel("every pair of elements at the same index");
        sub.setFont(font(Font.PLAIN, 12));
        sub.setForeground(MUTED);
        JPanel head = new JPanel(new BorderLayout(12, 0));
        head.setOpaque(false);
        head.add(sectionTitle("Element by element"), BorderLayout.WEST);
        head.add(sub, BorderLayout.CENTER);

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(450, 230));
        table.setRowSelectionAllowed(false);

        DefaultTableCellRenderer cells = new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean focus, int row, int col)
            {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, row, col);
                boolean bad = "Different".equals(t.getValueAt(row, 3));
                l.setOpaque(true);
                l.setBackground(bad ? BAD_ROW : (row % 2 == 0 ? Color.WHITE : ZEBRA));
                l.setForeground(col == 3 ? (bad ? DANGER : SUCCESS) : TEXT);
                l.setFont(font(col == 0 || col == 3 ? Font.BOLD : Font.PLAIN, 14));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(new EmptyBorder(0, 8, 0, 8));
                return l;
            }
        };
        table.setDefaultRenderer(Object.class, cells);

        DefaultTableCellRenderer heads = new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean focus, int row, int col)
            {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, row, col);
                l.setOpaque(true);
                l.setBackground(HEAD_BG);
                l.setForeground(MUTED);
                l.setFont(font(Font.BOLD, 12));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
                return l;
            }
        };
        table.getTableHeader().setDefaultRenderer(heads);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(head, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ============================================================== actions

    private void compare()
    {
        clearMarks();
        int[] a, b;
        try {
            a = read(fieldA, "A");
            b = read(fieldB, "B");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        tableModel.setRowCount(0);
        boolean equal = ArrayEquality.areEqual(a, b);

        // step 1: lengths
        if (a.length != b.length) {
            showState(State.DIFFERENT, "The arrays are NOT equal",
                    "Step 1: Array A has " + a.length + " element" + (a.length == 1 ? "" : "s")
                  + " and Array B has " + b.length + ". The lengths are different, so the arrays cannot be equal. "
                  + "The elements were not compared.");
            setMessage("Done.", SUCCESS);
            return;
        }

        // step 2: elements
        int different = 0;
        for (int i = 0; i < a.length; i++) {
            boolean match = a[i] == b[i];
            if (!match) different++;
            tableModel.addRow(new Object[] { i, a[i], b[i], match ? "Match" : "Different" });
        }

        if (equal) {
            showState(State.EQUAL, "The arrays are equal",
                    "Step 1: both arrays have " + a.length + " element" + (a.length == 1 ? "" : "s")
                  + ". Step 2: all " + a.length + " elements match.");
        } else {
            int first = ArrayEquality.firstDifference(a, b);
            showState(State.DIFFERENT, "The arrays are NOT equal",
                    "Step 1: both arrays have " + a.length + " elements. Step 2: the first difference is at index "
                  + first + " (" + a[first] + " in A, " + b[first] + " in B). "
                  + different + " of " + a.length + " elements differ.");
        }
        setMessage("Done.", SUCCESS);
        SwingUtilities.invokeLater(() -> table.scrollRectToVisible(new Rectangle(0, 0, 1, 1)));
    }

    private void example(String a, String b)
    {
        fieldA.setText(a);
        fieldB.setText(b);
        compare();
    }

    private void clearAll()
    {
        fieldA.setText("");
        fieldB.setText("");
        clearMarks();
        tableModel.setRowCount(0);
        showState(State.IDLE, "Compare two arrays to see the result", " ");
        setMessage(" ", MUTED);
        fieldA.requestFocusInWindow();
    }

    // =============================================================== helpers

    /** Reads one array from a field. Marks the field red and throws if it is not valid. */
    private int[] read(JTextField f, String name)
    {
        try {
            return ArrayStats.parse(f.getText());
        } catch (IllegalArgumentException e) {
            f.putClientProperty("invalid", true);
            f.setBorder(fieldBorder(DANGER));
            throw new IllegalArgumentException("Array " + name + ": " + e.getMessage());
        }
    }

    private void showError(String text)
    {
        tableModel.setRowCount(0);
        showState(State.IDLE, "Compare two arrays to see the result", " ");
        setMessage(text, DANGER);
    }

    private void clearMarks()
    {
        for (JTextField f : new JTextField[] { fieldA, fieldB }) {
            f.putClientProperty("invalid", false);
            f.setBorder(fieldBorder(f.hasFocus() ? PRIMARY : BORDER));
        }
        setMessage(" ", MUTED);
    }

    private void showState(State s, String title, String why)
    {
        badge.setState(s);
        verdict.setText(title);
        verdict.setForeground(s == State.EQUAL ? SUCCESS : (s == State.DIFFERENT ? DANGER : MUTED));
        reason.setText(why.trim());
    }

    private void setMessage(String text, Color c)
    {
        message.setForeground(c);
        message.setText(text);
    }

    // =============================================================== styling

    private static Border fieldBorder(Color c)
    {
        return new CompoundBorder(new LineBorder(c, 1, true), new EmptyBorder(10, 14, 10, 14));
    }

    private static JLabel fieldLabel(String text)
    {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 12));
        l.setForeground(MUTED);
        return l;
    }

    private void setupField(JTextField f)
    {
        f.setFont(font(Font.PLAIN, 15));
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
                    setMessage(" ", MUTED);
                }
            }
        });
    }

    /** Round badge: = for equal, a crossed = for different, ? before comparing. */
    private static class Badge extends JComponent
    {
        private State state = State.IDLE;

        Badge() { setPreferredSize(new Dimension(54, 54)); }

        void setState(State s)
        {
            state = s;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Color c = state == State.EQUAL ? SUCCESS : (state == State.DIFFERENT ? DANGER : MUTED);
            String symbol = state == State.EQUAL ? "=" : (state == State.DIFFERENT ? "\u2260" : "?");
            g2.setColor(c);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(Color.WHITE);
            g2.setFont(font(Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(symbol, (getWidth() - fm.stringWidth(symbol)) / 2,
                          (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}