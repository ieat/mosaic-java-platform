package mosaic.driver.interop;

/**
 * Generic class holding connection information about a resource driver.
 * 
 * @author Georgiana Macariu
 * 
 */
public class DriverConnectionData {

	private final String host;
	private final int port;
	private String user = "";
	private String password = "";
	private final String driverName;

	/**
	 * Creates a new data class
	 * 
	 * @param host
	 *            the hostname or ip address of the machine running the resource
	 * @param port
	 *            the port on which the resource is listening
	 * @param driverName
	 *            driver name
	 */
	public DriverConnectionData(String host, int port, String driverName) {
		super();
		this.host = host;
		this.port = port;
		this.driverName = driverName;
	}

	/**
	 * Creates a new data class
	 * 
	 * @param host
	 *            the hostname or ip address of the machine running the resource
	 * @param port
	 *            the port on which the resource is listening
	 * @param driverName
	 *            driver name
	 * @param user
	 *            username for connecting to resource
	 * @param password
	 *            password for connecting to resource
	 */
	public DriverConnectionData(String host, int port, String driverName,
			String user, String password) {
		this(host, port, driverName);
		this.user = user;
		this.password = password;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public String getUser() {
		return this.user;
	}

	public String getPassword() {
		return this.password;
	}

	public String getDriverName() {
		return this.driverName;
	}

	@Override
	public int hashCode() { // NOPMD by georgiana on 10/12/11 3:11 PM
		final int prime = 31; // NOPMD by georgiana on 10/12/11 3:09 PM
		int result = 1; // NOPMD by georgiana on 10/12/11 3:11 PM
		result = (prime * result)
				+ ((this.driverName == null) ? 0 : this.driverName.hashCode());
		result = (prime * result)
				+ ((this.host == null) ? 0 : this.host.hashCode());
		result = (prime * result)
				+ ((this.password == null) ? 0 : this.password.hashCode());
		result = (prime * result) + this.port;
		result = (prime * result)
				+ ((this.user == null) ? 0 : this.user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual;

		isEqual = (this == obj);
		if (!isEqual && (obj instanceof DriverConnectionData)) {
			DriverConnectionData other = (DriverConnectionData) obj;
			isEqual = (obj == null)
					|| (getClass() != obj.getClass())
					|| ((this.driverName == null) && (other.driverName != null))
					|| ((this.driverName != null) && (!this.driverName
							.equals(other.driverName)))
					|| ((this.host == null) && (other.host != null))
					|| ((this.host != null) && (!this.host.equals(other.host)))
					|| ((this.password == null) && (other.password != null))
					|| ((this.password != null) && (other.password == null))
					|| ((this.password != null) && ((other.password != null) && !this.password
							.equals(other.password)))
					|| ((this.user == null) && (other.user != null))
					|| ((this.user != null) && (!this.user.equals(other.user)));
			isEqual ^= true;
		}
		return isEqual;
	}
}
