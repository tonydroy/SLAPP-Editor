/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

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
