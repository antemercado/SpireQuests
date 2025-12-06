package spireQuests.quests.coda.monsters;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.Anniv8Mod.makeID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.quests.coda.powers.MonsterSpiritShieldPower;
import spireQuests.util.Wiz;
public class CharadeMonster extends AbstractSQMonster {

    public static final String ID = makeID(CharadeMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    private static final String NAME = monsterStrings.NAME;
    private static final String[] MOVES = monsterStrings.MOVES;

    private static final int HP_MIN = 300;
    private static final int HP_MAX = 325;
    private static final float HB_W = 420.0F;
    private static final float HB_H = 250.0F; //534

    private static final Byte IRONCLAD = 0, SILENT = 1, DEFECT = 2, WATCHER = 3, WAKEUP = 4;

    private enum OrbColor {
        RED,
        GREEN,
        BLUE,
        PURPLE
    }

    private OrbColor currColor;
    private ArrayList<OrbColor> validColors = new ArrayList<>();
    private int greenAttackAmount;
    private int redDebuffAmount;
    private int purpleDefendBuff;
    private int greenDebuffAmount;
    private int turnsTaken;
    private int blueBuffAmount;
    private int purpleAttackBuff;
    private boolean isAwake;
    private int redBlockAmount;
    private int blueDamageAmount;
    private boolean redDebuffUpgraded = false;
    
    public CharadeMonster() {
        this(0, 200);
    }

    public CharadeMonster(float x, float y) {
        super(NAME, ID, 225, -4f, -16f, HB_W, HB_H, null, x, y);
        this.hb_y = -75.0F;
 
        type = EnemyType.ELITE;

        this.isAwake = false;

        ArrayList<OrbColor> attackColors = new ArrayList();
        ArrayList<OrbColor> defendColors = new ArrayList();
        attackColors.add(OrbColor.GREEN);
        attackColors.add(OrbColor.BLUE);
        Collections.shuffle(attackColors, new Random(AbstractDungeon.aiRng.randomLong()));
        defendColors.add(OrbColor.RED);
        defendColors.add(OrbColor.PURPLE);
        Collections.shuffle(defendColors, new Random(AbstractDungeon.aiRng.randomLong()));
        validColors = new ArrayList();
        validColors.add(attackColors.get(0));
        validColors.add(defendColors.get(0));
        validColors.add(attackColors.get(1));
        validColors.add(defendColors.get(1));

        this.turnsTaken = 0;

        this.redBlockAmount = 20;
        this.redDebuffAmount = 2;
        
        this.greenAttackAmount = 4;
        this.greenDebuffAmount = 2;
        
        this.blueDamageAmount = 28;
        this.blueBuffAmount = 1;
        
        this.purpleDefendBuff = 4;
        this.purpleAttackBuff = 1;
        
        if (AbstractDungeon.ascensionLevel >= 3) {
            this.blueDamageAmount = 32;

            this.greenAttackAmount = 5;
        }
        if (AbstractDungeon.ascensionLevel >= 8) {
            this.purpleDefendBuff = 6;
        }
        if (AbstractDungeon.ascensionLevel >= 18) {
            this.redDebuffUpgraded = true;

            this.blueBuffAmount = 2;
            this.blueDamageAmount = 14;
        }

        addMove(IRONCLAD, Intent.DEFEND_DEBUFF);
        addMove(SILENT, Intent.ATTACK_DEBUFF, 7, greenAttackAmount, true);
        if (AbstractDungeon.ascensionLevel >= 18) {
            addMove(DEFECT, Intent.ATTACK_BUFF, calcAscensionDamage(blueDamageAmount), 2, true);
        } else {
            addMove(DEFECT, Intent.ATTACK_BUFF, calcAscensionDamage(blueDamageAmount));
        }
        addMove(WATCHER, Intent.DEFEND_BUFF);
        addMove(WAKEUP, Intent.UNKNOWN);

        setHp(calcAscensionTankiness(HP_MIN), calcAscensionTankiness(HP_MAX));
        
        this.loadAnimation(makeContributionPath("coda", "charadeOrb/skeleton.atlas"), makeContributionPath("coda", "charadeOrb/skeleton.json"), 1.0F);
        
        /**  Track Info
        0 - Base anims
        1 - Fall Translation
        2 - Fall Rotation
        4 - Color loops
        */

        AnimationState.TrackEntry e1 = this.state.setAnimation(0, "asleep", true);
        AnimationState.TrackEntry e2 = this.state.setAnimation(4, "color_loop", true);

        e1.setTimeScale(1.0F);
        e2.setTimeScale(1.0F);

        stateData.setMix("asleep", "idle", 0.25F);
        stateData.setMix("idle", "attack", 0.4F);
        stateData.setMix("attack", "idle", 0.4F);
        stateData.setMix("idle", "hit", 0.2F);
        stateData.setMix("hit", "idle", 0.2F);
        stateData.setMix("hit", "attack", 0.4F);
        stateData.setMix("attack", "hit", 0.4F);

        stateData.setMix("hit", "idle", 0.2F);

        stateData.setMix("red_loop", "green_loop", 0.4F);
        stateData.setMix("red_loop", "blue_loop", 0.4F);
        stateData.setMix("red_loop", "purple_loop", 0.4F);
        stateData.setMix("green_loop", "red_loop", 0.4F);
        stateData.setMix("green_loop", "blue_loop", 0.4F);
        stateData.setMix("green_loop", "purple_loop", 0.4F);
        stateData.setMix("blue_loop", "red_loop", 0.4F);
        stateData.setMix("blue_loop", "green_loop", 0.4F);
        stateData.setMix("blue_loop", "purple_loop", 0.4F);
        stateData.setMix("purple_loop", "red_loop", 0.4F);
        stateData.setMix("purple_loop", "green_loop", 0.4F);
        stateData.setMix("purple_loop", "blue_loop", 0.4F);
    }

    @Override
    public void usePreBattleAction() {
        if (!this.isAwake) {
            addToBot(new GainBlockAction(this, 100));
        }
    }

    @Override
    protected void getMove(int i) {
        if (this.firstMove) {
            setMoveShortcut(WAKEUP);
            this.firstMove = false;
            return;
        }
        currColor = validColors.get(this.turnsTaken % validColors.size());
        changeColor(currColor);
        switch (currColor) {
            case RED:
                setMoveShortcut(IRONCLAD);
                break;
            case GREEN:
                setMoveShortcut(SILENT);
                break;
            case BLUE:
                setMoveShortcut(DEFECT);
                break;
            case PURPLE:
                addToBot(new ApplyPowerAction(this, this, new MonsterSpiritShieldPower(this, this.purpleDefendBuff)));
                setMoveShortcut(WATCHER);
                break;
        }
    }

    @Override
    public void changeState(String stateName) {
        switch (stateName) {
            case "AWAKE":
                this.isAwake = true;
                state.setAnimation(0, "idle", true);
                state.setAnimation(1, "wake2", false);
                state.setAnimation(2, "fall_swing", false);
                break;
            case "ATTACK":
                state.setAnimation(0, "attack", false);
                state.addAnimation(0, "idle", true, 0.0F);
                break;
        }
    }

    @Override
    public void damage(DamageInfo info) {
        if (info.owner != null && info.type != DamageType.THORNS && info.output - currentBlock > 0) {
            if (!this.isAwake) {
                addToBot(new ChangeStateAction(this, "AWAKE"));
            } else {
                state.setAnimation(0, "hit", false);
                state.addAnimation(0, "idle", true, 0.0F);
            }
        }

        super.damage(info);
    }

    private void changeColor(OrbColor color) {
        AnimationState.TrackEntry e;
        switch (color) {
            case RED:
                e = state.addAnimation(4, "red_loop", true, 2.0F);
                break;
            case GREEN:
                e = state.addAnimation(4, "green_loop", true, 2.0F);
                break;
            case BLUE:
                e = state.addAnimation(4, "blue_loop", true, 2.0F);
                break;
            case PURPLE:
                e = state.addAnimation(4, "purple_loop", true, 2.0F);
                break;
            default:
                e = state.addAnimation(4, "color_loop", true, 2.0F);
                break;
        }
        e.setTimeScale(1.0F);
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        this.turnsTaken++;

        switch (nextMove) {
            case 0: // IRONCLAD
                addToBot(new GainBlockAction(this, calcAscensionTankiness(redBlockAmount)));
                addToBot(new ChangeStateAction(this, "ATTACK"));
                AbstractCard burn = new Burn();
                if (this.redDebuffUpgraded) {
                    burn.upgrade();
                }
                addToBot(new MakeTempCardInDiscardAction(burn, redDebuffAmount));
                break;
            case 1: // SILENT
                addToBot(new ChangeStateAction(this, "ATTACK"));
                for (int i = 0; i < this.greenAttackAmount; i++ ) {
                    addToBot(new DamageAction(Wiz.p(), info, AttackEffect.SLASH_HORIZONTAL));
                }
                addToBot(new ApplyPowerAction(Wiz.p(), this, new WeakPower(Wiz.p(), this.greenDebuffAmount, true)));
                break;
            case 2: // DEFECT
                addToBot(new ChangeStateAction(this, "ATTACK"));
                if (AbstractDungeon.ascensionLevel >= 18) {
                    addToBot(new DamageAction(Wiz.p(), info));
                    addToBot(new DamageAction(Wiz.p(), info));
                } else {
                    addToBot(new DamageAction(Wiz.p(), info));
                }
                addToBot(new ApplyPowerAction(this, this, new ArtifactPower(this, blueBuffAmount)));
                break;
            case 3: // WATCHER
                addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, this.purpleAttackBuff)));
                break;
            case 4: // WAKE UP
                if (!this.isAwake) {
                    addToBot(new WaitAction(1.0f));
                    addToBot(new ChangeStateAction(this, "AWAKE"));
                }
            }

        addToBot(new RollMoveAction(this));
    }

}
