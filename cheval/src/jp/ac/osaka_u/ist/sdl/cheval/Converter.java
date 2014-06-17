package jp.ac.osaka_u.ist.sdl.cheval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class Converter {

	public static void main(final String[] args) {

		if (2 != args.length) {
			System.err.println("the number of arguments must be 2.");
			System.exit(0);
		}

		final String input = args[0];
		final String output = args[1];

		List<ChangePair> pairs = readVectorPairs(input);
		final Map<Change, Set<Change>> vectorData = getVectorData(pairs);
		final Set<Set<Change>> cliques = getCliques(vectorData);
		writeCliques(cliques, output);
	}

	private static List<ChangePair> readVectorPairs(final String path) {

		final List<ChangePair> pairs = new ArrayList<ChangePair>();

		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					path));
			while (reader.ready()) {
				final String line = reader.readLine();
				final StringTokenizer tokenizer = new StringTokenizer(line,
						", ");
				final String leftBeforeID = tokenizer.nextToken();
				final String leftAfterID = tokenizer.nextToken();
				final String rightBeforeID = tokenizer.nextToken();
				final String rightAfterID = tokenizer.nextToken();
				final String similarity = tokenizer.nextToken();
				final String leftText = tokenizer.nextToken();
				final String rightText = tokenizer.nextToken();

				final ChangePair pair = new ChangePair(new Change(
						Long.parseLong(leftBeforeID),
						Long.parseLong(leftAfterID), leftText), new Change(
						Long.parseLong(rightBeforeID),
						Long.parseLong(rightAfterID), rightText),
						Double.parseDouble(similarity));
				pairs.add(pair);
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		return pairs;
	}

	private static Map<Change, Set<Change>> getVectorData(
			final List<ChangePair> pairs) {

		final Map<Change, Set<Change>> vectorData = new HashMap<Change, Set<Change>>();
		for (int i = 0; i < pairs.size(); i++) {

			Set<Change> neighbors = vectorData.get(pairs.get(i).left);
			if (null == neighbors) {
				neighbors = new HashSet<Change>();
				vectorData.put(pairs.get(i).left, neighbors);
			}
			neighbors.add(pairs.get(i).right);

			neighbors = vectorData.get(pairs.get(i).right);
			if (null == neighbors) {
				neighbors = new HashSet<Change>();
				vectorData.put(pairs.get(i).right, neighbors);
			}
			neighbors.add(pairs.get(i).left);
		}

		return vectorData;
	}

	private static Set<Set<Change>> getCliques(
			final Map<Change, Set<Change>> vectorData) {

		final Set<Set<Change>> allCliques = new HashSet<Set<Change>>();

		// creating size-2 cliques
		Set<Set<Change>> cliques = new HashSet<Set<Change>>();
		for (final Entry<Change, Set<Change>> entry : vectorData.entrySet()) {
			final Change node1 = entry.getKey();
			final Set<Change> neighbors = entry.getValue();

			for (final Change node2 : neighbors) {
				final Set<Change> clique = new HashSet<Change>();
				clique.add(node1);
				clique.add(node2);
				cliques.add(clique);
			}
		}
		System.out.println(Integer.toString(cliques.size()));

		while (!cliques.isEmpty()) {

			System.out.println("a");

			final Set<Set<Change>> largerCliques = new HashSet<Set<Change>>();
			getLargerCliques(cliques, largerCliques, vectorData);

			allCliques.addAll(cliques);
			cliques = largerCliques;
		}

		return allCliques;
	}

	private static void writeCliques(final Set<Set<Change>> cliques,
			final String output) {

		try {

			final BufferedWriter writer = new BufferedWriter(new FileWriter(
					output));
			for (final Set<Change> clique : cliques) {
				if (clique.size() < 3) {
					continue;
				}
				for (final Change node : clique) {
					writer.write(Long.toString(node.beforeMethodID));
					writer.write(".");
					writer.write(Long.toString(node.afterMethodID));
					writer.write(",");
				}
				writer.newLine();
			}
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void getLargerCliques(final Set<Set<Change>> cliques,
			final Set<Set<Change>> largerCliques,
			final Map<Change, Set<Change>> vectorData) {

		final Iterator<Set<Change>> iterator = cliques.iterator();
		while (iterator.hasNext()) {
			// System.out.println("b");
			final Set<Change> clique = iterator.next();
			final Set<Set<Change>> largers = getLargerCliques(clique,
					vectorData);
			if (!largers.isEmpty()) {
				largerCliques.addAll(largers);
				iterator.remove();
			}
		}
	}

	private static Set<Set<Change>> getLargerCliques(final Set<Change> clique,
			final Map<Change, Set<Change>> vectorData) {

		final Set<Set<Change>> largerCliques = new HashSet<Set<Change>>();

		for (final Change node : clique) {
			// System.out.println("c");
			Set<Change> neighbors = vectorData.get(node);
			neighbors.removeAll(clique);

			for (final Change neighbor : neighbors) {

				Set<Change> neighborNeighbors = vectorData.get(neighbor);
				if (neighborNeighbors.containsAll(clique)) {

					final Set<Change> largerClique = new HashSet<Change>();
					largerClique.addAll(clique);
					largerClique.add(neighbor);
					largerCliques.add(largerClique);
				}
			}
		}

		return largerCliques;
	}
}
