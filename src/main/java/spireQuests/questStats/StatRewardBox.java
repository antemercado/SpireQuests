package spireQuests.questStats;

import static spireQuests.Anniv8Mod.makeUIPath;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.QuestReward.CardReward;
import spireQuests.quests.QuestReward.GoldReward;
import spireQuests.quests.QuestReward.PotionReward;
import spireQuests.quests.QuestReward.RandomRelicReward;
import spireQuests.quests.QuestReward.RelicReward;
import spireQuests.util.ImageHelper;
import spireQuests.util.TexLoader;

public class StatRewardBox {
    private static final Texture CUSTOM_REWARD_IMG = ImageMaster.loadImage("images/ui/run_mods/shiny.png");
    private static final Texture BOX_OUTLINE = TexLoader.getTexture(makeUIPath("stats/reward_frame.png"));
    private static final Texture CARD_TEX = TexLoader.getTexture(makeUIPath("stats/card.png"));
    private static final Texture POTION_TEX = TexLoader.getTexture(makeUIPath("stats/potion.png"));
    private static final Texture RANDOM_RELIC_TEX = TexLoader.getTexture(makeUIPath("stats/relic.png"));
    private static final Texture GOLD_TEX = TexLoader.getTexture(makeUIPath("stats/gold.png"));

    private static final float BOX_SIDE = 125.0F;
    public static float HEIGHT = 100.0F * Settings.yScale;
    public static float WIDTH = 100.0F * Settings.xScale;
    public Hitbox hb;
    public TextureRegion img;
    private String header = "";
    private String body = "";
    private float xPos;
    private float yPos;
    private AbstractCard card = null;
    private AbstractPotion potion = null;
    private AbstractRelic relic = null;

    public StatRewardBox(QuestReward reward, float xPos, float yPos) {
        this(xPos, yPos);
        this.img = reward.icon();

        this.body = reward.rewardText;
        if (reward instanceof RelicReward) {
            relic = ((RelicReward)reward).getRelic().makeCopy();
            this.header = relic.name;
            this.body = relic.description;
        }
        if (reward instanceof CardReward) {
            card = ((CardReward)reward).getCard().makeStatEquivalentCopy();
            this.img = new TextureRegion(CARD_TEX);
        }
        if (reward instanceof PotionReward) {
            potion = ((PotionReward)reward).getPotion().makeCopy();
            this.header = potion.name;
            this.body = potion.description;
            this.img = new TextureRegion(POTION_TEX);
        }
        if (reward instanceof RandomRelicReward) {
            this.header = "TEXT[RANDOM_RELIC]";
            this.img = new TextureRegion(RANDOM_RELIC_TEX);
        }
        if (reward instanceof GoldReward) {
            this.header = "TEXT[GOLD REWARD]";
            this.img = new TextureRegion(GOLD_TEX);
        }

    }

    public StatRewardBox(AbstractQuest q, float xPos, float yPos) {
        this(xPos, yPos);
        this.img = new TextureRegion(CUSTOM_REWARD_IMG);
        this.header = "TEXT[OTHER_REWARD]";
        this.body = q.getRewardsText();
    }

    public StatRewardBox(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.hb = new Hitbox(xPos, yPos, WIDTH, HEIGHT);
    }

    public void render(SpriteBatch sb) {
        sb.draw(BOX_OUTLINE, xPos, yPos, BOX_SIDE, BOX_SIDE);
        sb.draw(this.img, xPos, yPos, WIDTH, HEIGHT);
        if (this.hb.hovered) {
            if (card != null) {
                card.current_x = InputHelper.mX + (AbstractCard.RAW_W * card.drawScale) * Settings.scale;
                card.current_y = InputHelper.mY;
                card.render(sb);
            } else {
               ImageHelper.tipBoxAtMousePos(this.header, this.body);
            }
        }
     }

    public void update() {
        this.hb.update();
        if (card != null && this.hb.hovered && InputHelper.justClickedRight) {
            CardCrawlGame.cardPopup.open(card);
        }
    }
}