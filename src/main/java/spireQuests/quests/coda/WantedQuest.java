package spireQuests.quests.coda;

import static spireQuests.Anniv8Mod.logger;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.util.NodeUtil;

public class WantedQuest extends AbstractQuest{

    public WantedQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.COMBAT_END, 3)
            .triggerCondition((x) -> AbstractDungeon.getCurrRoom().eliteTrigger)
            .setFailureTrigger(QuestTriggers.ACT_CHANGE)
            .add(this);
    }


    @Override
    public boolean canSpawn() {
        return NodeUtil.canPathToNodes(node -> node.room instanceof MonsterRoomElite, 3);
    }
    
}
