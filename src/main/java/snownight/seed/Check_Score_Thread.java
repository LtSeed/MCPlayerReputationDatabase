package snownight.seed;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static snownight.seed.Reputation_Database.*;

public class Check_Score_Thread extends Thread {

    Date update_interval;
    boolean once = false;

    @SuppressWarnings("all")
    @Override
    public void run() {

        while (true) {

            if(!once) {
                try {
                    sleep(update_interval.getTime());
                } catch (InterruptedException e) {
                    sendError("自动检查线程被终止！");
                }
            }

            for (UUID server : server_trust.keySet()) {
                loadEverySubmitFromServer(server.toString());
            }

            if(!config.getBoolean("enable_auto_recount",false))continue;

            reloadData();

            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                player_data.get(offlinePlayer).countScore();
                double score = player_data.get(offlinePlayer).getScore();
                for (RDAction action : actions) {
                    if (action.getWeighted_score()>=score) {
                        try {
                            action.getAction().accept(offlinePlayer);
                        } catch (Exception e) {
                            sendError("The command set may be wrong!");
                            sendError(e.getMessage());
                        }
                    }
                }
                if (!player_data.get(offlinePlayer).save()) {
                    sendError("保存playerdata时出错！");
                }
            }

            sendInfo("Data have been reload successfully!");
            if(once) {
                saveCon();
                try {
                    data.save(data_f);
                    config.save(config_f);
                } catch (IOException e) {
                    sendError("保存文件时出现错误:");
                    sendError(e.getMessage());
                }
                break;
            }
        }
    }

    Check_Score_Thread (Date update_interval){
        this.update_interval = update_interval;
    }
    Check_Score_Thread (boolean once){
        this.update_interval = new Date(0);
        this.once = once;
    }
}
