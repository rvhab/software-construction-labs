package lab07;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Colours, fonts and small styled components shared by all the task screens. */
final class Ui
{
    private Ui() { }

    static final Color HEADER   = new Color(0x0F3D5E);
    static final Color PRIMARY  = new Color(0x0E7C86);
    static final Color BG       = new Color(0xF1F5F9);
    static final Color BORDER   = new Color(0xD9E2EC);
    static final Color TEXT     = new Color(0x102A43);
    static final Color MUTED    = new Color(0x627D98);
    static final Color DANGER   = new Color(0xC0392B);
    static final Color SUCCESS  = new Color(0x1E8E5A);
    static final Color WARN     = new Color(0xB7791F);
    static final Color ZEBRA    = new Color(0xF7FAFC);
    static final Color HEAD_BG  = new Color(0xE8EEF4);
    static final Color SOFT_BTN = new Color(0xE8EEF4);
    static final Color PLAIN_BG = new Color(0xEDF2F7);
    static final Color HILITE   = new Color(0xD6EEF0);

    enum Kind { PRIMARY, SECONDARY }

    static Font font(int style, int size)
    {
        return new Font("Segoe UI", style, size);
    }

    static JLabel sectionTitle(String text)
    {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 16));
        l.setForeground(TEXT);
        return l;
    }

    /** Page body that follows the width of the window and scrolls up and down when the window is short. */
    static class ScrollBody extends JPanel implements Scrollable
    {
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }

        public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) { return 20; }

        public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) { return 80; }

        public boolean getScrollableTracksViewportWidth() { return true; }

        public boolean getScrollableTracksViewportHeight()
        {
            // stretch to fill the window when there is spare room, otherwise scroll
            return getParent() instanceof JViewport && getParent().getHeight() > getPreferredSize().height;
        }
    }

    /** Wraps a page body in a scroll pane that never scrolls sideways. */
    static JScrollPane scrollPage(JPanel body)
    {
        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(20);
        return sp;
    }

    /** Plain text that wraps onto the next line instead of being cut off. */
    static JTextArea wrapText(int rows, int columns)
    {
        JTextArea t = new JTextArea(rows, columns);
        t.setEditable(false);
        t.setFocusable(false);
        t.setOpaque(false);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setBorder(null);
        t.setFont(font(Font.PLAIN, 13));
        t.setForeground(MUTED);
        return t;
    }

    /** White rounded panel with a thin border. */
    static class Card extends JPanel
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

    /** Flat rounded button with a hover colour. */
    static class RoundButton extends JButton
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
            setBorder(new EmptyBorder(11, 18, 11, 18));
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
            Color base = kind == Kind.PRIMARY ? PRIMARY : SOFT_BTN;
            if (over) {
                base = (kind == Kind.SECONDARY) ? new Color(0xD5DFEA) : base.darker();
            }
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}