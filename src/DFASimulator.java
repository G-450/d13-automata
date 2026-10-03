import java.util.*;

public class DFASimulator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of states: ");
        int numStates = sc.nextInt();

        System.out.print("Enter the states (space-separated): ");
        String[] states = new String[numStates];
        for (int i = 0; i < numStates; i++) {
            states[i] = sc.next();
        }

        System.out.print("Enter the number of alphabet symbols: ");
        int numSymbols = sc.nextInt();

        System.out.print("Enter the alphabet symbols (space-separated): ");
        String[] alphabet = new String[numSymbols];
        for (int i = 0; i < numSymbols; i++) {
            alphabet[i] = sc.next();
        }

        System.out.print("Enter the start state: ");
        String startState = sc.next();

        System.out.print("Enter the number of accept states: ");
        int numAccept = sc.nextInt();

        System.out.print("Enter the accept states (space-separated): ");
        Set<String> acceptStates = new HashSet<>();
        for (int i = 0; i < numAccept; i++) {
            acceptStates.add(sc.next());
        }

        Map<String, Map<String, String>> transitions = new HashMap<>();
        System.out.println("Enter the transition function:");
        System.out.println("Format: current_state symbol next_state");
        System.out.println("Enter " + (numStates * numSymbols) + " transitions:");

        for (int i = 0; i < numStates * numSymbols; i++) {
            String from = sc.next();
            String symbol = sc.next();
            String to = sc.next();
            transitions.computeIfAbsent(from, k -> new HashMap<>()).put(symbol, to);
        }

        System.out.print("Enter the input string (use 'epsilon' for empty string): ");
        String input = sc.next();
        if (input.equals("epsilon")) {
            input = "";
        }

        String currentState = startState;
        boolean valid = true;

        System.out.println("\nSimulation trace:");
        System.out.println("Start state: " + currentState);

        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));
            Map<String, String> stateTransitions = transitions.get(currentState);

            if (stateTransitions == null || !stateTransitions.containsKey(symbol)) {
                System.out.println("No transition defined for state '" + currentState + "' on symbol '" + symbol + "'");
                valid = false;
                break;
            }

            String nextState = stateTransitions.get(symbol);
            System.out.println("  δ(" + currentState + ", " + symbol + ") = " + nextState);
            currentState = nextState;
        }

        if (valid) {
            System.out.println("Final state: " + currentState);
            if (acceptStates.contains(currentState)) {
                System.out.println("Result: ACCEPTED");
            } else {
                System.out.println("Result: REJECTED");
            }
        } else {
            System.out.println("Result: REJECTED (invalid transition)");
        }

        sc.close();
    }
}
