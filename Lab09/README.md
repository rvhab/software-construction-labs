# Lab 9: Designing Classes - I

Software Construction lab on class design in Java, focusing on **coupling** and **cohesion**.

## About

This project shows how class design affects how easy a program is to maintain. Each task compares a poorly designed version (tightly coupled, low cohesion) with an improved version (loosely coupled, high cohesion), and a Swing GUI lets you run both versions and compare the results.

## Concepts

| Concept | Poor design | Good design |
|---|---|---|
| Coupling | Tight: a change in one class forces changes in others | Loose: classes can change independently |
| Cohesion | Low: a class mixes unrelated tasks | High: a class has one clear responsibility |

## Tasks

**Task 1: Cohesion and coupling case study**
A real-world scenario written in two versions. The first puts several jobs in one class and hard-wires its dependencies. The second splits the work into focused classes connected through interfaces. Both versions give the same output, so only the design changes.

**Task 2: Box refactoring**
The original `Box` / `Volume` code is tightly coupled because the client reads a public field directly. The improved version uses private fields and a `getVolume()` method, with checks on the input.

**Task 3: Cohesion analysis**
Answered in the lab journal: the `Name`, `Age` and `Number` classes each have a single responsibility, so the design is highly cohesive.

## Features

- Side-by-side comparison of the tight and loose versions
- Swing GUI with input validation and error messages
- Console output available for each task

## Requirements

- Java JDK 8 or later
- Eclipse (or any Java IDE)

## How to Run

**In Eclipse**
1. Clone or download this repository.
2. Create a Java project (untick *Create module-info.java*) and copy the `.java` files into `src`.
3. Right-click the main GUI class and choose **Run As → Java Application**.


## Project Structure

```
Lab9/
├── src/        # Java source files
└── README.md
```

## Concepts Used

- Coupling and cohesion
- Encapsulation (private fields and public methods)
- Interfaces and constructor-based dependency passing
- Input validation
- Java Swing

## Author

Name: Rohab Israr
Reg. No: 09-131242-077
Course: Software Construction
