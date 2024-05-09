package slapp.editor;

import java.io.*;
import java.nio.channels.*;

public class JustOneLock {
    private static String appName = "SLAPP";
    private static File file;
    private static FileChannel channel;
    private static FileLock lock;

    public JustOneLock() {
    }

    public static boolean isAppActive() {
        try {
            file = new File
                    (System.getProperty("user.home"), appName + ".tmp");
            channel = new RandomAccessFile(file, "rw").getChannel();

            try {
                lock = channel.tryLock();
            }
            catch (OverlappingFileLockException e) {
                // already locked
                closeLock();
                return true;
            }

            if (lock == null) {
                closeLock();
                return true;
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                // destroy the lock when the JVM is closing
                public void run() {
                    closeLock();
                    deleteFile();
                }
            });
            return false;
        }
        catch (Exception e) {
            closeLock();
            return true;
        }
    }

    private static void closeLock() {
        try { lock.release();  }
        catch (Exception e) {  }
        try { channel.close(); }
        catch (Exception e) {  }
    }

    private static void deleteFile() {
        try { file.delete(); }
        catch (Exception e) { }
    }
}
