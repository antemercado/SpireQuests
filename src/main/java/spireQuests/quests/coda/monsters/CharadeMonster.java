package spireQuests.quests.coda.monsters;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.Anniv8Mod.makeID;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.GainStrengthPower;

import basemod.devcommands.Info;
import spireQuests.abstracts.AbstractSQMonster;
public class CharadeMonster extends AbstractSQMonster {

    public static final String ID = makeID(CharadeMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    private static final String NAME = monsterStrings.NAME;
    private static final String[] MOVES = monsterStrings.MOVES;

    private static final int HP_MIN = 200;
    private static final int HP_MAX = 250;
    private static final float HB_W = 420.0F;
    private static final float HB_H = 250.0F; //534

    private static final Byte TEST = 0;

    private enum OrbColor {
        RED,
        GREEN,
        BLUE,
        PURPLE
    }
    private enum MoveType {
        OFFENSE,
        DEFENSE,
        SPECIAL
    }
    
    public CharadeMonster() {
        this(0, 200);
    }

    public CharadeMonster(float x, float y) {
        super(NAME, ID, 225, -4f, -16f, HB_W, HB_H, null, x, y);
        this.hb_y = -75.0F;
        
        type = EnemyType.ELITE;

        addMove(TEST, Intent.UNKNOWN);

        setHp(calcAscensionTankiness(HP_MIN), calcAscensionTankiness(HP_MAX));
        
        this.loadAnimation(makeContributionPath("coda", "charadeOrb/skeleton.atlas"), makeContributionPath("coda", "charadeOrb/skeleton.json"), 1.0F);
        AnimationState.TrackEntry e1 = this.state.setAnimation(0, "idle", true);
        AnimationState.TrackEntry e2 = this.state.setAnimation(1, "color_loop", true);
        e1.setTimeScale(1.0F);
        e2.setTimeScale(1.0F);
        stateData.setMix("hit", "idle", 0.3F);
        stateData.setMix("attack", "idle", 0.25F);
        stateData.setMix("idle", "hit", 0.1F);
        stateData.setMix("idle", "attack", 0.1F);
        stateData.setMix("red_loop", "green_loop", 0.25F);
        stateData.setMix("red_loop", "blue_loop", 0.25F);
        stateData.setMix("red_loop", "purple_loop", 0.25F);
        stateData.setMix("green_loop", "red_loop", 0.25F);
        stateData.setMix("green_loop", "blue_loop", 0.25F);
        stateData.setMix("green_loop", "purple_loop", 0.25F);
        stateData.setMix("blue_loop", "red_loop", 0.25F);
        stateData.setMix("blue_loop", "green_loop", 0.25F);
        stateData.setMix("blue_loop", "purple_loop", 0.25F);
        stateData.setMix("purple_loop", "red_loop", 0.25F);
        stateData.setMix("purple_loop", "green_loop", 0.25F);
        stateData.setMix("purple_loop", "blue_loop", 0.25F);
    }

    @Override
    protected void getMove(int arg0) {
        setMoveShortcut(TEST);
    }
    @Override
    public void damage(DamageInfo info) {
        if (info.owner != null && info.type != DamageType.THORNS && info.output - currentBlock > 0) {
            AnimationState.TrackEntry e = state.setAnimation(0, "hit", false);
            state.addAnimation(0, "idle", true, 0.0F);
            e.setTimeScale(1.0f);
        }

        super.damage(info);
    }

    private void changeColor(OrbColor color) {
        AnimationState.TrackEntry e;
        switch (color) {
            case BLUE:
                e = state.setAnimation(1, "blue_loop", true);
                break;
            case GREEN:
                e = state.setAnimation(1, "green_loop", true);
                break;
            case PURPLE:
                e = state.setAnimation(1, "purple_loop", true);
                break;
            case RED:
                e = state.setAnimation(1, "red_loop", true);
                break;
            default:
                e = state.setAnimation(1, "color_loop", true);
                break;
        }
        e.setTimeScale(1.0F);
    }

    @Override
    public void takeTurn() {
        addToBot(new ApplyPowerAction(this, this, new GainStrengthPower(this, 1)));
        changeColor(OrbColor.values()[AbstractDungeon.miscRng.random(0, 3)]);
    }
    
}
