package spireQuests.questStats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import spireQuests.Anniv8Mod;
import spireQuests.quests.QuestManager;

public class QuestStats {    
    public int timesSeen = 0;
    public int timesTaken = 0;
    public int timesComplete = 0;
    public int timesFailed = 0;

    public QuestStats() {
        this.timesSeen = 0;
        this.timesTaken = 0;
        this.timesComplete = 0;
        this.timesFailed = 0;       
    }

    public QuestStats(String qid) {
        ensureInitialized(qid, getSlot());
        this.timesSeen = config.getInt(questKey(qid, "SEEN", getSlot()));
        this.timesTaken = config.getInt(questKey(qid, "TAKEN", getSlot()));
        this.timesComplete = config.getInt(questKey(qid, "COMPLETE", getSlot()));
        this.timesFailed = config.getInt(questKey(qid, "FAILED", getSlot()));
    }

    public static boolean isTracking = false;
    private static SpireConfig config;

    public static void initialize() {
        try {
            config = new SpireConfig(Anniv8Mod.modID, "questStats");
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveStats() {
        try {
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    private static String questKey(String questID, String field, int saveSlot) {
        StringBuilder sb = new StringBuilder();
        if (saveSlot != 0) {
            sb.append(saveSlot + "_");
        }
        sb.append(questID + "_" + field);
        return sb.toString();
    }
    
    private static void ensureInitialized(String questID, int saveSlot) {
        if (config.has(questKey(questID, "SEEN", saveSlot))){
            return;
        }
        config.setInt(questKey(questID, "SEEN", saveSlot), 0);
        config.setInt(questKey(questID, "TAKEN", saveSlot), 0);
        config.setInt(questKey(questID, "COMPLETE", saveSlot), 0);
        config.setInt(questKey(questID, "FAILED", saveSlot), 0);
        saveStats();
    }

    private static int getSlot() {
        return CardCrawlGame.saveSlot;
    }

    public static void increaseStat(String questId, String stat) {
        int slot = getSlot();
        ensureInitialized(questId, slot);
        String k = questKey(questId, stat, slot);
        config.setInt(k, config.getInt(k) + 1);
        saveStats();
    }

    public static void increaseSeen(String questId) {
        increaseStat(questId, "SEEN");
    }
    public static void increaseTaken(String questId) {
        increaseStat(questId, "TAKEN");
    }
    public static void increaseComplete(String questId) {
        increaseStat(questId, "COMPLETE");
    }
    public static void increaseFailed(String questId) {
        increaseStat(questId, "FAILED");
    }

    public static QuestStats getAllStats() {
        List<String> allIDs = QuestManager.getAllQuests().stream().map(q -> q.id).collect(Collectors.toList());
        List<QuestStats> allStats = new ArrayList<>();
        for (String q : allIDs) {
            allStats.add(new QuestStats(q));
        }
        QuestStats ret = new QuestStats();
        ret.timesSeen = allStats.stream().mapToInt(s -> s.timesSeen).sum();
        ret.timesTaken = allStats.stream().mapToInt(s -> s.timesTaken).sum();
        ret.timesComplete = allStats.stream().mapToInt(s -> s.timesComplete).sum();
        ret.timesFailed = allStats.stream().mapToInt(s -> s.timesFailed).sum();
        return ret;
    }
}
