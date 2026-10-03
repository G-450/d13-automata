import java.util.*;

public class DFAMinimizer {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of states: ");
        int numStates = sc.nextInt();

        System.out.print("Enter the states (space-separated): ");
        String[] states = new String[numStates];
        Map<String, Integer> stateIndex = new HashMap<>();
        for (int i = 0; i < numStates; i++) {
            states[i] = sc.next();
            stateIndex.put(states[i], i);
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

        boolean[][] distinguishable = new boolean[numStates][numStates];

        for (int i = 0; i < numStates; i++) {
            for (int j = i + 1; j < numStates; j++) {
                boolean iAccept = acceptStates.contains(states[i]);
                boolean jAccept = acceptStates.contains(states[j]);
                if (iAccept != jAccept) {
                    distinguishable[i][j] = true;
                }
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < numStates; i++) {
                for (int j = i + 1; j < numStates; j++) {
                    if (distinguishable[i][j]) continue;

                    for (String symbol : alphabet) {
                        String nextI = transitions.getOrDefault(states[i], Collections.emptyMap()).get(symbol);
                        String nextJ = transitions.getOrDefault(states[j], Collections.emptyMap()).get(symbol);

                        if (nextI == null || nextJ == null) continue;
                        if (nextI.equals(nextJ)) continue;

                        int ni = stateIndex.get(nextI);
                        int nj = stateIndex.get(nextJ);
                        int min = Math.min(ni, nj);
                        int max = Math.max(ni, nj);

                        if (distinguishable[min][max]) {
                            distinguishable[i][j] = true;
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("\n=== Distinguishability Table ===");
        System.out.printf("%-8s", "");
        for (int i = 0; i < numStates - 1; i++) {
            System.out.printf("%-8s", states[i]);
        }
        System.out.println();

        for (int j = 1; j < numStates; j++) {
            System.out.printf("%-8s", states[j]);
            for (int i = 0; i < j; i++) {
                System.out.printf("%-8s", distinguishable[i][j] ? "X" : "-");
            }
            System.out.println();
        }

        int[] group = new int[numStates];
        Arrays.fill(group, -1);
        int groupCount = 0;

        for (int i = 0; i < numStates; i++) {
            if (group[i] != -1) continue;
            group[i] = groupCount;
            for (int j = i + 1; j < numStates; j++) {
                if (!distinguishable[i][j]) {
                    group[j] = groupCount;
                }
            }
            groupCount++;
        }

        Map<Integer, List<String>> groups = new LinkedHashMap<>();
        for (int i = 0; i < numStates; i++) {
            groups.computeIfAbsent(group[i], k -> new ArrayList<>()).add(states[i]);
        }

        System.out.println("\n=== Minimized DFA ===");
        System.out.println("Equivalent state groups:");
        Map<Integer, String> groupNames = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : groups.entrySet()) {
            String name = "{" + String.join(",", entry.getValue()) + "}";
            groupNames.put(entry.getKey(), name);
            System.out.println("  " + name);
        }

        String startGroup = groupNames.get(group[stateIndex.get(startState)]);
        System.out.println("\nStart State: " + startGroup);

        System.out.print("Accept States: ");
        Set<String> minimizedAccept = new LinkedHashSet<>();
        for (String s : acceptStates) {
            minimizedAccept.add(groupNames.get(group[stateIndex.get(s)]));
        }
        System.out.println(minimizedAccept);

        System.out.println("\nTransition Table:");
        System.out.printf("%-20s", "State");
        for (String sym : alphabet) {
            System.out.printf("%-20s", sym);
        }
        System.out.println();
        System.out.println("-".repeat(20 + 20 * alphabet.length));

        Set<Integer> printed = new HashSet<>();
        for (int i = 0; i < numStates; i++) {
            if (printed.contains(group[i])) continue;
            printed.add(group[i]);

            System.out.printf("%-20s", groupNames.get(group[i]));
            for (String sym : alphabet) {
                String next = transitions.getOrDefault(states[i], Collections.emptyMap()).get(sym);
                if (next != null) {
                    System.out.printf("%-20s", groupNames.get(group[stateIndex.get(next)]));
                } else {
                    System.out.printf("%-20s", "-");
                }
            }
            System.out.println();
        }

        System.out.println("\nTotal states reduced from " + numStates + " to " + groupCount);

        sc.close();
    }
}
