package spireQuests.quests.coda.monsters;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.Anniv8Mod.makeID;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.GainStrengthPower;

import spireQuests.abstracts.AbstractSQMonster;

public class CharadeMonster extends AbstractSQMonster {

    public static final String ID = makeID(CharadeMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    private static final String NAME = monsterStrings.NAME;
    private static final String[] MOVES = monsterStrings.MOVES;

    private static final int HP_MIN = 200;
    private static final int HP_MAX = 250;
    private static final float HB_W = 420.0F;
    private static final float HB_H = 200.0F; //534

    private static final Byte TEST = 0;

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
    }

    @Override
    protected void getMove(int arg0) {
        setMoveShortcut(TEST);
    }

    @Override
    public void takeTurn() {
        addToBot(new ApplyPowerAction(this, this, new GainStrengthPower(this, 1)));
    }
    
}
