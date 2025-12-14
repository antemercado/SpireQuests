package spireQuests.quests.coda.relics;

import static spireQuests.Anniv8Mod.makeID;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import spireQuests.abstracts.AbstractSQRelic;

public class KeyringRelic extends AbstractSQRelic {

    public static final String ID = makeID(KeyringRelic.class.getSimpleName());
    private AbstractRelic extraRelic = null;

    public KeyringRelic() {
        super(ID, "coda", RelicTier.SPECIAL, LandingSound.CLINK);
        this.extraRelic = null;
    }

    public AbstractRelic getExtraRelic() {
        return extraRelic;
    }

    public void setExtraRelic(AbstractRelic extraRelic) {
        this.extraRelic = extraRelic;
    }

    
}
