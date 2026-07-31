import java.util.*;

/**
 * 8-Queens Problem Solver  –  CS4200 Project 2
 *
 * Two algorithms:
 *   1. Steepest-Ascent Hill Climbing (no restarts)
 *   2. Min-Conflicts
 *
 * Compile : javac NQueens.java
 * Run     : java NQueens
 */
public class NQueens {

    static final int N = 8;
    static final Random RAND = new Random();

    // ─── Board Utilities ────────────────────────────────────────────────────

    /** One queen per column; value = row of that queen. */
    static int[] randomState() {
        int[] s = new int[N];
        for (int c = 0; c < N; c++) s[c] = RAND.nextInt(N);
        return s;
    }

    /** Total number of attacking pairs in the board. */
    static int countAttacks(int[] s) {
        int attacks = 0;
        for (int i = 0; i < N; i++)
            for (int j = i + 1; j < N; j++)
                if (s[i] == s[j] || Math.abs(s[i] - s[j]) == j - i)
                    attacks++;
        return attacks;
    }

    /**
     * Number of queens that attack position (col, row),
     * excluding the queen already in column col.
     */
    static int conflictsAt(int[] s, int col, int row) {
        int cnt = 0;
        for (int k = 0; k < N; k++) {
            if (k == col) continue;
            if (s[k] == row || Math.abs(s[k] - row) == Math.abs(k - col)) cnt++;
        }
        return cnt;
    }

    static void printBoard(int[] s) {
        System.out.println("  Positions (col->row): " + Arrays.toString(s));
        for (int row = 0; row < N; row++) {
            System.out.print("  ");
            for (int col = 0; col < N; col++)
                System.out.print(s[col] == row ? "Q " : ". ");
            System.out.println();
        }
        System.out.println();
    }

    // ─── Result ─────────────────────────────────────────────────────────────

    static class Result {
        final boolean solved;
        final int     steps;
        final int[]   state;

        Result(boolean solved, int steps, int[] state) {
            this.solved = solved;
            this.steps  = steps;
            this.state  = state.clone();
        }
    }

    // ─── 1. Steepest-Ascent Hill Climbing ───────────────────────────────────

    /**
     * Moves to the neighbour with the fewest attacking pairs.
     * Stops when no strictly improving neighbour exists (local minimum)
     * or when attacks == 0 (solution found).
     * No random restarts — one run per call.
     */
    static Result hillClimbing() {
        int[] state = randomState();
        int steps = 0;

        while (true) {
            int h = countAttacks(state);
            if (h == 0) return new Result(true, steps, state);

            int bestH   = h;
            int bestCol = -1;
            int bestRow = -1;

            for (int col = 0; col < N; col++) {
                for (int row = 0; row < N; row++) {
                    if (row == state[col]) continue;
                    int orig = state[col];
                    state[col] = row;
                    int nh = countAttacks(state);
                    state[col] = orig;
                    if (nh < bestH) {
                        bestH   = nh;
                        bestCol = col;
                        bestRow = row;
                    }
                }
            }

            if (bestCol == -1) return new Result(false, steps, state); // local minimum
            state[bestCol] = bestRow;
            steps++;
        }
    }

    // ─── 2. Min-Conflicts ───────────────────────────────────────────────────

    /**
     * Repeatedly picks a conflicted queen at random and moves it to the row
     * with the fewest conflicts.  Ties broken randomly.
     */
    static Result minConflicts(int maxSteps) {
        int[] state = randomState();

        for (int step = 1; step <= maxSteps; step++) {
            if (countAttacks(state) == 0) return new Result(true, step, state);

            // Collect all conflicted columns
            int nc = 0;
            int[] conf = new int[N];
            for (int col = 0; col < N; col++)
                if (conflictsAt(state, col, state[col]) > 0)
                    conf[nc++] = col;

            if (nc == 0) return new Result(true, step, state);

            // Pick a random conflicted column
            int col = conf[RAND.nextInt(nc)];

            // Find the row(s) with minimum conflicts for that column
            int minC = Integer.MAX_VALUE;
            int nr = 0;
            int[] best = new int[N];
            for (int row = 0; row < N; row++) {
                int c = conflictsAt(state, col, row);
                if (c < minC)       { minC = c; nr = 0; best[nr++] = row; }
                else if (c == minC) { best[nr++] = row; }
            }
            state[col] = best[RAND.nextInt(nr)];
        }

        boolean solved = countAttacks(state) == 0;
        return new Result(solved, maxSteps, state);
    }

    // ─── Analysis ───────────────────────────────────────────────────────────

    static void runAnalysis(int instances) {
        System.out.println("Running analysis on " + instances + " random instances...\n");

        // ── Hill Climbing ──
        int    hcSolved = 0;
        long   hcSteps  = 0;
        long   hcNs     = 0;

        for (int i = 0; i < instances; i++) {
            long t0 = System.nanoTime();
            Result r = hillClimbing();
            hcNs += System.nanoTime() - t0;
            if (r.solved) hcSolved++;
            hcSteps += r.steps;
        }

        System.out.printf("--- Steepest-Ascent Hill Climbing (%d instances) ---%n", instances);
        System.out.printf("  Solved       : %d / %d  (%.1f%%)%n",
                hcSolved, instances, 100.0 * hcSolved / instances);
        System.out.printf("  Avg steps    : %.2f%n", (double) hcSteps / instances);
        System.out.printf("  Avg time     : %.4f ms%n%n",
                (double) hcNs / instances / 1_000_000.0);

        // ── Min-Conflicts ──
        int    mcSolved   = 0;
        long   mcSteps    = 0;
        long   mcNs       = 0;
        int    maxSteps   = 1000;

        for (int i = 0; i < instances; i++) {
            long t0 = System.nanoTime();
            Result r = minConflicts(maxSteps);
            mcNs += System.nanoTime() - t0;
            if (r.solved) mcSolved++;
            mcSteps += r.steps;
        }

        System.out.printf("--- Min-Conflicts (%d instances, max %d steps/instance) ---%n",
                instances, maxSteps);
        System.out.printf("  Solved       : %d / %d  (%.1f%%)%n",
                mcSolved, instances, 100.0 * mcSolved / instances);
        System.out.printf("  Avg steps    : %.2f%n", (double) mcSteps / instances);
        System.out.printf("  Avg time     : %.4f ms%n%n",
                (double) mcNs / instances / 1_000_000.0);
    }

    // ─── Main ───────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   8-Queens Problem Solver  (CS4200 P2)  ");
        System.out.println("==========================================\n");

        // ── Three sample solutions via Min-Conflicts ──
        System.out.println("=== Three Sample Solutions (Min-Conflicts) ===\n");
        int found = 0;
        while (found < 3) {
            Result r = minConflicts(10_000);
            if (r.solved) {
                System.out.println("Solution " + (++found) + "  (steps: " + r.steps + "):");
                printBoard(r.state);
            }
        }

        // ── Statistical analysis over 500 instances ──
        System.out.println("=== Algorithm Analysis ===\n");
        runAnalysis(500);
    }
}
