package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.utils.Utils;

public class Hooks {
    public static final String COBBREEDING = "cobbreeding";

    public static boolean cobbreedingPresent() {
        return Utils.modEnabled(COBBREEDING);
    }

    public static boolean cobbreedingCompat() {
        if (!cobbreedingPresent()) {
            return false;
        }

        String version = Utils.modVersion(COBBREEDING);
        String[] parts = version.split("\\.");
        if (parts.length < 3) {
            return false;
        }

        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);
            if (major == 1) {
                return minor > 8 || (minor == 8 && patch >= 8);
            } else if (major == 2) {
                return minor <= 1;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
