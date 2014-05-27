package jp.ac.osaka_u.ist.sdl.cheval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

		final Set<Set<Vector>> cliques = new HashSet<Set<Vector>>();

		NODE1: for (final Entry<Vector, Set<Vector>> entry : vectorData
				.entrySet()) {

			final Set<Vector> nodes1 = new HashSet<Vector>();
			nodes1.add(entry.getKey());
			nodes1.addAll(entry.getValue());

			NODE2: for (final Vector node : entry.getValue()) {
				final Set<Vector> nodes2 = new HashSet<Vector>();
				nodes2.add(node);
				nodes2.addAll(vectorData.get(node));

				if (!nodes1.equals(nodes2)) {
					continue NODE1;
				}
			}

			cliques.add(nodes1);
		}

		return cliques;
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
					writer.write(Long.toString(node.beforeID));
					writer.write(".");
					writer.write(Long.toString(node.afterID));
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
}
