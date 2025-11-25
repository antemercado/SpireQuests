package spireQuests.questStats;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.*;

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
}
