package snownight.seed;

import java.util.*;

import static snownight.seed.Reputation_Database.*;

@SuppressWarnings("all")
public class RDData {

    private String player_name;
    final private String player_uuid;
    final private List<RDSubmit> submits = new ArrayList<>();
    private double weighted_score;

    public List<RDSubmit> getSubmits() {
        return submits;
    }

    public RDData(String player_uuid) {
        this.player_uuid = player_uuid;
        this.weighted_score = 0;
        //this.player_name = Bukkit.getPlayer(player_uuid).getName();
    }

    public RDData(String player_uuid, Collection<RDSubmit> submits, double weighted_score) {
        this.player_uuid = player_uuid;
        this.submits.addAll(submits);
        this.weighted_score = weighted_score;
        //this.player_name = Bukkit.getPlayer(player_uuid).getName();
    }

    public double getScore() {
        return weighted_score;
    }

    public boolean save() {
        List<Map<String, String>> saving = new ArrayList<>();
        for (RDSubmit submit : submits) {
            try {
                saving.add(submit.serialize());
            } catch (NullPointerException e) {
                submit.submit();
                try {
                    saving.add(submit.serialize());
                } catch (NullPointerException ex) {
                    sendError(ex.getMessage());
                }
            }
        }
        data.set(player_uuid+".submits",saving);
        data.set(player_uuid+".weighted_score",weighted_score);
        try {
            data.save(data_f);
        } catch (Exception e) {
            sendError("data文件读取错误！");
            return false;
        }
        return true;
    }

    public void addSubmit(String uuid, long timestamp, String player_uuid, double points, String comments) {
        RDSubmit tmprds = new RDSubmit(player_uuid,points,comments);
        tmprds.setTimestamp(timestamp);
        tmprds.setUuid(uuid);
        submits.add(tmprds);
    }
    public void addSubmit(RDSubmit a) {
        submits.add(a);
    }

    public void countScore() {
        double score = 0;
        for (RDSubmit submit : submits) {
            if(submit.getTimestamp()>=period_use.getTime())continue;
            double trust = 0;
            if(server_trust.containsKey(UUID.fromString(submit.getServerUuid())))
                trust = server_trust.get(UUID.fromString(submit.getServerUuid()));
            score += submit.getPoints() * (trust/5);
        }
        this.weighted_score = score;
    }
}
