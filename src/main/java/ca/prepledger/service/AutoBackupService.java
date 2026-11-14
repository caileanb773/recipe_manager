package ca.prepledger.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

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
	
	static final int MAXIMUM_BACKUPS = 10;
	private static final String PREFIX = "backup_";	//Ex: backup_2026-11-13T16:43:00.JSON
	private static final String EXTENSION = ".json";
	
	private static final DateTimeFormatter TIMESTAMP_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss-SSS");
	private static final Pattern BACKUP_NAME = Pattern.compile(
			"backup_\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2}-\\d{2}-\\d{3}\\.json",
			Pattern.CASE_INSENSITIVE);
	
	private static final Path BACKUP_DIR =
			Path.of(Constants.LOCAL_APP_DATA + Constants.PREPLEDGER_DIR).resolve("Backup");

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
		createBackup(recipes, BACKUP_DIR, Clock.systemDefaultZone());
	}
	
	/**
	 * Package-private overload so tests can supply a temporary directory and a
	 * fixed clock instead of writing to the real backup folder.
	 *
	 * @param recipes - The list of recipes to export as a backup.
	 * @param backupDir - The directory to write the backup to.
	 * @param clock - The clock used to time-stamp the backup.
	 * @since v0.1.2
	 */
	static void createBackup(List<Recipe> recipes, Path backupDir, Clock clock) {
		String fileName = "";
		
		try {
			Files.createDirectories(backupDir); // no-op if it already exists

			fileName = backupFileName(LocalDateTime.now(clock));
			ImportExportService.exportRecipes(recipes, backupDir.resolve(fileName).toString());

			pruneOldBackups(backupDir);
		} catch (IOException | RuntimeException e) {
			logger.error("createBackup(): backup failed for {}", fileName);
		}
	}

	/**
	 * Builds the backup file name for a given time,
	 * e.g. backup_2026-11-13T16-43-00-123.json
	 *
	 * @param time - The time to stamp the backup with.
	 * @return The backup file name.
	 * @since v0.1.2
	 */
	static String backupFileName(LocalDateTime time) {
		return PREFIX + time.format(TIMESTAMP_FORMAT) + EXTENSION;
	}
	
	/**
	 * Deletes the oldest backups until at most MAXIMUM_BACKUPS remain.
	 *
	 * @since v0.1.2
	 */
	static void pruneOldBackups(Path backupDir) throws IOException {
		List<Path> backups = listBackups(backupDir);
		backups.sort(Comparator.comparing((Path p) -> p.getFileName().toString().toLowerCase()));

		int excess = backups.size() - MAXIMUM_BACKUPS;
		
		for (int i = 0; i < excess; i++) {
			Path oldest = backups.get(i);
			Files.deleteIfExists(oldest);
			logger.info("pruneOldBackups(): deleted old backup {}", oldest.getFileName());
		}
	}
	
	/**
	 * Lists the files in the backup directory whose names match the backup
	 * naming pattern. Other files in the directory are ignored and never deleted.
	 *
	 * @return The backup files, in no particular order.
	 * @since v0.1.2
	 */
	static List<Path> listBackups(Path backupDir) throws IOException {
		List<Path> backups = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupDir)) {
			for (Path p : stream) {
				if (Files.isRegularFile(p)
						&& BACKUP_NAME.matcher(p.getFileName().toString()).matches()) {
					backups.add(p);
				}
			}
		}
		
		return backups;
	}
	
}
