package ca.prepledger.config;

public class ApiConfig {
	
	public static final String LOCALHOST_ROOT = "http://localhost:8080";
	public static final String RAILWAY_ROOT = "https://prepledger-server-production.up.railway.app";
	public static Mode mode = Mode.PROD;
	
	public enum Mode {
		PROD, DEV
	}

	private ApiConfig() {}
}
