package util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/*
 * Author: Cailean Bernard
 * Contents: Contains the method that restricts the application to one instance
 * running at a time.
 */
public class InstanceLocker {
	
	/**
	 * 
	 * Source - https://stackoverflow.com/a
	 * Posted by Robert
	 * Retrieved 2025-11-08, License - CC BY-SA 2.5
	 */
	public static boolean lockInstance(final String lockFile) {
	    try {
	        final File file = new File(lockFile);
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	        if (fileLock != null) {
	            Runtime.getRuntime().addShutdownHook(new Thread() {
	                public void run() {
	                    try {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } catch (Exception e) {
	                        System.err.println("Unable to remove lock file: " + lockFile.toString() + e);
	                    }
	                }
	            });
	            return true;
	        }
	    } catch (Exception e) {
	        System.err.println("Unable to create and/or lock file: " + lockFile.toString() + e);
	    }
	    return false;
	}

}
