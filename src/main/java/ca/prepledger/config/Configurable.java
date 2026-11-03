package ca.prepledger.config;

/**
 * Contents: Interface for dependency injection of Application Configuration class.
 * Controllers that have configurable properties will implement this interface
 * and have an instance of AppConfig injected into them.
 * 
 * @author Cailean Bernard
 * @since v0.1.0
 */
public interface Configurable {
	
	void setAppConfig(AppConfig appConfig);

}
