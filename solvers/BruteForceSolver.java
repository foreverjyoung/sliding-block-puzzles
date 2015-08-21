package solvers;

import puzzles.PuzzleState;
import puzzles.PuzzleSolver;
import java.util.*;

/**
 * This is a solver that implements a BFS (breadth-first-search) when trying to
 * solve a Block puzzle. However, there were problems with it so a DFS
 * (depth-first-search) is used instead.
 *
 * @param <T>
 *            the type of the puzzle to solve and its state
 */
public class BruteForceSolver<T extends PuzzleState<T>> implements PuzzleSolver<T> {
	private final boolean breadthFirst;

	public BruteForceSolver(boolean breadthFirst) {
		this.breadthFirst = breadthFirst;
	}

	public BruteForceSolver() {
		this(true);
	}

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
		Set<T> observedStates = new HashSet<>();
		Deque<T> fringe = new ArrayDeque<>();
		observedStates.add(initialState);
		fringe.add(initialState);
		T current;
		steps = 0;
		long startTime = System.nanoTime();
		do {
			steps++;
			if (breadthFirst) {
				current = fringe.removeFirst();
			} else {
				current = fringe.removeLast();
			}
			for (T successor : current.successors()) {
				if (!observedStates.contains(successor)) {
					successor.initialize();
					observedStates.add(successor);
					fringe.addLast(successor);
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
