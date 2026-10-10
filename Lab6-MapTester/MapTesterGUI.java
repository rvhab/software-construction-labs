package lab06;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public class MapTesterGUI extends JFrame
{
    //colours
    private static final Color HEADER   = new Color(0x0F3D5E);
    private static final Color PRIMARY  = new Color(0x0E7C86);
    private static final Color BG       = new Color(0xF1F5F9);
    private static final Color BORDER   = new Color(0xD9E2EC);
    private static final Color TEXT     = new Color(0x102A43);
    private static final Color MUTED    = new Color(0x627D98);
    private static final Color DANGER   = new Color(0xC0392B);
    private static final Color SUCCESS  = new Color(0x1E8E5A);
    private static final Color ZEBRA    = new Color(0xF7FAFC);
    private static final Color SELECT   = new Color(0xD6EEF0);
    private static final Color HEAD_BG  = new Color(0xE8EEF4);
    private static final Color SOFT_BTN = new Color(0xE8EEF4);

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int DELETE_COL = 2;

    private enum Kind { PRIMARY, SECONDARY, DANGER }

    private Maptester book = new Maptester();

    private JTextField nameField = new JTextField();
    private JTextField numberField = new JTextField();
    private JLabel nameError = new JLabel(" ");
    private JLabel numberError = new JLabel(" ");
    private JLabel status = new JLabel(" ");
    private JTextField searchField = new JTextField(14);
    private JLabel countLabel = new JLabel("0 contacts");
    private JTextArea log = new JTextArea();
    private DefaultTableModel tableModel = new DefaultTableModel(new String[] { "Name", "Phone number", "" }, 0)
    {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private boolean quiet = false;   // true while the program (not the user) changes the selection
    private int hoverRow = -1;       // row whose bin icon the mouse is over

    public MapTesterGUI()
    {
        super("Phone Book");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(18, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.weighty = 1;
        g.gridx = 0; g.weightx = 0;
        JComponent left = buildLeft();
        left.setPreferredSize(new Dimension(390, 0));
        body.add(left, g);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(0, 18, 0, 0);
        body.add(buildRight(), g);
        add(body, BorderLayout.CENTER);

        setSize(1060, 760);
        setMinimumSize(new Dimension(960, 680));
        setLocationRelativeTo(null);
        updateCount();
    }

    //layout

    private JComponent buildHeader()
    {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(HEADER);
        bar.setBorder(new EmptyBorder(16, 28, 16, 28));

        JLabel title = new JLabel("Phone Book");
        title.setFont(font(Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Contact manager  |  names are keys, numbers are values");
        sub.setFont(font(Font.PLAIN, 12));
        sub.setForeground(new Color(0x9FB3C8));
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);
        bar.add(text, BorderLayout.WEST);

        JPanel accent = new JPanel();
        accent.setBackground(PRIMARY);
        accent.setPreferredSize(new Dimension(0, 4));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(bar, BorderLayout.CENTER);
        wrap.add(accent, BorderLayout.SOUTH);
        return wrap;
    }

    private JComponent buildLeft()
    {
        JPanel col = new JPanel(new BorderLayout(0, 18));
        col.setOpaque(false);
        col.add(buildFormCard(), BorderLayout.NORTH);
        col.add(buildLogCard(), BorderLayout.CENTER);
        return col;
    }

    private JComponent buildFormCard()
    {
        Card card = new Card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        int y = 0;

        g.gridy = y++; g.insets = new Insets(0, 0, 14, 0);
        card.add(sectionTitle("Contact details"), g);

        g.gridy = y++; g.insets = new Insets(0, 0, 5, 0);
        card.add(fieldLabel("Name"), g);
        setupField(nameField, nameError);
        g.gridy = y++; g.insets = new Insets(0, 0, 0, 0);
        card.add(nameField, g);
        g.gridy = y++; g.insets = new Insets(3, 2, 8, 0);
        styleError(nameError);
        card.add(nameError, g);

        g.gridy = y++; g.insets = new Insets(0, 0, 5, 0);
        card.add(fieldLabel("Phone number"), g);
        setupField(numberField, numberError);
        g.gridy = y++; g.insets = new Insets(0, 0, 0, 0);
        card.add(numberField, g);
        g.gridy = y++; g.insets = new Insets(3, 2, 14, 0);
        styleError(numberError);
        card.add(numberError, g);

        RoundButton save = new RoundButton("Save", Kind.PRIMARY);
        RoundButton find = new RoundButton("Look up", Kind.SECONDARY);
        RoundButton clear = new RoundButton("Clear", Kind.SECONDARY);
        save.addActionListener(e -> save());
        find.addActionListener(e -> lookup());
        clear.addActionListener(e -> clearForm());
        numberField.addActionListener(e -> save());

        // all three buttons in one row, so the activity log gets more room
        JPanel row1 = new JPanel(new GridLayout(1, 3, 8, 0));
        row1.setOpaque(false);
        row1.add(save);
        row1.add(find);
        row1.add(clear);
        g.gridy = y++; g.insets = new Insets(0, 0, 10, 0);
        card.add(row1, g);

        status.setFont(font(Font.BOLD, 12));
        g.gridy = y++; g.insets = new Insets(2, 2, 0, 0);
        card.add(status, g);
        return card;
    }

    private JComponent buildLogCard()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 10));

        RoundButton clearLog = new RoundButton("Clear", Kind.SECONDARY);
        clearLog.setBorder(new EmptyBorder(4, 12, 4, 12));
        clearLog.setFont(font(Font.BOLD, 11));
        clearLog.addActionListener(e -> log.setText(""));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(sectionTitle("Activity log"), BorderLayout.WEST);
        head.add(clearLog, BorderLayout.EAST);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(font(Font.PLAIN, 12));
        log.setForeground(TEXT);
        log.setBackground(ZEBRA);
        log.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(new LineBorder(BORDER));
        scroll.setPreferredSize(new Dimension(0, 190));

        card.add(head, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildRight()
    {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 12));

        // ---- top: title and search
        searchField.setFont(font(Font.PLAIN, 13));
        searchField.setBorder(fieldBorder(BORDER));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { applyFilter(); }
            public void removeUpdate(DocumentEvent e)  { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        search.add(fieldLabel("Search"), BorderLayout.WEST);
        search.add(searchField, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(sectionTitle("Contacts"), BorderLayout.WEST);
        top.add(search, BorderLayout.EAST);

        //table
        table = new JTable(tableModel)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                if (getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(MUTED);
                    g2.setFont(font(Font.PLAIN, 14));
                    String s = tableModel.getRowCount() == 0
                            ? "No contacts yet. Add one using the form."
                            : "No contacts match your search.";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(s, (getWidth() - fm.stringWidth(s)) / 2, 70);
                    g2.dispose();
                }
            }
        };
        sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        sorter.setSortable(DELETE_COL, false);
        table.setRowSorter(sorter);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(font(Font.PLAIN, 14));
        table.setForeground(TEXT);
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        TableColumn del = table.getColumnModel().getColumn(DELETE_COL);
        del.setMinWidth(64);
        del.setMaxWidth(64);
        del.setPreferredWidth(64);
        del.setResizable(false);

        DefaultTableCellRenderer cells = new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean focus, int row, int col)
            {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, row, col);
                l.setOpaque(true);
                l.setBackground(sel ? SELECT : (row % 2 == 0 ? Color.WHITE : ZEBRA));
                l.setForeground(TEXT);
                l.setFont(font(col == 0 ? Font.BOLD : Font.PLAIN, 14));
                if (col == DELETE_COL) {
                    l.setText("");
                    l.setIcon(new TrashIcon(row == hoverRow ? DANGER : MUTED));
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                    l.setBorder(new EmptyBorder(0, 0, 0, 0));
                    l.setToolTipText("Delete this contact");
                } else {
                    l.setIcon(null);
                    l.setHorizontalAlignment(SwingConstants.LEADING);
                    l.setBorder(new EmptyBorder(0, 14, 0, 14));
                    l.setToolTipText(null);
                }
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
                l.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, BORDER),
                                               new EmptyBorder(0, 14, 0, 14)));
                return l;
            }
        };
        table.getTableHeader().setDefaultRenderer(heads);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setReorderingAllowed(false);

        // clicking a row fills the form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || quiet) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            int m = table.convertRowIndexToModel(row);
            nameField.setText((String) tableModel.getValueAt(m, 0));
            numberField.setText((String) tableModel.getValueAt(m, 1));
            clearErrors();
        });

        // bin icon: hover effect and click to delete
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                int now = (row >= 0 && col == DELETE_COL) ? row : -1;
                if (now != hoverRow) {
                    hoverRow = now;
                    table.repaint();
                }
                table.setCursor(Cursor.getPredefinedCursor(now >= 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e)
            {
                hoverRow = -1;
                table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                table.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == DELETE_COL) {
                    int m = table.convertRowIndexToModel(row);
                    deleteContact((String) tableModel.getValueAt(m, 0));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        countLabel.setFont(font(Font.BOLD, 12));
        countLabel.setForeground(MUTED);

        card.add(top, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(countLabel, BorderLayout.SOUTH);
        return card;
    }
    // actions
    private void save()
    {
        clearErrors();
        String nameErr = Maptester.validateName(nameField.getText());
        String numErr = Maptester.validateNumber(numberField.getText());
        showError(nameField, nameError, nameErr);
        showError(numberField, numberError, numErr);
        if (nameErr != null || numErr != null) {
            setStatus("Please fix the highlighted fields.", true);
            return;
        }

        String name = Maptester.cleanName(nameField.getText());
        String number = Maptester.cleanNumber(numberField.getText());

        List<String> others = book.getNamesWithNumber(number);
        others.remove(name);

        String old = book.enterNumber(name, number);
        if (old == null) {
            addLog("Saved " + name + " -> " + number + ".");
            setStatus("Saved " + name + ".", false);
        } else if (old.equals(number)) {
            addLog("SAME KEY and SAME VALUE: " + name + " -> " + number + " was saved again. "
                 + "The HashMap still holds only one entry for " + name + ".");
            setStatus("Saved " + name + " again.", false);
        } else {
            addLog("SAME KEY: " + name + " already existed, so the old number " + old
                 + " was replaced by " + number + ". There is still only one " + name + " entry.");
            setStatus("Updated " + name + ".", false);
        }
        if (!others.isEmpty()) {
            addLog("SAME VALUE: the number " + number + " is now used by " + name + " and "
                 + String.join(", ", others) + ". Both entries are kept.");
        }

        refreshTable();
        clearForm();
        selectContact(name, false);
    }
    private void lookup()
    {
        clearErrors();
        String nameErr = Maptester.validateName(nameField.getText());
        if (nameErr != null) {
            showError(nameField, nameError, "Type the name to look up.");
            setStatus("Type a name to look up.", true);
            return;
        }
        String name = Maptester.cleanName(nameField.getText());
        String number = book.lookupNumber(name);
        if (number == null) {
            numberField.setText("");
            table.clearSelection();
            addLog("Look up " + name + ": not found (get() returned null).");
            setStatus("No contact named " + name + ".", true);
        } else {
            addLog("Look up " + name + ": found " + number + ".");
            setStatus("Found " + name + ".", false);
            selectContact(name, true);
        }
    }

    private void deleteContact(String name)
    {
        String number = book.lookupNumber(name);
        if (number == null) return;
        boolean yes = confirm("Delete contact",
                "Delete <b>" + name + "</b> (" + number + ") from the phone book?",
                "Delete", Kind.DANGER);
        if (!yes) {
            setStatus("Cancelled. Nothing was deleted.", false);
            return;
        }
        book.removeEntry(name);
        addLog("Deleted " + name + " (" + number + ").");
        refreshTable();
        clearForm();
        setStatus("Deleted " + name + ".", false);
    }

    private void clearForm()
    {
        quiet = true;
        nameField.setText("");
        numberField.setText("");
        table.clearSelection();
        quiet = false;
        clearErrors();
        nameField.requestFocusInWindow();
    }

    //helpers

    private void refreshTable()
    {
        quiet = true;
        tableModel.setRowCount(0);
        for (Map.Entry<String, String> e : book.getEntries().entrySet()) {
            tableModel.addRow(new Object[] { e.getKey(), e.getValue(), "" });
        }
        quiet = false;
        updateCount();
    }

    private void selectContact(String name, boolean fill)
    {
        quiet = true;
        searchField.setText("");
        sorter.setRowFilter(null);
        quiet = !fill;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (name.equals(tableModel.getValueAt(i, 0))) {
                int v = table.convertRowIndexToView(i);
                table.setRowSelectionInterval(v, v);
                table.scrollRectToVisible(table.getCellRect(v, 0, true));
                break;
            }
        }
        quiet = false;
    }

    private void applyFilter()
    {
        String t = searchField.getText().trim();
        sorter.setRowFilter(t.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(t), 0, 1));
        updateCount();
    }

    private void updateCount()
    {
        int total = book.size();
        int shown = table.getRowCount();
        if (total == 0) {
            countLabel.setText("0 contacts");
        } else if (shown == total) {
            countLabel.setText(total + (total == 1 ? " contact" : " contacts"));
        } else {
            countLabel.setText("Showing " + shown + " of " + total + " contacts");
        }
    }

    private void addLog(String text)
    {
        log.append("[" + LocalTime.now().format(TIME) + "]  " + text + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private void setStatus(String text, boolean error)
    {
        status.setForeground(error ? DANGER : SUCCESS);
        status.setText(text);
    }

    private void clearErrors()
    {
        showError(nameField, nameError, null);
        showError(numberField, numberError, null);
    }
    private void showError(JTextField f, JLabel label, String msg)
    {
        boolean bad = msg != null;
        f.putClientProperty("invalid", bad);
        label.setText(bad ? msg : " ");
        f.setBorder(fieldBorder(bad ? DANGER : (f.hasFocus() ? PRIMARY : BORDER)));
    }

    private boolean confirm(String title, String htmlMessage, String yesText, Kind yesKind)
    {
        JDialog d = new JDialog(this, title, true);
        final boolean[] result = { false };

        JLabel head = new JLabel(title);
        head.setFont(font(Font.BOLD, 17));
        head.setForeground(TEXT);
        JLabel msg = new JLabel("<html><body style='width:300px'>" + htmlMessage + "</body></html>");
        msg.setFont(font(Font.PLAIN, 14));
        msg.setForeground(TEXT);

        RoundButton yes = new RoundButton(yesText, yesKind);
        RoundButton no = new RoundButton("Cancel", Kind.SECONDARY);
        yes.addActionListener(e -> { result[0] = true; d.dispose(); });
        no.addActionListener(e -> d.dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(no);
        buttons.add(yes);

        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(24, 28, 20, 28));
        p.add(head, BorderLayout.NORTH);
        p.add(msg, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);

        d.setContentPane(p);
        d.setResizable(false);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        return result[0];
    }

    //styling

    private static Font font(int style, int size) { return new Font("Segoe UI", style, size); }

    private static Border fieldBorder(Color c)
    {
        return new CompoundBorder(new LineBorder(c, 1, true), new EmptyBorder(9, 12, 9, 12));
    }

    private static JLabel sectionTitle(String text)
    {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 16));
        l.setForeground(TEXT);
        return l;
    }

    private static JLabel fieldLabel(String text)
    {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 12));
        l.setForeground(MUTED);
        return l;
    }

    private static void styleError(JLabel l)
    {
        l.setFont(font(Font.PLAIN, 11));
        l.setForeground(DANGER);
    }
    private void setupField(JTextField f, JLabel err)
    {
        f.setFont(font(Font.PLAIN, 14));
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
        // typing removes the red error for that field
        f.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                if (Boolean.TRUE.equals(f.getClientProperty("invalid"))) showError(f, err, null);
            }
        });
    }
    private static class TrashIcon implements Icon
    {
        private final Color color;

        TrashIcon(Color color) { this.color = color; }

        public int getIconWidth()  { return 18; }
        public int getIconHeight() { return 18; }

        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(3, 5, 15, 5);                                    // lid
            g2.drawLine(7, 5, 7, 3);                                     // handle
            g2.drawLine(7, 3, 11, 3);
            g2.drawLine(11, 3, 11, 5);
            g2.drawLine(4, 5, 5, 15);                                    // body
            g2.drawLine(5, 15, 13, 15);
            g2.drawLine(13, 15, 14, 5);
            g2.drawLine(7, 8, 7, 12);                                    // lines inside
            g2.drawLine(11, 8, 11, 12);
            g2.dispose();
        }
    }
    private static class Card extends JPanel
    {
        Card()
        {
            setOpaque(false);
            setBorder(new EmptyBorder(18, 20, 18, 20));
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    private static class RoundButton extends JButton
    {
        private final Kind kind;
        private boolean over = false;

        RoundButton(String text, Kind kind)
        {
            super(text);
            this.kind = kind;
            setFont(font(Font.BOLD, 13));
            setForeground(kind == Kind.SECONDARY ? TEXT : Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 16, 10, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { over = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { over = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = kind == Kind.PRIMARY ? PRIMARY : (kind == Kind.DANGER ? DANGER : SOFT_BTN);
            if (over) {
                base = (kind == Kind.SECONDARY) ? new Color(0xD5DFEA) : base.darker();
            }
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // keep default look and feel
        }
        SwingUtilities.invokeLater(() -> new MapTesterGUI().setVisible(true));
    }
}