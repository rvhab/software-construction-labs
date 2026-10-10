import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Responder {

    private final HashMap<String, List<String>> responses = new HashMap<>();
    private final HashMap<String, String> synonyms = new HashMap<>();
    private final HashMap<String, Integer> nextIndex = new HashMap<>();
    private final List<String> defaultResponses = new ArrayList<>();
    private final Set<String> exitWords = new LinkedHashSet<>(
            Arrays.asList("bye", "exit", "quit", "goodbye", "q"));

    private int defaultIndex = 0;
    private String lastTopic = null;

    public Responder() {
        fillResponses();
        fillSynonyms();
        fillDefaults();
    }

    public boolean isExit(String input) {
        return exitWords.contains(input.trim().toLowerCase());
    }

    public String getLastTopic() {
        return lastTopic;
    }

    public String generateResponse(String input) {
        Set<String> words = tokenize(input);

        // Count how many keywords of each topic appear in the message
        LinkedHashMap<String, Integer> hits = new LinkedHashMap<>();
        for (String word : words) {
            String topic = synonyms.get(word);
            if (topic != null) {
                hits.merge(topic, 1, Integer::sum);
            }
        }

        // Pick the topic with the most matches (first found wins a tie)
        String best = null;
        int max = 0;
        for (String topic : hits.keySet()) {
            if (hits.get(topic) > max) {
                max = hits.get(topic);
                best = topic;
            }
        }

        lastTopic = best;

        if (best == null) {
            String reply = defaultResponses.get(defaultIndex);
            defaultIndex = (defaultIndex + 1) % defaultResponses.size();
            return reply;
        }

        // Rotate through the responses of that topic
        List<String> list = responses.get(best);
        int i = nextIndex.getOrDefault(best, 0);
        nextIndex.put(best, (i + 1) % list.size());
        return list.get(i);
    }
    public void reset() {
        nextIndex.clear();
        defaultIndex = 0;
        lastTopic = null;
    }

    private Set<String> tokenize(String input) {
        String clean = input.toLowerCase().replace("wi-fi", "wifi");
        Set<String> words = new LinkedHashSet<>();
        for (String w : clean.split("[^a-z]+")) {
            if (!w.isEmpty()) {
                words.add(w);
            }
        }
        return words;
    }

    private void addTopic(String topic, String... replies) {
        responses.put(topic, new ArrayList<>(Arrays.asList(replies)));
        synonyms.put(topic, topic); // the topic name is itself a keyword
    }

    private void addSynonyms(String topic, String... words) {
        for (String w : words) {
            synonyms.put(w, topic);
        }
    }

    private void fillResponses() {
        addTopic("greeting",
                "Hello! Welcome to TechSupport. Please describe the problem you are facing.",
                "Hi there! Tell me what is going wrong and I will try to help.");

        addTopic("thanks",
                "You're welcome! Is there anything else I can help you with?",
                "Glad I could help. Let me know if another issue comes up.");

        addTopic("crash",
                "Crashes are often caused by corrupted files or low memory. Try restarting the application and your computer.",
                "If the crash continues, check for software updates and make sure your system meets the minimum requirements.",
                "Try reinstalling the application. If it still crashes, please share the exact error message you see.");

        addTopic("slow",
                "A slow system is usually caused by too many background programs. Close anything you are not using.",
                "Check your free disk space - below 10% free can noticeably slow a computer down.",
                "Run a virus scan and restart your computer. If it is still slow, a hardware upgrade (RAM or SSD) may be needed.");

        addTopic("install",
                "Make sure you are using the latest installer and that you have administrator rights.",
                "Check that you have enough free disk space and temporarily disable your antivirus during installation.",
                "If installation keeps failing, download the installer again - the file may be corrupted.");

        addTopic("password",
                "Use the 'Forgot password' link on the login page to reset your password by email.",
                "Check that Caps Lock is off. After several failed attempts accounts may be locked for 15 minutes.",
                "If you no longer have access to your recovery email, contact the administrator to verify your identity.");

        addTopic("network",
                "Restart your router by unplugging it for 30 seconds, then plug it back in.",
                "Make sure Wi-Fi is turned on and you are connected to the correct network. Try forgetting and rejoining it.",
                "If other devices connect fine, update your network adapter driver. Otherwise contact your internet provider.");

        addTopic("virus",
                "Disconnect from the internet and run a full scan with your antivirus software.",
                "Do not open unknown email attachments or links. Change your passwords after the system is clean.",
                "If the scan cannot remove the threat, boot in Safe Mode and scan again or contact IT security.");

        addTopic("update",
                "Open your system settings and check for updates. Keep the computer plugged in while updating.",
                "If an update fails, restart and try again. Make sure there is at least 10 GB of free disk space.",
                "Persistent update failures can be fixed with the built-in troubleshooter in your system settings.");

        addTopic("printer",
                "Check that the printer is switched on, has paper and ink, and is connected to the same network.",
                "Remove stuck jobs from the print queue, then restart both the printer and the computer.",
                "Reinstall the printer driver from the manufacturer's website if it still does not respond.");

        addTopic("battery",
                "Reduce screen brightness and close background apps to extend battery life.",
                "If the laptop will not charge, try a different power outlet and inspect the charger cable for damage.",
                "A battery older than 2-3 years may need replacing. Check the battery health report in your system settings.");
    }

    private void fillSynonyms() {
        addSynonyms("greeting", "hi", "hello", "hey", "morning", "afternoon", "evening");
        addSynonyms("thanks", "thank", "thanks", "thx", "cheers");
        addSynonyms("crash", "crashes", "crashing", "crashed", "freeze", "freezes", "freezing",
                "frozen", "hang", "hangs", "stuck", "error", "bug");
        addSynonyms("slow", "lag", "laggy", "lagging", "sluggish", "performance", "delay", "slowly");
        addSynonyms("install", "installing", "installation", "installed", "setup", "download", "uninstall");
        addSynonyms("password", "login", "log", "signin", "forgot", "locked", "account", "username");
        addSynonyms("network", "wifi", "internet", "router", "connection", "connect", "online", "offline");
        addSynonyms("virus", "malware", "trojan", "antivirus", "spyware", "hacked", "infected");
        addSynonyms("update", "updates", "updating", "upgrade", "patch", "version");
        addSynonyms("printer", "print", "printing", "scanner", "scan", "ink", "paper");
        addSynonyms("battery", "charge", "charging", "charger", "power", "drain", "drains");
    }

    private void fillDefaults() {
        defaultResponses.add("Sorry, I can't answer that. Could you describe the problem using different words?");
        defaultResponses.add("Could you give me more details? For example, mention the device or program involved.");
        defaultResponses.add("I could not find a matching topic. Try keywords such as crash, slow, install, password, network, virus, update, printer or battery.");
    }
}