package ca.prepledger.config;

/*
 * Author: Cailean Bernard
 * Contents: Interface for dependency injection of Application Configuration class.
 * Controllers that have configurable properties will implement this interface
 * and have an instance of AppConfig injected into them.
 */
public interface Configurable {
	
	void setAppConfig(AppConfig appConfig);

}
