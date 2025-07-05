package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.utils.Utils;

public class Hooks {
    public static final String COBBREEDING = "cobbreeding";

    public static boolean cobbreedingCompat() {
        if (!Utils.modEnabled(COBBREEDING)) {
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
            return major == 1 && (minor > 8 || (minor == 8 && patch >= 8));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
