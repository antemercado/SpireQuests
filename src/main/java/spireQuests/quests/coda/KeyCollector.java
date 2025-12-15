package spireQuests.quests.coda;

import com.megacrit.cardcrawl.core.Settings;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward.RelicReward;
import spireQuests.quests.coda.relics.KeyringRelic;

public class KeyCollector extends AbstractQuest {

    public KeyCollector() {
        super(QuestType.SHORT, QuestDifficulty.CHALLENGE);

        new PassiveTracker<Boolean>(() -> Settings.hasRubyKey && Settings.hasEmeraldKey && Settings.hasSapphireKey, true)
            {
                @Override
                public String progressString() {
                    return "";
                }
            }
            .setFailureTrigger(QuestTriggers.ACT_CHANGE)
            .add(this);

        addReward(new RelicReward(new KeyringRelic()));

        this.isAutoComplete = true;
    }

    @Override
    public boolean canSpawn() {
        return Settings.isFinalActAvailable;      
    }

}
