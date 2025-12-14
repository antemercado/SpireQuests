package spireQuests.quests.coda.patches;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;

import javassist.CtBehavior;
import spireQuests.quests.coda.relics.KeyringRelic;

public class ExtraBossRelicPatch {

    // Add additional relic option
    // Postion nth Relic in circle on screen
    @SpirePatch(
        clz = BossRelicSelectScreen.class,
        method = "open"
    )
    public static class RelicScreenPatch {
        private static ArrayList<AbstractRelic> relicsToAdd = new ArrayList<>();

        @SpireInsertPatch(
            locator = RelicPlacementLocator.class
        )
        private static void Insert(BossRelicSelectScreen __instance) {
        
            float cX = Settings.WIDTH / 2.0F;
            float cY = AbstractDungeon.floorY + 294.0F * Settings.scale;

            if (AbstractDungeon.player.hasRelic(KeyringRelic.ID)) {
                AbstractRelic r2;
                for (AbstractRelic r: AbstractDungeon.player.relics.stream().filter((r) -> r.relicId.equals(KeyringRelic.ID)).collect(Collectors.toList())) {
                    r.usedUp();
                    KeyringRelic krr = (KeyringRelic) r;
                    if (krr.getExtraRelic() == null) {
                        krr.setExtraRelic(AbstractDungeon.returnRandomRelic(RelicTier.BOSS));
                    }
                    r2 = krr.getExtraRelic();
                    r2.spawn(0.0F, 0.0F);
                    __instance.relics.add(r2);
                }
            }

            for (AbstractRelic r : relicsToAdd) {
                r.spawn(Settings.WIDTH / 2.0F + 124.0F * Settings.scale, AbstractDungeon.floorY + 150.0F * Settings.scale);
                __instance.relics.add(r);
            }
        
            if (__instance.relics.size() > 3) {

                for (int i = 0; i < __instance.relics.size(); i++) {
                    float angle = 45.0F + (i * 360.0F / __instance.relics.size());
                    float radius = 128.0F;

                    float xOffset = radius * MathUtils.cosDeg(angle);
                    float yOffset = radius * MathUtils.sinDeg(angle);

                    AbstractRelic r = __instance.relics.get(i);
                    r.currentX = cX + xOffset;
                    r.currentY = cY + yOffset;
                    r.hb.move(r.currentX, r.currentY);
                }
            }
        }
    
        private static class RelicPlacementLocator extends SpireInsertLocator {
    
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(BossRelicSelectScreen.class, "relics");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, matcher);
                return new int[]{found[found.length-1]};
            }
    
        }
    }
    
}
