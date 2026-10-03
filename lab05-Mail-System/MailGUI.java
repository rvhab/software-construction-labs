package lab05;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;

public class MailGUI
{
    /** The single server shared by every client. */
    static final MailServer SERVER = new MailServer();

    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // keep default look and feel
        }
        SwingUtilities.invokeLater(() -> new MailApp().setVisible(true));
    }
}

class Theme
{
    static final Color DARK      = new Color(0x111111);  // header / login panel
    static final Color SIDEBAR   = new Color(0x1C1C1C);  // sidebar
    static final Color ACTIVE    = new Color(0x3A3A3A);  // selected folder
    static final Color HIGHLIGHT = new Color(0x505050);  // "mail waiting" button
    static final Color ACCENT    = new Color(0x111111);  // primary buttons on light areas
    static final Color SOFT      = new Color(0xB8B8B8);  // light grey text on dark
    static final Color ONDARK    = new Color(0xEDEDED);
    static final Color TEXT      = new Color(0x1A1A1A);
    static final Color MUTED     = new Color(0x777777);
    static final Color LINE      = new Color(0xDDDDDD);
    static final Color BG        = new Color(0xF4F4F4);
    static final Color PAPER     = Color.WHITE;
    static final Color UNREAD    = new Color(0xF0F0F0);
    static final Color SELECT    = new Color(0xE2E2E2);
    static final Color ERROR     = new Color(0xB42318);

    static Font sans(int style, int size) { return new Font("Segoe UI", style, size); }

    static void styleField(JTextComponent c)
    {
        c.setBackground(PAPER);
        c.setForeground(TEXT);
        c.setCaretColor(TEXT);
        c.setFont(sans(Font.PLAIN, 14));
        c.setBorder(new CompoundBorder(new LineBorder(LINE, 1), new EmptyBorder(8, 10, 8, 10)));
    }
}

class VectorIcon implements Icon
{
    enum Kind { MAIL, COMPOSE, INBOX, SENT, RECEIVE, REPLY, DELETE, USERPLUS, SIGNOUT, ARROW, CLOSE, DRAFT, RESTORE }

    private Kind kind;
    private Color color;
    private int size;

    VectorIcon(Kind kind, Color color, int size)
    {
        this.kind = kind;
        this.color = color;
        this.size = size;
    }

    public int getIconWidth()  { return size; }
    public int getIconHeight() { return size; }

    private static java.awt.geom.Path2D path(double... p)
    {
        java.awt.geom.Path2D.Double d = new java.awt.geom.Path2D.Double();
        d.moveTo(p[0], p[1]);
        for (int i = 2; i < p.length; i += 2) d.lineTo(p[i], p[i + 1]);
        return d;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        g2.scale(size / 16.0, size / 16.0);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        boolean on = (c == null) || c.isEnabled();
        g2.setColor(on ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), 110));

        switch (kind) {
            case MAIL:
                g2.draw(new java.awt.geom.RoundRectangle2D.Double(1, 3, 14, 10, 2.5, 2.5));
                g2.draw(path(1.8, 4.6, 8, 9.2, 14.2, 4.6));
                break;
            case COMPOSE: {
                java.awt.geom.Path2D p = path(2, 14, 3, 10.5, 11.5, 2, 14, 4.5, 5.5, 13);
                p.closePath();
                g2.draw(p);
                g2.draw(path(9.5, 4, 12, 6.5));
                break;
            }
            case INBOX: {
                java.awt.geom.Path2D p = path(1.5, 9, 4, 2.5, 12, 2.5, 14.5, 9, 14.5, 13, 1.5, 13);
                p.closePath();
                g2.draw(p);
                g2.draw(path(1.5, 9, 5.5, 9, 6.5, 11, 9.5, 11, 10.5, 9, 14.5, 9));
                break;
            }
            case SENT: {
                java.awt.geom.Path2D p = path(14.5, 1.5, 9.5, 14.5, 6.5, 9.5, 1.5, 6.5);
                p.closePath();
                g2.draw(p);
                g2.draw(path(14.5, 1.5, 6.5, 9.5));
                break;
            }
            case RECEIVE:
                g2.draw(path(8, 2, 8, 10));
                g2.draw(path(4.5, 6.5, 8, 10, 11.5, 6.5));
                g2.draw(path(2.5, 13.5, 13.5, 13.5));
                break;
            case REPLY: {
                g2.draw(path(6, 3.5, 2.5, 7, 6, 10.5));
                java.awt.geom.Path2D p = new java.awt.geom.Path2D.Double();
                p.moveTo(2.5, 7);
                p.lineTo(9.5, 7);
                p.quadTo(13.5, 7, 13.5, 11);
                p.lineTo(13.5, 13);
                g2.draw(p);
                break;
            }
            case DELETE:
                g2.draw(path(2.5, 4, 13.5, 4));
                g2.draw(path(6, 4, 6, 2.5, 10, 2.5, 10, 4));
                g2.draw(path(3.5, 4, 4.5, 14, 11.5, 14, 12.5, 4));
                g2.draw(path(6.5, 7, 6.5, 11));
                g2.draw(path(9.5, 7, 9.5, 11));
                break;
            case USERPLUS: {
                g2.draw(new java.awt.geom.Ellipse2D.Double(4, 3, 5, 5));
                java.awt.geom.Path2D p = new java.awt.geom.Path2D.Double();
                p.moveTo(1.5, 14);
                p.curveTo(1.5, 9.5, 11.5, 9.5, 11.5, 14);
                g2.draw(p);
                g2.draw(path(13, 3.5, 13, 8.5));
                g2.draw(path(10.5, 6, 15.5, 6));
                break;
            }
            case SIGNOUT:
                g2.draw(path(6.5, 2.5, 2.5, 2.5, 2.5, 13.5, 6.5, 13.5));
                g2.draw(path(6, 8, 14, 8));
                g2.draw(path(11, 5, 14, 8, 11, 11));
                break;
            case ARROW:
                g2.draw(path(2.5, 8, 13.5, 8));
                g2.draw(path(9, 3.5, 13.5, 8, 9, 12.5));
                break;
            case DRAFT: {
                java.awt.geom.Path2D p = path(3.5, 1.5, 10, 1.5, 13, 4.5, 13, 14.5, 3.5, 14.5);
                p.closePath();
                g2.draw(p);
                g2.draw(path(10, 1.5, 10, 4.5, 13, 4.5));
                g2.draw(path(6, 8, 10.5, 8));
                g2.draw(path(6, 11, 10.5, 11));
                break;
            }
            case RESTORE: {
                g2.draw(path(3, 3, 3, 7.5, 7.5, 7.5));
                java.awt.geom.Path2D p = new java.awt.geom.Path2D.Double();
                p.moveTo(3.5, 7.2);
                p.curveTo(5, 4, 10.5, 3, 13, 7.5);
                p.curveTo(14.5, 10.5, 12, 13.5, 8.5, 13.5);
                g2.draw(p);
                break;
            }
            case CLOSE:
                g2.draw(path(3.5, 3.5, 12.5, 12.5));
                g2.draw(path(12.5, 3.5, 3.5, 12.5));
                break;
        }
        g2.dispose();
    }
}

@SuppressWarnings("serial")
class FlatButton extends JButton
{
    private Color base;
    private boolean over = false;

    FlatButton(String text, VectorIcon.Kind icon, Color base, Color fg)
    {
        super(text);
        this.base = base;
        setForeground(fg);
        setFont(Theme.sans(Font.BOLD, 13));
        if (icon != null) {
            setIcon(new VectorIcon(icon, fg, 16));
            setIconTextGap(10);
        }
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 14, 10, 14));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { over = true;  repaint(); }
            public void mouseExited(MouseEvent e)  { over = false; repaint(); }
        });
    }

    void setBase(Color c) { this.base = c; repaint(); }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = base;
        if (over && isEnabled()) {
            int avg = (base.getRed() + base.getGreen() + base.getBlue()) / 3;
            int d = (avg > 200) ? -14 : 18;
            c = new Color(Math.max(0, Math.min(255, base.getRed() + d)),
                          Math.max(0, Math.min(255, base.getGreen() + d)),
                          Math.max(0, Math.min(255, base.getBlue() + d)));
        }
        if (!isEnabled()) {
            g2.setComposite(AlphaComposite.SrcOver.derive(0.4f));
        }
        g2.setColor(c);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
        g2.dispose();
        super.paintComponent(g);
    }
}
@SuppressWarnings("serial")
class Avatar extends JComponent
{
    private String letter;

    Avatar(String name)
    {
        setUser(name);
        setPreferredSize(new Dimension(40, 40));
    }

    void setUser(String name)
    {
        this.letter = name.substring(0, 1).toUpperCase();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int d = 36;
        int x = (getWidth() - d) / 2;
        int y = (getHeight() - d) / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, d, d);
        g2.setColor(Theme.DARK);
        g2.setFont(Theme.sans(Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(letter, x + (d - fm.stringWidth(letter)) / 2,
                      y + (d - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
@SuppressWarnings("serial")
class LoginPanel extends JPanel
{
    private MailApp app;
    private JTextField nameField = new JTextField(18);
    private JLabel msg = new JLabel(" ");
    private FlatButton backBtn;

    LoginPanel(MailApp app)
    {
        this.app = app;
        setLayout(new GridLayout(1, 2));

        // ---- left brand panel
        JPanel brand = new JPanel(new GridBagLayout());
        brand.setBackground(Theme.DARK);
        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);
        JLabel logo = new JLabel(new VectorIcon(VectorIcon.Kind.MAIL, Color.WHITE, 44));
        JLabel title = new JLabel("Mail System");
        title.setFont(Theme.sans(Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        JLabel tag = new JLabel("Client / server messaging");
        tag.setFont(Theme.sans(Font.PLAIN, 14));
        tag.setForeground(Theme.SOFT);
        brandText.add(logo);
        brandText.add(Box.createVerticalStrut(14));
        brandText.add(title);
        brandText.add(Box.createVerticalStrut(6));
        brandText.add(tag);
        brand.add(brandText);

        // ---- right form panel
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.PAPER);
        form.setBorder(new EmptyBorder(0, 44, 0, 44));

        JLabel head = new JLabel("Sign in to your account");
        head.setFont(Theme.sans(Font.BOLD, 18));
        head.setForeground(Theme.TEXT);

        JLabel prompt = new JLabel("Username");
        prompt.setFont(Theme.sans(Font.BOLD, 12));
        prompt.setForeground(Theme.MUTED);
        Theme.styleField(nameField);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel hint = new JLabel("2-20 characters: letters, numbers or _");
        hint.setFont(Theme.sans(Font.PLAIN, 12));
        hint.setForeground(Theme.MUTED);

        FlatButton signIn = new FlatButton("Sign in", VectorIcon.Kind.ARROW, Theme.ACCENT, Color.WHITE);
        FlatButton register = new FlatButton("Register", VectorIcon.Kind.USERPLUS, Theme.SELECT, Theme.TEXT);
        signIn.setHorizontalAlignment(SwingConstants.CENTER);
        register.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.add(signIn);
        buttons.add(register);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        backBtn = new FlatButton("Back to mailbox", VectorIcon.Kind.INBOX, Theme.BG, Theme.TEXT);
        backBtn.setHorizontalAlignment(SwingConstants.CENTER);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        backBtn.setVisible(false);

        msg.setFont(Theme.sans(Font.PLAIN, 12));
        msg.setForeground(Theme.MUTED);

        Component[] parts = { head, Box.createVerticalStrut(22), prompt,
                Box.createVerticalStrut(6), nameField, Box.createVerticalStrut(6), hint,
                Box.createVerticalStrut(20), buttons, Box.createVerticalStrut(10), backBtn,
                Box.createVerticalStrut(14), msg };
        for (Component c : parts) {
            if (c instanceof JComponent) {
                ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            form.add(c);
        }
        JPanel formWrap = new JPanel(new GridBagLayout());
        formWrap.setBackground(Theme.PAPER);
        formWrap.add(form, new GridBagConstraints());
        form.setPreferredSize(new Dimension(390, 320));

        add(brand);
        add(formWrap);

        signIn.addActionListener(e -> signIn());
        register.addActionListener(e -> register());
        nameField.addActionListener(e -> signIn());
        backBtn.addActionListener(e -> app.showMailbox());
    }

    /** Called every time this screen is shown. */
    void reset(boolean hasAccounts)
    {
        nameField.setText("");
        msg.setText(" ");
        backBtn.setVisible(hasAccounts);
        nameField.requestFocusInWindow();
    }

    private void setMsg(String text, boolean error)
    {
        msg.setForeground(error ? Theme.ERROR : Theme.TEXT);
        msg.setText(text);
    }

    private String readName()
    {
        String name = nameField.getText().trim().toLowerCase();
        if (!name.matches("[a-z0-9_]{2,20}")) {
            setMsg("Invalid username. Use 2-20 letters, numbers or _.", true);
            return null;
        }
        return name;
    }

    private void register()
    {
        String name = readName();
        if (name == null) return;
        if (!MailGUI.SERVER.registerUser(name)) {
            setMsg("\"" + name + "\" is already registered. Use Sign in.", true);
            return;
        }
        app.openAccount(name, "Account created: " + name + ".");
    }

    private void signIn()
    {
        String name = readName();
        if (name == null) return;
        if (!MailGUI.SERVER.isRegistered(name)) {
            setMsg("No such user. Use Register to create it.", true);
            return;
        }
        app.openAccount(name, "Signed in as " + name + ".");
    }
}


@SuppressWarnings("serial")
class MailApp extends JFrame
{
    private static final String LOGIN = "login";
    private static final String MAIL  = "mail";

    private CardLayout cards = new CardLayout();
    private JPanel root = new JPanel(cards);
    private LoginPanel login;

    private Map<String, MailClient> clients = new LinkedHashMap<String, MailClient>();
    private MailClient client;              // the account currently on screen
    private Folder current = Folder.INBOX;

    private DefaultListModel<MailItem> model = new DefaultListModel<MailItem>();
    private JList<MailItem> list = new JList<MailItem>(model);
    private JLabel folderLabel = new JLabel();
    private JLabel badge = new JLabel(" ", SwingConstants.RIGHT);
    private JLabel status = new JLabel(" Ready");
    private JLabel rSubject = new JLabel(" ");
    private JLabel rFrom = new JLabel(" ");
    private JLabel rTime = new JLabel(" ");
    private JTextArea rBody = new JTextArea();
    private JLabel who = new JLabel(" ", SwingConstants.RIGHT);
    private Avatar avatar = new Avatar(" ");

    private JComboBox<String> accountBox = new JComboBox<String>();
    private boolean switching = false;      // true while we change the selector ourselves

    private FlatButton inboxBtn, draftsBtn, sentBtn, binBtn;
    private FlatButton replyBtn, editBtn, restoreBtn, deleteBtn, emptyBinBtn;
    private JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private javax.swing.Timer poller;

    MailApp()
    {
        super("Mail System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mailView = new JPanel(new BorderLayout());
        mailView.add(buildHeader(), BorderLayout.NORTH);
        mailView.add(buildSidebar(), BorderLayout.WEST);
        mailView.add(buildCenter(), BorderLayout.CENTER);
        mailView.add(buildStatusBar(), BorderLayout.SOUTH);

        login = new LoginPanel(this);
        root.add(login, LOGIN);
        root.add(mailView, MAIL);
        setContentPane(root);

        setSize(1020, 650);
        setMinimumSize(new Dimension(860, 540));
        setLocationRelativeTo(null);

        showLogin();

        // New mail is fetched from the server automatically, for every signed-in account.
        poller = new javax.swing.Timer(1000, e -> pollAll());
        poller.start();
    }

    void showLogin()
    {
        login.reset(!clients.isEmpty());
        setTitle("Mail System - Sign in");
        cards.show(root, LOGIN);
    }

    /** Goes back to the mailbox of the account that was open. */
    void showMailbox()
    {
        if (client == null) return;
        setTitle("Mail System - " + client.getUser());
        cards.show(root, MAIL);
    }

    /** Signs in an account inside this window and switches to it. */
    void openAccount(String name, String note)
    {
        if (!clients.containsKey(name)) {
            clients.put(name, new MailClient(MailGUI.SERVER, name));
        }
        refreshAccountBox();
        activate(name);
        cards.show(root, MAIL);
        setStatus(note);
    }

    /** Fills the account selector with every signed-in account. */
    private void refreshAccountBox()
    {
        switching = true;
        accountBox.removeAllItems();
        for (String n : clients.keySet()) {
            accountBox.addItem(n);
        }
        switching = false;
    }

    /** Makes the given account the one shown on screen. */
    private void activate(String name)
    {
        client = clients.get(name);
        current = Folder.INBOX;

        setTitle("Mail System - " + name);
        who.setText(name);
        avatar.setUser(name);

        switching = true;
        accountBox.setSelectedItem(name);
        switching = false;

        highlightFolders();
        emptyBinBtn.setVisible(false);
        list.clearSelection();
        checkNewMail();
        refreshList();
        setStatus("Switched to " + name + ".");
    }

    /** Removes the current account from this window; switches to another one if any remain. */
    private void signOut()
    {
        if (client == null) return;
        clients.remove(client.getUser());
        refreshAccountBox();
        if (clients.isEmpty()) {
            client = null;
            model.clear();
            showLogin();
        } else {
            activate(clients.keySet().iterator().next());
        }
    }

    /** Checks the server for every signed-in account. */
    private void pollAll()
    {
        for (MailClient c : clients.values()) {
            if (c == client) {
                checkNewMail();
            } else {
                List<MailItem> arrived = c.receiveNewMail();
                if (!arrived.isEmpty()) {
                    setStatus("New mail for " + c.getUser() + ".");
                    accountBox.repaint();
                }
            }
        }
    }


    private JPanel buildHeader()
    {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.DARK);
        p.setBorder(new EmptyBorder(12, 22, 12, 22));

        JLabel title = new JLabel("Mail System",
                new VectorIcon(VectorIcon.Kind.MAIL, Color.WHITE, 22), SwingConstants.LEFT);
        title.setIconTextGap(10);
        title.setFont(Theme.sans(Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        who.setFont(Theme.sans(Font.BOLD, 14));
        who.setForeground(Color.WHITE);
        badge.setFont(Theme.sans(Font.PLAIN, 12));
        badge.setForeground(Theme.SOFT);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 1));
        text.setOpaque(false);
        text.add(who);
        text.add(badge);

        JPanel right = new JPanel(new BorderLayout(12, 0));
        right.setOpaque(false);
        right.add(text, BorderLayout.CENTER);
        right.add(avatar, BorderLayout.EAST);

        p.add(title, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private void sectionLabel(JPanel p, String text)
    {
        JLabel l = new JLabel(text);
        l.setFont(Theme.sans(Font.BOLD, 11));
        l.setForeground(Theme.SOFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(4, 6, 8, 0));
        p.add(l);
    }

    private FlatButton sideButton(JPanel p, String text, VectorIcon.Kind icon, Color bg, Color fg,
                                  Runnable action)
    {
        FlatButton b = new FlatButton(text, icon, bg, fg);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.addActionListener(e -> action.run());
        p.add(b);
        p.add(Box.createVerticalStrut(6));
        return b;
    }

    private JPanel buildSidebar()
    {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.SIDEBAR);
        p.setBorder(new EmptyBorder(18, 14, 18, 14));
        p.setPreferredSize(new Dimension(210, 0));

        // ---- account selector (switch accounts without opening a new window)
        sectionLabel(p, "ACCOUNT");
        accountBox.setFont(Theme.sans(Font.BOLD, 13));
        accountBox.setBackground(Theme.PAPER);
        accountBox.setForeground(Theme.TEXT);
        accountBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        accountBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        accountBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                    boolean selected, boolean focus)
            {
                String name = String.valueOf(value);
                MailClient c = clients.get(name);
                int unread = (c == null) ? 0 : c.getUnreadCount();
                String text = unread > 0 ? name + "  (" + unread + " unread)" : name;
                JLabel lb = (JLabel) super.getListCellRendererComponent(l, text, index, selected, focus);
                lb.setBorder(new EmptyBorder(6, 10, 6, 10));
                lb.setFont(Theme.sans(unread > 0 ? Font.BOLD : Font.PLAIN, 13));
                return lb;
            }
        });
        accountBox.addActionListener(e -> {
            if (switching) return;
            String name = (String) accountBox.getSelectedItem();
            if (name != null && client != null && !name.equals(client.getUser())) {
                activate(name);
            }
        });
        p.add(accountBox);
        p.add(Box.createVerticalStrut(16));

        sideButton(p, "Compose", VectorIcon.Kind.COMPOSE, Color.WHITE, Theme.DARK,
                   () -> openCompose("", "", "", null));
        p.add(Box.createVerticalStrut(16));

        sectionLabel(p, "FOLDERS");
        inboxBtn  = sideButton(p, "Inbox", VectorIcon.Kind.INBOX, Theme.SIDEBAR, Theme.ONDARK,
                               () -> setFolder(Folder.INBOX));
        draftsBtn = sideButton(p, "Drafts", VectorIcon.Kind.DRAFT, Theme.SIDEBAR, Theme.ONDARK,
                               () -> setFolder(Folder.DRAFTS));
        sentBtn   = sideButton(p, "Sent", VectorIcon.Kind.SENT, Theme.SIDEBAR, Theme.ONDARK,
                               () -> setFolder(Folder.SENT));
        binBtn    = sideButton(p, "Bin", VectorIcon.Kind.DELETE, Theme.SIDEBAR, Theme.ONDARK,
                               () -> setFolder(Folder.BIN));

        p.add(Box.createVerticalGlue());
        sideButton(p, "Add account", VectorIcon.Kind.USERPLUS, Theme.DARK, Theme.SOFT,
                   this::showLogin);
        sideButton(p, "Sign out", VectorIcon.Kind.SIGNOUT, Theme.DARK, Theme.SOFT, this::signOut);
        return p;
    }

    /** Small button used in the reading-pane toolbar. */
    private FlatButton toolButton(String text, VectorIcon.Kind icon, Color bg, Color fg,
                                  Runnable action)
    {
        FlatButton b = new FlatButton(text, icon, bg, fg);
        b.setBorder(new EmptyBorder(7, 12, 7, 12));
        b.addActionListener(e -> action.run());
        return b;
    }

    private JComponent buildCenter()
    {
        list.setCellRenderer(new MailCell());
        list.setBackground(Theme.PAPER);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelected();
        });

        folderLabel.setFont(Theme.sans(Font.BOLD, 16));
        folderLabel.setForeground(Theme.TEXT);

        emptyBinBtn = toolButton("Empty bin", VectorIcon.Kind.DELETE, Theme.SELECT, Theme.TEXT,
                                 this::emptyBin);
        emptyBinBtn.setVisible(false);

        JPanel folderBar = new JPanel(new BorderLayout());
        folderBar.setOpaque(false);
        folderBar.setBorder(new EmptyBorder(10, 20, 10, 14));
        folderBar.add(folderLabel, BorderLayout.CENTER);
        folderBar.add(emptyBinBtn, BorderLayout.EAST);

        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setBorder(new MatteBorder(1, 0, 0, 0, Theme.LINE));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Theme.PAPER);
        top.add(folderBar, BorderLayout.NORTH);
        top.add(listScroll, BorderLayout.CENTER);

        // ---- reading pane (with its own Reply / Edit / Restore / Delete toolbar)
        rSubject.setFont(Theme.sans(Font.BOLD, 19));
        rSubject.setForeground(Theme.TEXT);
        for (JLabel l : new JLabel[] { rFrom, rTime }) {
            l.setFont(Theme.sans(Font.PLAIN, 13));
            l.setForeground(Theme.MUTED);
        }
        JPanel head = new JPanel(new GridLayout(3, 1, 0, 3));
        head.setOpaque(false);
        head.add(rSubject);
        head.add(rFrom);
        head.add(rTime);

        replyBtn   = toolButton("Reply", VectorIcon.Kind.REPLY, Theme.ACCENT, Color.WHITE, this::reply);
        editBtn    = toolButton("Edit draft", VectorIcon.Kind.COMPOSE, Theme.ACCENT, Color.WHITE, this::editDraft);
        restoreBtn = toolButton("Restore", VectorIcon.Kind.RESTORE, Theme.ACCENT, Color.WHITE, this::restore);
        deleteBtn  = toolButton("Delete", VectorIcon.Kind.DELETE, Theme.SELECT, Theme.TEXT, this::deleteSelected);
        toolbar.setOpaque(false);
        toolbar.add(replyBtn);
        toolbar.add(editBtn);
        toolbar.add(restoreBtn);
        toolbar.add(deleteBtn);

        JPanel headRow = new JPanel(new BorderLayout(12, 0));
        headRow.setOpaque(false);
        headRow.add(head, BorderLayout.CENTER);
        JPanel toolWrap = new JPanel(new BorderLayout());
        toolWrap.setOpaque(false);
        toolWrap.add(toolbar, BorderLayout.NORTH);
        headRow.add(toolWrap, BorderLayout.EAST);

        rBody.setEditable(false);
        rBody.setLineWrap(true);
        rBody.setWrapStyleWord(true);
        rBody.setFont(Theme.sans(Font.PLAIN, 15));
        rBody.setForeground(Theme.TEXT);
        rBody.setBackground(Theme.BG);
        rBody.setBorder(new EmptyBorder(14, 14, 14, 14));
        JScrollPane bodyScroll = new JScrollPane(rBody);
        bodyScroll.setBorder(new LineBorder(Theme.LINE));

        JPanel reader = new JPanel(new BorderLayout(0, 12));
        reader.setBackground(Theme.PAPER);
        reader.setBorder(new EmptyBorder(16, 20, 16, 20));
        reader.add(headRow, BorderLayout.NORTH);
        reader.add(bodyScroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, reader);
        split.setResizeWeight(0.42);
        split.setDividerSize(5);
        split.setBorder(null);
        return split;
    }

    private JPanel buildStatusBar()
    {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG);
        p.setBorder(new MatteBorder(1, 0, 0, 0, Theme.LINE));
        status.setFont(Theme.sans(Font.PLAIN, 12));
        status.setForeground(Theme.MUTED);
        status.setBorder(new EmptyBorder(6, 14, 6, 14));
        p.add(status, BorderLayout.CENTER);
        return p;
    }


    private void setStatus(String text) { status.setText(" " + text); }

    private String folderName(Folder f)
    {
        switch (f) {
            case INBOX:  return "Inbox";
            case DRAFTS: return "Drafts";
            case SENT:   return "Sent";
            default:     return "Bin";
        }
    }

    private void setFolder(Folder f)
    {
        current = f;
        highlightFolders();
        emptyBinBtn.setVisible(f == Folder.BIN);
        list.clearSelection();
        refreshList();
    }

    private void highlightFolders()
    {
        inboxBtn.setBase(current == Folder.INBOX ? Theme.ACTIVE : Theme.SIDEBAR);
        draftsBtn.setBase(current == Folder.DRAFTS ? Theme.ACTIVE : Theme.SIDEBAR);
        sentBtn.setBase(current == Folder.SENT ? Theme.ACTIVE : Theme.SIDEBAR);
        binBtn.setBase(current == Folder.BIN ? Theme.ACTIVE : Theme.SIDEBAR);
    }
    private void refreshList()
    {
        if (client == null) return;
        MailItem selected = list.getSelectedValue();
        model.clear();
        for (MailItem m : client.getFolder(current)) {
            model.addElement(m);
        }
        updateFolderLabel();
        updateCounts();
        if (selected != null && model.contains(selected)) {
            list.setSelectedValue(selected, true);
        } else {
            showSelected();
        }
    }

    private void updateFolderLabel()
    {
        int n = client.getFolder(current).size();
        if (current == Folder.INBOX) {
            folderLabel.setText("Inbox  (" + client.getUnreadCount() + " unread of " + n + ")");
        } else {
            folderLabel.setText(folderName(current) + "  (" + n + ")");
        }
    }

    private void updateCounts()
    {
        int unread = client.getUnreadCount();
        int drafts = client.getFolder(Folder.DRAFTS).size();
        inboxBtn.setText(unread > 0 ? "Inbox  (" + unread + ")" : "Inbox");
        draftsBtn.setText(drafts > 0 ? "Drafts  (" + drafts + ")" : "Drafts");
        badge.setText(unread > 0 ? unread + " unread message" + (unread > 1 ? "s" : "")
                                 : "No unread messages");
        accountBox.repaint();
    }
    private void checkNewMail()
    {
        if (client == null) return;
        List<MailItem> arrived = client.receiveNewMail();
        if (arrived.isEmpty()) return;
        MailItem last = arrived.get(arrived.size() - 1);
        setStatus(arrived.size() == 1
                ? "New mail from " + last.getFrom() + ": " + last.getSubject()
                : arrived.size() + " new messages arrived.");
        if (current == Folder.INBOX) {
            refreshList();
        } else {
            updateCounts();
        }
    }
    private void showSelected()
    {
        MailItem m = list.getSelectedValue();
        replyBtn.setVisible(m != null && current == Folder.INBOX);
        editBtn.setVisible(m != null && current == Folder.DRAFTS);
        restoreBtn.setVisible(m != null && current == Folder.BIN);
        deleteBtn.setVisible(m != null);
        deleteBtn.setText(current == Folder.BIN ? "Delete forever" : "Delete");
        toolbar.revalidate();
        toolbar.repaint();

        if (m == null) {
            rSubject.setText(" ");
            rFrom.setText(" ");
            rTime.setText(" ");
            rBody.setText("Select a message to read it.");
            return;
        }
        if (current == Folder.INBOX && !m.isRead()) {
            m.markRead();
            updateFolderLabel();
            updateCounts();
            list.repaint();
        }
        String to = m.getTo().isEmpty() ? "(no recipient)" : m.getTo();
        rSubject.setText(m.getSubject());
        rFrom.setText(current == Folder.DRAFTS ? "Draft to: " + to
                                               : "From: " + m.getFrom() + "      To: " + to);
        rTime.setText(m.getTime());
        rBody.setText(m.getMessage());
        rBody.setCaretPosition(0);
    }

    private void reply()
    {
        MailItem m = list.getSelectedValue();
        if (m == null) return;
        String subj = m.getSubject().startsWith("Re: ") ? m.getSubject() : "Re: " + m.getSubject();
        String quote = "\n\n----- On " + m.getTime() + ", " + m.getFrom() + " wrote -----\n"
                     + m.getMessage().replaceAll("(?m)^", "> ");
        openCompose(m.getFrom(), subj, quote, null);
    }

    private void editDraft()
    {
        MailItem m = list.getSelectedValue();
        if (m == null) return;
        String subj = m.getSubject().equals("(no subject)") ? "" : m.getSubject();
        openCompose(m.getTo(), subj, m.getMessage(), m);
    }

    private void restore()
    {
        MailItem m = list.getSelectedValue();
        if (m == null) return;
        client.restoreMailItem(m);
        refreshList();
        setStatus("Message restored to " + folderName(m.getOrigin()) + ".");
    }

    private void deleteSelected()
    {
        MailItem m = list.getSelectedValue();
        if (m == null) return;
        client.deleteMailItem(m, current);
        refreshList();
        setStatus(current == Folder.BIN ? "Message deleted permanently." : "Message moved to the bin.");
    }

    private void emptyBin()
    {
        int n = client.getFolder(Folder.BIN).size();
        if (n == 0) {
            setStatus("The bin is already empty.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Permanently delete all " + n + " message(s) in the bin?",
                "Empty bin", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            client.emptyBin();
            refreshList();
            setStatus("Bin emptied.");
        }
    }
    private void openCompose(String toText, String subjectText, String bodyText, MailItem draft)
    {
        JDialog d = new JDialog(this, "Compose", true);

        JLabel head = new JLabel("Compose", new VectorIcon(VectorIcon.Kind.COMPOSE, Theme.TEXT, 20),
                SwingConstants.LEFT);
        head.setIconTextGap(10);
        head.setFont(Theme.sans(Font.BOLD, 20));
        head.setForeground(Theme.TEXT);

        JComboBox<String> toBox = new JComboBox<String>(
                MailGUI.SERVER.getUsers().toArray(new String[0]));
        toBox.setEditable(true);
        toBox.setSelectedItem(toText);
        toBox.setBackground(Theme.PAPER);
        Theme.styleField((JTextComponent) toBox.getEditor().getEditorComponent());

        JTextField subjectField = new JTextField(subjectText);
        Theme.styleField(subjectField);

        JTextArea body = new JTextArea(bodyText, 10, 42);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        Theme.styleField(body);
        body.setCaretPosition(0);
        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(null);

        JLabel err = new JLabel(" ");
        err.setForeground(Theme.ERROR);
        err.setFont(Theme.sans(Font.PLAIN, 12));

        FlatButton send = new FlatButton("Send", VectorIcon.Kind.SENT, Theme.ACCENT, Color.WHITE);
        FlatButton saveDraft = new FlatButton("Save draft", VectorIcon.Kind.DRAFT, Theme.SELECT, Theme.TEXT);
        FlatButton cancel = new FlatButton("Cancel", VectorIcon.Kind.CLOSE, Theme.SELECT, Theme.TEXT);
        send.setHorizontalAlignment(SwingConstants.CENTER);
        saveDraft.setHorizontalAlignment(SwingConstants.CENTER);
        cancel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.add(saveDraft);
        btns.add(cancel);
        btns.add(send);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 0, 5, 12);
        String[] labels = { "To", "Subject", "Message" };
        JComponent[] inputs = { toBox, subjectField, bodyScroll };
        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            l.setFont(Theme.sans(Font.BOLD, 12));
            l.setForeground(Theme.MUTED);
            g.gridx = 0; g.gridy = i; g.weightx = 0; g.weighty = 0;
            g.fill = GridBagConstraints.NONE;
            g.anchor = (i == 2) ? GridBagConstraints.NORTHWEST : GridBagConstraints.WEST;
            fields.add(l, g);
            g.gridx = 1; g.weightx = 1;
            g.fill = (i == 2) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
            g.weighty = (i == 2) ? 1 : 0;
            fields.add(inputs[i], g);
        }

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(err, BorderLayout.NORTH);
        south.add(btns, BorderLayout.EAST);

        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Theme.PAPER);
        p.setBorder(new EmptyBorder(20, 24, 18, 24));
        p.add(head, BorderLayout.NORTH);
        p.add(fields, BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);

        send.addActionListener(e -> {
            String to = String.valueOf(toBox.getEditor().getItem()).trim().toLowerCase();
            String error = client.sendMailItem(to, subjectField.getText().trim(),
                                               body.getText().trim(), draft);
            if (error != null) {
                err.setText(error);
                return;
            }
            d.dispose();
            setStatus("Message sent to " + to + ".");
            if (current == Folder.SENT || current == Folder.DRAFTS) refreshList();
            else updateCounts();
        });
        saveDraft.addActionListener(e -> {
            String to = String.valueOf(toBox.getEditor().getItem()).trim().toLowerCase();
            MailItem saved = client.saveDraft(to, subjectField.getText().trim(),
                                              body.getText().trim(), draft);
            if (saved == null) {
                err.setText("There is nothing to save.");
                return;
            }
            d.dispose();
            setStatus("Draft saved.");
            if (current == Folder.DRAFTS) refreshList();
            else updateCounts();
        });
        cancel.addActionListener(e -> d.dispose());

        d.setContentPane(p);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
    private class MailCell extends JPanel implements ListCellRenderer<MailItem>
    {
        private JLabel whoLabel = new JLabel();
        private JLabel subject = new JLabel();
        private JLabel time = new JLabel();

        MailCell()
        {
            setLayout(new BorderLayout(10, 2));
            setOpaque(true);
            JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
            left.setOpaque(false);
            left.add(whoLabel);
            left.add(subject);
            add(left, BorderLayout.CENTER);
            add(time, BorderLayout.EAST);
            time.setFont(Theme.sans(Font.PLAIN, 12));
            time.setForeground(Theme.MUTED);
            subject.setForeground(Theme.MUTED);
            setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Theme.LINE),
                                         new EmptyBorder(10, 20, 10, 20)));
        }

        public Component getListCellRendererComponent(JList<? extends MailItem> l, MailItem m,
                int index, boolean selected, boolean focus)
        {
            boolean unread = current == Folder.INBOX && !m.isRead();
            boolean mine = client != null && m.getFrom().equals(client.getUser());
            String to = m.getTo().isEmpty() ? "(no recipient)" : m.getTo();
            whoLabel.setText(current != Folder.INBOX && mine ? "To: " + to : m.getFrom());
            whoLabel.setFont(Theme.sans(unread ? Font.BOLD : Font.PLAIN, 14));
            whoLabel.setForeground(Theme.TEXT);
            subject.setText(m.getSubject());
            subject.setFont(Theme.sans(unread ? Font.BOLD : Font.PLAIN, 13));
            time.setText(m.getTime());
            setBackground(selected ? Theme.SELECT : (unread ? Theme.UNREAD : Theme.PAPER));
            return this;
        }
    }
}