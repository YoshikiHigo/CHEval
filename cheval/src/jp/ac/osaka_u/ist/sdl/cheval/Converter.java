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

		List<VectorPair> pairs = readVectorPairs(input);
		final Map<Vector, Set<Vector>> vectorData = getVectorData(pairs);
		final Set<Set<Vector>> cliques = getCliques(vectorData);
		writeCliques(cliques, output);
	}

	private static List<VectorPair> readVectorPairs(final String path) {

		final List<VectorPair> pairs = new ArrayList<VectorPair>();

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

				final VectorPair pair = new VectorPair(new Vector(
						Long.parseLong(leftBeforeID),
						Long.parseLong(leftAfterID), new int[] {}), new Vector(
						Long.parseLong(rightBeforeID),
						Long.parseLong(rightAfterID), new int[] {}),
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

	private static Map<Vector, Set<Vector>> getVectorData(
			final List<VectorPair> pairs) {

		final Map<Vector, Set<Vector>> vectorData = new HashMap<Vector, Set<Vector>>();
		for (int i = 0; i < pairs.size(); i++) {

			Set<Vector> neighbors = vectorData.get(pairs.get(i).left);
			if (null == neighbors) {
				neighbors = new HashSet<Vector>();
				vectorData.put(pairs.get(i).left, neighbors);
			}
			neighbors.add(pairs.get(i).right);

			neighbors = vectorData.get(pairs.get(i).right);
			if (null == neighbors) {
				neighbors = new HashSet<Vector>();
				vectorData.put(pairs.get(i).right, neighbors);
			}
			neighbors.add(pairs.get(i).left);
		}

		return vectorData;
	}

	private static Set<Set<Vector>> getCliques(
			final Map<Vector, Set<Vector>> vectorData) {

		final Set<Set<Vector>> allCliques = new HashSet<Set<Vector>>();

		// creating size-2 cliques
		Set<Set<Vector>> cliques = new HashSet<Set<Vector>>();
		for (final Entry<Vector, Set<Vector>> entry : vectorData.entrySet()) {
			final Vector node1 = entry.getKey();
			final Set<Vector> neighbors = entry.getValue();

			for (final Vector node2 : neighbors) {
				final Set<Vector> clique = new HashSet<Vector>();
				clique.add(node1);
				clique.add(node2);
				cliques.add(clique);
			}
		}
		System.out.println(Integer.toString(cliques.size()));

		while (!cliques.isEmpty()) {

			System.out.println("a");

			final Set<Set<Vector>> largerCliques = new HashSet<Set<Vector>>();
			getLargerCliques(cliques, largerCliques, vectorData);

			allCliques.addAll(cliques);
			cliques = largerCliques;
		}

		return allCliques;
	}

	private static void writeCliques(final Set<Set<Vector>> cliques,
			final String output) {

		try {

			final BufferedWriter writer = new BufferedWriter(new FileWriter(
					output));
			for (final Set<Vector> clique : cliques) {
				if (clique.size() < 3) {
					continue;
				}
				for (final Vector node : clique) {
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

	private static void getLargerCliques(final Set<Set<Vector>> cliques,
			final Set<Set<Vector>> largerCliques,
			final Map<Vector, Set<Vector>> vectorData) {

		final Iterator<Set<Vector>> iterator = cliques.iterator();
		while (iterator.hasNext()) {
			// System.out.println("b");
			final Set<Vector> clique = iterator.next();
			final Set<Set<Vector>> largers = getLargerCliques(clique,
					vectorData);
			if (!largers.isEmpty()) {
				largerCliques.addAll(largers);
				iterator.remove();
			}
		}
	}

	private static Set<Set<Vector>> getLargerCliques(final Set<Vector> clique,
			final Map<Vector, Set<Vector>> vectorData) {

		final Set<Set<Vector>> largerCliques = new HashSet<Set<Vector>>();

		for (final Vector node : clique) {
			// System.out.println("c");
			Set<Vector> neighbors = vectorData.get(node);
			neighbors.removeAll(clique);

			for (final Vector neighbor : neighbors) {

				Set<Vector> neighborNeighbors = vectorData.get(neighbor);
				if (neighborNeighbors.containsAll(clique)) {

					final Set<Vector> largerClique = new HashSet<Vector>();
					largerClique.addAll(clique);
					largerClique.add(neighbor);
					largerCliques.add(largerClique);
				}
			}
		}

		return largerCliques;
	}
}
