public class Vertex {
	private double latitude, longitude;
	private float height;
	private String street;

	public Vertex() {
		this.latitude = 0.000000f;
		this.longitude = 0.000000f;
		this.height = 0.0f;
		this.street = "Unknown";
	}

	/** 
	 * Alternate constructor.
	 * @param lat Vertex's latitude
	 * @param lon Vertex's longitude
	 * @param hi Vertex's height from sea level
	 * @param str Vertex's street name
	 */
	public Vertex(double lat, double lon, float hi, String str) {
		this.latitude = lat;
		this.longitude = lon;
		this.height = hi;
		this.street = str;
	}

	/**
	 * Gets the latitude value.
	 * @return Latitude value
	 */
	public double GetLatitude() {
		double temp = this.latitude;
		return temp;
	}

	/**
	 * Gets the longitude value.
	 * @return Longitude value
	 */
	public double GetLongitude() {
		double temp = this.longitude;
		return temp;
	}

	/**
	 * Gets the height from sea level.
	 * @return Height value
	 */
	public double GetHeight() {
		double temp = this.height;
		return temp;
	}

	/**
	 * Gets the street name.
	 * @return Street name
	 */
	public String GetStreet() {
		return this.street;
	}

	public String toString() {
		return "[Vertex] latitude: " + latitude + ", longitude: " + longitude + ", height: " + height + ", street: " + street;
	}

	public boolean equals(Vertex v) {
		return (this.toString().equals(v.toString()));
	}
}
