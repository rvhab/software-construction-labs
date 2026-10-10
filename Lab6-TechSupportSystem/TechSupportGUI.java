import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TechSupportGUI extends JFrame {

    //Colour 
    private static final Color SIDEBAR = new Color(0x1F1D36);
    private static final Color SIDEBAR_TEXT = new Color(0xC9C7E6);
    private static final Color SIDEBAR_SELECT = new Color(0x39365F);
    private static final Color ACCENT = new Color(0x6C5CE7);
    private static final Color CORAL = new Color(0xFF7A59);
    private static final Color CHAT_BG = new Color(0xEFEDFA);
    private static final Color TEXT = new Color(0x2B2A3D);
    private static final Color MUTED = new Color(0x8D8BA7);
    private static final Color LINE = new Color(0xDAD7EE);
    private static final Color ERROR = new Color(0xD64545);
    private static final Color ONLINE = new Color(0x1FAF6B);

    private static final int MAX_LENGTH = 200;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final Responder responder = new Responder();
    private final StringBuilder chatHtml = new StringBuilder();
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();

    private final JEditorPane chatPane = new JEditorPane("text/html", "");
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");
    private final JLabel warningLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel("\u25CF Online");
    private final JLabel counterLabel = new JLabel("0 / " + MAX_LENGTH);

    public TechSupportGUI() {
        super("TechSupport Assistant");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 650);
        setMinimumSize(new Dimension(760, 500));
        setLocationRelativeTo(null);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainPanel(), BorderLayout.CENTER);

        startSession();
    }


    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(SIDEBAR);
        side.setPreferredSize(new Dimension(250, 0));

        // Brand
        JLabel brand = new JLabel("TechSupport");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel tag = new JLabel("Help Desk Assistant");
        tag.setForeground(SIDEBAR_TEXT);
        tag.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 18, 20));
        brandPanel.add(brand, BorderLayout.NORTH);
        brandPanel.add(tag, BorderLayout.SOUTH);

        // History list
        JLabel historyTitle = new JLabel("CONVERSATION HISTORY");
        historyTitle.setForeground(MUTED);
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        historyTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 20));

        JList<String> list = new JList<>(historyModel);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setForeground(SIDEBAR_TEXT);
        list.setBackground(SIDEBAR);
        list.setSelectionBackground(SIDEBAR_SELECT);
        list.setSelectionForeground(Color.WHITE);
        list.setFixedCellHeight(28);
        list.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.getViewport().setBackground(SIDEBAR);

        JPanel middle = new JPanel(new BorderLayout());
        middle.setOpaque(false);
        middle.add(historyTitle, BorderLayout.NORTH);
        middle.add(listScroll, BorderLayout.CENTER);

        // New session button
        JButton newSession = new JButton("+  New Session");
        styleButton(newSession, CORAL);
        newSession.setPreferredSize(new Dimension(0, 42));
        newSession.addActionListener(e -> startSession());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(14, 18, 20, 18));
        bottom.add(newSession, BorderLayout.CENTER);

        side.add(brandPanel, BorderLayout.NORTH);
        side.add(middle, BorderLayout.CENTER);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.add(buildTopBar(), BorderLayout.NORTH);
        main.add(buildChatArea(), BorderLayout.CENTER);
        main.add(buildInputPanel(), BorderLayout.SOUTH);
        return main;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LINE),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));

        JLabel title = new JLabel("Support Chat");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        statusLabel.setForeground(ONLINE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        bar.add(title, BorderLayout.WEST);
        bar.add(statusLabel, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildChatArea() {
        chatPane.setEditable(false);
        chatPane.setBackground(CHAT_BG);
        chatPane.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JScrollPane scroll = new JScrollPane(chatPane);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 4));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, LINE),
                BorderFactory.createEmptyBorder(14, 20, 10, 20)));

        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        inputField.setForeground(TEXT);
        inputField.setBackground(new Color(0xF7F6FD));
        inputField.setCaretColor(ACCENT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        inputField.addActionListener(e -> sendMessage());
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onTyping(); }
            public void removeUpdate(DocumentEvent e) { onTyping(); }
            public void changedUpdate(DocumentEvent e) { onTyping(); }
        });

        styleButton(sendButton, ACCENT);
        sendButton.setPreferredSize(new Dimension(104, 42));
        sendButton.addActionListener(e -> sendMessage());

        warningLabel.setForeground(ERROR);
        warningLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        counterLabel.setForeground(MUTED);
        counterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(warningLabel, BorderLayout.WEST);
        footer.add(counterLabel, BorderLayout.EAST);

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private void styleButton(JButton b, Color color) {
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void startSession() {
        responder.reset();
        chatHtml.setLength(0);
        historyModel.clear();
        inputField.setText("");
        setInputEnabled(true);
        warningLabel.setText(" ");
        addBotMessage("Welcome to TechSupport! Describe your problem "
                + "(for example: crash, slow, network, password) and I will help you. "
                + "Type 'bye' to end the chat.");
        inputField.requestFocusInWindow();
    }

    private void onTyping() {
        int len = inputField.getText().length();
        counterLabel.setText(len + " / " + MAX_LENGTH);
        counterLabel.setForeground(len > MAX_LENGTH ? ERROR : MUTED);
        if (len <= MAX_LENGTH) {
            warningLabel.setText(" ");
        }
    }

    private String validate(String text) {
        if (text.isEmpty()) {
            return "Please type a message before sending.";
        }
        if (text.length() > MAX_LENGTH) {
            return "Message is too long. Please keep it under " + MAX_LENGTH + " characters.";
        }
        return null;
    }

    private void sendMessage() {
        if (!inputField.isEnabled()) {
            return;
        }
        String text = inputField.getText().trim();
        String error = validate(text);
        if (error != null) {
            warningLabel.setText(error);
            return;
        }

        warningLabel.setText(" ");
        addUserMessage(text);
        inputField.setText("");
        setInputEnabled(false);
        statusLabel.setText("\u25CF Typing...");

        // Short delay so the conversation feels natural
        Timer timer = new Timer(600, e -> reply(text));
        timer.setRepeats(false);
        timer.start();
    }

    private void reply(String text) {
        boolean exit = responder.isExit(text);
        String response;
        String topic;

        if (exit) {
            response = "Thank you for contacting TechSupport. Goodbye! "
                    + "Click 'New Session' to start a new conversation.";
            topic = "Session ended";
        } else {
            response = responder.generateResponse(text);
            String t = responder.getLastTopic();
            topic = (t == null) ? "Not recognised" : t.substring(0, 1).toUpperCase() + t.substring(1);
        }

        historyModel.addElement(LocalTime.now().format(TIME) + "   " + topic);
        addBotMessage(response);
        statusLabel.setText("\u25CF Online");
        setInputEnabled(!exit);
    }

    private void setInputEnabled(boolean enabled) {
        inputField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
        sendButton.setBackground(enabled ? ACCENT : MUTED);
        if (enabled) {
            inputField.requestFocusInWindow();
        }
    }

    private void addUserMessage(String text) {
        chatHtml.append("<table width='100%' cellpadding='4'><tr><td width='25%'></td>"
                + "<td align='right'>"
                + "<table align='right' cellpadding='10' bgcolor='#6C5CE7'><tr><td>"
                + "<font face='SansSerif' size='4' color='#FFFFFF'>" + escape(text) + "</font>"
                + "</td></tr></table>"
                + "<br><font face='SansSerif' size='2' color='#8D8BA7'>You  " + now() + "</font>"
                + "</td></tr></table>");
        render();
    }

    private void addBotMessage(String text) {
        chatHtml.append("<table width='100%' cellpadding='4'><tr>"
                + "<td align='left'>"
                + "<table align='left' cellpadding='10' bgcolor='#FFFFFF'><tr><td>"
                + "<font face='SansSerif' size='4' color='#2B2A3D'>" + escape(text) + "</font>"
                + "</td></tr></table>"
                + "<br><font face='SansSerif' size='2' color='#8D8BA7'>TechSupport  " + now() + "</font>"
                + "</td><td width='25%'></td></tr></table>");
        render();
    }

    private void render() {
        chatPane.setText("<html><body bgcolor='#EFEDFA'>" + chatHtml + "</body></html>");
        SwingUtilities.invokeLater(() -> chatPane.setCaretPosition(chatPane.getDocument().getLength()));
    }

    private String now() {
        return LocalTime.now().format(TIME);
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to default look and feel
        }
        SwingUtilities.invokeLater(() -> new TechSupportGUI().setVisible(true));
    }
}