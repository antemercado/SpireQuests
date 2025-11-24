package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.screens.options.ConfirmPopup;

public class DeleteSavePatch {

    @SpirePatch2 (clz = ConfirmPopup.class, method = "abandonRunFromMainMenu")
    public static class FailQuests {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(ConfirmPopup __instance) {

        }

        // Locator @ line:217 in ConfirmPopup class
    }
    
}
