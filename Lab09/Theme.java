package lab9;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.CardLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public final class Theme {

    private Theme() { }

    // ------------------------------------------------------------------ colours
    public static final Color BG = new Color(0xF3F4F8);
    public static final Color CARD = Color.WHITE;
    public static final Color BORDER = new Color(0xE3E6EF);
    public static final Color TEXT = new Color(0x1F2937);
    public static final Color MUTED = new Color(0x6B7280);
    public static final Color PRIMARY = new Color(0x4F46E5);
    public static final Color PRIMARY_DARK = new Color(0x3730A3);
    public static final Color DANGER = new Color(0xB91C1C);
    public static final Color DANGER_BG = new Color(0xFEE2E2);
    public static final Color SUCCESS = new Color(0x15803D);
    public static final Color SUCCESS_BG = new Color(0xDCFCE7);
    public static final Color CODE_BG = new Color(0x0F172A);
    public static final Color CODE_FG = new Color(0xE2E8F0);

    // -------------------------------------------------------------------- fonts
    private static final Set<String> FAMILIES = new HashSet<String>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

    private static String pick(String fallback, String... names) {
        for (String n : names) {
            if (FAMILIES.contains(n)) {
                return n;
            }
        }
        return fallback;
    }

    public static final String UI_FAMILY = pick(Font.SANS_SERIF,
            "Segoe UI", "SF Pro Text", "Helvetica Neue", "Ubuntu", "Noto Sans", "Arial");
    public static final String MONO_FAMILY = pick(Font.MONOSPACED,
            "Consolas", "JetBrains Mono", "Menlo", "DejaVu Sans Mono", "Courier New");

    public static Font ui(int style, int size) {
        return new Font(UI_FAMILY, style, size);
    }

    public static Font mono(int size) {
        return new Font(MONO_FAMILY, Font.PLAIN, size);
    }

    public static String lines(String... lines) {
        return String.join("\n", lines);
    }

    // ------------------------------------------------------------ small helpers
    public static JLabel label(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(ui(style, size));
        l.setForeground(color);
        return l;
    }

    public static Component gap(int height) {
        JComponent s = (JComponent) Box.createVerticalStrut(height);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        return s;
    }

    public static Component glue() {
        JComponent g = (JComponent) Box.createVerticalGlue();
        g.setAlignmentX(Component.LEFT_ALIGNMENT);
        return g;
    }

    private static void antialias(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    // =============================================================== components

    /** White (or coloured) card with rounded corners, optional border and accent bar. */
    @SuppressWarnings("serial")
    public static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;
        private final Color stroke;
        private Color accent;

        public RoundedPanel(int arc, Color fill, Color stroke) {
            this.arc = arc;
            this.fill = fill;
            this.stroke = stroke;
            setOpaque(false);
        }

        public RoundedPanel accent(Color color) {
            this.accent = color;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth() - 1;
            int h = getHeight() - 1;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, arc, arc);
            if (accent != null) {
                g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
                g2.setColor(accent);
                g2.fillRect(0, 0, 6, getHeight());
                g2.setClip(null);
            }
            if (stroke != null) {
                g2.setColor(stroke);
                g2.drawRoundRect(0, 0, w, h, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Header background: diagonal gradient with two soft decorative circles. */
    @SuppressWarnings("serial")
    public static class GradientPanel extends JPanel {
        private final Color from;
        private final Color to;

        public GradientPanel(Color from, Color to) {
            this.from = from;
            this.to = to;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            g2.setPaint(new GradientPaint(0, 0, from, getWidth(), getHeight(), to));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillOval(getWidth() - 260, -120, 340, 340);
            g2.fillOval(getWidth() - 140, 30, 200, 200);
            g2.dispose();
        }
    }

    /** Fully rounded button used for actions and tabs. */
    @SuppressWarnings("serial")
    public static class PillButton extends JButton {
        private Color bg;
        private Color hoverBg;
        private Color outline;
        private boolean hovering;
        private boolean pressing;

        public PillButton(String text, Color bg, Color hoverBg, Color fg, Color outline) {
            super(text);
            this.bg = bg;
            this.hoverBg = hoverBg;
            this.outline = outline;
            setForeground(fg);
            setFont(ui(Font.BOLD, 13));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(9, 20, 9, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovering = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    pressing = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressing = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressing = false;
                    repaint();
                }
            });
        }

        public void setColors(Color bg, Color hoverBg, Color fg, Color outline) {
            this.bg = bg;
            this.hoverBg = hoverBg;
            this.outline = outline;
            setForeground(fg);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            Color c = pressing ? hoverBg.darker() : (hovering ? hoverBg : bg);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            if (outline != null) {
                g2.setColor(outline);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static PillButton primary(String text) {
        PillButton b = new PillButton(text, PRIMARY, PRIMARY_DARK, Color.WHITE, null);
        b.setPreferredSize(new Dimension(160, 40));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    public static PillButton ghost(String text) {
        PillButton b = new PillButton(text, Color.WHITE, new Color(0xEEF0FF), PRIMARY_DARK, BORDER);
        b.setPreferredSize(new Dimension(160, 40));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    /** Row of pill tabs that switches the cards of a CardLayout. Card names must equal tab names. */
    public static JPanel tabBar(final CardLayout layout, final JPanel cards, final String... names) {
        JPanel bar = new JPanel();
        bar.setOpaque(false);
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        final PillButton[] buttons = new PillButton[names.length];
        for (int i = 0; i < names.length; i++) {
            final int index = i;
            buttons[i] = new PillButton(names[i], CARD, new Color(0xEEF0FF), MUTED, BORDER);
            buttons[i].addActionListener(e -> {
                layout.show(cards, names[index]);
                highlight(buttons, index);
            });
            bar.add(buttons[i]);
            bar.add(Box.createHorizontalStrut(8));
        }
        bar.add(Box.createHorizontalGlue());
        highlight(buttons, 0);
        return bar;
    }

    private static void highlight(PillButton[] buttons, int active) {
        for (int i = 0; i < buttons.length; i++) {
            if (i == active) {
                buttons[i].setColors(PRIMARY, PRIMARY_DARK, Color.WHITE, null);
            } else {
                buttons[i].setColors(CARD, new Color(0xEEF0FF), MUTED, BORDER);
            }
        }
    }

    // --------------------------------------------------------------- inputs
    /** Rounded 1px border whose colour can change (used for the focus highlight). */
    @SuppressWarnings("serial")
    public static class RoundedBorder extends AbstractBorder {
        private Color color;
        private final int arc;

        public RoundedBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }

    public static JTextField field(String text) {
        final JTextField f = new JTextField(text);
        final RoundedBorder rb = new RoundedBorder(BORDER, 12);
        f.setFont(ui(Font.PLAIN, 14));
        f.setForeground(TEXT);
        f.setBackground(Color.WHITE);
        f.setCaretColor(PRIMARY);
        f.setSelectionColor(new Color(0xC7D2FE));
        f.setSelectedTextColor(TEXT);
        f.setBorder(new CompoundBorder(rb, new EmptyBorder(7, 10, 7, 10)));
        f.setPreferredSize(new Dimension(180, 38));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                rb.setColor(PRIMARY);
                f.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                rb.setColor(BORDER);
                f.repaint();
            }
        });
        return f;
    }

    private static class ComboUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            BasicArrowButton b = new BasicArrowButton(SwingConstants.SOUTH,
                    Color.WHITE, Color.WHITE, MUTED, Color.WHITE);
            b.setBorder(new EmptyBorder(0, 0, 0, 6));
            return b;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(Color.WHITE);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox);
            popup.setBorder(new LineBorder(BORDER));
            return popup;
        }
    }

    @SuppressWarnings("serial")
    public static JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<String>(items);
        c.setUI(new ComboUI());
        c.setFont(ui(Font.PLAIN, 14));
        c.setBackground(Color.WHITE);
        c.setForeground(TEXT);
        c.setBorder(new RoundedBorder(BORDER, 12));
        c.setPreferredSize(new Dimension(180, 38));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, false);
                setBorder(new EmptyBorder(7, 10, 7, 10));
                setFont(ui(Font.PLAIN, 14));
                setForeground(TEXT);
                setBackground(index >= 0 && isSelected ? new Color(0xE0E7FF) : Color.WHITE);
                return this;
            }
        });
        return c;
    }

    public static JLabel formLabel(String text) {
        JLabel l = label(text, Font.BOLD, 12, MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 2, 5, 0));
        return l;
    }

    public static JPanel labeled(JLabel label, JComponent input) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(label);
        p.add(input);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }

    public static JPanel labeled(String text, JComponent input) {
        return labeled(formLabel(text), input);
    }

    // ------------------------------------------------------------ scrolling
    private static class SlimScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(100, 116, 139, 160);
            trackColor = new Color(0, 0, 0, 0);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private JButton zeroButton() {
            JButton b = new JButton();
            Dimension d = new Dimension(0, 0);
            b.setPreferredSize(d);
            b.setMinimumSize(d);
            b.setMaximumSize(d);
            return b;
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(10, 10);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            // transparent track
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
            g2.dispose();
        }
    }

    public static JScrollPane scroll(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(null);
        sp.setViewportBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new SlimScrollBarUI());
        sp.getVerticalScrollBar().setOpaque(false);
        sp.getHorizontalScrollBar().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getHorizontalScrollBar().setUnitIncrement(18);
        return sp;
    }

    // ------------------------------------------------------------ text views
    /** Dark read-only console that colours lines by their prefix. */
    @SuppressWarnings("serial")
    public static class Console extends JTextPane {
        private static final SimpleAttributeSet NORMAL = attr(0xE2E8F0, false);
        private static final SimpleAttributeSet ERROR = attr(0xFCA5A5, false);
        private static final SimpleAttributeSet WARN = attr(0xFCD34D, false);
        private static final SimpleAttributeSet INFO = attr(0x7DD3FC, false);

        public Console() {
            setEditable(false);
            setOpaque(false);
            setFont(mono(13));
            setForeground(CODE_FG);
            setBorder(null);
            setSelectionColor(new Color(0x334155));
            setSelectedTextColor(Color.WHITE);
        }

        public void display(String text) {
            setText("");
            StyledDocument doc = getStyledDocument();
            try {
                for (String line : text.split("\n")) {
                    doc.insertString(doc.getLength(), line + "\n", styleFor(line));
                }
            } catch (BadLocationException e) {
                throw new IllegalStateException(e);
            }
            setCaretPosition(0);
        }

        private AttributeSet styleFor(String line) {
            if (line.startsWith("ERROR")) {
                return ERROR;
            }
            if (line.startsWith("WARNING")) {
                return WARN;
            }
            if (line.startsWith("> ")) {
                return INFO;
            }
            return NORMAL;
        }
    }

    private static SimpleAttributeSet attr(int rgb, boolean italic) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setForeground(a, new Color(rgb));
        StyleConstants.setItalic(a, italic);
        return a;
    }

    private static final SimpleAttributeSet C_PLAIN = attr(0xE2E8F0, false);
    private static final SimpleAttributeSet C_COMMENT = attr(0x64748B, true);
    private static final SimpleAttributeSet C_STRING = attr(0xFCD34D, false);
    private static final SimpleAttributeSet C_KEYWORD = attr(0xC4B5FD, false);
    private static final SimpleAttributeSet C_ANNOTATION = attr(0xF9A8D4, false);
    private static final SimpleAttributeSet C_TYPE = attr(0x67E8F9, false);
    private static final SimpleAttributeSet C_NUMBER = attr(0xFDBA74, false);

    private static final Pattern TOKEN = Pattern.compile(
            "(//[^\\n]*)"
            + "|(\"(?:[^\"\\\\\\n]|\\\\.)*\")"
            + "|(\\b(?:public|private|protected|static|final|class|interface|implements|extends|return|new|"
            + "if|else|switch|case|default|break|void|int|double|long|boolean|this|null|throw|import|package)\\b)"
            + "|(@\\w+)"
            + "|(\\b[A-Z][A-Za-z0-9_]*\\b)"
            + "|(\\b\\d+(?:\\.\\d+)?\\b)");

    /** Read-only code viewer with simple Java syntax colouring. */
    @SuppressWarnings("serial")
    public static JTextPane codePane(String code) {
        JTextPane pane = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return false;
            }
        };
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setFont(mono(13));
        pane.setBorder(null);
        pane.setSelectionColor(new Color(0x334155));
        pane.setSelectedTextColor(Color.WHITE);
        StyledDocument doc = pane.getStyledDocument();
        Matcher m = TOKEN.matcher(code);
        int pos = 0;
        try {
            while (m.find()) {
                if (m.start() > pos) {
                    doc.insertString(doc.getLength(), code.substring(pos, m.start()), C_PLAIN);
                }
                AttributeSet style;
                if (m.group(1) != null) {
                    style = C_COMMENT;
                } else if (m.group(2) != null) {
                    style = C_STRING;
                } else if (m.group(3) != null) {
                    style = C_KEYWORD;
                } else if (m.group(4) != null) {
                    style = C_ANNOTATION;
                } else if (m.group(5) != null) {
                    style = C_TYPE;
                } else {
                    style = C_NUMBER;
                }
                doc.insertString(doc.getLength(), m.group(), style);
                pos = m.end();
            }
            if (pos < code.length()) {
                doc.insertString(doc.getLength(), code.substring(pos), C_PLAIN);
            }
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
        pane.setCaretPosition(0);
        return pane;
    }

    // --------------------------------------------------------------- cards
    public static RoundedPanel badge(String text, Color fg, Color bg) {
        RoundedPanel b = new RoundedPanel(22, bg, null);
        b.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 3));
        b.add(label(text, Font.BOLD, 11, fg));
        return b;
    }

    public static RoundedPanel introCard(String title, String body, int height) {
        RoundedPanel card = new RoundedPanel(18, CARD, BORDER).accent(PRIMARY);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(14, 24, 12, 20));
        card.setPreferredSize(new Dimension(10, height));
        JTextArea text = new JTextArea(body);
        text.setEditable(false);
        text.setFocusable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setOpaque(false);
        text.setBorder(null);
        text.setFont(ui(Font.PLAIN, 13));
        text.setForeground(TEXT);
        card.add(label(title, Font.BOLD, 16, PRIMARY_DARK), BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    public static RoundedPanel formCard(String title, JPanel content, int width) {
        RoundedPanel card = new RoundedPanel(18, CARD, BORDER);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 16, 16, 8));
        card.setPreferredSize(new Dimension(width, 10));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 0, 0, 8));
        JScrollPane sp = scroll(content);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        card.add(label(title, Font.BOLD, 15, TEXT), BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    public static RoundedPanel outputCard(String title, String badgeText, Color badgeFg, Color badgeBg,
            Console console) {
        RoundedPanel card = new RoundedPanel(18, CARD, BORDER);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(14, 16, 16, 16));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(label(title, Font.BOLD, 14, TEXT), BorderLayout.WEST);
        head.add(badge(badgeText, badgeFg, badgeBg), BorderLayout.EAST);
        RoundedPanel screen = new RoundedPanel(12, CODE_BG, null);
        screen.setLayout(new BorderLayout());
        screen.setBorder(new EmptyBorder(10, 12, 10, 8));
        screen.add(scroll(console), BorderLayout.CENTER);
        card.add(head, BorderLayout.NORTH);
        card.add(screen, BorderLayout.CENTER);
        return card;
    }

    public static RoundedPanel codeCard(String fileName, String code, String badgeText, Color badgeFg,
            Color badgeBg) {
        RoundedPanel card = new RoundedPanel(18, CODE_BG, null);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(14, 18, 14, 10));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(0, 0, 0, 8));
        head.add(label(fileName, Font.BOLD, 12, new Color(0x94A3B8)), BorderLayout.WEST);
        head.add(badge(badgeText, badgeFg, badgeBg), BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);
        card.add(scroll(codePane(code)), BorderLayout.CENTER);
        return card;
    }

    public static RoundedPanel htmlCard(String html) {
        RoundedPanel card = new RoundedPanel(18, CARD, BORDER);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 14, 12, 6));
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        pane.setFont(ui(Font.PLAIN, 13));
        pane.setForeground(TEXT);
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setBorder(new EmptyBorder(4, 6, 4, 10));
        pane.setText(html);
        pane.setCaretPosition(0);
        JScrollPane sp = scroll(pane);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    /** Three-column HTML comparison table (aspect / bad / good) for the analysis tabs. */
    public static String compareTable(String h1, String h2, String h3, String[][] rows) {
        StringBuilder t = new StringBuilder();
        t.append("<table width='100%' cellpadding='9' cellspacing='3'>");
        t.append("<tr><td width='18%' bgcolor='#E0E7FF'><b>").append(h1).append("</b></td>");
        t.append("<td width='41%' bgcolor='#FECACA'><b>").append(h2).append("</b></td>");
        t.append("<td width='41%' bgcolor='#BBF7D0'><b>").append(h3).append("</b></td></tr>");
        for (String[] r : rows) {
            t.append("<tr><td bgcolor='#F3F4F6'><b>").append(r[0]).append("</b></td>");
            t.append("<td bgcolor='#FEF2F2'>").append(r[1]).append("</td>");
            t.append("<td bgcolor='#F0FDF4'>").append(r[2]).append("</td></tr>");
        }
        t.append("</table>");
        return t.toString();
    }
}
