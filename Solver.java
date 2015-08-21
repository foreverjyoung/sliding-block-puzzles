import puzzles.PuzzleSolver;
import solvers.BruteForceSolver;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The Solver class is the main class for the project. From here, the files are
 * read, solved, and show the amount of steps the program went through to solve
 * the puzzle and the amount of time it took to reach the goal file.
 * 
 */
public class Solver {
	
	public static void main(String[] args) throws FileNotFoundException {
		boolean writeResults;
		if (args.length != 2) {
			System.out.println("Invalid init and/or goal file.");
			return;
		}
		Tray initState;
		writeResults = Files.isDirectory(Paths.get("results"));

		try {
			initState = Block.readFiles(args[0], args[1]);
		} catch (IOException e) {
			System.err.println("Couldn't load files: " + e);
			return;
		} catch (IllegalArgumentException e) {
			System.err.println("Bad input files: " + e);
			return;
		}

		PuzzleSolver<Tray> solver = new BruteForceSolver<>(false);
		String[] initFilePath = args[0].split("/");
		String initFile = initFilePath[initFilePath.length - 1];

		List<Tray> states = solver.solve(initState);
		if (states == null) {
			return;
		}
		for (int k = 1; k < states.size(); k++) {
			Tray state = states.get(k);
			int fromBlock = state.getFromBlock();
			int toBlock = state.getToBlock();
			System.out.printf("%d %d %d %d%n", Tray.BlockConfig.getLeftX(fromBlock),
					Tray.BlockConfig.getTopY(fromBlock), Tray.BlockConfig.getLeftX(toBlock),
					Tray.BlockConfig.getTopY(toBlock));
		}
		if (writeResults) {
			PrintWriter resultWriter = new PrintWriter("results/" + initFile + "-result.txt");
			states.forEach(resultWriter::println);
			System.out.println();
			System.out.println("steps: " + solver.getSteps());
			if ((solver.getCompletionTime() / 1000000.0) < 100.0) {
				System.out.println("time: " + (solver.getCompletionTime() / 1000000.0) + " ms");
			} else {
				System.out.println("time: " + ((solver.getCompletionTime() / 1000000.0) / 1000.0) + " seconds");
			}
			resultWriter.close();
		}
	}
}
