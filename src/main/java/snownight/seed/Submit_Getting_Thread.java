package snownight.seed;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static snownight.seed.Reputation_Database.*;

public class Submit_Getting_Thread extends Thread{

    String server_uuid;

    @SuppressWarnings("all")
    @Override
    public void run() {
        List<String> uuidlist = null;
        try {
            uuidlist = getListFromServer(server_uuid);
        } catch (InterruptedException e) {
            sendError(e.getMessage());
        }
        if(uuidlist == null) return;
        for (String s : uuidlist) {
            List<String> cmds = new ArrayList<>();
            cmds.add("--detail");
            cmds.add("-u");
            cmds.add(s);
            CLIRuntime cliRuntime = new CLIRuntime(cmds,
                    outs->{
                        RDSubmit a = new RDSubmit(
                                getP(outs.get(8),2),
                                Double.parseDouble(getP(outs.get(9),2)),
                                getP(outs.get(10),2),
                                getP(outs.get(1),2),
                                Long.parseLong(getP(outs.get(7),2))
                        );
                        player_data.get(Bukkit.getPlayer(UUID.fromString(getP(outs.get(8),2)))).addSubmit(a);
                        player_data.get(Bukkit.getPlayer(UUID.fromString(getP(outs.get(8),2)))).countScore();
                    });
            cliRuntime.start();
        }
    }

    Submit_Getting_Thread(String server_uuid){
        this.server_uuid = server_uuid;
    }

}
