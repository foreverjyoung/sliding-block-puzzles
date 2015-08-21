package puzzles;

import java.util.List;

/**
 * The state of a puzzle
 * 
 * @param <T>
 *            the type of the current puzzle state
 */
public interface PuzzleState<T extends PuzzleState<T>> {
	/**
	 * Find possible puzzle states that can be arrived at by making a single
	 * move from this state.
	 * 
	 * @return a list of "next" states
	 */
	List<T> successors();

	/**
	 * Gets the state that that immediately preceded this state; a state that
	 * can lead to this state in one move.
	 * 
	 * @return the predecessor of this state
	 */
	T predecessor();

	/**
	 * Checks whether this state is a winning state
	 * 
	 * @return whether this is a winning state
	 */
	boolean win();

	/**
	 * Checks whether the puzzle can be solved from this state.
	 * 
	 * @return whether the puzzle is solvable from this state
	 */
	boolean canWin();

	/**
	 * Initializes redundant, expensive-to-calculate parts of this puzzle state.
	 * Called by the Tray class after it has determined that this state is
	 * "worth keeping" (for example, it has not been seen or visited before).
	 */
	void initialize();

	/**
	 * A "golf-like" (lower is better) rating of how good this state is: How
	 * close to a solution it appears to be
	 * 
	 * @return a rating of this puzzle state
	 */
	int score();
}
