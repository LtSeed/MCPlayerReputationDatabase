package snownight.seed;

import java.util.*;
import java.util.function.Consumer;

import static snownight.seed.Reputation_Database.*;

public class RDSubmit {

    String uuid = null;
    long timestamp;
    final String player_uuid;
    final double points;
    final String comments;
    String server_uuid;

    public void submit(){
        submit(null);
    }

    public void submit(Consumer<RDSubmit> consumer){
        List<String> cmds = new ArrayList<>();
        cmds.add("--new");
        cmds.add("-n");
        cmds.add(player_uuid);
        cmds.add("-r");
        cmds.add(comments);
        cmds.add("-s");
        cmds.add(String.valueOf(points));
        new CLIRuntime(cmds,
                outs -> {
                    if (outs.get(outs.size() - 1).contains("Submitted successfully!")) {
                        try {
                            this.timestamp = Long.parseLong(outs.get(3).split(":")[1]);
                            this.uuid = outs.get(outs.size() - 1).split(": ")[1];
                        } catch (Exception e) {
                            sendError("接受submit结果时可能出现错误！");
                        }
                        if(consumer != null) consumer.accept(this);
                    }
                }).start();
    }



    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RDSubmit)) return false;
        RDSubmit rdSubmit = (RDSubmit) o;
        return timestamp == rdSubmit.timestamp && Double.compare(rdSubmit.points, points) == 0 && Objects.equals(uuid, rdSubmit.uuid) && Objects.equals(player_uuid, rdSubmit.player_uuid) && Objects.equals(comments, rdSubmit.comments);
    }

    @Override
    public String toString() {
        if(uuid != null)
            return "{" +
                "uuid: " + uuid +
                ", timestamp: " + timestamp +
                ", player_uuid: " + player_uuid +
                ", points: " + points +
                ", comments: " + comments +
                '}';
        return "{" +
                "player_uuid: " + player_uuid +
                ", points: " + points +
                ", comments: " + comments +
                '}';
    }

    public double getPoints() {
        return points;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getComments() {
        return comments;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, timestamp, player_uuid, points, comments);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public RDSubmit(String player_uuid, double points, String comments) {
        this.player_uuid = player_uuid;
        this.points = points;
        this.comments = comments;
        server_uuid = Reputation_Database.uuid;
    }

    public RDSubmit(String player_uuid, double points, String comments, String server_uuid, Long timestamp) {
        this.player_uuid = player_uuid;
        this.points = points;
        this.comments = comments;
        this.server_uuid = server_uuid;
        this.timestamp = timestamp;
    }

    public Map<String, String> serialize() throws NullPointerException {
        if(uuid == null) throw new NullPointerException("Found the uuid null when serializing a submit");
        Map<String, String> result = new HashMap<>();
        result.put("uuid",uuid);
        result.put("timestamp", String.valueOf(timestamp));
        result.put("player_uuid",player_uuid);
        result.put("score", String.valueOf(points));
        result.put("comments",comments);
        return result;
    }

    public String getServerUuid() {
        return server_uuid;
    }

}
