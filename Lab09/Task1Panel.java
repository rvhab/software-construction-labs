package lab9;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Task1Panel extends JPanel {

    private static final String TAB_LIVE = "Live Output";
    private static final String TAB_BEFORE = "Before: Code";
    private static final String TAB_AFTER = "After: Code";
    private static final String TAB_ANALYSIS = "Analysis";

    private static final String[] ITEMS = { "Chicken Karahi", "Zinger Burger", "Beef Biryani", "Margherita Pizza" };
    private static final double[] PRICES = { 1450, 650, 520, 1100 };
    private static final String[] PAY_NAMES = { "Credit Card", "Mobile Wallet", "Cash on Delivery" };
    private static final String[] PAY_CODES = { "CARD", "WALLET", "COD" };
    private static final String[] NOTE_NAMES = { "Email", "SMS", "WhatsApp" };
    private static final String[] NOTE_CODES = { "EMAIL", "SMS", "WHATSAPP" };

    private final JTextField nameField = Theme.field("Ayesha Khan");
    private final JTextField priceField = Theme.field("1450");
    private final JComboBox<String> itemBox = Theme.combo(ITEMS);
    private final JComboBox<String> payBox = Theme.combo(PAY_NAMES);
    private final JComboBox<String> noteBox = Theme.combo(NOTE_NAMES);
    private final Theme.Console beforeOut = new Theme.Console();
    private final Theme.Console afterOut = new Theme.Console();

    public Task1Panel() {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);
        setBorder(new EmptyBorder(18, 24, 20, 24));

        add(Theme.introCard("Task 1  |  Case study: Online Food Ordering System",
                "Placing a food order needs validation, payment, a customer notification and a receipt. "
                + "The BEFORE design pushes all four jobs into one class that is hard-wired to specific "
                + "payment and notification types: tight coupling and low cohesion. The AFTER design gives "
                + "each job its own class and joins them through interfaces: loose coupling and high "
                + "cohesion. Run both with the same order - the output is identical, the design is not.", 124),
                BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        body.add(buildForm(), BorderLayout.WEST);
        body.add(buildResults(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        itemBox.addActionListener(e -> priceField.setText(
                String.format(Locale.US, "%.0f", PRICES[itemBox.getSelectedIndex()])));
        runBoth();
    }

    private JPanel buildForm() {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(Theme.labeled("Customer name", nameField));
        c.add(Theme.gap(12));
        c.add(Theme.labeled("Menu item", itemBox));
        c.add(Theme.gap(12));
        c.add(Theme.labeled("Price (Rs.)  -  edit to test validation", priceField));
        c.add(Theme.gap(12));
        c.add(Theme.labeled("Payment method", payBox));
        c.add(Theme.gap(12));
        c.add(Theme.labeled("Notify customer by", noteBox));
        c.add(Theme.gap(18));

        Theme.PillButton run = Theme.primary("Run Both Versions");
        run.addActionListener(e -> runBoth());
        c.add(run);
        c.add(Theme.gap(8));

        Theme.PillButton reset = Theme.ghost("Reset Form");
        reset.addActionListener(e -> {
            nameField.setText("Ayesha Khan");
            itemBox.setSelectedIndex(0);
            priceField.setText("1450");
            payBox.setSelectedIndex(0);
            noteBox.setSelectedIndex(0);
            runBoth();
        });
        c.add(reset);
        c.add(Theme.glue());
        return Theme.formCard("Place an order", c, 300);
    }

    // --------------------------------------------------------------- results
    private JPanel buildResults() {
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setOpaque(false);

        final CardLayout layout = new CardLayout();
        JPanel cards = new JPanel(layout);
        cards.setOpaque(false);

        JPanel live = new JPanel(new GridLayout(1, 2, 16, 0));
        live.setOpaque(false);
        live.add(Theme.outputCard("BEFORE", "Tight coupling  |  Low cohesion",
                Theme.DANGER, Theme.DANGER_BG, beforeOut));
        live.add(Theme.outputCard("AFTER", "Loose coupling  |  High cohesion",
                Theme.SUCCESS, Theme.SUCCESS_BG, afterOut));

        cards.add(live, TAB_LIVE);
        cards.add(Theme.codeCard("BadOrderManager.java", BEFORE_CODE,
                "Tight coupling  |  Low cohesion", Theme.DANGER, Theme.DANGER_BG), TAB_BEFORE);
        cards.add(Theme.codeCard("OrderService.java  (and helper classes)", AFTER_CODE,
                "Loose coupling  |  High cohesion", Theme.SUCCESS, Theme.SUCCESS_BG), TAB_AFTER);
        cards.add(Theme.htmlCard(analysisHtml()), TAB_ANALYSIS);

        wrap.add(Theme.tabBar(layout, cards, TAB_LIVE, TAB_BEFORE, TAB_AFTER, TAB_ANALYSIS), BorderLayout.NORTH);
        wrap.add(cards, BorderLayout.CENTER);
        return wrap;
    }

    // ----------------------------------------------------------------- logic
    private void runBoth() {
        String customer = nameField.getText().trim();
        String item = (String) itemBox.getSelectedItem();
        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim().replace(",", ""));
            if (Double.isNaN(price) || Double.isInfinite(price)) {
                throw new NumberFormatException("not a finite number");
            }
        } catch (NumberFormatException ex) {
            String msg = "ERROR: price must be a valid number";
            beforeOut.display(msg);
            afterOut.display(msg);
            return;
        }
        int pay = payBox.getSelectedIndex();
        int note = noteBox.getSelectedIndex();

        // BEFORE: one big class
        StringBuilder before = new StringBuilder();
        new DesignDemo.BadOrderManager(line -> before.append(line).append('\n'))
                .placeOrder(customer, item, price, PAY_CODES[pay], NOTE_CODES[note]);
        beforeOut.display(before.toString());

        // AFTER: small classes plugged together
        StringBuilder after = new StringBuilder();
        DesignDemo.OrderService service = new DesignDemo.OrderService(
                new DesignDemo.OrderValidator(),
                paymentFor(pay),
                notifierFor(note),
                new DesignDemo.ReceiptFormatter(),
                line -> after.append(line).append('\n'));
        service.place(new DesignDemo.Order(customer, item, price));
        afterOut.display(after.toString());
    }

    private static DesignDemo.PaymentMethod paymentFor(int index) {
        switch (index) {
            case 0:
                return new DesignDemo.CardPayment();
            case 1:
                return new DesignDemo.WalletPayment();
            default:
                return new DesignDemo.CashOnDelivery();
        }
    }

    private static DesignDemo.Notifier notifierFor(int index) {
        switch (index) {
            case 0:
                return new DesignDemo.EmailNotifier();
            case 1:
                return new DesignDemo.SmsNotifier();
            default:
                return new DesignDemo.WhatsAppNotifier();
        }
    }

    // ----------------------------------------------------------- code samples
    private static final String BEFORE_CODE = Theme.lines(
            "// BEFORE - one class does everything and is hard-wired to specific types",
            "// (out = where messages go: console or GUI)",
            "public class BadOrderManager {",
            "    private final Consumer<String> out;",
            "",
            "    public void placeOrder(String customer, String item, double price,",
            "                           String payType, String notifyType) {",
            "",
            "        // job 1: validation",
            "        if (customer == null || customer.trim().isEmpty()) {",
            "            out.accept(\"ERROR: customer name is required\");",
            "            return;",
            "        }",
            "        if (price <= 0) { out.accept(\"ERROR: price must be greater than zero\"); return; }",
            "",
            "        // job 2: payment - hard-coded types",
            "        switch (payType) {",
            "            case \"CARD\":   out.accept(\"Charging ... to credit card\");   break;",
            "            case \"WALLET\": out.accept(\"Debiting ... from wallet\");      break;",
            "            case \"COD\":    out.accept(\"Cash on delivery ...\");          break;",
            "            default:       out.accept(\"ERROR: unknown payment type\");    return;",
            "        }",
            "",
            "        // job 3: notification - hard-coded channels",
            "        switch (notifyType) {",
            "            case \"EMAIL\":    out.accept(\"Email sent ...\");     break;",
            "            case \"SMS\":      out.accept(\"SMS sent ...\");       break;",
            "            case \"WHATSAPP\": out.accept(\"WhatsApp sent ...\");  break;",
            "            default:         out.accept(\"ERROR: unknown notification type\"); return;",
            "        }",
            "",
            "        // job 4: receipt formatting",
            "        out.accept(\"---- RECEIPT ----\");",
            "        out.accept(\"Customer : \" + customer);",
            "        // ... item, total, payment, channel",
            "    }",
            "}",
            "",
            "// Adding Easypaisa or a push notification means editing THIS method",
            "// and risking the code that already works.");

    private static final String AFTER_CODE = Theme.lines(
            "// AFTER - each class has one job; they talk through interfaces",
            "",
            "public interface PaymentMethod {",
            "    String getName();",
            "    String pay(double amount);",
            "}",
            "",
            "public interface Notifier {",
            "    String getChannel();",
            "    String send(String customer, String message);",
            "}",
            "",
            "public class CardPayment implements PaymentMethod {",
            "    @Override public String getName() { return \"Credit Card\"; }",
            "    @Override public String pay(double amount) {",
            "        return \"Charging \" + money(amount) + \" to credit card...\";",
            "    }",
            "}",
            "// WalletPayment, CashOnDelivery, EmailNotifier, SmsNotifier and",
            "// WhatsAppNotifier follow exactly the same pattern.",
            "",
            "public class OrderValidator {          // one job: validation",
            "    public String validate(Order order) { ... }",
            "}",
            "",
            "public class ReceiptFormatter {        // one job: receipt text",
            "    public String format(Order o, PaymentMethod p, Notifier n) { ... }",
            "}",
            "",
            "public class OrderService {            // one job: coordinate the steps",
            "    private final OrderValidator validator;",
            "    private final PaymentMethod payment;   // an abstraction, not a concrete class",
            "    private final Notifier notifier;       // an abstraction, not a concrete class",
            "    private final ReceiptFormatter receipt;",
            "    private final Consumer<String> out;",
            "",
            "    public OrderService(OrderValidator validator, PaymentMethod payment,",
            "                        Notifier notifier, ReceiptFormatter receipt,",
            "                        Consumer<String> out) { ... }",
            "",
            "    public void place(Order order) {",
            "        String error = validator.validate(order);",
            "        if (error != null) { out.accept(\"ERROR: \" + error); return; }",
            "        out.accept(payment.pay(order.getPrice()));",
            "        out.accept(notifier.send(order.getCustomer(), \"order confirmed\"));",
            "        out.accept(receipt.format(order, payment, notifier));",
            "    }",
            "}",
            "",
            "// Client code: behaviour is chosen by plugging in objects",
            "OrderService service = new OrderService(new OrderValidator(),",
            "        new CardPayment(), new SmsNotifier(), new ReceiptFormatter(), out);",
            "service.place(new Order(\"Ayesha Khan\", \"Chicken Karahi\", 1450));");

    // -------------------------------------------------------------- analysis
    private static String analysisHtml() {
        String[][] rows = {
            { "Responsibilities",
              "One class validates, charges, notifies <i>and</i> prints receipts - four unrelated jobs.",
              "Each class has a single job: <b>OrderValidator</b>, <b>PaymentMethod</b>, "
              + "<b>Notifier</b>, <b>ReceiptFormatter</b>, <b>OrderService</b>." },
            { "Cohesion",
              "<b>Low.</b> Unrelated tasks are mixed inside one method.",
              "<b>High.</b> Every method in a class serves that class's one purpose." },
            { "Coupling",
              "<b>Tight.</b> The method switches on type strings, so it must know every payment "
              + "and notification type that exists.",
              "<b>Loose.</b> OrderService knows only the <i>PaymentMethod</i> and <i>Notifier</i> "
              + "interfaces, never the concrete classes." },
            { "Add a new payment type (e.g. Easypaisa)",
              "Edit <b>BadOrderManager</b> and risk breaking card / COD code that already works.",
              "Write <b>one new class</b>. Nothing that exists today has to change." },
            { "Testing and reuse",
              "Payment cannot be tested without also triggering notification and receipt.",
              "Every class can be tested or reused on its own." },
            { "Ripple effect of a change",
              "A small change can ripple through the one big method.",
              "A change stays inside the single class that owns that job." }
        };
        StringBuilder h = new StringBuilder("<html><body>");
        h.append("<p><font size='5' color='#3730A3'><b>Why the AFTER design is better</b></font></p>");
        h.append("<p><b>Coupling</b> is how tightly classes depend on each other - we want it <i>low</i>. ");
        h.append("<b>Cohesion</b> is how focused a class is on one task - we want it <i>high</i>.</p>");
        h.append(Theme.compareTable("Aspect", "BEFORE  (BadOrderManager)",
                "AFTER  (OrderService + helpers)", rows));
        h.append("<p><b>Lab rule of thumb:</b> tight coupling usually gives low cohesion, ");
        h.append("and loose coupling usually gives high cohesion.</p>");
        h.append("</body></html>");
        return h.toString();
    }
}
