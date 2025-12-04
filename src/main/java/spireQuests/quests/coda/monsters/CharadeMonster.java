package spireQuests.quests.coda.monsters;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.Anniv8Mod.makeID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.quests.coda.powers.FakeVigorPower;
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

    private static final Byte R_A = 0, G_A = 1, B_A = 2, P_A = 3, R_D = 4, G_D = 5, B_D = 6, P_D = 7, R_S = 8, G_S = 9, B_S = 10, P_S = 11;

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

    private OrbColor currColor;
    private MoveType currMoveType;
    private ArrayList<OrbColor> validColors = new ArrayList<>();
    private ArrayList<MoveType> validMoves = new ArrayList<>();
    private int greenAttackCount;
    private int redSpecialDebuff;
    private int purpleDefendBuff;
    private int blueBlockCount;
    private int greenSpecialDebuff;
    private int redAttackDebuff;
    private int turnsTaken;
    private int greenDefendDebuff;
    private int blueSpecialAmount;
    
    public CharadeMonster() {
        this(0, 200);
    }

    public CharadeMonster(float x, float y) {
        super(NAME, ID, 225, -4f, -16f, HB_W, HB_H, null, x, y);
        this.hb_y = -75.0F;
 
        type = EnemyType.ELITE;

        validColors = new ArrayList(Arrays.asList(OrbColor.values()));
        Collections.shuffle(validColors, new Random(AbstractDungeon.aiRng.randomLong()));
        validMoves = new ArrayList();
            validMoves.add(MoveType.OFFENSE);
            validMoves.add(MoveType.DEFENSE);
            validMoves.add(MoveType.OFFENSE);
            validMoves.add(MoveType.SPECIAL);
            validMoves.add(MoveType.OFFENSE);
            validMoves.add(MoveType.DEFENSE);
        this.turnsTaken = 0;
        this.redAttackDebuff = 3;
        this.redSpecialDebuff = 3;
        this.greenAttackCount = 4;
        this.greenDefendDebuff = 2;
        this.blueBlockCount = 0;
        this.purpleDefendBuff = 10;
        this.greenSpecialDebuff = 5;
        this.blueSpecialAmount = calcAscensionTankiness(75);

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.greenAttackCount = 5;
            
        }
        if (AbstractDungeon.ascensionLevel >= 8) {
            this.blueBlockCount = 1;
        }
        if (AbstractDungeon.ascensionLevel >= 18) {
            this.redAttackDebuff = 6;
            this.redSpecialDebuff = 5;
            this.greenDefendDebuff = 4;
            this.greenSpecialDebuff = 8;
        }
        

        addMove(R_A, Intent.ATTACK, calcAscensionDamage(20));
        addMove(R_D, Intent.DEFEND);
        addMove(R_S, Intent.DEBUFF);
        addMove(G_A, Intent.ATTACK, calcAscensionDamage(6), greenAttackCount, true);
        addMove(G_D, Intent.DEFEND_BUFF);
        addMove(G_S, Intent.DEBUFF);
        addMove(B_A, Intent.ATTACK, calcAscensionDamage(40));
        addMove(B_D, Intent.DEFEND);
        addMove(B_S, Intent.UNKNOWN);
        addMove(P_A, Intent.ATTACK_DEFEND, calcAscensionDamage(20));
        addMove(P_D, Intent.DEFEND);
        addMove(P_S, Intent.BUFF);

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
    protected void getMove(int i) {
        getRandomMove();
        switch (currColor) {
            case RED:
                switch (currMoveType) {
                    case DEFENSE:
                        setMoveShortcut(R_D);
                        break;
                    case OFFENSE:
                        setMoveShortcut(R_A);
                        break;
                    case SPECIAL:
                        setMoveShortcut(R_S);
                        break;
                }
                break;
            case GREEN:
                switch (currMoveType) {
                    case DEFENSE:
                        setMoveShortcut(G_D);
                        break;
                    case OFFENSE:
                        setMoveShortcut(G_A);
                        break;
                    case SPECIAL:
                        setMoveShortcut(G_S);
                        break;
                }
                break;
            case BLUE:
                switch (currMoveType) {
                    case DEFENSE:
                        setMoveShortcut(B_D);
                        break;
                    case OFFENSE:
                        setMoveShortcut(B_A);
                        break;
                    case SPECIAL:
                        setMoveShortcut(B_S);
                        break;
                }
                break;
            case PURPLE:
                switch (currMoveType) {
                    case DEFENSE:
                        setMoveShortcut(P_D);
                        break;
                    case OFFENSE:
                        setMoveShortcut(P_A);
                        break;
                    case SPECIAL:
                        setMoveShortcut(P_S);
                        break;
                }
                break;
        }
    }
    private void getRandomMove() {
        currColor = validColors.get(this.turnsTaken % validColors.size());
        changeColor(currColor);
        currMoveType = validMoves.get(this.turnsTaken % validMoves.size());
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
            case RED:
                e = state.setAnimation(1, "red_loop", true);
                break;
            case GREEN:
                e = state.setAnimation(1, "green_loop", true);
                break;
            case BLUE:
                e = state.setAnimation(1, "blue_loop", true);
                break;
            case PURPLE:
                e = state.setAnimation(1, "purple_loop", true);
                break;
            default:
                e = state.setAnimation(1, "color_loop", true);
                break;
        }
        e.setTimeScale(1.0F);
    }

    @Override
    public void takeTurn() {
        int vigorAmount = 0;
        if (this.hasPower(FakeVigorPower.ID)) {
            vigorAmount = this.getPower(FakeVigorPower.ID).amount;
        }
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage + vigorAmount, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        this.turnsTaken++;

        switch (nextMove) {
            case 0: // Red Attack: Uppercut
                addToBot(new DamageAction(Wiz.p(), info, AttackEffect.SMASH));
                addToBot(new ApplyPowerAction(Wiz.p(), this, new VulnerablePower(Wiz.p(), this.redAttackDebuff, true)));
                addToBot(new ApplyPowerAction(Wiz.p(), this, new WeakPower(Wiz.p(), this.redAttackDebuff, true)));
                clearVigor();
                break;
            case 1: // Green Attack: Blade Dance
                for (int i = 0; i < this.greenAttackCount; i++ ) {
                    addToBot(new DamageAction(Wiz.p(), info, AttackEffect.SLASH_HORIZONTAL));
                }
                clearVigor();
                break;
            case 2: // Blue Attack: Hyperbeam
                addToBot(new DamageAction(Wiz.p(), info));
                clearVigor();
                break;
            case 3: // Purple Attack: Wallop
                addToBot(new DamageAction(Wiz.p(), info, AttackEffect.BLUNT_HEAVY));
                addToBot(new GainBlockAction(this, this, Wiz.p().lastDamageTaken));
                clearVigor();
                break;
            case 4: // Red Block: Impervious
                addToBot(new GainBlockAction(this, 30));
                break;
            case 5: // Green Block: Leg Sweep
                addToBot(new GainBlockAction(this, 20));
                addToBot(new ApplyPowerAction(Wiz.p(), this, new WeakPower(Wiz.p(), this.greenDefendDebuff, true)));
                break;
            case 6: // Blue Block: Genetic Algorithm
                addToBot(new GainBlockAction(this, 10 * (this.blueBlockCount + 1)));
                this.blueBlockCount += 1;
                break;
            case 7: // Purple Block: Wish (Plated Armor)
                AbstractPower pta;
                if (AbstractDungeon.ascensionLevel >= 8) {
                    pta = new MetallicizePower(this, purpleDefendBuff);
                } else {
                    pta = new PlatedArmorPower(this, purpleDefendBuff);
                }
                addToBot(new ApplyPowerAction(this, this, pta));
                break;
            case 8: // Red Special: Immolate
                AbstractCard burn = new Burn();
                burn.upgrade();
                addToBot(new MakeTempCardInDiscardAction(burn, redSpecialDebuff));
                break;
            case 9: // Green Special: Malaise
                addToBot(new ApplyPowerAction(Wiz.p(), this, new StrengthPower(Wiz.p(), -1 * this.greenSpecialDebuff)));
                addToBot(new ApplyPowerAction(Wiz.p(), this, new WeakPower(Wiz.p(), this.greenSpecialDebuff, true)));
                break;
            case 10: // Blue Special: Self Repair
                addToBot(new HealAction(this, this, this.blueSpecialAmount));
                break;
            case 11: // Purple Special: Akkebeko
                addToBot(new ApplyPowerAction(this, this, new FakeVigorPower(this, Math.max(1, this.turnsTaken))));
                break;
        }

        addToBot(new RollMoveAction(this));
    }

    private void clearVigor() {
        if (this.hasPower(FakeVigorPower.ID)){
            addToBot(new RemoveSpecificPowerAction(this, this, this.getPower(FakeVigorPower.ID)));
        }
    }
    
}
