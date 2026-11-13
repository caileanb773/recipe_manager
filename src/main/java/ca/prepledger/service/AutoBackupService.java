package ca.prepledger.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.constant.Constants;
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
	private static final String BACKUP_DIR = "\\Backup\\";
	private static final String BACKUP_PATH = 
			Constants.LOCAL_APP_DATA + Constants.PREPLEDGER_DIR + BACKUP_DIR;
	private static final Logger logger = LoggerFactory.getLogger(AutoBackupService.class);

	
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
		Path backupPath = Path.of(BACKUP_PATH);
		
		if (!isPathValid(backupPath)) {
			createBackupPath(backupPath);
		}
		
		// Call ImportExportService.Export(recipes)
		
	}
	
	/**
	 * Validates the path that backup files are written to. If the path doesn't
	 * exist, then the helper method 'createBackupPath()' is called.
	 * 
	 * @since v0.1.2
	 */
	private static boolean isPathValid(Path path) {
		if (!Files.exists(path)) {
			return false;			
		}
		
		return true;
	}
	
	/**
	 * Creates the default path that backup files are written to. On Windows:
	 * 
	 * %APPDATA%/Local/PrepLedger/Backup
	 * 
	 * @since v0.1.2
	 */
	private static void createBackupPath(Path path) {
		try {
			Files.createDirectories(path);			
		} catch (IOException e) {
			logger.warn("createBackupPath(): "
					+ "IOException caught during Files.createDirectories().");
		}
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
		int numBackups = 0;

		// Get a list of files
		
		// Count the files
		
		// Count number of backup files in directory
		
		if (numBackups > MAXIMUM_BACKUPS) {
			
		} else {
			return;
		}
		
		// Delete oldest backup
	}
	
}
