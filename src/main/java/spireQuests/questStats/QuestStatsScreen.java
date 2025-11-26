package spireQuests.questStats;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;

import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.util.TexLoader;

import static spireQuests.Anniv8Mod.makeUIPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QuestStatsScreen implements DropdownMenuListener {
    
    public static class Enum {
        @SpireEnum
        public static MainMenuScreen.CurScreen QUEST_STATS_SCREEN;
    }

    private static final Logger logger = LogManager.getLogger(QuestStatsScreen.class.getName());

    private static final float X_ANCHOR = 440.0F * Settings.xScale;
    private static final float Y_ANCHOR = (1080.0F - 195.0F) * Settings.yScale; // 825
    
    private static final float LEFT_ALIGN = X_ANCHOR + (25.0F * Settings.xScale);
    private static final float DROPDOWN_Y = Y_ANCHOR - (75.0F * Settings.yScale);

    private static final float QUEST_NAME_Y = Y_ANCHOR - (130.0F * Settings.yScale);
    private static final float QUEST_AUTHOR_Y = Y_ANCHOR - (170.0F * Settings.yScale);
    private static final float QUEST_DESCRIPTION_Y = Y_ANCHOR - (205.0F * Settings.yScale);
    private static final float QUEST_DESCRIPTION_LENGTH = 750.0F * Settings.xScale;

    private static final float QUEST_STAT_Y = Y_ANCHOR - (525.0F * Settings.yScale);

    private static final float REWARD_X = LEFT_ALIGN + (35.0F * Settings.xScale);
    private static final float REWARD_OFFSET = 150.0F * Settings.xScale;
    private static final float REWARD_Y = Y_ANCHOR - (375.0F * Settings.yScale);

    private static final Texture BG = TexLoader.getTexture(makeUIPath("stats/background.png"));
    private static final float BG_X = X_ANCHOR;
    private static final float BG_Y = 225.0F * Settings.yScale;

    private MenuCancelButton cancelButton = new MenuCancelButton();
    private DropdownMenu questDropdown;

    private Collection<AbstractQuest> allQuests;
    private Map<String, AbstractQuest> allQuestsMap;
    private ArrayList<String> allQuestList;
    private AbstractQuest selectedQuest;
    private QuestStats selectedQuestStats;
    private Map<String, String> nameIDMap;

    private int timesSeen = 0;
    private int timesTaken = 0;
    private int timesCompleted = 0;
    private int timesFailed = 0;
    private float descriptionHeight = 0.0f;

    private ArrayList<StatRewardBox> rewardBoxes = new ArrayList<>();

    private StringBuilder strbuild = new StringBuilder();
    
    public QuestStatsScreen() {
        allQuests = QuestManager.getAllQuests();
        allQuestsMap = allQuests.stream().collect(Collectors.toMap(q -> q.id, q -> q));
        nameIDMap = allQuests.stream().collect(Collectors.toMap(q -> q.name, q -> q.id));
        allQuestList = new ArrayList<>(allQuestsMap.values().stream().map(q -> q.name).collect(Collectors.toList()));
        allQuestList.sort(null);
        allQuestList.add(0, "TEXT[ALL_QUESTS]");
        questDropdown = new DropdownMenu(this, allQuestList, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        selectedQuestStats = QuestStats.getAllStats();
        refreshData();
    }

    public void open() {
        CardCrawlGame.mainMenuScreen.screen = Enum.QUEST_STATS_SCREEN;
        CardCrawlGame.mainMenuScreen.darken();
        cancelButton.show("TEXT[CANCEL]");
    }

    public void update() {
        if (questDropdown.isOpen) {
            questDropdown.update();
        } else {
            updateButtons();
            questDropdown.update();
            for (StatRewardBox box : rewardBoxes) {
                box.update();
            }
        }
    }

    private void updateButtons() {
        cancelButton.update();
        if (cancelButton.hb.clicked || InputHelper.pressedEscape) {
            CardCrawlGame.mainMenuScreen.superDarken = false;
            InputHelper.pressedEscape = false;
            cancelButton.hb.clicked = false;
            cancelButton.hide();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.mainMenuScreen.lighten();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(BG, BG_X, BG_Y);
        renderStats(sb);
        renderTrophy(sb);
        renderRewards(sb);
        questDropdown.render(sb, LEFT_ALIGN, DROPDOWN_Y);
        cancelButton.render(sb);
    }

    private void renderStats(SpriteBatch sb) {
        String nameText;
        // Name
        if (selectedQuest == null) {
            nameText = "TEXT[ALL_QUESTS]";
        } else {
            nameText = selectedQuest.name;
        }
        FontHelper.renderFont(sb, FontHelper.losePowerFont, nameText, LEFT_ALIGN, QUEST_NAME_Y, Settings.CREAM_COLOR);

        // Quest Info
        if (selectedQuest == null) {
            // General Quest Info
        } else {
            // Author
            FontHelper.renderFont(sb, FontHelper.tipBodyFont, selectedQuest.author, 
                LEFT_ALIGN + (10.0F * Settings.xScale),
                QUEST_AUTHOR_Y, Settings.CREAM_COLOR
            );
            // Description
            FontHelper.renderSmartText(
                sb, FontHelper.cardDescFont_N, 
                selectedQuest.description, 
                LEFT_ALIGN, QUEST_DESCRIPTION_Y, QUEST_DESCRIPTION_LENGTH,
                FontHelper.cardDescFont_N.getLineHeight(),
                Settings.CREAM_COLOR
            );
            
        }
        // Stats
        strbuild.setLength(0);
        strbuild.append(String.format("%s: %d NL ", "TEXT[SEEN]", timesSeen));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", "TEXT[TAKEN]", timesTaken, timesSeen, getPercent(timesTaken, timesSeen)));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", "TEXT[COMPLETE]", timesCompleted, timesTaken, getPercent(timesCompleted, timesTaken)));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", "TEXT[FAILED]", timesFailed, timesTaken, getPercent(timesFailed, timesTaken)));

        FontHelper.renderSmartText(
            sb, FontHelper.tipBodyFont, strbuild.toString(), 
            LEFT_ALIGN, QUEST_STAT_Y, QUEST_DESCRIPTION_LENGTH,
            FontHelper.tipBodyFont.getLineHeight(),
            Settings.CREAM_COLOR
        );
    }

    private float getPercent(int num, int den) {
        if (den == 0) {
            return 0.0f;
        }
        return (num * 100.0f)/den;
    }

    private void renderTrophy(SpriteBatch sb) {

    }

    private void renderRewards(SpriteBatch sb) {
        if (selectedQuest == null) {
            return;
        }
        for (StatRewardBox box : rewardBoxes) {
            box.render(sb);
        }
    }

    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        logger.warn("Changed dropdown detected");
        if (i == 0) {
            selectedQuestStats = QuestStats.getAllStats();
            selectedQuest = null;
        } else {
            String qid = nameIDMap.get(s);
            selectedQuestStats = new QuestStats(qid);
            selectedQuest = allQuestsMap.get(qid);
        }
        refreshData();
    }

    private void refreshData() {
        timesSeen = selectedQuestStats.timesSeen;
        timesTaken = selectedQuestStats.timesTaken;
        timesCompleted = selectedQuestStats.timesComplete;
        timesFailed = selectedQuestStats.timesFailed;

        rewardBoxes.clear();
        if (selectedQuest == null) {
            return;
        }

        this.descriptionHeight = FontHelper.getSmartHeight(FontHelper.cardDescFont_N, selectedQuest.description,
                QUEST_DESCRIPTION_LENGTH, FontHelper.cardDescFont_N.getLineHeight()
            );
        this.descriptionHeight -= FontHelper.cardDescFont_N.getLineHeight();

        float yLine = ((QUEST_DESCRIPTION_Y + this.descriptionHeight) - (QUEST_STAT_Y + FontHelper.tipBodyFont.getLineHeight())) / 2.0F;
        yLine = (QUEST_DESCRIPTION_Y + this.descriptionHeight) - (StatRewardBox.FRAME_Y / 2.0F) - yLine;

        if (yLine > REWARD_Y) {
            yLine = REWARD_Y;
        }

        float offset = 0.0f;
        if (selectedQuest.useDefaultReward) { // I thought this var was for if you had custom rewards or used addRewards. I was incorrect.
            rewardBoxes.add(new StatRewardBox(selectedQuest, REWARD_X, yLine));
        } else {
            for (QuestReward r : selectedQuest.questRewards) {
                rewardBoxes.add(new StatRewardBox(r, REWARD_X + offset, yLine));
                offset += REWARD_OFFSET;
            }
        }
        Collections.reverse(rewardBoxes);
    }

}
