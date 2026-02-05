package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.utils.Utils;

public class Hooks {
    public static final String COBBREEDING = "cobbreeding";
    public static final String CLOTH_CONFIG_FABRIC = "cloth-config";
    public static final String CLOTH_CONFIG_NEOFORGE = "cloth_config";

    public static SupportStatus cobbreedingCompat = null;

    public static boolean clothConfig() {
        return Utils.modEnabled(CLOTH_CONFIG_FABRIC) || Utils.modEnabled(CLOTH_CONFIG_NEOFORGE);
    }

    public static boolean cobbreedingPresent() {
        return Utils.modEnabled(COBBREEDING);
    }

    public static SupportStatus cobbreedingCompat() {
        if (cobbreedingCompat != null) {
            return cobbreedingCompat;
        }

        cobbreedingCompat = SupportStatus.UNSUPPORTED;
        if (!cobbreedingPresent()) {
            return cobbreedingCompat;
        }

        String version = Utils.modVersion(COBBREEDING);
        String[] parts = version.split("\\.");
        if (parts.length < 3) {
            return cobbreedingCompat;
        }

        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);
            if (major == 1) {
                if (minor > 8 || (minor == 8 && patch >= 8)) {
                    cobbreedingCompat = SupportStatus.SUPPORTED;
                }
            } else if (major == 2) {
                cobbreedingCompat = minor > 2 ? SupportStatus.UNTESTED : SupportStatus.SUPPORTED;
            }
            return cobbreedingCompat;
        } catch (NumberFormatException e) {
            return cobbreedingCompat;
        }
    }

    public enum SupportStatus {
        SUPPORTED,
        UNSUPPORTED,
        UNTESTED;

        public boolean enabled() {
            return this == SUPPORTED || this == UNTESTED;
        }
    }
}
