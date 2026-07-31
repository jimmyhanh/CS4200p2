# CS4200 Project 2 – N-Queens (N = 8)

Local search algorithms for solving the 8-Queens problem, implemented in **Java**.

## Algorithms

| Algorithm | Success Rate |
|---|---|
| Steepest-Ascent Hill Climbing | ~14 % |
| Min-Conflicts | ~95 % |

## How to Compile and Run

Requires **Java 8** or later.

```bash
# Compile
javac NQueens.java

# Run (prints 3 sample solutions + analysis over 500 instances)
java NQueens
```

Sample output is saved in `sample_output.txt`.

## Files

| File | Description |
|---|---|
| `NQueens.java` | Full Java source – both algorithms + analysis |
| `report.md` | Project report (approach, analysis, findings, sample solutions) |
| `sample_output.txt` | Captured program output |
