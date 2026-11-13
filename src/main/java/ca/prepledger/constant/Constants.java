package ca.prepledger.constant;

/**
 * Constants that would otherwise need to be defined in two places should live
 * here.
 * 
 * Contains definitions for Windows-specific pathing for the %APPDATA%/Local.
 * Future supported platforms should supply their platform-specific pathing here.
 * 
 * @author Cailean Bernard
 * @since v0.1.2
 */
public class Constants {
	
	public static final String LOCAL_APP_DATA = System.getenv("LOCALAPPDATA");
	public static final String PREPLEDGER_DIR = "\\PrepLedger\\";
	
	private Constants() {}

}
