package lab9;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.math.BigInteger;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Task2Panel extends JPanel {

    private static final String TAB_LIVE = "Live Demo";
    private static final String TAB_ORIGINAL = "Original: Code";
    private static final String TAB_FIXED = "Refactored: Code";
    private static final String TAB_ANALYSIS = "Analysis";

    private static final String[] SHAPES = { "Box", "Cylinder", "Sphere" };
    private static final String[][] LABELS = {
        { "Length", "Width", "Height" },
        { "Radius", "Height" },
        { "Radius" }
    };
    private static final String[][] DEFAULTS = {
        { "5", "5", "5" },
        { "3", "10" },
        { "4" }
    };

    private final JComboBox<String> shapeBox = Theme.combo(SHAPES);
    private final JLabel[] dimLabels = new JLabel[3];
    private final JTextField[] dimFields = new JTextField[3];
    private final JPanel[] dimGroups = new JPanel[3];
    private final Theme.Console originalOut = new Theme.Console();
    private final Theme.Console fixedOut = new Theme.Console();
    private JPanel formContent;

    public Task2Panel() {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);
        setBorder(new EmptyBorder(18, 24, 20, 24));

        add(Theme.introCard("Task 2  |  Is the Box / Volume code tightly coupled?  YES",
                "Volume reaches straight into Box and reads its public field 'volume', and Box does its "
                + "calculation in the constructor and stores the result. Volume therefore depends on how Box "
                + "is built inside, so renaming that field, changing its type or computing it later would "
                + "break Volume. The fix hides the data behind getVolume() and lets the client talk to a "
                + "Solid interface instead of the concrete Box.", 124),
                BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        body.add(buildForm(), BorderLayout.WEST);
        body.add(buildResults(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        shapeBox.addActionListener(e -> {
            applyShape();
            calculate();
        });
        applyShape();
        calculate();
    }

    // ------------------------------------------------------------------ form
    private JPanel buildForm() {
        formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.add(Theme.labeled("Shape", shapeBox));
        formContent.add(Theme.gap(12));
        for (int i = 0; i < 3; i++) {
            dimLabels[i] = Theme.formLabel("Dimension");
            dimFields[i] = Theme.field("");
            dimGroups[i] = Theme.labeled(dimLabels[i], dimFields[i]);
            formContent.add(dimGroups[i]);
            formContent.add(Theme.gap(12));
        }
        formContent.add(Theme.gap(6));

        Theme.PillButton calc = Theme.primary("Calculate Volume");
        calc.addActionListener(e -> calculate());
        formContent.add(calc);
        formContent.add(Theme.gap(8));

        Theme.PillButton overflow = Theme.ghost("Try Overflow Case");
        overflow.addActionListener(e -> {
            shapeBox.setSelectedIndex(0);
            for (int i = 0; i < 3; i++) {
                dimFields[i].setText("5000");
            }
            calculate();
        });
        formContent.add(overflow);
        formContent.add(Theme.gap(8));

        Theme.PillButton reset = Theme.ghost("Reset");
        reset.addActionListener(e -> {
            shapeBox.setSelectedIndex(0);
            applyShape();
            calculate();
        });
        formContent.add(reset);
        formContent.add(Theme.glue());
        return Theme.formCard("Try a shape", formContent, 300);
    }

    /** Shows only the fields the chosen shape needs and loads sensible defaults. */
    private void applyShape() {
        int idx = shapeBox.getSelectedIndex();
        for (int i = 0; i < 3; i++) {
            boolean used = i < LABELS[idx].length;
            dimGroups[i].setVisible(used);
            if (used) {
                dimLabels[i].setText(LABELS[idx][i]);
                dimFields[i].setText(DEFAULTS[idx][i]);
            }
        }
        formContent.revalidate();
        formContent.repaint();
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
        live.add(Theme.outputCard("ORIGINAL", "Tight coupling", Theme.DANGER, Theme.DANGER_BG, originalOut));
        live.add(Theme.outputCard("REFACTORED", "Loose coupling", Theme.SUCCESS, Theme.SUCCESS_BG, fixedOut));

        cards.add(live, TAB_LIVE);
        cards.add(Theme.codeCard("Volume.java + Box.java  (from the lab)", ORIGINAL_CODE,
                "Tight coupling", Theme.DANGER, Theme.DANGER_BG), TAB_ORIGINAL);
        cards.add(Theme.codeCard("Solid.java + Box.java + Volume.java", FIXED_CODE,
                "Loose coupling", Theme.SUCCESS, Theme.SUCCESS_BG), TAB_FIXED);
        cards.add(Theme.htmlCard(analysisHtml()), TAB_ANALYSIS);

        wrap.add(Theme.tabBar(layout, cards, TAB_LIVE, TAB_ORIGINAL, TAB_FIXED, TAB_ANALYSIS), BorderLayout.NORTH);
        wrap.add(cards, BorderLayout.CENTER);
        return wrap;
    }

    // ----------------------------------------------------------------- logic
    private void calculate() {
        int idx = shapeBox.getSelectedIndex();
        int n = LABELS[idx].length;
        String[] raw = new String[n];
        for (int i = 0; i < n; i++) {
            raw[i] = dimFields[i].getText().trim();
        }
        originalOut.display(runOriginal(idx, raw));
        fixedOut.display(runRefactored(idx, raw));
    }

    private String runOriginal(int idx, String[] raw) {
        if (idx != 0) {
            return "> Volume.main() only knows how to read Box.volume\n"
                    + "> A " + SHAPES[idx] + " cannot be used without rewriting Volume\n"
                    + "ERROR: unsupported shape in the original design\n";
        }
        int l;
        int w;
        int h;
        try {
            l = Integer.parseInt(raw[0]);
            w = Integer.parseInt(raw[1]);
            h = Integer.parseInt(raw[2]);
        } catch (NumberFormatException ex) {
            return "ERROR: the original Box accepts whole numbers (int) only\n";
        }
        DesignDemo.TightBox b = new DesignDemo.TightBox(l, w, h);
        StringBuilder s = new StringBuilder();
        s.append("> Box b = new Box(").append(l).append(", ").append(w).append(", ").append(h).append(");\n");
        s.append("> System.out.println(b.volume);\n");
        s.append(b.volume).append('\n');

        BigInteger exact = BigInteger.valueOf(l).multiply(BigInteger.valueOf(w)).multiply(BigInteger.valueOf(h));
        if (!exact.equals(BigInteger.valueOf(b.volume))) {
            s.append(String.format(Locale.US, "WARNING: int overflow! The real volume is %,d\n", exact));
        }
        if (l <= 0 || w <= 0 || h <= 0) {
            s.append("WARNING: no validation - a non-positive size was accepted\n");
        }
        return s.toString();
    }

    private String runRefactored(int idx, String[] raw) {
        double[] v = new double[raw.length];
        try {
            for (int i = 0; i < raw.length; i++) {
                v[i] = Double.parseDouble(raw[i]);
            }
        } catch (NumberFormatException ex) {
            return "ERROR: please enter valid numbers\n";
        }
        try {
            DesignDemo.Solid solid;
            switch (idx) {
                case 0:
                    solid = new DesignDemo.Box(v[0], v[1], v[2]);
                    break;
                case 1:
                    solid = new DesignDemo.Cylinder(v[0], v[1]);
                    break;
                default:
                    solid = new DesignDemo.Sphere(v[0]);
                    break;
            }
            StringBuilder s = new StringBuilder();
            s.append("> Solid s = new ").append(solid.getName()).append('(')
                    .append(String.join(", ", raw)).append(");\n");
            s.append("> System.out.println(describe(s));\n");
            s.append(DesignDemo.describe(solid)).append('\n');
            if (idx != 0) {
                s.append("> Same client code, new shape - Volume was not touched\n");
            }
            return s.toString();
        } catch (IllegalArgumentException ex) {
            return "ERROR: " + ex.getMessage() + "\n";
        }
    }

    // ----------------------------------------------------------- code samples
    private static final String ORIGINAL_CODE = Theme.lines(
            "// Java program to illustrate",
            "// tight coupling concept",
            "class Volume {",
            "    public static void main(String args[]) {",
            "        Box b = new Box(5, 5, 5);",
            "        System.out.println(b.volume);     // reaches into Box's internals",
            "    }",
            "}",
            "",
            "class Box {",
            "    public int volume;                    // public field: no encapsulation",
            "",
            "    Box(int length, int width, int height) {",
            "        this.volume = length * width * height;   // locked in at construction",
            "    }",
            "}");

    private static final String FIXED_CODE = Theme.lines(
            "// Loosely coupled version",
            "",
            "interface Solid {                         // all a client needs to know",
            "    String getName();",
            "    double getVolume();",
            "}",
            "",
            "class Box implements Solid {",
            "    private final double length;          // hidden: encapsulated",
            "    private final double width;",
            "    private final double height;",
            "",
            "    Box(double length, double width, double height) {",
            "        this.length = requirePositive(length, \"length\");",
            "        this.width  = requirePositive(width,  \"width\");",
            "        this.height = requirePositive(height, \"height\");",
            "    }",
            "",
            "    @Override public String getName()   { return \"Box\"; }",
            "    @Override public double getVolume() { return length * width * height; }",
            "",
            "    static double requirePositive(double value, String name) {",
            "        if (!(value > 0) || Double.isInfinite(value)) {",
            "            throw new IllegalArgumentException(name + \" must be a positive number\");",
            "        }",
            "        return value;",
            "    }",
            "}",
            "",
            "class Cylinder implements Solid { ... }   // new shapes plug in",
            "class Sphere   implements Solid { ... }   // without touching Volume",
            "",
            "class Volume {",
            "    public static void main(String[] args) {",
            "        Solid s = new Box(5, 5, 5);       // depends on the interface only",
            "        System.out.println(s.getName() + \" volume = \" + s.getVolume());",
            "    }",
            "}");

    // -------------------------------------------------------------- analysis
    private static String analysisHtml() {
        String[][] rows = {
            { "Access to data",
              "Volume reads the <b>public field</b> <i>b.volume</i> directly.",
              "Volume calls <b>getVolume()</b>. The fields are <b>private</b>." },
            { "Knowledge of internals",
              "Volume must know the result is stored in an <i>int</i> field called <i>volume</i>.",
              "Volume knows only the <b>Solid</b> contract: getName() and getVolume()." },
            { "Effect of changing Box",
              "Renaming or retyping the field, or calculating the volume lazily, <b>breaks Volume</b>.",
              "Box can change freely as long as getVolume() keeps its promise." },
            { "Reuse",
              "Works with <b>Box only</b>.",
              "Works with <b>any Solid</b>: Box, Cylinder, Sphere, ..." },
            { "Safety",
              "Anyone can overwrite <i>b.volume</i>; int arithmetic can overflow; negative sizes are accepted.",
              "Fields are final, values are validated and <i>double</i> arithmetic avoids the overflow." }
        };
        StringBuilder h = new StringBuilder("<html><body>");
        h.append("<p><font size='5' color='#3730A3'><b>Verdict: yes, the original code is tightly coupled</b></font></p>");
        h.append("<p>Two classes are tightly coupled when one depends on the <i>inner details</i> of the other. ");
        h.append("Here <b>Volume</b> depends on Box's public field <i>volume</i>, so the two must always change together.</p>");
        h.append("<p><font size='4' color='#3730A3'><b>How the refactored version removes the coupling</b></font></p>");
        h.append(Theme.compareTable("Problem", "ORIGINAL", "REFACTORED", rows));
        h.append("<p><b>Bonus:</b> Box now only describes a box (high cohesion), and Volume works through an ");
        h.append("interface (loose coupling) - the same rule of thumb from the lab.</p>");
        h.append("</body></html>");
        return h.toString();
    }
}
