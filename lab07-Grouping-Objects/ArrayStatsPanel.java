package lab07;

import static lab07.Ui.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * ArrayStatsPanel - the screen for Lab 07, Task 1.
 * Type an array of whole numbers and the program finds the largest, second largest,
 * smallest and second smallest values. The step-by-step table shows how the
 * comparison logic updates its four trackers as each number is read.
 */
public class ArrayStatsPanel extends JPanel implements TaskPanel
{

    // colours that mark the four answers (cards, chips and legend use the same ones)
    private static final Color BIG_BG    = PRIMARY;
    private static final Color BIG2_BG   = new Color(0xBFE3E6);
    private static final Color SMALL_BG  = HEADER;
    private static final Color SMALL2_BG = new Color(0xC5D5E4);
    private static final Color PLAIN_BG  = new Color(0xEDF2F7);

    private JTextField input = new JTextField();
    private JLabel message = new JLabel(" ");
    private JLabel bigValue = valueLabel(), big2Value = valueLabel();
    private JLabel smallValue = valueLabel(), small2Value = valueLabel();
    private JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    private DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Step", "Number read", "Largest", "2nd largest", "Smallest", "2nd smallest" }, 0)
    {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private JTable table;

    public ArrayStatsPanel()
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
        body.add(buildResults(), g);
        g.gridy = 2;
        body.add(buildArrayCard(), g);
        g.gridy = 3; g.weighty = 1; g.insets = new Insets(0, 0, 0, 0);
        body.add(buildStepsCard(), g);
        add(scrollPage(body), BorderLayout.CENTER);

        resetResults();
    }

    public String title()    { return "Second largest and second smallest"; }

    public String subtitle() { return "Find the second largest and second smallest element of an array"; }

    // ================================================================ layout

    private JComponent buildInputCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 10));

        card.add(sectionTitle("Enter the array"), BorderLayout.NORTH);

        // ---- field and buttons
        input.setFont(font(Font.PLAIN, 15));
        input.setForeground(TEXT);
        input.setCaretColor(TEXT);
        input.setBorder(fieldBorder(BORDER));
        input.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (!Boolean.TRUE.equals(input.getClientProperty("invalid"))) input.setBorder(fieldBorder(PRIMARY));
            }
            @Override public void focusLost(FocusEvent e) {
                if (!Boolean.TRUE.equals(input.getClientProperty("invalid"))) input.setBorder(fieldBorder(BORDER));
            }
        });
        input.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                if (Boolean.TRUE.equals(input.getClientProperty("invalid"))) {
                    input.putClientProperty("invalid", false);
                    input.setBorder(fieldBorder(PRIMARY));
                    setMessage(" ", MUTED);
                }
            }
        });
        input.addActionListener(e -> find());

        RoundButton find = new RoundButton("Find", Kind.PRIMARY);
        RoundButton random = new RoundButton("Random array", Kind.SECONDARY);
        RoundButton clear = new RoundButton("Clear", Kind.SECONDARY);
        find.addActionListener(e -> find());
        random.addActionListener(e -> randomArray());
        clear.addActionListener(e -> clearAll());

        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setOpaque(false);
        buttons.add(find);
        buttons.add(random);
        buttons.add(clear);

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.add(input, BorderLayout.CENTER);
        row.add(buttons, BorderLayout.EAST);

        // ---- hint and message
        JLabel hint = new JLabel("Whole numbers separated by spaces or commas, for example: 12, 45, 7, 89, 23, 56");
        hint.setFont(font(Font.PLAIN, 12));
        hint.setForeground(MUTED);
        message.setFont(font(Font.BOLD, 12));
        JPanel notes = new JPanel(new GridLayout(2, 1, 0, 4));
        notes.setOpaque(false);
        notes.add(hint);
        notes.add(message);

        JPanel middle = new JPanel(new BorderLayout(0, 8));
        middle.setOpaque(false);
        middle.add(row, BorderLayout.NORTH);
        middle.add(notes, BorderLayout.CENTER);
        card.add(middle, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildResults()
    {
        JPanel p = new JPanel(new GridLayout(1, 4, 14, 0));
        p.setOpaque(false);
        p.add(statCard("Largest", bigValue, BIG_BG));
        p.add(statCard("Second largest", big2Value, BIG2_BG));
        p.add(statCard("Smallest", smallValue, SMALL_BG));
        p.add(statCard("Second smallest", small2Value, SMALL2_BG));
        return p;
    }

    private JComponent statCard(String title, JLabel value, Color marker)
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel t = new JLabel(title);
        t.setFont(font(Font.BOLD, 12));
        t.setForeground(MUTED);
        t.setIcon(new Dot(marker));
        t.setIconTextGap(8);

        card.add(t, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildArrayCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(14, 20, 10, 20));

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        legend.setOpaque(false);
        legend.add(legendItem("Largest", BIG_BG));
        legend.add(legendItem("2nd largest", BIG2_BG));
        legend.add(legendItem("Smallest", SMALL_BG));
        legend.add(legendItem("2nd smallest", SMALL2_BG));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(sectionTitle("Your array"), BorderLayout.WEST);
        head.add(legend, BorderLayout.EAST);

        chips.setOpaque(false);
        JScrollPane scroll = new JScrollPane(chips,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(0, 66));

        card.add(head, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildStepsCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 10));

        JLabel sub = new JLabel("how the four trackers change after each number is read");
        sub.setFont(font(Font.PLAIN, 12));
        sub.setForeground(MUTED);
        JPanel head = new JPanel(new BorderLayout(12, 0));
        head.setOpaque(false);
        head.add(sectionTitle("Step by step"), BorderLayout.WEST);
        head.add(sub, BorderLayout.CENTER);

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(450, 250));
        table.setRowSelectionAllowed(false);
        table.setFont(font(Font.PLAIN, 14));

        DefaultTableCellRenderer cells = new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean focus, int row, int col)
            {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, row, col);
                l.setOpaque(true);
                l.setBackground(row % 2 == 0 ? Color.WHITE : ZEBRA);
                l.setForeground(TEXT);
                l.setFont(font(col <= 1 ? Font.BOLD : Font.PLAIN, 14));
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

    /** Reads the array from the text field and shows all the answers. */
    private void find()
    {
        int[] a;
        try {
            a = ArrayStats.parse(input.getText());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }
        if (a.length < 2) {
            showError("Enter at least 2 numbers to find a second largest and second smallest.");
            return;
        }

        Integer[] r = ArrayStats.analyse(a);
        showValue(bigValue, r[0], BIG_BG);
        showValue(big2Value, r[1], BIG2_BG);
        showValue(smallValue, r[2], SMALL_BG);
        showValue(small2Value, r[3], SMALL2_BG);

        fillChips(a, r);
        fillSteps(a);

        if (r[1] == null) {
            setMessage("All the numbers are the same, so there is no second largest or second smallest.", WARN);
        } else {
            setMessage("Done. " + a.length + " numbers were checked.", SUCCESS);
        }
    }

    private void randomArray()
    {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (i > 0) sb.append(", ");
            sb.append(rnd.nextInt(99) + 1);
        }
        input.setText(sb.toString());
        find();
    }

    private void clearAll()
    {
        input.setText("");
        input.putClientProperty("invalid", false);
        input.setBorder(fieldBorder(PRIMARY));
        resetResults();
        input.requestFocusInWindow();
    }

    // =============================================================== helpers

    private void showError(String text)
    {
        resetResults();
        input.putClientProperty("invalid", true);
        input.setBorder(fieldBorder(DANGER));
        setMessage(text, DANGER);
    }

    private void resetResults()
    {
        for (JLabel l : new JLabel[] { bigValue, big2Value, smallValue, small2Value }) {
            l.setText("-");
            l.setForeground(MUTED);
        }
        chips.removeAll();
        chips.revalidate();
        chips.repaint();
        tableModel.setRowCount(0);
        setMessage(" ", MUTED);
    }

    private void showValue(JLabel l, Integer v, Color accent)
    {
        if (v == null) {
            l.setText("None");
            l.setForeground(MUTED);
        } else {
            l.setText(String.valueOf(v));
            l.setForeground(accent == SMALL2_BG ? new Color(0x4A6785) : (accent == BIG2_BG ? new Color(0x3E8E96) : accent));
        }
    }

    private void fillChips(int[] a, Integer[] r)
    {
        chips.removeAll();
        for (int n : a) {
            Color bg = PLAIN_BG;
            Color fg = TEXT;
            if (r[0] != null && n == r[0]) { bg = BIG_BG; fg = Color.WHITE; }
            else if (r[2] != null && n == r[2]) { bg = SMALL_BG; fg = Color.WHITE; }
            else if (r[1] != null && n == r[1]) { bg = BIG2_BG; }
            else if (r[3] != null && n == r[3]) { bg = SMALL2_BG; }
            chips.add(new Chip(String.valueOf(n), bg, fg));
        }
        chips.revalidate();
        chips.repaint();
    }

    private void fillSteps(int[] a)
    {
        tableModel.setRowCount(0);
        List<Integer[]> rows = ArrayStats.states(a);
        for (int i = 0; i < rows.size(); i++) {
            Integer[] s = rows.get(i);
            tableModel.addRow(new Object[] { i + 1, a[i], text(s[0]), text(s[1]), text(s[2]), text(s[3]) });
        }
        SwingUtilities.invokeLater(() -> table.scrollRectToVisible(new Rectangle(0, 0, 1, 1)));
    }

    private static String text(Integer v) { return v == null ? "-" : String.valueOf(v); }

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

    private static JLabel valueLabel()
    {
        JLabel l = new JLabel("-");
        l.setFont(font(Font.BOLD, 34));
        l.setForeground(MUTED);
        return l;
    }

    private JComponent legendItem(String text, Color c)
    {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.PLAIN, 11));
        l.setForeground(MUTED);
        l.setIcon(new Dot(c));
        l.setIconTextGap(5);
        return l;
    }

    /** Small coloured circle used in the cards and the legend. */
    private static class Dot implements Icon
    {
        private final Color color;

        Dot(Color color) { this.color = color; }

        public int getIconWidth()  { return 10; }
        public int getIconHeight() { return 10; }

        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y, 10, 10);
            g2.dispose();
        }
    }

    /** One number of the array drawn as a rounded box. */
    private static class Chip extends JComponent
    {
        private final String text;
        private final Color bg;
        private final Color fg;

        Chip(String text, Color bg, Color fg)
        {
            this.text = text;
            this.bg = bg;
            this.fg = fg;
            setFont(font(Font.BOLD, 14));
            FontMetrics fm = getFontMetrics(getFont());
            Dimension size = new Dimension(Math.max(44, fm.stringWidth(text) + 28), 34);
            setPreferredSize(size);
            setMinimumSize(size);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(fg);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                          (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}