import java.util.ArrayList;

public class WDGraph {
	private ArrayList<Vertex> verts;
	private ArrayList<Edge> edges;
	private int vertCount;

	public WDGraph() {
		this.verts = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		this.vertCount = 0;
	}

	/**
	 * Adds a vertex to the graph.
	 * @param v Vertex to add to the graph
	 */
	public void AddVert(Vertex v) {
		this.verts.add(v);
		this.vertCount++;
	}

	/**
	 * Adds an edge between two vertices on the graph.
	 * @param fromVert Edge's source vertex
	 * @param toVert Edge's destination vertex
	 */
	public void AddEdge(Vertex fromVert, Vertex toVert) {
		this.edges.add(new Edge(fromVert, toVert));
	}

	/**
	 * Gets the number of vertices in the graph.
	 * @return Number of vertices in the graph
	 */
	public int GetVertCount() {
		int temp = this.vertCount;
		return temp;
	}

	/**
	 * Gets the list of vertices in the graph.
	 * @return Graph's vertices
	 */
	public ArrayList<Vertex> GetVerts() {
		return this.verts;
	}

	/**
	 * Gets a vertex given its index.
	 * @return Vertex at the specidied index
	 */
	public Vertex GetVertAt(int index) {
		return this.verts.get(index);
	}

	/**
	 * Get the list of edges in the graph.
	 * @return Graph's edges
	 */
	public ArrayList<Edge> GetEdges() {
		return this.edges;
	}

	/**
	 * Gets a given vertex's index.
	 * @param vert Vertex to retrieve the index of
	 * @return Index of the given vertex
	 */
	public int GetVertIndex(Vertex vert) {
		for (int i = 0; i < this.verts.size(); i++) {
			if (this.verts.get(i).equals(vert)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets whether or not the graph has an edge between two given points.
	 * @param fVert Source vertex of edge to look for
	 * @param tVert Destination vertex of edge to look for
	 * @return Whether or not an edge exists between the source and destination vertices
	 */
	public boolean HasEdge(Vertex fVert, Vertex tVert) {
		Edge testEdge = new Edge(fVert, tVert);
		for (Edge e : this.edges) {
			if (e.equals(testEdge))
				return true;
		}
		return false;
	}

	/**
	 * Gets the distance between two vertices on the graph if they have an edge.
	 * @param fromVert Source vertex
	 * @param toVert Destination vertex
	 * @return Distance between the source and destination vertices if an edge exists, positive infinity otherwise
	 */
	public double GetDistance(Vertex fromVert, Vertex toVert) {
		if (HasEdge(fromVert, toVert))
			return (new Edge(fromVert, toVert).GetWeight());
		else
			return Double.POSITIVE_INFINITY;
	}
}