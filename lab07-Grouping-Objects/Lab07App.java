package lab07;

import static lab07.Ui.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * Lab07App - one window for all the tasks of Lab 07.
 * Run this class. Use the menu on the left to switch between the tasks.
 */
public class Lab07App extends JFrame
{
    private static final Color SIDEBAR_ACTIVE = new Color(0x1D5C84);
    private static final Color SIDEBAR_HOVER  = new Color(0x174B6E);
    private static final Color SIDEBAR_TEXT   = new Color(0xC5D5E4);

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel pageTitle = new JLabel(" ");
    private final JLabel pageSub = new JLabel(" ");

    private final JPanel[] pages = { new ArrayStatsPanel(), new MatrixPanel(), new EqualityPanel() };
    private final String[] menu = { "Second largest and smallest", "Matrix multiplication", "Array equality" };
    private final NavButton[] navs = new NavButton[3];

    public Lab07App()
    {
        super("Lab 07 - Grouping Objects");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        // ---- page header and the task screens
        pageTitle.setFont(font(Font.BOLD, 22));
        pageTitle.setForeground(TEXT);
        pageSub.setFont(font(Font.PLAIN, 13));
        pageSub.setForeground(MUTED);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        titles.add(pageTitle);
        titles.add(pageSub);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new javax.swing.border.CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER), new EmptyBorder(16, 28, 16, 28)));
        header.add(titles, BorderLayout.WEST);

        for (int i = 0; i < pages.length; i++) {
            content.add(pages[i], "task" + i);
        }
        JPanel right = new JPanel(new BorderLayout());
        right.add(header, BorderLayout.NORTH);
        right.add(content, BorderLayout.CENTER);
        add(right, BorderLayout.CENTER);

        setSize(1260, 880);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);
        show(0);
    }

    private JComponent buildSidebar()
    {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(HEADER);
        p.setBorder(new EmptyBorder(26, 16, 20, 16));
        p.setPreferredSize(new Dimension(250, 0));

        JLabel brand = new JLabel("Lab 07");
        brand.setFont(font(Font.BOLD, 26));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tag = new JLabel("Grouping Objects");
        tag.setFont(font(Font.PLAIN, 13));
        tag.setForeground(new Color(0x9FB3C8));
        tag.setAlignmentX(Component.LEFT_ALIGNMENT);
        tag.setBorder(new EmptyBorder(2, 0, 0, 0));
        p.add(brand);
        p.add(tag);

        JPanel accent = new JPanel();
        accent.setBackground(PRIMARY);
        accent.setMaximumSize(new Dimension(48, 4));
        accent.setPreferredSize(new Dimension(48, 4));
        accent.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Box.createVerticalStrut(16));
        p.add(accent);
        p.add(Box.createVerticalStrut(26));

        JLabel section = new JLabel("TASKS");
        section.setFont(font(Font.BOLD, 11));
        section.setForeground(new Color(0x9FB3C8));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setBorder(new EmptyBorder(0, 6, 8, 0));
        p.add(section);

        for (int i = 0; i < navs.length; i++) {
            final int index = i;
            navs[i] = new NavButton((i + 1) + "   " + menu[i]);
            navs[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            navs[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            navs[i].addActionListener(e -> show(index));
            p.add(navs[i]);
            p.add(Box.createVerticalStrut(6));
        }

        p.add(Box.createVerticalGlue());
        JLabel foot = new JLabel("Software Construction Lab");
        foot.setFont(font(Font.PLAIN, 11));
        foot.setForeground(new Color(0x9FB3C8));
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(foot);
        return p;
    }

    /** Shows one task screen and updates the page header and the menu. */
    private void show(int index)
    {
        TaskPanel t = (TaskPanel) pages[index];
        pageTitle.setText(t.title());
        pageSub.setText(t.subtitle());
        cards.show(content, "task" + index);
        for (int i = 0; i < navs.length; i++) {
            navs[i].setSelected(i == index);
        }
    }

    /** Menu button on the sidebar. */
    private static class NavButton extends JButton
    {
        private boolean active = false;
        private boolean over = false;

        NavButton(String text)
        {
            super(text);
            setFont(font(Font.BOLD, 13));
            setForeground(SIDEBAR_TEXT);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(12, 18, 12, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { over = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { over = false; repaint(); }
            });
        }

        void setActive(boolean a)
        {
            active = a;
            setForeground(a ? Color.WHITE : SIDEBAR_TEXT);
            repaint();
        }

        @Override
        public void setSelected(boolean b)
        {
            setActive(b);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active || over) {
                g2.setColor(active ? SIDEBAR_ACTIVE : SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            if (active) {
                g2.setColor(PRIMARY);
                g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
            }
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
        SwingUtilities.invokeLater(() -> new Lab07App().setVisible(true));
    }
}