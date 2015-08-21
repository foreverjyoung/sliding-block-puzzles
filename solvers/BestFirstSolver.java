package solvers;

import puzzles.PuzzleState;
import puzzles.PuzzleSolver;
import java.util.*;

/**
 * This is the solver that the program first uses when solving a Block puzzle.
 * 
 * @param <T>
 *            the type of the puzzle to solve and its state
 */
public class BestFirstSolver<T extends PuzzleState<T>> implements PuzzleSolver<T> {
	@Override
	public int getSteps() {
		return steps;
	}

	@Override
	public long getCompletionTime() {
		return nanoseconds;
	}

	private int steps;
	private long nanoseconds;

	@Override
	public List<T> solve(T initialState) {
		if (!initialState.canWin()) {
			System.out.println("No solution found.");
		}
		// Keeps tracks of previous states so we don't re-add them to the fringe
		Set<T> observedStates = new HashSet<>();
		PriorityQueue<T> fringe = new PriorityQueue<>(Comparator.comparingInt(T::score));
		observedStates.add(initialState);
		fringe.add(initialState);

		// Search for the solution
		T current;
		steps = 0;
		long startTime = System.nanoTime();
		// Keep picking the best-looking game state from the fringe
		do {
			steps++;
			current = fringe.poll();
			for (T successor : current.successors()) {
				if (!observedStates.contains(successor)) {
					successor.initialize();
					observedStates.add(successor);
					fringe.add(successor);
				}
			}
		} while (!current.win() && !fringe.isEmpty());

		nanoseconds = System.nanoTime() - startTime;

		if (current.win()) {
			LinkedList<T> winningMoves = new LinkedList<>();
			for (T step = current; step != null; step = step.predecessor()) {
				winningMoves.addFirst(step);
			}
			return winningMoves;
		} else {
			System.out.println("No solution found.");
			return null;

		}
	}
}
