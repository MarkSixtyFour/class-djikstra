import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class Dijkstra {
	public static void main(String[] args) {
		File file = new File("map.dat");
		Scanner sc;

		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File not found, is it in the right path?");
			return;
		}

		WDGraph graph = new WDGraph();

		/* Set up the scanner to use proper delimiters.
		 * The default doesn't inlcude newlines, carriage
		 * returns, or commas. */
		sc.useDelimiter("[\\r\\n\\p{javaWhitespace},]");
		
		// Populate the graph with vertices read from map.dat.

		int vertCount = sc.nextInt();	// Number of vertices
		sc.next();			// Discard the newline

		for (int i = 0; i < vertCount; i++) {
			double lat, lon;
			float hi;
			String str;
			
			sc.next();
			sc.skip(",");
			lat = sc.nextDouble();
			lon = sc.nextDouble();
			hi = sc.nextFloat();
			str = sc.nextLine();

			/* The map.dat is inconistent with its field separation.
			 * Some streets are separated by commas, others by spaces.
			 * This cleans up the street names. */
			if (str.startsWith(","))
				str = str.replaceFirst(",", "");
			if (str.startsWith(" "))
				str = str.replaceFirst(" ", "");

			graph.AddVert(new Vertex(lat, lon, hi, str));
		}

		// Populate the graph's edges read from map.dat.

		int numEdges = sc.nextInt();	// Number of edges
		sc.next();			// Discard the newline

		for (int i = 0; i < numEdges; i++) {
			int fromVert;
			int toVert;
			int directionType;

			fromVert = sc.nextInt();
			toVert = sc.nextInt();
			directionType = sc.nextInt();

			graph.AddEdge(graph.GetVertAt(fromVert), graph.GetVertAt(toVert));

			// A directionType of 2 means the edge is bidirectional.
			if (directionType == 2)
				graph.AddEdge(graph.GetVertAt(toVert), graph.GetVertAt(fromVert));

			sc.next();
		}

		// Read cases from the map.dat. These are the source and destination sets.

		int numCases = sc.nextInt();	// Number of cases
		sc.next();			// Discard the newline
		
		try {
			for (int i = 0; i < numCases; i++) {
				Vertex fromVert = graph.GetVertAt(sc.nextInt());
				Vertex toVert = graph.GetVertAt(sc.nextInt());
				ShortestPath(graph, fromVert, toVert);

				if (sc.hasNext())
					sc.next();
			}
		} catch (IOException e) {
			System.out.println("Couldn't write to the directions file, exiting...");
			sc.close();
			return;
		}

		sc.close();
	}

	/**
	 * Calculates the GPS distance between two vertices in kilometers.
	 * @param fromVert Source vertex
	 * @param toVert Destination vertex
	 * @return Distance between the source and destination vertices in kilometers.
	 */
	public static double GPSDistance(Vertex fromVert, Vertex toVert) {
		double earthRadiusKm = 6371.0d;		// Radius of the Earth in km
		double lat1 = fromVert.GetLatitude();	// Latitude of the first point
		double lon1 = fromVert.GetLongitude();	// Longitude of the first point
		double lat2 = toVert.GetLatitude();	// Latitude of the second point
		double lon2 = toVert.GetLongitude();	// Longitude of the second point

		double dLat = lat2 - lat1;
		double dLong = lon2 - lon1;

		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.pow(Math.sin(dLat / 2), 2) 
				    + Math.pow(Math.sin(dLong / 2), 2)
				    * Math.cos(lat1)
				    * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return earthRadiusKm * c;
	}

	/**
	 * Finds the shortest path between a source and destination vertex using Dijkstra's algorithm, and gives direction in the console and to a file directions.dat.
	 * @param graph Graph to find the path in
	 * @param fromVert Source vertex
	 * @param toVert Destination vertex
	 * @throws IOException When the file can't be opened or written to
	 */
	private static void ShortestPath(WDGraph graph, Vertex fromVert, Vertex toVert) throws IOException {
		FileWriter fw = new FileWriter("directions.dat", true);
		BufferedWriter bw = new BufferedWriter(fw);	// BufferedWriter allows adding newlines, which is needed

		// This part is the actual Dijkstra algorithm.

		int graphSize = graph.GetVertCount();
		double dist[] = new double[graphSize];
		HashSet<Vertex> unvisited = new HashSet<Vertex>();
		int previous[] = new int[graphSize];

		Arrays.fill(dist, Double.POSITIVE_INFINITY);
		dist[graph.GetVertIndex(fromVert)] = 0;
		Arrays.fill(previous, -1);

		for (Vertex v : graph.GetVerts()) {
			unvisited.add(v);
		}

		while (!unvisited.isEmpty()) {
			Vertex u = new Vertex();
			double dist_u = Double.POSITIVE_INFINITY;
			for (Vertex v : unvisited) {
				if (dist[graph.GetVertIndex(v)] < dist_u) {
					u = v;
					dist_u = dist[graph.GetVertIndex(v)];
				}
			}

			unvisited.remove(u);

			HashSet<Edge> neighbors = new HashSet<Edge>();
			for (Vertex v : unvisited) {
				if (graph.HasEdge(u, v)) {
					neighbors.add(new Edge(u, v));
				}
			}

			for (Edge e : neighbors) {
				double alt = dist[graph.GetVertIndex(u)] + e.GetWeight();
				if (alt < dist[graph.GetVertIndex(e.GetToVert())]) {
					dist[graph.GetVertIndex(e.GetToVert())] = alt;
					previous[graph.GetVertIndex(e.GetToVert())] = graph.GetVertIndex(u);
				}
			}
		}

		// This part is the directions output.

		ArrayList<Vertex> path = new ArrayList<Vertex>();
		ArrayList<Double> distances = new ArrayList<Double>();
		double totalDist = 0.0d;

		/* Get the shortest path via backtracking prev[].
		 * This is for convenience of traversal. */
		int current = graph.GetVertIndex(toVert);
		while (current != -1) {
			path.add(graph.GetVertAt(current));
			current = previous[current];
		}

		// Make a parallel ArrayList containing edge distances.
		for (int i = 0; i < path.size() - 1; i++) {
			double d = graph.GetDistance(path.get(i), path.get(i + 1));
			distances.add(d);
			totalDist += d;
		}

		/* Since Dijkstra's algorithm sets up prev[] in a manner that
		 * you can backtrack from the destination to the source, getting
		 * the path in that way gives you the desired path in reverse
		 * order. Therefore, they're reversed for convenience here. */
		Collections.reverse(path);
		Collections.reverse(distances);

		System.out.println("Path from " + fromVert.GetStreet() + " to " + toVert.GetStreet()
				   + " (" + (float) totalDist + "km)");
		System.out.println("-----------------------");
		bw.write("Path from " + fromVert.GetStreet() + " to " + toVert.GetStreet() 
			 + " (" + (float) totalDist + "km)");
		bw.newLine();
		bw.write("-----------------------");
		bw.newLine();

		for (int i = 0; i < path.size() - 1; i++) {
			System.out.println("Go " + distances.get(i).floatValue() + "km" + " from "
					   + path.get(i).GetStreet() + " to " + path.get(i + 1).GetStreet());
			bw.write("Go " + distances.get(i).floatValue() + "km" + " from "
				 + path.get(i).GetStreet() + " to " + path.get(i + 1).GetStreet());
			bw.newLine();
		}

		System.out.println();
		bw.newLine();

		// Close the BufferedWriter to prevent a resource leak.
		bw.close();
	}

	// MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKxddddollllllxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWkoodxO0000KKKKKKKOxdddddxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMMMMMKxdoo0MMMMMMMMMMMMMMMMMMMMMMMKkooxWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMKoookXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKll0MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMo:WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWkcoXMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMOcdMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNdlkWMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMk;WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM0:kMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMM;OMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNMMMMMMMMMMMMMMMMMd:WMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMWcoMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWcWMMMMMMMMMMMMMMMMMk;WMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMocMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMkkMMMMMMMNNMMMMMMMMMx;MMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMM'0MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXoMMMMMMXlNMMMMMMMMMM,OMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMM0'WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMlMMMMMMlWMMMMMMMMMMWO:MMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMM,XMMMMMMMMMMMMMMMMMMMMMMMMMMXxWMMMMMOdMMMMMWoMMMMMMMMMWMOl.NMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMM'MMMMMMMMMMMMMMMMMMMMMMMMMMMMKlxXMMkxMMMMMMXdMMMMMMMXx0MMMK,0MMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMX'MMMMMMMMMMMMMMMMMMMMMMNNMMMMMMNxxkOWM0NMWWNoMMMXKkdxNMMMWM0,MMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMM0:MMMMWKKKKKWMMMMMKdolc:.....,coooolllOlKNXOWdWdWNNWMMMMMMMMW.NMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMkcMMMc.clllc;:;;;:odddo: oxkxOdl:. ,dl,.'OXc0;:'WNkl:;;;;;::: xMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMX,MMM.cMMMMMMMMMWKOOOxc..l:. .:x0WXKXkck' ..;,.  . .oxO0O0WWWW oMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMM'XMMl.xMMMMMMMMMMMKk0O..xc.'''..;;oNWMMc 0MMWMK,  '..;oXNWMMX .MMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMK'NMMMMMMMMMMMMMMMMMWo. xkkxclddl;OMMMM:.MMMMMN;  c'....xNNWK  MMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMocMMMMMMMMMMMMMMMMMW0, o000oo00x:xMMMM.:MMMMMOk,.xkKXok:0NNM; MMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMM,OMMMMMMMMMMMMMMWxONXo,;'........::,;'OMMMMKKx, ;,;l:;;;,;;..NMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMX;OMMMNxNMMKNMMWo0MMMNdW0dOOXWWWWWWMMMMMMMMK:NMXKOldk:cOKKlx:MMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMN:oMMMNxx;':MMOxMMMMMoKddkWMMMK0MMMMMMMMMMM0cxWMMOOMkxWMWlkcMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMdcMMMMMMx,MMd0MMMMMdkkKMMMNl:XMMWMMMMMMMMMMOlWMOd0OdWMWx,dMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMM:kMMMWKO:WMNWMMMMMkd0MMWk;OMW;ddXMMMMMMMMMWckMWxxkdNMMl NMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMX.MMMWK0;;MMMMMMMMXdKMW;oWMMW;kWMMXdxMMMWM;,WMMOxddXMK.XMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMN.MMMMMWW;oMMMMMMM0xWN,xWKNMMWOdOMWNMMMKdlo:dMMKxkdKXkMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMN.MMMMWOk;:0MMMMMWxON,odxOWMMWkloc,o;cdddOWX.XMKxXkO'0MMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMN.MMMMMMMN'cMMMMMWdOllMoMMMN: l.x:'x;lK.X..OlcW0kK,dNWMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMX.MMMMMMMM::XMMMM0dl'WMlOMM.                k,OxXl.WMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMloMMMMMMMMoMMNMMMO0OdMMXOMX                 xWXkM,MMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMO;MMMMMMMMx0MK,kk0dWWxXWMMMK                 OMO0O'XMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMM:kMMMMMMMMMMMMMMK..oWKxdKMMM.               .WWdXcNMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMk,NMMMMMMMMMMMMMMKk;d,0xXWMMM.               cMXok;0MMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMo:WMMMMMMMXOdKMMMMMMMMo,OOMMMM.               kMOd;NMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMNlcMMMMMMMNcoKMMMMMMMMMMM:.oWMMM,               NWx,cMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMM0ocKMMMMMMMK;0MMMMMMMMMMMMMk'lkWMM;              cMN:;XMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMN0xodd;o0MMMMMM0dlNMMMMMMMMMMMMMMMNo:dMMx              KNx .lddddox0NNNWMMMMMMMMMMMMM
	// MMMX000dcdONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMo NMMN             .Wx,.WMMMMMWXOxkONMMMMMMMMMMMMM
	// MMMX000WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW000ONk,:KMM;            oMN:'0MMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWkxx cMMO           .WMO.xWMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNl0KM;         .KMK.kMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMOckWd.      oWMWo0MMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMOodlkOlclOWXXK,XMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMO:d0Oc,dl.,o0MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
	// MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWckONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
}
