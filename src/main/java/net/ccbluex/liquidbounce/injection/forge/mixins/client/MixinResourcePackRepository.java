/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.client;

import net.minecraft.client.resources.ResourcePackRepository;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.comparator.LastModifiedFileComparator.LASTMODIFIED_REVERSE;
import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;

@Mixin(ResourcePackRepository.class)
public class MixinResourcePackRepository {
    @Shadow @Final private File dirServerResourcepacks;
    @Shadow @Final private static Logger logger;

    /**
     * @author Mojang
     * @reason Fix a bug
     */
    @Overwrite
    private void deleteOldServerResourcesPacks() {
        try {
            List<File> serverResourcepacks = newArrayList(listFiles(dirServerResourcepacks, TRUE, null));
            serverResourcepacks.sort(LASTMODIFIED_REVERSE);
            int packs = 0;

            for (File resourcePack : serverResourcepacks) {
                if (packs++ >= 10) {
                    logger.info("Deleting old server resource pack " + resourcePack.getName());
                    deleteQuietly(resourcePack);
                }
            }
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }
}
