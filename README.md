# FLA Course Project

Java implementations of fundamental automata theory concepts, done as part of the course project for **22AIE302 — Formal Language and Automata**.

## Programs

1. **DFA Simulation** — Simulate a DFA and check if an input string is accepted or rejected.
2. **NFA to DFA Conversion** — Convert an NFA to an equivalent DFA using subset construction.
3. **DFA State Reduction** — Minimize a DFA by identifying and merging equivalent states (table-filling algorithm).
4. **Intersection of Two DFAs** — Construct a DFA accepting L1 ∩ L2 using product construction.
5. **PDA Simulation** — Simulate a Pushdown Automaton with stack operations and determine acceptance.

## How to Run

Each program is a standalone Java file. Compile and run individually:

```bash
cd src
javac DFASimulator.java
java DFASimulator
```

All programs use console-based input via `Scanner`.

## License

[MIT](LICENSE)
