import java.util.*;

public class DFAIntersection {

    static String[] readAlphabet(Scanner sc) {
        System.out.print("Enter the number of alphabet symbols: ");
        int n = sc.nextInt();
        System.out.print("Enter the alphabet symbols (space-separated): ");
        String[] alphabet = new String[n];
        for (int i = 0; i < n; i++) {
            alphabet[i] = sc.next();
        }
        return alphabet;
    }

    static String[] readStates(Scanner sc) {
        System.out.print("Enter the number of states: ");
        int n = sc.nextInt();
        System.out.print("Enter the states (space-separated): ");
        String[] states = new String[n];
        for (int i = 0; i < n; i++) {
            states[i] = sc.next();
        }
        return states;
    }

    static Set<String> readAcceptStates(Scanner sc) {
        System.out.print("Enter the number of accept states: ");
        int n = sc.nextInt();
        System.out.print("Enter the accept states (space-separated): ");
        Set<String> accept = new HashSet<>();
        for (int i = 0; i < n; i++) {
            accept.add(sc.next());
        }
        return accept;
    }

    static Map<String, Map<String, String>> readTransitions(Scanner sc, int numStates, int numSymbols) {
        Map<String, Map<String, String>> transitions = new HashMap<>();
        System.out.println("Enter transitions (format: current_state symbol next_state):");
        System.out.println("Enter " + (numStates * numSymbols) + " transitions:");
        for (int i = 0; i < numStates * numSymbols; i++) {
            String from = sc.next();
            String symbol = sc.next();
            String to = sc.next();
            transitions.computeIfAbsent(from, k -> new HashMap<>()).put(symbol, to);
        }
        return transitions;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== DFA 1 ===");
        String[] states1 = readStates(sc);
        String[] alphabet1 = readAlphabet(sc);
        System.out.print("Enter the start state: ");
        String start1 = sc.next();
        Set<String> accept1 = readAcceptStates(sc);
        Map<String, Map<String, String>> trans1 = readTransitions(sc, states1.length, alphabet1.length);

        System.out.println("\n=== DFA 2 ===");
        String[] states2 = readStates(sc);
        String[] alphabet2 = readAlphabet(sc);
        System.out.print("Enter the start state: ");
        String start2 = sc.next();
        Set<String> accept2 = readAcceptStates(sc);
        Map<String, Map<String, String>> trans2 = readTransitions(sc, states2.length, alphabet2.length);

        Set<String> commonAlphabet = new LinkedHashSet<>(Arrays.asList(alphabet1));
        commonAlphabet.retainAll(new HashSet<>(Arrays.asList(alphabet2)));
        String[] alphabet = commonAlphabet.toArray(new String[0]);

        System.out.println("\n=== Product Construction (Intersection) ===");

        List<String> productStates = new ArrayList<>();
        Map<String, Map<String, String>> productTrans = new LinkedHashMap<>();
        Set<String> productAccept = new LinkedHashSet<>();
        String productStart = "(" + start1 + "," + start2 + ")";

        for (String s1 : states1) {
            for (String s2 : states2) {
                String productState = "(" + s1 + "," + s2 + ")";
                productStates.add(productState);

                if (accept1.contains(s1) && accept2.contains(s2)) {
                    productAccept.add(productState);
                }

                productTrans.put(productState, new HashMap<>());
                for (String sym : alphabet) {
                    String next1 = trans1.getOrDefault(s1, Collections.emptyMap()).get(sym);
                    String next2 = trans2.getOrDefault(s2, Collections.emptyMap()).get(sym);
                    if (next1 != null && next2 != null) {
                        productTrans.get(productState).put(sym, "(" + next1 + "," + next2 + ")");
                    }
                }
            }
        }

        System.out.println("States: " + productStates);
        System.out.println("Start State: " + productStart);
        System.out.println("Accept States: " + productAccept);
        System.out.println("Alphabet: " + Arrays.toString(alphabet));

        System.out.println("\nTransition Table:");
        System.out.printf("%-15s", "State");
        for (String sym : alphabet) {
            System.out.printf("%-15s", sym);
        }
        System.out.println();
        System.out.println("-".repeat(15 + 15 * alphabet.length));

        for (String state : productStates) {
            System.out.printf("%-15s", state);
            for (String sym : alphabet) {
                String next = productTrans.get(state).getOrDefault(sym, "-");
                System.out.printf("%-15s", next);
            }
            System.out.println();
        }

        System.out.print("\nTest a string? (yes/no): ");
        String choice = sc.next();
        if (choice.equalsIgnoreCase("yes")) {
            System.out.print("Enter the input string (use 'epsilon' for empty): ");
            String input = sc.next();
            if (input.equals("epsilon")) input = "";

            String current = productStart;
            boolean valid = true;

            for (int i = 0; i < input.length(); i++) {
                String sym = String.valueOf(input.charAt(i));
                String next = productTrans.getOrDefault(current, Collections.emptyMap()).get(sym);
                if (next == null) {
                    valid = false;
                    break;
                }
                current = next;
            }

            if (valid && productAccept.contains(current)) {
                System.out.println("Result: ACCEPTED (string is in L1 ∩ L2)");
            } else {
                System.out.println("Result: REJECTED (string is NOT in L1 ∩ L2)");
            }
        }

        sc.close();
    }
}
