package spireQuests.questStats;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;

import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    private static final float LEFT_X_ANCHOR = 490.0F * Settings.xScale;

    private static final float DROPDOWN_Y = 780.0F * Settings.yScale;

    private static final float QUEST_NAME_Y = 715.0F * Settings.yScale;
    private static final float QUEST_AUTHOR_Y = 675.0F * Settings.yScale;
    private static final float QUEST_DESCRIPTION_Y = 650.0F * Settings.yScale;
    private static final float QUEST_DESCRIPTION_LENGTH = 700.0F * Settings.xScale;

    private static final float QUEST_SEEN_Y = 425.0F * Settings.yScale;
    private static final float QUEST_TAKEN_Y = 375.0F * Settings.yScale;
    private static final float QUEST_COMPLETE_Y = 325.0F * Settings.yScale;
    private static final float QUEST_FAILED_Y = 275.0F * Settings.yScale;

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
        renderStats(sb);
        renderTrophy(sb);
        questDropdown.render(sb, LEFT_X_ANCHOR, DROPDOWN_Y);
        cancelButton.render(sb);
    }

    private void renderStats(SpriteBatch sb) {
        // Name
        if (selectedQuest == null) {
            FontHelper.renderFont(sb, FontHelper.tipHeaderFont, "TEXT[ALL_QUESTS]", LEFT_X_ANCHOR, QUEST_NAME_Y, Settings.CREAM_COLOR);
        } else {
            FontHelper.renderFont(sb, FontHelper.tipHeaderFont, selectedQuest.name, LEFT_X_ANCHOR, QUEST_NAME_Y, Settings.CREAM_COLOR);
        }

        // Quest Info
        if (selectedQuest == null) {
            // General Quest Info
        } else {
            // Author
            FontHelper.renderFont(sb, FontHelper.tipBodyFont, selectedQuest.author, LEFT_X_ANCHOR, QUEST_AUTHOR_Y, Settings.CREAM_COLOR);
            // Description
            FontHelper.renderSmartText(
                sb, FontHelper.tipBodyFont, 
                selectedQuest.description, 
                LEFT_X_ANCHOR, QUEST_DESCRIPTION_Y, QUEST_DESCRIPTION_LENGTH,
                FontHelper.tipBodyFont.getLineHeight(),
                Settings.CREAM_COLOR
            );
        }
        // Stats
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, 
            String.format("%s: %d", "TEXT[SEEN]", timesSeen), LEFT_X_ANCHOR, QUEST_SEEN_Y, Settings.CREAM_COLOR);
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, 
            String.format("%s: %d/%d (%.2f%%)", "TEXT[TAKEN]", timesTaken, timesSeen, getPercent(timesTaken, timesSeen)), 
            LEFT_X_ANCHOR, QUEST_TAKEN_Y, Settings.CREAM_COLOR);
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, 
            String.format("%s: %d/%d (%.2f%%)", "TEXT[COMPLETE]", timesCompleted, timesTaken, getPercent(timesCompleted, timesTaken)), 
            LEFT_X_ANCHOR, QUEST_COMPLETE_Y, Settings.CREAM_COLOR);
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, 
            String.format("%s: %d/%d (%.2f%%)", "TEXT[FAILED]", timesFailed, timesTaken, getPercent(timesFailed, timesTaken)), 
            LEFT_X_ANCHOR, QUEST_FAILED_Y, Settings.CREAM_COLOR);
    }

    private float getPercent(int num, int den) {
        if (den == 0) {
            return 0.0f;
        }
        return (num * 100.0f)/den;
    }

    private void renderTrophy(SpriteBatch sb) {

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
    }
}
