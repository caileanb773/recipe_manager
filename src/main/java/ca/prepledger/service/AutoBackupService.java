package ca.prepledger.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
	 * @param recipes - The list of recipes to export as a backup (all recipes,
	 * by default).
	 * @since v0.1.2
	 */
	public static void createBackup(List<Recipe> recipes) {
		Path backupPath = Path.of(BACKUP_PATH);
		
		if (!isPathValid(backupPath)) {
			createBackupPath(backupPath);
		}
		
		// Create the backup and age out the oldest backup if necessary
		try {
			timestamp = LocalDateTime.now();
			ImportExportService.exportRecipes(recipes, BACKUP_PATH + fileName);
			countBackups(backupPath);
		} catch (IOException e) {
			logger.error("createBackup(): IOException caught while exporting: {}", e);
		}
		
	}
	
	/**
	 * Validates the path that backup files are written to. If the path doesn't
	 * exist, then the helper method 'createBackupPath()' is called.
	 * 
	 * @param path - The path under which to look.
	 * @return True if the path exists, false otherwise.
	 * @since v0.1.2
	 */
	private static boolean isPathValid(Path path) {
		return Files.exists(path);
	}
	
	/**
	 * Creates the default path that backup files are written to. On Windows:
	 * 
	 * %APPDATA%/Local/PrepLedger/Backup
	 * 
	 * @param path - The path under which to create a backup.
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
	 * Counts how many backup files exist in the backup directory. If there are 
	 * > MAXIMUM_BACKUPS backup files, the service will determine which is the 
	 * oldest and remove it.
	 * 
 	 * @param path - The path under which to look.
	 * @since v0.1.2
	 */
	private static void countBackups(Path path) throws IOException {
		int numBackups = 0;
		List<Path> filePaths;

		// Get a list of files
		filePaths = Files.list(path).toList();
		
		// Count the files
		for (Path p : filePaths) {
			if (p.toString().endsWith(".json") || p.toString().endsWith(".JSON")) {
				numBackups++;
			}
		}
				
		// Delete oldest backup if necessary
		if (numBackups > MAXIMUM_BACKUPS) {
			Path oldestPath = findOldestBackup(path);
			deleteOldestBackup(oldestPath);
		}
	}
	
	/**
	 * Finds the backup with the oldest marked time-stamp.
	 * 
	 * @param path - The path under which to look.
	 * @return The path of the oldest backup.
	 * @since v0.1.2
	 */
	private static Path findOldestBackup(Path path) throws IOException {
		Path oldest = null;
		LocalDateTime lastComparedDateTime = null;
		
		List<Path> filePaths = Files.list(path).filter(Files::isRegularFile).toList();
		
		for (Path p : filePaths) {
			String pathString = p.toString();
			String dateString = pathString.substring(pathString.indexOf('_') + 1, '.');
			LocalDateTime dateTime = LocalDateTime.parse(dateString);
			
			// If this is the first comparison, or if p is older than oldest
			if (oldest == null 
					|| lastComparedDateTime == null 
					|| dateTime.isBefore(lastComparedDateTime)) {
				oldest = p;
				lastComparedDateTime = dateTime;
			}
		}
		
		return oldest;
	}
	
	/**
	 * Deletes the oldest backup at the provided path.
	 * 
	 * @param path - The path of the oldest backup.
	 * @since v0.1.2
	 */
	private static void deleteOldestBackup(Path path) throws IOException {
		if (Files.exists(path)) {
			Files.delete(path);
		}
	}
	
}
