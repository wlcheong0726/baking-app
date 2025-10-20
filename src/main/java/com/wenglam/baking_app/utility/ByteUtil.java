package com.wenglam.baking_app.utility;

public class ByteUtil {
    /**
     * Parses a human-readable file size string (e.g., "5MB", "10KB", "1GB") and converts it to bytes.
     * @param sizeStr
     * @return the equivalent size in bytes
     */
    public static long parseSizeToBytes (String sizeStr) {
        sizeStr = sizeStr.trim().toUpperCase();

        
        if (sizeStr.endsWith("KB")) {
            return Long.parseLong(sizeStr.replace("KB", "").trim()) * (long) Math.pow(1024,2);
        } else if (sizeStr.endsWith("MB")) {
            return Long.parseLong(sizeStr.replace("MB", "").trim()) * 1024 * 1024;
        } else if (sizeStr.endsWith("GB")) {
            return Long.parseLong(sizeStr.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        } else {
            // Assume bytes
            return Long.parseLong(sizeStr);
        }
    }

    /**
     * Converts a byte count into a human-readable string using appropriate units (B, KB, MB, GB)
     * @param bytes - size in bytes to format
     * @return a human-readable string representing the size in appropriate units e.g. 1KB for 1024B
     */
    public static String formatBytes (long bytes) {
        if (bytes >= 1024 * 1024 * 1024) {
            return String.format("%.2fGB", bytes / (1024.0 * 1024 * 1024));
        } else if (bytes >= 1024 * 1024) {
            return String.format("%.2fMB", bytes / (1024.0 * 1024));
        } else if (bytes >= 1024) {
            return String.format("%.2fKB", bytes / 1024.0);
        } else {
            return bytes + " B";
        }
    }
}
