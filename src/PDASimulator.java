import java.util.*;

public class PDASimulator {

    static Map<String, List<Transition>> transitions = new HashMap<>();

    static class Transition {
        String toState;
        String popSymbol;
        String pushSymbols;

        Transition(String toState, String popSymbol, String pushSymbols) {
            this.toState = toState;
            this.popSymbol = popSymbol;
            this.pushSymbols = pushSymbols;
        }
    }

    static class Configuration {
        String state;
        int inputPos;
        Stack<String> stack;

        Configuration(String state, int inputPos, Stack<String> stack) {
            this.state = state;
            this.inputPos = inputPos;
            this.stack = stack;
        }

        @SuppressWarnings("unchecked")
        Configuration copy() {
            Stack<String> newStack = new Stack<>();
            newStack.addAll(this.stack);
            return new Configuration(this.state, this.inputPos, newStack);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of states: ");
        int numStates = sc.nextInt();

        System.out.print("Enter the states (space-separated): ");
        String[] states = new String[numStates];
        for (int i = 0; i < numStates; i++) {
            states[i] = sc.next();
        }

        System.out.print("Enter the number of input alphabet symbols: ");
        int numInputSymbols = sc.nextInt();

        System.out.print("Enter the input alphabet symbols (space-separated): ");
        String[] inputAlphabet = new String[numInputSymbols];
        for (int i = 0; i < numInputSymbols; i++) {
            inputAlphabet[i] = sc.next();
        }

        System.out.print("Enter the number of stack alphabet symbols: ");
        int numStackSymbols = sc.nextInt();

        System.out.print("Enter the stack alphabet symbols (space-separated): ");
        String[] stackAlphabet = new String[numStackSymbols];
        for (int i = 0; i < numStackSymbols; i++) {
            stackAlphabet[i] = sc.next();
        }

        System.out.print("Enter the start state: ");
        String startState = sc.next();

        System.out.print("Enter the initial stack symbol: ");
        String initialStackSymbol = sc.next();

        System.out.print("Enter the number of accept states: ");
        int numAccept = sc.nextInt();

        Set<String> acceptStates = new HashSet<>();
        if (numAccept > 0) {
            System.out.print("Enter the accept states (space-separated): ");
            for (int i = 0; i < numAccept; i++) {
                acceptStates.add(sc.next());
            }
        }

        System.out.print("Accept by (1) Final State, (2) Empty Stack, or (3) Both? ");
        int acceptMode = sc.nextInt();

        System.out.print("Enter the number of transitions: ");
        int numTransitions = sc.nextInt();

        System.out.println("Enter transitions:");
        System.out.println("Format: current_state input_symbol pop_symbol next_state push_symbols");
        System.out.println("  Use 'eps' for epsilon (no input / empty push)");
        System.out.println("  Push symbols are written top-first (e.g., 'AB' pushes A on top, then B)");

        for (int i = 0; i < numTransitions; i++) {
            String from = sc.next();
            String inputSym = sc.next();
            String popSym = sc.next();
            String to = sc.next();
            String pushSyms = sc.next();

            String key = from + "," + inputSym + "," + popSym;
            transitions.computeIfAbsent(key, k -> new ArrayList<>())
                .add(new Transition(to, popSym, pushSyms));
        }

        System.out.print("Enter the input string (use 'eps' for empty string): ");
        String input = sc.next();
        if (input.equals("eps")) {
            input = "";
        }

        boolean accepted = simulate(input, startState, initialStackSymbol, acceptStates, acceptMode);

        if (accepted) {
            System.out.println("\nResult: ACCEPTED");
        } else {
            System.out.println("\nResult: REJECTED");
        }

        sc.close();
    }

    static boolean simulate(String input, String startState, String initialStackSymbol,
                            Set<String> acceptStates, int acceptMode) {

        Queue<Configuration> queue = new LinkedList<>();
        Stack<String> initialStack = new Stack<>();
        initialStack.push(initialStackSymbol);
        queue.add(new Configuration(startState, 0, initialStack));

        int maxSteps = 10000;
        int steps = 0;

        System.out.println("\nSimulation trace:");

        while (!queue.isEmpty() && steps < maxSteps) {
            Configuration config = queue.poll();
            steps++;

            String stackStr = config.stack.toString();
            String remaining = input.substring(config.inputPos);
            System.out.println("  State: " + config.state +
                             ", Remaining input: " + (remaining.isEmpty() ? "ε" : remaining) +
                             ", Stack: " + stackStr);

            if (config.inputPos == input.length()) {
                boolean acceptedByFinalState = acceptStates.contains(config.state);
                boolean acceptedByEmptyStack = config.stack.isEmpty();

                switch (acceptMode) {
                    case 1:
                        if (acceptedByFinalState) return true;
                        break;
                    case 2:
                        if (acceptedByEmptyStack) return true;
                        break;
                    case 3:
                        if (acceptedByFinalState || acceptedByEmptyStack) return true;
                        break;
                }
            }

            if (!config.stack.isEmpty()) {
                String topSymbol = config.stack.peek();

                if (config.inputPos < input.length()) {
                    String inputSym = String.valueOf(input.charAt(config.inputPos));
                    String key = config.state + "," + inputSym + "," + topSymbol;
                    List<Transition> trans = transitions.get(key);
                    if (trans != null) {
                        for (Transition t : trans) {
                            Configuration next = config.copy();
                            next.stack.pop();
                            next.state = t.toState;
                            next.inputPos = config.inputPos + 1;
                            if (!t.pushSymbols.equals("eps")) {
                                for (int i = t.pushSymbols.length() - 1; i >= 0; i--) {
                                    next.stack.push(String.valueOf(t.pushSymbols.charAt(i)));
                                }
                            }
                            queue.add(next);
                        }
                    }
                }

                String epsKey = config.state + ",eps," + topSymbol;
                List<Transition> epsTrans = transitions.get(epsKey);
                if (epsTrans != null) {
                    for (Transition t : epsTrans) {
                        Configuration next = config.copy();
                        next.stack.pop();
                        next.state = t.toState;
                        if (!t.pushSymbols.equals("eps")) {
                            for (int i = t.pushSymbols.length() - 1; i >= 0; i--) {
                                next.stack.push(String.valueOf(t.pushSymbols.charAt(i)));
                            }
                        }
                        queue.add(next);
                    }
                }
            }
        }

        if (steps >= maxSteps) {
            System.out.println("  (Exploration limit reached)");
        }

        return false;
    }
}
