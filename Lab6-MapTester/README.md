# Lab 06: Maptester (Phone Book)

A phone book built with a Java HashMap and a Swing GUI.
Each contact's name is the key and the phone number is the value.

## Classes

- **Maptester.java**: the phone book. It holds the HashMap, and `enterNumber()` adds an entry using `put()`. `lookupNumber()` finds a number by name using `get()`.
- **MapTesterGUI.java**: the graphical user interface, which runs in a single window.

## Features

- Save a contact with a name and a phone number
- Look up a number by name
- Search the contacts as you type
- Delete a contact with the bin icon in its row (asks for confirmation first)
- Activity log that shows what happens when the same name or the same number is saved again
- Input checks: the name and number cannot be empty, and the number can only contain digits and normal phone symbols

## What the HashMap shows

- **Same key:** saving the same name again replaces the old number, so there is still only one entry for that name.
- **Same value:** two different names can have the same number, and both entries are kept.

## How to run

1. Put both files in a package named `lab06`.
2. Run `MapTesterGUI.java`.
