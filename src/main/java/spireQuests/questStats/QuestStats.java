package spireQuests.questStats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.Logger;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestboundModPatch.saveQuestboundCards;
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
        JsonObject statObj = QuestStatManager.getStatsForQuest(qid);
        this.timesSeen = statObj.get(QuestStatManager.SEEN).getAsInt();
        this.timesTaken = statObj.get(QuestStatManager.TAKEN).getAsInt();
        this.timesComplete = statObj.get(QuestStatManager.COMPLETED).getAsInt();
        this.timesFailed = statObj.get(QuestStatManager.FAILED).getAsInt();
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
    
    public static class QuestStatManager {
        public static final String FAILED = "failed";
        public static final String COMPLETED = "completed";
        public static final String SEEN = "seen";
        public static final String TAKEN = "taken";
        private static final String[] STAT_ENTRIES = {SEEN, TAKEN, COMPLETED, FAILED};
        
        private static SpireConfig config;
        private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        private static Logger logger = new Logger(QuestStatManager.class.getSimpleName());
        
        private static ArrayList<String> seenBuffer = new ArrayList<>();
        private static ArrayList<String> takenBuffer = new ArrayList<>();
        private static ArrayList<String> failedBuffer = new ArrayList<>();
        private static ArrayList<String> completedBuffer = new ArrayList<>();

        public static void initialize() {
            try {
                config = new SpireConfig(Anniv8Mod.modID, "questStats");
                if (!config.has("questData")) {
                    config.setString("questData", "{}");
                    config.save();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static JsonObject getRoot() {
            return gson.fromJson(config.getString("questData"), JsonObject.class);
        }

        private static void saveRoot(JsonObject root) {
            config.setString("questData", gson.toJson(root));
            try {
                config.save();
            } catch (IOException e) {
                logger.info("Can't save Quest Stats root, skipping ...");
                e.printStackTrace();
            }
        }

        private static JsonObject getSaveJson(JsonObject root) {
            return getSaveJson(root, CardCrawlGame.saveSlot);
        }

        private static JsonObject getSaveJson(JsonObject root, int saveSlot) {
            String key = Integer.toString(saveSlot);
            if (!root.has(key)) {
                root.add(key, new JsonObject());
            }
            return root.getAsJsonObject(key);
        }

        private static JsonObject getAndValidateQuestObject(JsonObject saveJson, String questId) {
            if (!saveJson.has(questId)) {
                saveJson.add(questId, new JsonObject());
            }
            JsonObject obj = saveJson.getAsJsonObject(questId);

            for (String e : STAT_ENTRIES) {
                if (!obj.has(e)) {
                    obj.addProperty(e, 0);
                }
            }

            return obj;
        }

        public static void markSeen(String questID) {
            seenBuffer.add(questID);
        }
        public static void markTaken(String questID) {
            takenBuffer.add(questID);
        }
        public static void markComplete(String questID) {
            completedBuffer.add(questID);
        }
        public static void markFailed(String questID) {
            failedBuffer.add(questID);
        }
        
        public static void commitStats() {
            JsonObject root = getRoot();
            JsonObject save = getSaveJson(root);

            for (String q : seenBuffer) {
                JsonObject obj = getAndValidateQuestObject(save, q);
                obj.addProperty(SEEN, obj.get(SEEN).getAsInt() + 1);
            }
            for (String q : takenBuffer) {
                JsonObject obj = getAndValidateQuestObject(save, q);
                obj.addProperty(TAKEN, obj.get(TAKEN).getAsInt() + 1);
            }
            for (String q : completedBuffer) {
                JsonObject obj = getAndValidateQuestObject(save, q);
                obj.addProperty(COMPLETED, obj.get(COMPLETED).getAsInt() + 1);
            }
            for (String q : failedBuffer) {
                JsonObject obj = getAndValidateQuestObject(save, q);
                obj.addProperty(FAILED, obj.get(FAILED).getAsInt() + 1);
            }

            saveRoot(root);
            resetBuffers();
        }

        private static void resetBuffers() {
            seenBuffer.clear();
            takenBuffer.clear();
            completedBuffer.clear();
            failedBuffer.clear();
        }

        public static JsonObject getStatsForQuest(String questID) {
            return getAndValidateQuestObject(getSaveJson(getRoot()), questID);
        }
    }
}
