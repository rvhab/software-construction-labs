# TechSupport Assistant

A keyword-based tech support chatbot with a Swing GUI, built in Java using `HashMap` collections.

Lab 6 – Using Maps for Associations and Using Library Classes.

## Features

- Chat-style interface (Java Swing) with a dark sidebar and message bubbles
- Keyword detection using HashMaps
- Synonym matching (for example, "freezes", "hangs" and "frozen" all map to the *crash* topic)
- Several responses per topic, which rotate each time the same topic comes up
- Default reply when no keyword is recognised
- Conversation history panel showing the time and detected topic of each message
- Input checks: empty messages and messages over 200 characters
- Type `bye`, `exit` or `quit` to end the chat, and use **New Session** to start again

## Supported Topics

crash, slow, install, password, network, virus, update, printer, battery (plus greetings and thanks)

## How It Works

`Responder.java` uses three HashMaps:

| HashMap | Purpose |
|---|---|
| `HashMap<String, String> synonyms` | Maps every keyword or synonym to a main topic |
| `HashMap<String, List<String>> responses` | Maps each topic to a list of responses |
| `HashMap<String, Integer> nextIndex` | Remembers which response comes next for each topic |

When the user sends a message, it is split into words, and each word is looked up in `synonyms`. The topic with the most matching words is chosen, and its next response is returned. If nothing matches, a default response is used.

## Project Structure

```
TechSupport/
└── src/
    ├── Responder.java        # HashMap logic (keywords, responses, defaults)
    └── TechSupportGUI.java   # Swing interface and program entry point
```

## Requirements

- Java JDK 8 or later
- Eclipse (or any Java IDE)

## Run in Eclipse

1. Clone or download this repository.
2. In Eclipse, choose **File → Import → Existing Projects into Workspace** (or create a new Java project and copy the two files into `src`).
3. Make sure there is no `module-info.java`, or the classes are in a package.
4. Right-click `TechSupportGUI.java` and choose **Run As → Java Application**.

## Run from the Command Line

```bash
cd src
javac *.java
java TechSupportGUI
```

## Example Inputs

| Input | Result |
|---|---|
| `my app keeps freezing` | Crash response |
| `my printer is not working` | Printer response (ask again for the next response) |
| `12345` or `asdfgh` | Default "can't answer" response |
| `bye` | Ends the session |

## Concepts Used

- `HashMap` with `put()` and `get()`
- Library classes (`java.util`, `javax.swing`, `java.time`)
- Collections of collections (`HashMap<String, List<String>>`)
- Event-driven GUI programming

## Author

Rohab Israr (09-131242-077)
