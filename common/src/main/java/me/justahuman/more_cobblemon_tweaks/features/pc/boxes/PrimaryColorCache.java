package me.justahuman.more_cobblemon_tweaks.features.pc.boxes;

import com.cobblemon.mod.common.client.render.SpriteType;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.VaryingModelRepository;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PrimaryColorCache {
    private static final Map<ResourceLocation, Integer> POKEMON_COLOR_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, Integer> WALLPAPER_COLOR_CACHE = new HashMap<>();
    private static final PosableState state = new FloatingState();
    private static final int FALLBACK_COLOR = 0xFF888888;

    public static Integer getPokemonColor(ResourceLocation species) {
        return POKEMON_COLOR_CACHE.computeIfAbsent(species, $ -> {
            ResourceLocation textureToSample = VaryingModelRepository.INSTANCE.getSprite(species, state, SpriteType.PROFILE);
            if (textureToSample == null) {
                textureToSample = VaryingModelRepository.INSTANCE.getTexture(species, state);
            }

            try {
                return samplePrimaryColor(textureToSample);
            } catch (Exception e) {
                return FALLBACK_COLOR;
            }
        });
    }

    public static Integer getWallpaperColor(ResourceLocation wallpaper) {
        return WALLPAPER_COLOR_CACHE.computeIfAbsent(wallpaper, $ -> {
            try {
                return samplePrimaryColor(wallpaper);
            } catch (Exception e) {
                return FALLBACK_COLOR;
            }
        });
    }

    private static int samplePrimaryColor(ResourceLocation texture) throws IOException {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        Resource resource = rm.getResource(texture).orElseThrow(() -> new IOException("Resource not found: " + texture));
        try (InputStream is = resource.open()) {

            BufferedImage img = ImageIO.read(is);
            if (img == null) throw new IOException("Unable to read image for " + texture);

            // Build a small histogram by quantizing colors to 5 bits per channel.
            Map<Integer, Integer> counts = new HashMap<>();
            int w = img.getWidth();
            int h = img.getHeight();

            // sample every 2nd pixel for speed
            for (int y = 0; y < h; y += 2) {
                for (int x = 0; x < w; x += 2) {
                    int rgba = img.getRGB(x, y);
                    int a = (rgba >> 24) & 0xFF;
                    if (a < 128) continue; // ignore transparent

                    int r = (rgba >> 16) & 0xFF;
                    int g = (rgba >> 8) & 0xFF;
                    int b = rgba & 0xFF;

                    int qr = (r >> 3) & 0x1F;
                    int qg = (g >> 3) & 0x1F;
                    int qb = (b >> 3) & 0x1F;
                    int key = (qr << 10) | (qg << 5) | qb;
                    counts.merge(key, 1, Integer::sum);
                }
            }

            if (counts.isEmpty()) return FALLBACK_COLOR;

            int bestKey = -1;
            double bestScore = Double.NEGATIVE_INFINITY;
            for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
                int key = e.getKey();
                int count = e.getValue();

                int qr = (key >> 10) & 0x1F;
                int qg = (key >> 5) & 0x1F;
                int qb = key & 0x1F;

                // Expand 5-bit to 8-bit with simple replication.
                int rr = (qr << 3) | (qr >> 2);
                int gg = (qg << 3) | (qg >> 2);
                int bb = (qb << 3) | (qb >> 2);

                // Compute a simple saturation estimate (HSV-like):
                // sat = (max - min) / max, clamped to [0,1].
                int max = Math.max(rr, Math.max(gg, bb));
                int min = Math.min(rr, Math.min(gg, bb));
                double sat = (max == 0) ? 0.0 : (double) (max - min) / (double) max;

                // Bias towards more colorful bins, but keep "area" (count) as the main driver.
                // A sat of 1.0 yields ~35% more effective weight than sat of 0.0.
                // This mainly breaks near-ties rather than letting tiny noisy pixels win.
                double score = count * (1.0 + 0.35 * sat);

                if (score > bestScore) {
                    bestScore = score;
                    bestKey = key;
                }
            }

            int qr = (bestKey >> 10) & 0x1F;
            int qg = (bestKey >> 5) & 0x1F;
            int qb = bestKey & 0x1F;

            // expand 5-bit to 8-bit with simple replication
            int rr = (qr << 3) | (qr >> 2);
            int gg = (qg << 3) | (qg >> 2);
            int bb = (qb << 3) | (qb >> 2);

            return (0xFF << 24) | (rr << 16) | (gg << 8) | bb;
        }
    }
}
