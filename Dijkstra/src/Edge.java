public class Edge implements Comparable<Edge> {
	private Vertex fromVert;
	private Vertex toVert;
	private double weight;	// Distance

	public Edge() {
		this.fromVert = null;
		this.toVert = null;
		this.weight = 0.0f;
	}

	/**
	 * Alternate constructor using two vertices to make an edge.
	 * @param fVert Source vertex
	 * @param tVert Destination vertex
	 */
	public Edge(Vertex fVert, Vertex tVert) {
		this.fromVert = fVert;
		this.toVert = tVert;
		this.weight = GPSDistance(fVert, tVert);
	}

	/**
	 * Gets the source vertex.
	 * @return Source vertex
	 */
	public Vertex GetFromVert() {
		return this.fromVert;
	}

	/**
	 * Gets the destination vertex.
	 * @return Destination vertex
	 */
	public Vertex GetToVert() {
		return this.toVert;
	}

	/**
	 * Gets the distance between the source and destination vertex
	 * @return Distance between the source and destination vertices
	 */
	public double GetWeight() {
		double temp = this.weight;
		return temp;
	}

	/**
	 * Calculates the distance between two GPS points.
	 * @param fromVert Source vertex
	 * @param toVert Destination vertex
	 * @return Distance between the source and destination vertex in kilometers
	 */
	private double GPSDistance(Vertex fromVert, Vertex toVert) {
		double earthRadiusKm = 6371.0d;		// Radius of the Earth in km
		double lat1 = fromVert.GetLatitude();	// Latitude of the first point
		double lon1 = fromVert.GetLongitude();	// Longitude of the first point
		double lat2 = toVert.GetLatitude();	// Latitude of the second point
		double lon2 = toVert.GetLongitude();	// Longitude of the sedond point

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

	public String toString() {
		return "[Edge] toVert: {" + this.toVert.toString() + "} weight: " + this.weight;
	}

	public int compareTo(Edge e) {
		if (this.weight > e.GetWeight())
			return 1;
		else if (this.weight < e.GetWeight())
			return -1;
		else
			return 0;
	}

	public boolean equals(Edge e) {
		return e.GetWeight() == this.GetWeight()
			&& e.GetFromVert().equals(this.GetFromVert())
			&& e.GetToVert().equals(this.GetToVert());
	}
}