# CS4200 Project 2 – N-Queens (N = 8)
## Local Search Algorithms

---

## 1. Approach

The board is represented as an integer array of length 8, where index `i` is column `i` and the value is the row of the queen in that column. This representation guarantees one queen per column automatically, so only row and diagonal conflicts must be tracked.

### 1.1 Steepest-Ascent Hill Climbing

The heuristic **h** is the number of attacking pairs of queens. Starting from a random initial state the algorithm:

1. Evaluates every neighbour (move one queen to any other row in its column — 7 × 8 = 56 candidates).
2. Selects the neighbour with the **strictly smallest** h.
3. If no neighbour improves h the search terminates at a local minimum (failure).
4. If h reaches 0 a solution has been found.

No random restarts are performed; each instance is one single run from a fresh random state.

### 1.2 Min-Conflicts

Starting from a random initial state the algorithm:

1. If no attacks exist, return the solution.
2. Randomly select one of the conflicted queens.
3. Move that queen to the row in its column that minimises the number of conflicts (ties broken randomly).
4. Repeat up to `maxSteps = 1000` iterations.

---

## 2. Analysis (500 random instances)

| Algorithm | Solved | Success Rate | Avg Steps | Avg Time |
|---|---|---|---|---|
| Steepest-Ascent Hill Climbing | 70 / 500 | **14.0 %** | 3.17 | 0.038 ms |
| Min-Conflicts (max 1000 steps) | 474 / 500 | **94.8 %** | 115.96 | 0.047 ms |

---

## 3. Findings and Explanation

### Why does Hill Climbing solve only ~14% of cases?

The 8-queens landscape is rich in **local minima** — states where every single-queen move makes things worse or equal, yet the board is not a solution. Russell & Norvig (AIMA) report the same ~14% figure. In the other 86% of runs the algorithm reaches one of these plateaus after only a few moves (average 3 steps) and cannot escape without a restart.

The low average step count (3.17) also shows that when hill climbing *does* succeed it gets there quickly; the difficulty is purely about the initial placement landing in a basin that leads to a solution.

### Why does Min-Conflicts solve ~95% of cases?

Min-Conflicts avoids the local-minimum trap by:

* **Randomised variable selection** — it picks any conflicted queen at random, introducing diversity.
* **Tie-breaking** — when several rows are equally good, one is chosen at random, which helps escape plateaus.

For 8-queens the algorithm typically needs only 5–200 steps (average ~116 here) and converges well within the 1000-step budget. The ~5% failures are cases where the search cycles on a hard plateau within the step limit; increasing `maxSteps` or adding a restart on failure would push success above 99%.

---

## 4. Three Sample Solutions

### Solution 1 — `[3, 7, 0, 4, 6, 1, 5, 2]`
```
. . Q . . . . .
. . . . . Q . .
. . . . . . . Q
Q . . . . . . .
. . . Q . . . .
. . . . . . Q .
. . . . Q . . .
. Q . . . . . .
```

### Solution 2 — `[5, 2, 0, 6, 4, 7, 1, 3]`
```
. . Q . . . . .
. . . . . . Q .
. Q . . . . . .
. . . . . . . Q
. . . . Q . . .
Q . . . . . . .
. . . Q . . . .
. . . . . Q . .
```

### Solution 3 — `[4, 1, 5, 0, 6, 3, 7, 2]`
```
. . . Q . . . .
. Q . . . . . .
. . . . . . . Q
. . . . . Q . .
Q . . . . . . .
. . Q . . . . .
. . . . Q . . .
. . . . . . Q .
```

---

## 5. How to Compile and Run

```bash
javac NQueens.java
java NQueens
```

Requires Java 8 or later.  Full program output is also saved in `sample_output.txt`.
