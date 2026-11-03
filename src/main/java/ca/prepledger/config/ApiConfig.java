package ca.prepledger.config;

/**
 * Contains configurations for production and development modes, as well as the
 * root addresses of both dev and production backend APIs.
 * 
 * @author Cailean Bernard
 * @since v0.1.1
 */
public class ApiConfig {
	
	public static final String LOCALHOST_ROOT = "http://localhost:8080";
	public static final String RAILWAY_ROOT = "https://prepledger-server-production.up.railway.app";
	public static Mode mode = Mode.PROD;
	
	public enum Mode {
		PROD, DEV
	}

	private ApiConfig() {}
}
