package ca.prepledger.service;

import java.time.LocalDateTime;
import java.util.List;

import ca.prepledger.model.Recipe;

/**
 * The service responsible for automatically backing up recipes in memory to a 
 * time-stamped (Local Time) JSON file upon application close. The backup service 
 * will write up to 10 backup files; subsequent backup operations will age-out 
 * the oldest backup.
 * 
 * The default Windows directory in which backups will be written is:
 * %APPDATA%/Local/PrepLedger/Backup
 * 
 * @author Cailean Bernard
 * @since v0.1.2
 */
public class AutoBackupService {
	
	private static final int MAXIMUM_BACKUPS = 10;
	private static final String PREFIX = "backup";	//Ex: backup_2026-11-13T16:43:00.JSON
	private static final String EXTENSION = "JSON";
	private static LocalDateTime timestamp = LocalDateTime.now();
	private static String fileName = PREFIX + "_" + timestamp + "." + EXTENSION;
	
	/**
	 * Private no-argument constructor overrides JVM default constructor; all
	 * methods in this class are static. Creating an object of this class serves
	 * no purpose.
	 * 
	 * @since v0.1.2
	 */
	private AutoBackupService() {}

	/**
	 * Creates a new backup file. Utilizes the export() method from ImportExport
	 * Service to do so.
	 * 
	 * @since v0.1.2
	 */
	public static void createBackup(List<Recipe> recipes) {
		if (!isPathValid()) {
			createBackupPath();
		}
		
		// Call ImportExportService.Export(recipes)
		
		// Count number of backup files in directory
		int numBackups = 0;
		
		if (numBackups > MAXIMUM_BACKUPS) {
			ageOutOldestBackup();
		}
	}
	
	/**
	 * Validates the path that backup files are written to. If the path doesn't
	 * exist, then the helper method 'createBackupPath()' is called.
	 * 
	 * @since v0.1.2
	 */
	private static boolean isPathValid() {
		return false;
	}
	
	/**
	 * Creates the default path that backup files are written to. On Windows:
	 * 
	 * %APPDATA%/Local/PrepLedger/Backup
	 * 
	 * @since v0.1.2
	 */
	private static void createBackupPath() {
		
	}
	
	/**
	 * Deletes the oldest backup in the backup directory. The backup directory
	 * should only ever have MAXIMUM_BACKUPS backup files in it, to prevent bloat 
	 * on the file-system.
	 * 
	 * To do this, any time the service writes a backup, it then counts how many
	 * backup files exist in the backup directory. If there are > MAXIMUM_BACKUPS
	 * backup files, it will determine which is the oldest and delete it.
	 * 
	 * @since v0.1.2
	 */
	private static void ageOutOldestBackup() {
		
	}
	
}
