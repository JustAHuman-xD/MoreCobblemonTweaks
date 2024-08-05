package me.justahuman.dystoriantweaks.features;

import java.util.ArrayList;
import java.util.List;

public class PcEnhancements {
    public static final List<Integer> BOX_ORDER = new ArrayList<>();

    public static int getBoxIndex(int box) {
        if (BOX_ORDER.size() <= box) {
            for (int i = BOX_ORDER.size(); i <= box; i++) {
                BOX_ORDER.add(i);
            }
        }
        return BOX_ORDER.indexOf(box);
    }
}
