import puzzles.PuzzleState;
import java.util.*;

/**
 * The Tray class represents a Tray configuration for the current Block set-up.
 * A new Tray configuration is created for each new move that the program makes
 * while on its way to reaching the correct goal Tray. At first, the Tray was
 * represented as an ArrayList. However, this was changed to be represented as a
 * simple integer array that stored Blocks represented as integers. This was the
 * key to drastically improving the runtime of puzzles that took 10,000+ steps.
 *
 */
public class Tray implements PuzzleState<Tray> {
	private int[] board;
	private final Tray predecessor;
	private final int width;
	private final int height;
	private final int[] goal;
	private final int[] blocks;
	private final int fromBlock;
	private final int toBlock;

	/**
	 * An impossible block with its top left corner below and to the right of
	 * its bottom left, to be used as a sentinel value for the board array when
	 * there is no piece present.
	 * 
	 */
	static final int ABSENT = BlockConfig.fromCoords(1, 1, 0, 0);

	/**
	 * Tray constructor for a new Tray configuration. The constructor utilizes
	 * the BlockConfig class that is nested within this Tray class.
	 * 
	 * @param width
	 *            the width of the Tray
	 * @param height
	 *            the height of the Tray
	 * @param blocks
	 *            a set containing all of the Blocks on the board
	 * @param goal
	 *            a list of the Blocks required for the solution
	 */
	public Tray(int width, int height, int[] blocks, int[] goal) {
		predecessor = null;
		fromBlock = ABSENT;
		toBlock = ABSENT;
		this.width = width;
		this.height = height;
		this.blocks = Arrays.stream(blocks).map((k) -> k + Integer.MIN_VALUE).sorted()
				.map((k) -> k + Integer.MIN_VALUE).toArray();
		this.goal = Arrays.copyOf(goal, goal.length);
		board = new int[width * height];
		for (int k = 0; k < width * height; k++)
			board[k] = ABSENT;
		for (int block : blocks) {
			for (int x = 0; x < BlockConfig.getWidth(block); x++)
				for (int y = 0; y < BlockConfig.getHeight(block); y++)
					setTile(BlockConfig.getLeftX(block) + x, BlockConfig.getTopY(block) + y, block);
		}
	}

	/**
	 * Makes a copy of a different Tray with one Block moved.
	 * 
	 * @param prev
	 *            the Tray to start from
	 * @param currBlock
	 *            the index of the Block to move
	 * @param distance
	 *            how far a Block will be moved
	 * @param vertical
	 *            whether to move the Block vertically or horizontally
	 */
	Tray(Tray prev, int currBlock, int distance, boolean vertical) {
		predecessor = prev;
		width = prev.width;
		height = prev.height;
		goal = prev.goal;

		// Update the current array of Blocks
		blocks = Arrays.copyOf(prev.blocks, prev.blocks.length);
		fromBlock = prev.blocks[currBlock];
		toBlock = vertical ? BlockConfig.moveVertical(fromBlock, distance) : BlockConfig.moveHorizontal(fromBlock,
				distance);
		blocks[currBlock] = toBlock;

		// Restores the order in the Blocks if necessary
		if (vertical) {
			if (Integer.compareUnsigned(toBlock, fromBlock) < 0) {
				for (int k = currBlock - 1; k >= 0 && Integer.compareUnsigned(toBlock, blocks[k]) < 0; k--) {
					int tmp = blocks[k + 1];
					blocks[k + 1] = blocks[k];
					blocks[k] = tmp;
				}
			} else {
				for (int k = currBlock + 1; (k < (blocks.length - 1))
						&& (Integer.compareUnsigned(toBlock, blocks[k]) > 0); k++) {
					int tmp = blocks[k - 1];
					blocks[k - 1] = blocks[k];
					blocks[k] = tmp;

				}
			}
		}
	}

	/**
	 * Initializes the new Tray configuration.
	 */
	public void initialize() {
		board = Arrays.copyOf(predecessor.board, predecessor.board.length);
		for (int y = BlockConfig.getTopY(fromBlock); y <= BlockConfig.getBottomY(fromBlock); y++) {
			for (int x = BlockConfig.getLeftX(fromBlock); x <= BlockConfig.getRightX(fromBlock); x++) {
				setTile(x, y, ABSENT);
			}
		}
		for (int y = BlockConfig.getTopY(toBlock); y <= BlockConfig.getBottomY(toBlock); y++) {
			for (int x = BlockConfig.getLeftX(toBlock); x <= BlockConfig.getRightX(toBlock); x++) {
				setTile(x, y, toBlock);
			}
		}
	}

	/**
	 * Initializes the tile that a Block is currently on.
	 * 
	 * @param x
	 *            x-coordinate of the location to be checked
	 * @param y
	 *            y-coordinate of the location to be checked
	 * @param block
	 *            the Block at (x, y)
	 */
	private void setTile(int x, int y, int block) {
		board[x + y * width] = block;
	}

	/**
	 * Gets the block at the given location on the board
	 * 
	 * @param x
	 *            the x coordinate of the location to check
	 * @param y
	 *            the y coordinate of the location to check
	 * @return The Block at (x, y)
	 */
	public int getBlockAt(int x, int y) {
		return board[x + y * width];
	}

	@Override
	public List<Tray> successors() {
		List<Tray> result = new ArrayList<>();
		for (int k = 0; k < blocks.length; k++) {
			result.addAll(possibleMoves(k));
		}
		return result;
	}

	@Override
	public Tray predecessor() {
		return predecessor;
	}

	/**
	 * Makes a list of possible moves for the given Block
	 * 
	 * @param blockID
	 *            the index of the Block to list possible moves for
	 * @return a list of moves for the given Block
	 */
	private List<Tray> possibleMoves(int blockID) {
		List<Tray> result = new ArrayList<>();
		int block = blocks[blockID];
		// Check if there is an empty space above
		for (int k = 1, spaceAbove = spaceAbove(block); k <= spaceAbove; k++) {
			result.add(new Tray(this, blockID, -k, true));
		}
		// Check if there is an empty space below
		for (int k = 1, spaceBelow = spaceBelow(block); k <= spaceBelow; k++) {
			result.add(new Tray(this, blockID, k, true));
		}
		// Check if there is an empty space to the left
		for (int k = 1, spaceLeft = spaceLeft(block); k <= spaceLeft; k++) {
			result.add(new Tray(this, blockID, -k, false));
		}
		// Check if there is an empty space to the right
		for (int k = 1, spaceRight = spaceRight(block); k <= spaceRight; k++) {
			result.add(new Tray(this, blockID, k, false));
		}
		return result;
	}

	/**
	 * Checks space above the current Block
	 * 
	 * @param block
	 *            the Block being checked
	 * @return the space above the current Block
	 */
	public int spaceAbove(int block) {
		int result = 0;
		for (int y = BlockConfig.getTopY(block) - 1; y >= 0; y--) {
			for (int x = BlockConfig.getLeftX(block), right = BlockConfig.getRightX(block); x <= right; x++) {
				if (getBlockAt(x, y) != ABSENT) {
					return result;
				}
			}
			result++;
		}
		return result;
	}

	/**
	 * Checks space below the current Block
	 * 
	 * @param block
	 *            the Block being checked
	 * @return the space below the current Block
	 */
	public int spaceBelow(int block) {
		int result = 0;
		for (int y = BlockConfig.getBottomY(block) + 1; y < height; y++) {
			for (int x = BlockConfig.getLeftX(block), right = BlockConfig.getRightX(block); x <= right; x++) {
				if (getBlockAt(x, y) != ABSENT)
					return result;
			}
			result++;
		}
		return result;
	}

	/**
	 * Checks space to the left of the current Block
	 * 
	 * @param block
	 *            the Block being checked
	 * @return the space to the left of the current Block
	 */
	public int spaceLeft(int block) {
		int result = 0;
		for (int x = BlockConfig.getLeftX(block) - 1; x >= 0; x--) {
			for (int y = BlockConfig.getTopY(block), bottom = BlockConfig.getBottomY(block); y <= bottom; y++) {
				if (getBlockAt(x, y) != ABSENT)
					return result;
			}
			result++;
		}
		return result;
	}

	/**
	 * Checks space to the right of the current Block
	 * 
	 * @param block
	 *            the Block being checked
	 * @return the space to the right of the current Block
	 */
	public int spaceRight(int block) {
		int result = 0;
		for (int x = BlockConfig.getRightX(block) + 1; x < width; x++) {
			for (int y = BlockConfig.getTopY(block), bottom = BlockConfig.getBottomY(block); y <= bottom; y++) {
				if (getBlockAt(x, y) != ABSENT)
					return result;
			}
			result++;
		}
		return result;
	}

	@Override
	public boolean win() {
		for (int target : goal)
			if (target != getBlockAt(BlockConfig.getLeftX(target), BlockConfig.getTopY(target)))
				return false;
		return true;
	}

	@Override
	public boolean canWin() {
		// If the goal Blocks aren't all present, then the puzzle is unsolvable
		for (int goalBlock : goal) {
			if (Arrays.stream(blocks).noneMatch(
					block -> BlockConfig.getWidth(goalBlock) == BlockConfig.getWidth(block)
							&& BlockConfig.getHeight(goalBlock) == BlockConfig.getHeight(block)))
				return false;
		}
		return true;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	int[] getBlocks() {
		return blocks;
	}

	public int[] getGoal() {
		return goal;
	}

	public int getFromBlock() {
		return fromBlock;
	}

	public int getToBlock() {
		return toBlock;
	}

	@Override
	public boolean equals(Object o) {
		return Arrays.equals(blocks, ((Tray) o).blocks);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(blocks);
	}

	@Override
	public int score() {
		return 0;
	}

	/**
	 * This nested BLockConfig class represents the Blocks as integer values. A
	 * single integer value stores the entire state of a Block: byte 0: top Y;
	 * byte 1: left X; byte 2: bottom Y; byte 3: right X
	 *
	 */
	public static class BlockConfig {
		public static int getTopY(int description) {
			return description >>> 24;
		}

		public static int getLeftX(int description) {
			return (description >>> 16) & 255;
		}

		public static int getBottomY(int description) {
			return (description >>> 8) & 255;
		}

		public static int getRightX(int description) {
			return description & 255;
		}

		public static int getWidth(int description) {
			return ((description) - (description >>> 16) + 1) & 255;
		}

		public static int getHeight(int description) {
			return ((description >>> 8) - (description >>> 24) + 1) & 255;
		}

		public static int moveVertical(int block, int distance) {
			return block + distance * ((1 << 24) + (1 << 8));
		}

		public static int moveHorizontal(int block, int distance) {
			return block + distance * ((1 << 16) + 1);
		}

		public static int fromCoords(int leftX, int topY, int rightX, int bottomY) {
			assert leftX >= 0 && leftX < 256;
			assert topY >= 0 && topY < 256;
			assert rightX >= 0 && rightX < 256;
			assert bottomY >= 0 && bottomY < 256;
			return (topY << 24) + (leftX << 16) + (bottomY << 8) + rightX;
		}

		public static int fromCornerSize(int leftX, int topY, int width, int height) {
			assert leftX >= 0 && leftX < 256;
			assert topY >= 0 && topY < 256;
			assert width > 0 && width <= 256;
			assert height > 0 && height <= 256;
			return (topY << 24) + (leftX << 16) + ((topY + height - 1) << 8) + (leftX + width - 1);
		}
	}
}
