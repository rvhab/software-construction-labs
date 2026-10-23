package lab9;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


public final class Main {

    private Main() { }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(Main::start);
    }

    private static void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            // the default look and feel is acceptable if this fails
        }

        final CardLayout layout = new CardLayout();
        final JPanel cards = new JPanel(layout);
        cards.setOpaque(false);
        cards.add(new Task1Panel(), "task1");
        cards.add(new Task2Panel(), "task2");

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(buildHeader(layout, cards), BorderLayout.NORTH);
        root.add(cards, BorderLayout.CENTER);

        JFrame frame = new JFrame("Lab 9 - Designing Classes I: Coupling and Cohesion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(root);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setMinimumSize(new Dimension(1000, 640));
        frame.setSize(Math.min(1240, screen.width - 40), Math.min(800, screen.height - 60));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel buildHeader(final CardLayout layout, final JPanel cards) {
        Theme.GradientPanel header = new Theme.GradientPanel(new Color(0x312E81), new Color(0x7C3AED));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(16, 28, 16, 28));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(Theme.label("Designing Classes - I", Font.BOLD, 24, Color.WHITE));
        titles.add(Box.createVerticalStrut(2));
        titles.add(Theme.label("Lab 9   |   Software Construction   |   Coupling and Cohesion in Java",
                Font.PLAIN, 13, new Color(0xC7D2FE)));
        header.add(titles, BorderLayout.WEST);

        final Theme.PillButton[] nav = new Theme.PillButton[2];
        nav[0] = new Theme.PillButton("Task 1   Case Study", Color.WHITE, Color.WHITE, Theme.PRIMARY_DARK, null);
        nav[1] = new Theme.PillButton("Task 2   Box Refactoring", Color.WHITE, Color.WHITE, Theme.PRIMARY_DARK, null);
        nav[0].addActionListener(e -> {
            layout.show(cards, "task1");
            styleNav(nav, 0);
        });
        nav[1].addActionListener(e -> {
            layout.show(cards, "task2");
            styleNav(nav, 1);
        });
        styleNav(nav, 0);

        JPanel navRow = new JPanel();
        navRow.setOpaque(false);
        navRow.setLayout(new BoxLayout(navRow, BoxLayout.X_AXIS));
        navRow.add(nav[0]);
        navRow.add(Box.createHorizontalStrut(10));
        navRow.add(nav[1]);

        JPanel navWrap = new JPanel(new GridBagLayout());
        navWrap.setOpaque(false);
        navWrap.add(navRow);
        header.add(navWrap, BorderLayout.EAST);
        return header;
    }

    private static void styleNav(Theme.PillButton[] buttons, int active) {
        for (int i = 0; i < buttons.length; i++) {
            if (i == active) {
                buttons[i].setColors(Color.WHITE, new Color(0xEEF0FF), Theme.PRIMARY_DARK, null);
            } else {
                buttons[i].setColors(new Color(255, 255, 255, 45), new Color(255, 255, 255, 80),
                        Color.WHITE, null);
            }
        }
    }
}
