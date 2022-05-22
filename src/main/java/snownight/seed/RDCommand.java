package snownight.seed;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static snownight.seed.Reputation_Database.*;

public class RDCommand implements CommandExecutor {
    /**
     * Executes the given command, returning its success
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!label.equalsIgnoreCase("prdb")||args.length == 0){
            return help(sender);
        }


        if(args[0].equalsIgnoreCase("debug")){
            debug = !debug;
            config.set("debug",debug);
            try {
                config.save(config_f);
            } catch (IOException e) {
                sendError(e.getMessage());
            }
        }

        if(args[0].equalsIgnoreCase("config")){

            if(args.length == 1){
                try {
                    config.save(config_f);
                    data.save(data_f);
                } catch (IOException e) {
                    sender.sendMessage("保存时出现错误！");
                }
            }
            if(args.length == 2) sender.sendMessage("请输入键值对！");
            switch (args[1]){
                case "server.email":
                case "server.name":
                case "server.passphrase":
                    config.set(args[1],args[2]);

                    try {
                        config.save(config_f);
                    } catch (IOException e) {
                        sendError("config文件保存错误！");
                    }
                    return true;
                case "data.update_interval":
                case "data.period":
                    try {
                        config.set(args[1],Double.parseDouble(args[2]));
                    } catch (NumberFormatException e) {
                        sender.sendMessage("请输入正确的数字格式！");
                    }
                    try {
                        config.save(config_f);
                    } catch (IOException e) {
                        sendError("config文件保存错误！");
                    }
                default:
                    sender.sendMessage("你所修改的键不受支持！请手动修改config.yml");
                    return help(sender);
            }
        }

        if(args[0].equalsIgnoreCase("init")){
            sender.sendMessage("准备开始初始化！");
            Thread a = new Thread(() -> {
                try {
                    init();
                    sender.sendMessage("初始化完成！");
                    sender.sendMessage("公钥储存在"+pu_key+"处，请妥善保管！");
                } catch (InterruptedException e) {
                    sender.sendMessage(e.getMessage());
                }
            });
            a.start();
            return true;
        }

        if(args[0].equalsIgnoreCase("key")){
            sender.sendMessage("服务器的普通公钥uuid为：");
            sender.sendMessage(uuid);
            sender.sendMessage("服务器的普通公钥地址为：");
            sender.sendMessage(pu_key);
            return true;
        }

        if(args[0].equalsIgnoreCase("trust")){
            if(args.length<4)return help(sender);
            if(args[1].equalsIgnoreCase("add")){
                try {
                    if (server_trust.containsKey(UUID.fromString(args[2]))) {
                        sender.sendMessage("该uuid已被信任，现在更改它的权重为："+args[3]);
                        server_trust.replace(UUID.fromString(args[2]),Double.parseDouble(args[3]));
                        return true;
                    }
                    server_trust.put(UUID.fromString(args[2]),Double.parseDouble(args[3]));
                    sender.sendMessage("uuid:"+args[2]+"添加信任成功，权重为"+args[3]);
                    return true;
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("请检查uuid和权重的格式！");
                }
            }
            if(args[1].equalsIgnoreCase("remove")){
                try {
                    server_trust.remove(UUID.fromString(args[2]));
                    sender.sendMessage("已经删除了曾信任的服务器:"+args[2]);
                    return true;
                } catch (Exception e) {
                    sender.sendMessage("请检查uuid和权重的格式！");
                }
            }
            return help(sender);
        }

        if(args[0].equalsIgnoreCase("remote")){
            sender.sendMessage("暂不支持更改远程数据库！");
            return true;
        }

        if(args[0].equalsIgnoreCase("update")){
            sender.sendMessage("开始update");
            sender.sendMessage("你可以在控制台接受到update完成的消息");
            new Check_Score_Thread(true).start();
            return true;
        }

        if(args[0].equalsIgnoreCase("submit")){
            if(args.length == 1) return help(sender);
            if(args[1].equalsIgnoreCase("add")) {
                if (args.length <= 4) return help(sender);

                UUID pl = null;
                try {
                    pl = UUID.fromString(args[2]);
                } catch (Exception e) {
                    try {
                        boolean found = false;
                        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                            if(offlinePlayer.getName().equals(args[2])){
                                if(!found) {
                                    pl = offlinePlayer.getUniqueId();
                                    found = true;
                                } else {
                                    sender.sendMessage("该名字对应多个玩家，请使用uuid进行提交");
                                    return true;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        sender.sendMessage("请检查Player name/uuid格式！");
                    }
                }
                if(pl == null){
                    sendError("玩家没有被找到！");
                    return true;
                }
                RDSubmit temp_submit = null;
                try {
                    temp_submit = new RDSubmit(
                            pl.toString(),
                            Double.parseDouble(args[3]),
                            args[4]
                    );
                } catch (Exception e) {
                    sender.sendMessage("请检查格式！");
                }
                if (temp_submit != null) {
                    temp_submit.submit(player_data.get(Bukkit.getOfflinePlayer(pl))::addSubmit);
                    return true;
                } else return help(sender);
            }
            if(args[1].equalsIgnoreCase("delete")){
                if(args.length != 4)return help(sender);
                UUID pl = null;
                try {
                    pl = UUID.fromString(args[2]);
                } catch (Exception e) {
                    try {
                        pl = Bukkit.getPlayer(args[2]).getUniqueId();
                    } catch (Exception ex) {
                        sender.sendMessage("请检查Player name/uuid格式！");
                    }
                }
                if(pl == null) {
                    sender.sendMessage("请检查Player name/uuid格式！");
                    return help(sender);
                }
                //true when the uuid is player_uuid
                List<RDSubmit> del = new ArrayList<>();
                OfflinePlayer player = null;
                for (OfflinePlayer offlinePlayer : player_data.keySet()) {
                    if(offlinePlayer.getUniqueId().equals(pl)) {
                        del = player_data.get(offlinePlayer).getSubmits();
                        player = offlinePlayer;
                        player_data.get(offlinePlayer).getSubmits().clear();
                        break;
                    }
                    List<RDSubmit> submits = player_data.get(offlinePlayer).getSubmits();
                    for (RDSubmit submit : submits) {
                        if(submit.getUuid().equals(pl.toString())) {
                            del.add(submit);
                            break;
                        }
                    }
                }
                List<RDSubmit> res = new ArrayList<>(del);
                OfflinePlayer finalPlayer = player;
                del.forEach(sub->{
                    try {
                        new Thread(() -> {
                            try {
                                if (!deleteSubmit(sub.getUuid(),args[3])) {
                                    sendError("删除submit时可能错误");
                                }
                            } catch (InterruptedException e) {
                                sendError("删除submit时可能错误");
                                sendError(e.getMessage());
                                if(finalPlayer != null)
                                    player_data.get(finalPlayer).getSubmits().addAll(res);
                            }
                        }).start();
                    } catch (Exception ignored) {}
                });
                sender.sendMessage("已经删除符合条件的submit！");
                return true;
            }
        }

        if(args[0].equalsIgnoreCase("lookup")){
            if(args.length==1)return help(sender);
            UUID pl = null;
            try {
                pl = UUID.fromString(args[2]);
            } catch (Exception e) {
                try {
                    pl = Bukkit.getPlayer(args[2]).getUniqueId();
                } catch (Exception ex) {
                    sender.sendMessage("请检查Player name/uuid格式！");
                }
            }
            if(pl == null) {
                sender.sendMessage("请检查Player name/uuid格式！");
                return true;
            }
            if(player_data.containsKey(Bukkit.getPlayer(pl))){
                player_data.get(Bukkit.getPlayer(pl)).getSubmits().forEach(submit->{
                    sender.sendMessage("=================================================");
                    sender.sendMessage("submit uuid: "+submit.getUuid());
                    sender.sendMessage("comment: "+submit.getComments());
                    sender.sendMessage("score: "+submit.getPoints());
                    sender.sendMessage("timestamp: "+submit.getTimestamp());
                    sender.sendMessage("=================================================");
                });
            } else {
                sender.sendMessage("该玩家没有记录！");
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("action")){
            if(args.length == 1) return help(sender);
            if(args[1].equalsIgnoreCase("add")) {
                if (args.length <= 3) return help(sender);
                RDAction temp_action = null;
                try {
                    temp_action = readCommand(args);
                } catch (Exception e) {
                    sender.sendMessage("可能在设置Action时出现错误：");
                    sender.sendMessage(e.getMessage());
                    sendError("可能在设置Action时出现错误：");
                    sendError(e.getMessage());
                }
                if(temp_action!=null) {
                    actions.add(temp_action);
                    return true;
                } else {
                    sender.sendMessage("添加Action时错误：Action为null,请检查参数是否正确!");
                    return help(sender);
                }
            }
            if(args[1].equalsIgnoreCase("remove")){
                if(args.length != 3)return help(sender);
                boolean s;
                List<RDAction> del = new ArrayList<>();
                for (RDAction action : actions) {
                    s = action.getName().equalsIgnoreCase(args[2]);
                    if(s)del.add(action);
                }
                sender.sendMessage("将要删除符合条件的"+del.size()+"个Action");
                del.forEach(actions::remove);
                sender.sendMessage("已经全部删除！");
            }
        }
        return help(sender);
    }

    private RDAction readCommand(String[] args) {
        String name = args[2];
        int i = 1;
        List<String> cmd = new ArrayList<>();
        if(args[3].startsWith("@")){
            if(args[3].endsWith("@")) {
                cmd.add(args[3].substring(1));
            } else {
                try {
                    while (!args[++i].endsWith("@")) {
                        cmd.add(args[i]);
                    }
                } catch (Exception ignored) {}
                cmd.add(args[i].substring(0, args[i + 1].length() - 1));
            }
        }
        boolean enable = Boolean.getBoolean(args[i+1]);
        double score = Double.parseDouble(args[i+2]);
        return new RDAction(cmd.toArray(new String[0]), enable,score,name);
    }

    boolean help(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN+"Minecraft Player Reputation Database Help Pages");
        sender.sendMessage("/prdb config <key> <value> # 更新配置，配置的<key>可以是以下内容：server.name " +
                "server.email server.passphrase data.update_interval data.period");
        sender.sendMessage("/prdb init # 初始化。插件在加载时会自动进行初始化，所以一般情况下不需要调用该命令，除非在加载插件时出错。");
        sender.sendMessage("/prdb pubkey export # 导出公钥用于交换");
        sender.sendMessage("/prdb trust add|remove <key-id> <level/weight: 0.0-5.0> # 导入并信任公钥/删除信任公钥");
        sender.sendMessage("/prdb update # 手动更新数据并保存配置文件");
        sender.sendMessage("/prdb submit add <player/uuid> <point> <comment> # 手动提交记录");
        sender.sendMessage("/prdb submit remove <player/uuid/submit_uuid> <reason> # 手动删除记录");
        sender.sendMessage("/prdb lookup <player/uuid> # 查看特定玩家在数据库中的所有记录");
        sender.sendMessage("/prdb action add <name> <command> <enable:true/false> <weighted_score> # 添加一个行动，当玩家的" +
                "加权平均分低于weighted_score时，执行command，若需要玩家名，请用“<player>”代替，命令前后用@包围");
        sender.sendMessage("/prdb action remove <name> # 删除一个行动");
        sender.sendMessage("/prdb debug # 切换debug状态，默认为false，debug若为true，控制台会获得更多信息");
        return true;
    }


}
