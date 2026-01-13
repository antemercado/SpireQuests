package spireQuests.quests.coda;

import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.OrangePellets;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class TripleDipperQuest extends AbstractQuest {

    public TripleDipperQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.ADD_CARD, 3)
            .triggerCondition((c) -> c.type == CardType.ATTACK)
            .add(this);

        new TriggerTracker<>(QuestTriggers.ADD_CARD, 3)
            .triggerCondition((c) -> c.type == CardType.SKILL)
            .add(this);

        new TriggerTracker<>(QuestTriggers.ADD_CARD, 3)
            .triggerCondition((c) -> c.type == CardType.POWER)
            .add(this);

        addReward(new QuestReward.RelicReward(new OrangePellets()));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum > 1;
    }

}