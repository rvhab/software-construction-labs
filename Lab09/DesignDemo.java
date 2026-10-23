package lab9;

import java.util.Locale;
import java.util.function.Consumer;


public final class DesignDemo {

    private DesignDemo() { }

    static String money(double value) {
        return String.format(Locale.US, "Rs. %,.2f", value);
    }

    public static final class BadOrderManager {
        private final Consumer<String> out;

        public BadOrderManager(Consumer<String> out) {
            this.out = out;
        }

        public void placeOrder(String customer, String item, double price,
                               String payType, String notifyType) {
            // job 1: validation
            if (customer == null || customer.trim().isEmpty()) {
                out.accept("ERROR: customer name is required");
                return;
            }
            if (price <= 0) {
                out.accept("ERROR: price must be greater than zero");
                return;
            }

            // job 2: payment (hard-coded types)
            String payName;
            switch (payType) {
                case "CARD":
                    out.accept("Charging " + money(price) + " to credit card...");
                    payName = "Credit Card";
                    break;
                case "WALLET":
                    out.accept("Debiting " + money(price) + " from mobile wallet...");
                    payName = "Mobile Wallet";
                    break;
                case "COD":
                    out.accept("Cash on delivery: collect " + money(price) + " at the door");
                    payName = "Cash on Delivery";
                    break;
                default:
                    out.accept("ERROR: unknown payment type");
                    return;
            }

            // job 3: notification (hard-coded channels)
            String channel;
            switch (notifyType) {
                case "EMAIL":
                    out.accept("Email sent to " + customer + ": order confirmed");
                    channel = "Email";
                    break;
                case "SMS":
                    out.accept("SMS sent to " + customer + ": order confirmed");
                    channel = "SMS";
                    break;
                case "WHATSAPP":
                    out.accept("WhatsApp message sent to " + customer + ": order confirmed");
                    channel = "WhatsApp";
                    break;
                default:
                    out.accept("ERROR: unknown notification type");
                    return;
            }

            // job 4: receipt formatting
            out.accept("---- RECEIPT ----");
            out.accept("Customer : " + customer);
            out.accept("Item     : " + item);
            out.accept("Total    : " + money(price));
            out.accept("Payment  : " + payName);
            out.accept("Notified : " + channel);
            out.accept("-----------------");
        }
    }


    public interface PaymentMethod {
        String getName();

        String pay(double amount);
    }

    public interface Notifier {
        String getChannel();

        String send(String customer, String message);
    }

    public static final class CardPayment implements PaymentMethod {
        @Override
        public String getName() {
            return "Credit Card";
        }

        @Override
        public String pay(double amount) {
            return "Charging " + money(amount) + " to credit card...";
        }
    }

    public static final class WalletPayment implements PaymentMethod {
        @Override
        public String getName() {
            return "Mobile Wallet";
        }

        @Override
        public String pay(double amount) {
            return "Debiting " + money(amount) + " from mobile wallet...";
        }
    }

    public static final class CashOnDelivery implements PaymentMethod {
        @Override
        public String getName() {
            return "Cash on Delivery";
        }

        @Override
        public String pay(double amount) {
            return "Cash on delivery: collect " + money(amount) + " at the door";
        }
    }

    public static final class EmailNotifier implements Notifier {
        @Override
        public String getChannel() {
            return "Email";
        }

        @Override
        public String send(String customer, String message) {
            return "Email sent to " + customer + ": " + message;
        }
    }

    public static final class SmsNotifier implements Notifier {
        @Override
        public String getChannel() {
            return "SMS";
        }

        @Override
        public String send(String customer, String message) {
            return "SMS sent to " + customer + ": " + message;
        }
    }

    public static final class WhatsAppNotifier implements Notifier {
        @Override
        public String getChannel() {
            return "WhatsApp";
        }

        @Override
        public String send(String customer, String message) {
            return "WhatsApp message sent to " + customer + ": " + message;
        }
    }

    /** Holds order data only. */
    public static final class Order {
        private final String customer;
        private final String item;
        private final double price;

        public Order(String customer, String item, double price) {
            this.customer = customer;
            this.item = item;
            this.price = price;
        }

        public String getCustomer() {
            return customer;
        }

        public String getItem() {
            return item;
        }

        public double getPrice() {
            return price;
        }
    }

    public static final class OrderValidator {
        public String validate(Order order) {
            if (order.getCustomer() == null || order.getCustomer().trim().isEmpty()) {
                return "customer name is required";
            }
            if (order.getPrice() <= 0) {
                return "price must be greater than zero";
            }
            return null;
        }
    }

    public static final class ReceiptFormatter {
        public String format(Order order, PaymentMethod payment, Notifier notifier) {
            return "---- RECEIPT ----\n"
                    + "Customer : " + order.getCustomer() + "\n"
                    + "Item     : " + order.getItem() + "\n"
                    + "Total    : " + money(order.getPrice()) + "\n"
                    + "Payment  : " + payment.getName() + "\n"
                    + "Notified : " + notifier.getChannel() + "\n"
                    + "-----------------";
        }
    }

    public static final class OrderService {
        private final OrderValidator validator;
        private final PaymentMethod payment;
        private final Notifier notifier;
        private final ReceiptFormatter receipt;
        private final Consumer<String> out;

        public OrderService(OrderValidator validator, PaymentMethod payment, Notifier notifier,
                            ReceiptFormatter receipt, Consumer<String> out) {
            this.validator = validator;
            this.payment = payment;
            this.notifier = notifier;
            this.receipt = receipt;
            this.out = out;
        }

        public void place(Order order) {
            String error = validator.validate(order);
            if (error != null) {
                out.accept("ERROR: " + error);
                return;
            }
            out.accept(payment.pay(order.getPrice()));
            out.accept(notifier.send(order.getCustomer(), "order confirmed"));
            out.accept(receipt.format(order, payment, notifier));
        }
    }

    public static final class TightBox {
        public int volume;                       // public field, no encapsulation

        public TightBox(int length, int width, int height) {
            this.volume = length * width * height;   // computed once, int arithmetic
        }
    }

    public interface Solid {
        String getName();

        double getVolume();
    }

    static double requirePositive(double value, String name) {
        if (!(value > 0) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(name + " must be a positive number");
        }
        return value;
    }

    public static final class Box implements Solid {
        private final double length;
        private final double width;
        private final double height;

        public Box(double length, double width, double height) {
            this.length = requirePositive(length, "length");
            this.width = requirePositive(width, "width");
            this.height = requirePositive(height, "height");
        }

        @Override
        public String getName() {
            return "Box";
        }

        @Override
        public double getVolume() {
            return length * width * height;
        }
    }

    public static final class Cylinder implements Solid {
        private final double radius;
        private final double height;

        public Cylinder(double radius, double height) {
            this.radius = requirePositive(radius, "radius");
            this.height = requirePositive(height, "height");
        }

        @Override
        public String getName() {
            return "Cylinder";
        }

        @Override
        public double getVolume() {
            return Math.PI * radius * radius * height;
        }
    }

    public static final class Sphere implements Solid {
        private final double radius;

        public Sphere(double radius) {
            this.radius = requirePositive(radius, "radius");
        }

        @Override
        public String getName() {
            return "Sphere";
        }

        @Override
        public double getVolume() {
            return 4.0 / 3.0 * Math.PI * radius * radius * radius;
        }
    }

    /** Client code: works with ANY Solid and never touches a shape's internals. */
    public static String describe(Solid solid) {
        return String.format(Locale.US, "%s volume = %,.2f cubic units",
                solid.getName(), solid.getVolume());
    }

    /** Console version of the loosely coupled program (Run As > Java Application). */
    public static final class Volume {
        public static void main(String[] args) {
            Solid b = new Box(5, 5, 5);
            System.out.println(describe(b));
        }
    }
}
