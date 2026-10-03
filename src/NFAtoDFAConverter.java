import java.util.*;

public class NFAtoDFAConverter {

    static Map<String, Map<String, Set<String>>> nfaTransitions = new HashMap<>();
    static Set<String> nfaStates = new HashSet<>();
    static String[] alphabet;
    static Set<String> nfaAcceptStates = new HashSet<>();
    static String nfaStartState;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of NFA states: ");
        int numStates = sc.nextInt();

        System.out.print("Enter the NFA states (space-separated): ");
        for (int i = 0; i < numStates; i++) {
            nfaStates.add(sc.next());
        }

        System.out.print("Enter the number of alphabet symbols (excluding epsilon): ");
        int numSymbols = sc.nextInt();

        System.out.print("Enter the alphabet symbols (space-separated): ");
        alphabet = new String[numSymbols];
        for (int i = 0; i < numSymbols; i++) {
            alphabet[i] = sc.next();
        }

        System.out.print("Enter the start state: ");
        nfaStartState = sc.next();

        System.out.print("Enter the number of accept states: ");
        int numAccept = sc.nextInt();

        System.out.print("Enter the accept states (space-separated): ");
        for (int i = 0; i < numAccept; i++) {
            nfaAcceptStates.add(sc.next());
        }

        System.out.print("Enter the number of transitions: ");
        int numTransitions = sc.nextInt();

        System.out.println("Enter transitions (use 'eps' for epsilon):");
        System.out.println("Format: current_state symbol next_state");

        for (int i = 0; i < numTransitions; i++) {
            String from = sc.next();
            String symbol = sc.next();
            String to = sc.next();
            nfaTransitions
                .computeIfAbsent(from, k -> new HashMap<>())
                .computeIfAbsent(symbol, k -> new HashSet<>())
                .add(to);
        }

        convertToDFA();
        sc.close();
    }

    static Set<String> epsilonClosure(Set<String> states) {
        Set<String> closure = new HashSet<>(states);
        Stack<String> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            String state = stack.pop();
            Map<String, Set<String>> trans = nfaTransitions.get(state);
            if (trans != null && trans.containsKey("eps")) {
                for (String next : trans.get("eps")) {
                    if (!closure.contains(next)) {
                        closure.add(next);
                        stack.push(next);
                    }
                }
            }
        }
        return closure;
    }

    static Set<String> move(Set<String> states, String symbol) {
        Set<String> result = new HashSet<>();
        for (String state : states) {
            Map<String, Set<String>> trans = nfaTransitions.get(state);
            if (trans != null && trans.containsKey(symbol)) {
                result.addAll(trans.get(symbol));
            }
        }
        return result;
    }

    static void convertToDFA() {
        Map<Set<String>, Map<String, Set<String>>> dfaTransitions = new LinkedHashMap<>();
        Set<Set<String>> dfaAcceptStates = new HashSet<>();

        Set<String> startClosure = epsilonClosure(new HashSet<>(Collections.singleton(nfaStartState)));
        Queue<Set<String>> queue = new LinkedList<>();
        Set<Set<String>> visited = new HashSet<>();

        queue.add(startClosure);
        visited.add(startClosure);

        while (!queue.isEmpty()) {
            Set<String> current = queue.poll();
            dfaTransitions.put(current, new HashMap<>());

            for (String symbol : alphabet) {
                Set<String> moveResult = move(current, symbol);
                Set<String> nextState = epsilonClosure(moveResult);

                dfaTransitions.get(current).put(symbol, nextState);

                if (!visited.contains(nextState)) {
                    visited.add(nextState);
                    queue.add(nextState);
                }
            }

            for (String s : current) {
                if (nfaAcceptStates.contains(s)) {
                    dfaAcceptStates.add(current);
                    break;
                }
            }
        }

        System.out.println("\n=== Equivalent DFA ===");
        System.out.println("DFA States:");
        Map<Set<String>, String> stateNames = new HashMap<>();
        int index = 0;
        for (Set<String> state : dfaTransitions.keySet()) {
            String name = "D" + index++;
            stateNames.put(state, name);
            System.out.println("  " + name + " = " + state);
        }

        System.out.println("\nStart State: " + stateNames.get(startClosure));

        System.out.print("Accept States: ");
        List<String> acceptNames = new ArrayList<>();
        for (Set<String> state : dfaAcceptStates) {
            acceptNames.add(stateNames.get(state));
        }
        System.out.println(acceptNames);

        System.out.println("\nTransition Table:");
        System.out.printf("%-10s", "State");
        for (String sym : alphabet) {
            System.out.printf("%-15s", sym);
        }
        System.out.println();
        System.out.println("-".repeat(10 + 15 * alphabet.length));

        for (Map.Entry<Set<String>, Map<String, Set<String>>> entry : dfaTransitions.entrySet()) {
            System.out.printf("%-10s", stateNames.get(entry.getKey()));
            for (String sym : alphabet) {
                Set<String> target = entry.getValue().get(sym);
                String targetName = stateNames.getOrDefault(target, "∅");
                System.out.printf("%-15s", targetName);
            }
            System.out.println();
        }
    }
}
