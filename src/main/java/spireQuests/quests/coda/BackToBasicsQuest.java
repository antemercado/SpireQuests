package spireQuests.quests.coda;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

import java.util.ArrayList;

public class BackToBasicsQuest  extends AbstractQuest {

    public BackToBasicsQuest() {   
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggeredUpdateTracker<Integer, Void>(QuestTriggers.VICTORY, 0, 1, () -> {
            ArrayList<AbstractCard> cardsPlayed = AbstractDungeon.actionManager.cardsPlayedThisCombat;
            if (!AbstractDungeon.getCurrRoom().eliteTrigger) {
                return 0;
            }
            if (cardsPlayed == null || cardsPlayed.isEmpty()){
                return 1;
            }
            for (AbstractCard c : cardsPlayed) {
                if (c.rarity != CardRarity.BASIC) {
                    return 0;
                }
            }
            return 1;
        }, () -> {
            if (!AbstractDungeon.getCurrRoom().eliteTrigger) {
                return false;
            }
            ArrayList<AbstractCard> cardsPlayed = AbstractDungeon.actionManager.cardsPlayedThisCombat;
            for (AbstractCard c : cardsPlayed) {
                if (c.rarity != CardRarity.BASIC) {
                    return true;
                }
            }
            return false;
        })
            {
                @Override
                public String progressString() {
                    return "";
                }
            }
            .add(this);

        useDefaultReward = false;
        rewardsText = localization.EXTRA_TEXT[1];
    }

    @Override
    public boolean canSpawn() {
        for(AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == CardRarity.BASIC && AbstractDungeon.actNum > 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onComplete() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if ((c.rarity == CardRarity.BASIC ) && c.canUpgrade()) {
                c.upgrade();
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(),
                    MathUtils.random(0.1F, 0.9F) * Settings.WIDTH,
                    MathUtils.random(0.2F, 0.8F) * Settings.HEIGHT));
            }
        }
    }

}