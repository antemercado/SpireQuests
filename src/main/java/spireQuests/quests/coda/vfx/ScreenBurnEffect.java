package spireQuests.quests.coda.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.GiantFireEffect;

public class ScreenBurnEffect extends AbstractGameEffect {

    private float timer = 0.0F;

    public ScreenBurnEffect() {
        this.duration = this.startingDuration = 1.0F;
    }

    @Override
    public void update() {
        if (this.duration == this.startingDuration) {
            CardCrawlGame.sound.play("???");
            AbstractDungeon.effectsQueue.add(new BorderLongFlashEffect(Color.FIREBRICK));
            
        }        
        this.duration -= Gdx.graphics.getDeltaTime();
        this.timer -= Gdx.graphics.getDeltaTime();
        if (this.timer < 0.0F) {
            this.timer = 0.05F;
            AbstractDungeon.effectsQueue.add(new GiantFireEffect());
            AbstractDungeon.effectsQueue.add(new GiantFireEffect());
            AbstractDungeon.effectsQueue.add(new GiantFireEffect());
            AbstractDungeon.effectsQueue.add(new GiantFireEffect());
            AbstractDungeon.effectsQueue.add(new GiantFireEffect());
            AbstractDungeon.effectsQueue.add(new GiantFireEffect());
            AbstractDungeon.effectsQueue.add(new GiantFireEffect());
            AbstractDungeon.effectsQueue.add(new GiantFireEffect());
        }
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    @Override
    public void dispose() {}

    @Override
    public void render(SpriteBatch sb) {}
    
}
