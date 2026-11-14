package ca.prepledger.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ca.prepledger.model.Recipe;

/**
 * Tests for AutoBackupService. Every test runs in its own temporary directory,
 * so the real backup folder is never touched.
 */
class AutoBackupServiceTest {

	private static final List<Recipe> NO_RECIPES = List.of();
	private static final LocalDateTime BASE_TIME = LocalDateTime.of(2026, 11, 13, 16, 43, 0);

	@TempDir
	Path tempDir;

	// ---------------------------------------------------------------- helpers

	/** A clock frozen at the given local time. */
	private static Clock clockAt(LocalDateTime time) {
		return Clock.fixed(time.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
	}

	/** Creates a fake backup file for the given time, without using ImportExportService. */
	private Path createFakeBackup(Path dir, LocalDateTime time) throws IOException {
		return Files.writeString(dir.resolve(AutoBackupService.backupFileName(time)), "[]");
	}

	/** Creates `count` fake backups one hour apart, oldest first, and returns them in that order. */
	private List<Path> createFakeBackups(Path dir, int count) throws IOException {
		List<Path> created = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			created.add(createFakeBackup(dir, BASE_TIME.plusHours(i)));
		}
		return created;
	}

	private int backupCount(Path dir) throws IOException {
		return AutoBackupService.listBackups(dir).size();
	}

	// ---------------------------------------------------------------- file names

	@Test
	void fileNameHasExpectedFormat() {
		LocalDateTime time = LocalDateTime.of(2026, 11, 13, 16, 43, 0, 123_000_000);
		assertEquals("backup_2026-11-13T16-43-00-123.json", AutoBackupService.backupFileName(time));
	}

	@Test
	void fileNameContainsNoCharactersWindowsForbids() {
		String name = AutoBackupService.backupFileName(LocalDateTime.now());
		for (char c : "<>:\"/\\|?*".toCharArray()) {
			assertFalse(name.indexOf(c) >= 0, "File name contains '" + c + "': " + name);
		}
	}

	@Test
	void fileNamesSortInChronologicalOrder() {
		List<LocalDateTime> times = List.of(
				LocalDateTime.of(2026, 1, 2, 3, 4, 5, 6_000_000),
				LocalDateTime.of(2026, 1, 2, 3, 4, 5, 60_000_000),   // same second, later millis
				LocalDateTime.of(2026, 1, 2, 9, 0, 0),
				LocalDateTime.of(2026, 1, 2, 10, 0, 0),              // 09 vs 10: needs zero padding
				LocalDateTime.of(2026, 10, 1, 0, 0, 0),
				LocalDateTime.of(2027, 1, 1, 0, 0, 0));

		List<String> expected = times.stream().map(AutoBackupService::backupFileName).toList();
		List<String> sorted = new ArrayList<>(expected);
		Collections.shuffle(sorted);
		Collections.sort(sorted);

		assertEquals(expected, sorted);
	}

	// ---------------------------------------------------------------- createBackup

	@Test
	void createBackupCreatesMissingDirectories() {
		Path backupDir = tempDir.resolve("PrepLedger").resolve("Backup");
		assertFalse(Files.exists(backupDir));

		AutoBackupService.createBackup(NO_RECIPES, backupDir, clockAt(BASE_TIME));

		assertTrue(Files.isDirectory(backupDir));
		assertTrue(Files.exists(backupDir.resolve(AutoBackupService.backupFileName(BASE_TIME))));
	}

	@Test
	void eachBackupGetsItsOwnFileName() throws IOException {
		// Regression: the file name used to be computed once when the class loaded,
		// so a second backup in the same session overwrote the first.
		AutoBackupService.createBackup(NO_RECIPES, tempDir, clockAt(BASE_TIME));
		AutoBackupService.createBackup(NO_RECIPES, tempDir, clockAt(BASE_TIME.plusMinutes(5)));

		assertEquals(2, backupCount(tempDir));
	}

	@Test
	void createBackupWorksWhenPathContainsUnderscores() throws IOException {
		// Regression: the old date parsing used the first '_' in the whole path.
		Path backupDir = tempDir.resolve("cailean_b").resolve("app_data");
		createFakeBackups(Files.createDirectories(backupDir), AutoBackupService.MAXIMUM_BACKUPS);

		AutoBackupService.createBackup(NO_RECIPES, backupDir, clockAt(BASE_TIME.plusDays(1)));

		assertEquals(AutoBackupService.MAXIMUM_BACKUPS, backupCount(backupDir));
	}

	@Test
	void createBackupWithFullDirectoryReplacesOldestWithNewest() throws IOException {
		List<Path> existing = createFakeBackups(tempDir, AutoBackupService.MAXIMUM_BACKUPS);
		LocalDateTime newTime = BASE_TIME.plusDays(1);

		AutoBackupService.createBackup(NO_RECIPES, tempDir, clockAt(newTime));

		assertEquals(AutoBackupService.MAXIMUM_BACKUPS, backupCount(tempDir));
		assertFalse(Files.exists(existing.get(0)), "Oldest backup should have been deleted");
		assertTrue(Files.exists(existing.get(1)), "Second-oldest backup should be kept");
		assertTrue(Files.exists(tempDir.resolve(AutoBackupService.backupFileName(newTime))));
	}

	@Test
	void createBackupDoesNotThrowWhenDirectoryCannotBeCreated() throws IOException {
		// A regular file where the directory should be makes createDirectories fail.
		Path blocker = Files.writeString(tempDir.resolve("Backup"), "not a directory");

		assertDoesNotThrow(() ->
				AutoBackupService.createBackup(NO_RECIPES, blocker, clockAt(BASE_TIME)));
		assertEquals("not a directory", Files.readString(blocker));
	}

	// ---------------------------------------------------------------- pruneOldBackups

	@Test
	void pruneDeletesNothingWhenUnderLimit() throws IOException {
		createFakeBackups(tempDir, AutoBackupService.MAXIMUM_BACKUPS - 1);

		AutoBackupService.pruneOldBackups(tempDir);

		assertEquals(AutoBackupService.MAXIMUM_BACKUPS - 1, backupCount(tempDir));
	}

	@Test
	void pruneDeletesNothingWhenExactlyAtLimit() throws IOException {
		createFakeBackups(tempDir, AutoBackupService.MAXIMUM_BACKUPS);

		AutoBackupService.pruneOldBackups(tempDir);

		assertEquals(AutoBackupService.MAXIMUM_BACKUPS, backupCount(tempDir));
	}

	@Test
	void pruneDeletesOldestWhenOneOverLimit() throws IOException {
		List<Path> backups = createFakeBackups(tempDir, AutoBackupService.MAXIMUM_BACKUPS + 1);

		AutoBackupService.pruneOldBackups(tempDir);

		assertEquals(AutoBackupService.MAXIMUM_BACKUPS, backupCount(tempDir));
		assertFalse(Files.exists(backups.get(0)));
		for (Path kept : backups.subList(1, backups.size())) {
			assertTrue(Files.exists(kept), "Should be kept: " + kept.getFileName());
		}
	}

	@Test
	void pruneDeletesAllExcessBackups() throws IOException {
		// Regression: the old code deleted only one file per run.
		List<Path> backups = createFakeBackups(tempDir, AutoBackupService.MAXIMUM_BACKUPS + 3);

		AutoBackupService.pruneOldBackups(tempDir);

		assertEquals(AutoBackupService.MAXIMUM_BACKUPS, backupCount(tempDir));
		for (Path deleted : backups.subList(0, 3)) {
			assertFalse(Files.exists(deleted), "Should be deleted: " + deleted.getFileName());
		}
	}

	@Test
	void pruneUsesTimestampNotCreationOrder() throws IOException {
		// Create the oldest-dated backup last; it should still be the one deleted.
		createFakeBackups(tempDir, AutoBackupService.MAXIMUM_BACKUPS);
		Path oldest = createFakeBackup(tempDir, BASE_TIME.minusYears(1));

		AutoBackupService.pruneOldBackups(tempDir);

		assertFalse(Files.exists(oldest));
		assertEquals(AutoBackupService.MAXIMUM_BACKUPS, backupCount(tempDir));
	}

	@Test
	void pruneIgnoresAndKeepsNonBackupFiles() throws IOException {
		List<Path> others = List.of(
				Files.writeString(tempDir.resolve("notes.json"), "{}"),
				Files.writeString(tempDir.resolve("readme.txt"), "hi"),
				Files.writeString(tempDir.resolve("backup_not-a-date.json"), "[]"),
				Files.writeString(tempDir.resolve("backup_2020-01-01T00-00-00-000.json.bak"), "[]"),
				Files.createDirectory(tempDir.resolve("backup_2000-01-01T00-00-00-000.json")));
		List<Path> backups = createFakeBackups(tempDir, AutoBackupService.MAXIMUM_BACKUPS + 1);

		AutoBackupService.pruneOldBackups(tempDir);

		for (Path other : others) {
			assertTrue(Files.exists(other), "Non-backup should be kept: " + other.getFileName());
		}
		assertFalse(Files.exists(backups.get(0)));
		assertEquals(AutoBackupService.MAXIMUM_BACKUPS, backupCount(tempDir));
	}

	@Test
	void uppercaseExtensionIsCountedAsBackup() throws IOException {
		Path upper = Files.writeString(
				tempDir.resolve("backup_2020-01-01T00-00-00-000.JSON"), "[]");
		createFakeBackups(tempDir, AutoBackupService.MAXIMUM_BACKUPS);

		assertEquals(AutoBackupService.MAXIMUM_BACKUPS + 1, backupCount(tempDir));

		AutoBackupService.pruneOldBackups(tempDir);

		assertFalse(Files.exists(upper), "Oldest (uppercase .JSON) backup should be deleted");
	}

	@Test
	void pruneOnEmptyDirectoryDoesNothing() {
		assertDoesNotThrow(() -> AutoBackupService.pruneOldBackups(tempDir));
	}
}
