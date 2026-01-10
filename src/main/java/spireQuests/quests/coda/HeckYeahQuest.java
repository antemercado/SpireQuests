package spireQuests.quests.coda;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

public class HeckYeahQuest extends AbstractQuest {

    public HeckYeahQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.TURN_END, 5)
            .triggerCondition((x) -> AbstractDungeon.player.hand.isEmpty())
            .add(this);

        addGenericReward();
    }
    
}
