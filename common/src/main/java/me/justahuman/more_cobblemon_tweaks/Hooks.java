package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.utils.Utils;

public class Hooks {
    public static final String COBBREEDING = "cobbreeding";
    public static final String CLOTH_CONFIG_FABRIC = "cloth-config";
    public static final String CLOTH_CONFIG_NEOFORGE = "cloth_config";

    public static Boolean cobbreedingCompat = null;

    public static boolean clothConfig() {
        return Utils.modEnabled(CLOTH_CONFIG_FABRIC) || Utils.modEnabled(CLOTH_CONFIG_NEOFORGE);
    }

    public static boolean cobbreedingPresent() {
        return Utils.modEnabled(COBBREEDING);
    }

    public static boolean cobbreedingCompat() {
        if (cobbreedingCompat != null) {
            return cobbreedingCompat;
        }

        if (!cobbreedingPresent()) {
            cobbreedingCompat = false;
            return false;
        }

        String version = Utils.modVersion(COBBREEDING);
        String[] parts = version.split("\\.");
        if (parts.length < 3) {
            cobbreedingCompat = false;
            return false;
        }

        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);
            if (major == 1) {
                cobbreedingCompat = minor > 8 || (minor == 8 && patch >= 8);
            } else if (major == 2) {
                cobbreedingCompat = minor <= 1;
            } else {
                cobbreedingCompat = false;
            }
            return cobbreedingCompat;
        } catch (NumberFormatException e) {
            cobbreedingCompat = false;
            return false;
        }
    }
}
