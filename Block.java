import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The Block class loads the Block configurations for the game. It does this by
 * reading input files from the command line using 'java.io.File' and utilizes
 * the Tray class to return a Tray configuration with the proper Block set-up.
 * All Blocks must be valid.
 * 
 */
public class Block {
	private static Scanner s1;
	private static Scanner s2;

	/**
	 * Creates a new Block by reading the initial files and the goal files line
	 * by line. All Blocks are represented by a String of 4 numbers: its top
	 * left corner's row, its top left corner's column, its bottom right
	 * corner's row, and its bottom right corner's column.
	 * 
	 * @param init
	 *            the file that represents the initial sliding block puzzle
	 * @param goal
	 *            the file that represents the goal sliding block puzzle
	 * @return a new Tray configuration of possible moves
	 * @throws IOException
	 *             if the scanner picks up invalid inputs or outputs
	 */
	public static Tray readFiles(String init, String goal) throws IOException {
		// Block set-up at its current step
		List<Integer> myBlocks = new ArrayList<>();
		// Represents how the goal block set-up should be
		List<Integer> goalBlocks = new ArrayList<>();
		s1 = new Scanner(new File(init));
		int width, height;

		try {
			// The first number of the initial file represents the board's width
			width = s1.nextInt();
			// The second number represents the board's height
			height = s1.nextInt();
		} catch (InputMismatchException e) {
			throw new IllegalArgumentException("Invalid block format", e);
		}

		while (s1.hasNext()) {
			try {
				int leftX = s1.nextInt();
				int topY = s1.nextInt();
				int rightX = s1.nextInt();
				int bottomY = s1.nextInt();
				if (!validBlock(leftX, topY, rightX, bottomY)) {
					throw new IllegalArgumentException("Invalid block in puzzle");
				}
				myBlocks.add(Tray.BlockConfig.fromCoords(leftX, topY, rightX, bottomY));
			} catch (InputMismatchException e) {
				throw new IllegalArgumentException("Invalid block format", e);
			}
		}

		s2 = new Scanner(new File(goal));
		while (s2.hasNext()) {
			try {
				int leftX = s2.nextInt();
				int topY = s2.nextInt();
				int rightX = s2.nextInt();
				int bottomY = s2.nextInt();
				if (!validBlock(leftX, topY, rightX, bottomY)) {
					throw new IllegalArgumentException("Invalid block in goal");
				}
				goalBlocks.add(Tray.BlockConfig.fromCoords(leftX, topY, rightX, bottomY));
			} catch (InputMismatchException e) {
				throw new IllegalArgumentException("Invalid block format", e);
			}
		}
		int[] blocksArr = myBlocks.stream().mapToInt(Integer::intValue).toArray();
		int[] goalArr = goalBlocks.stream().mapToInt(Integer::intValue).toArray();
		return new Tray(width, height, blocksArr, goalArr);
	}

	/**
	 * Checks to make sure that each Block is valid. The maximum width and
	 * height of a Tray is 256x256 due to the way the Blocks are represented
	 * (256 bits). Our runtime is exceptional but if we encounter a Tray that
	 * has either a width or a height that is greater than 256, we are screwed.
	 * 
	 * @param leftX
	 *            top left corner's row (x-value)
	 * @param topY
	 *            top left corner's column (y-value)
	 * @param rightX
	 *            bottom right corner's row
	 * @param bottomY
	 *            bottom right corner's column
	 * @return true if all Block coordinates are within 0 and 255
	 */
	private static boolean validBlock(int leftX, int topY, int rightX, int bottomY) {
		return (rightX >= leftX && bottomY >= topY) && (leftX >= 0 && leftX <= 255) && (topY >= 0 && topY <= 255)
				&& (rightX >= 0 && rightX <= 255) && (bottomY >= 0 && bottomY <= 255);
	}
}
