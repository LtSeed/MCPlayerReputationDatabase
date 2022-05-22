package snownight.seed;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static snownight.seed.Reputation_Database.sendError;


public class RDAction {
    String name;
    @SuppressWarnings("all")
    private Consumer<OfflinePlayer> action;
    String[] command;
    final private boolean enable;
    final public double weighted_score;

    @Override
    public String toString() {
        return "RDAction{" +
                "action=" + action +
                ", enable=" + enable +
                ", weighted_score=" + weighted_score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RDAction)) return false;
        RDAction rdAction = (RDAction) o;
        return enable == rdAction.enable && Double.compare(rdAction.weighted_score, weighted_score) == 0 && Objects.equals(action, rdAction.action);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Consumer<OfflinePlayer> getAction() {
        return action;
    }

    public double getWeighted_score() {
        return weighted_score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, enable, weighted_score);
    }

    public RDAction(String[] args, boolean enable, double weighted_score, String name) {
        this.action = player->{
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                if(s.equalsIgnoreCase("<player>"))sb.append(player.getName()).append(' ');
                else sb.append(s).append(' ');
            }
            Player op = null;
            OfflinePlayer offline_op = null;
            for (OfflinePlayer operator : Bukkit.getOperators()) {
                if(operator.isOnline()) {
                    op = operator.getPlayer();
                    break;
                }
                offline_op = operator.getPlayer();
            }
            if(op==null){
                assert offline_op != null;
                op = offline_op.getPlayer();
            }
            if (!op.performCommand(sb.substring(0,sb.length()-1))) {
                sendError("在执行时"+ sb +"时出现错误！");
            }
        };
        this.enable = enable;
        this.weighted_score = weighted_score;
        this.name = name;
        this.command = args;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        StringBuilder a = new StringBuilder();
        for (String s : command) {
            a.append(s).append(" ");
        }
        ret.put("command",a.toString());
        ret.put("enable",enable);
        ret.put("weighted_score",weighted_score);
        return ret;
    }
}
