package snownight.seed;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

import static snownight.seed.CopyDir.copy;

public final class Reputation_Database extends JavaPlugin {

    public static Map<UUID, Double> server_trust = new HashMap<>();
    public static Map<OfflinePlayer, RDData> player_data = new HashMap<>();
    public static File config_f,data_f,folder;
    public static FileConfiguration config;
    public static YamlConfiguration data;
    public static boolean debug = true;

    static Date period_use = new Date();
    static List<RDAction> actions = new ArrayList<>();
    static String uuid = null;
    static final Date update_interval = new Date();
    static String pu_key;

    private static String name;
    private static String email;
    private static String passphrase;


    public static void sendInfo(String mes){
        Bukkit.getConsoleSender().sendMessage("[INFO][Reputation_Database]"+mes);
    }

    public static void sendWarn(String mes){
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+"[WARN][Reputation_Database]"+mes);
    }

    public static void sendError(String mes){
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"[ERROR][Reputation_Database]"+mes);
    }

    public static void sendDebug(String mes) {
        if(debug) Bukkit.getConsoleSender().sendMessage("[DEBUG][Reputation_Database]"+mes);
    }

    @SuppressWarnings("All")
    private File checkFile(File file,String path) {
        if(file == null) file = new File(path);
        int cnt = 0;
        while (!file.exists()){
            try {
                file.createNewFile();
                sendDebug("创建文件"+file.getName()+" at "+path);
                cnt++;
                Thread.sleep(1000);
                if(cnt>=20)break;
            } catch (IOException | InterruptedException e) {
                sendError("文件检查发生问题！");
                sendError(file.toString());
                sendError("path="+path);
            }
            file = new File(path);
        }
        return file;
    }

    @SuppressWarnings("all")
    @Override
    public void onEnable() {

        sendInfo("-----------------------------------------------------------------");
        sendInfo("MC Player Reputation Database begins to load!");
        
        getCommand("prdb").setExecutor(new RDCommand());

        config = getConfig();
        data = new YamlConfiguration();
        folder = getDataFolder();
        folder.mkdirs();
        try {
            data_f = checkFile(data_f,getDataFolder().getAbsolutePath()+File.separator+"data.yml");
            data.load(data_f);
        } catch (IOException | InvalidConfigurationException e) {
            sendError("Data文件加载错误："+e.getMessage());
        }
        config_f = checkFile(config_f,getDataFolder().getAbsolutePath()+File.separator+"config.yml");

        debug = config.getBoolean("debug",false);

        boolean loadstate = false;
        try {
            loadstate = loadConfig(config)&&loadData(data);
        } catch (Exception e) {
            sendError(e.getMessage());
        }
        File mprfolder = new File(getDataFolder().getAbsolutePath()+File.separator+"mpr");
        File gpgfolder = new File(getDataFolder().getAbsolutePath()+File.separator+"mpr"+File.separator+"wingpg");
        if (!mprfolder.exists()) {
            try {
                new File(gpgfolder.getAbsolutePath()).mkdirs();
            } catch (Exception e) {
                sendError(e.getMessage());
            }
            sendInfo("First run，start copying file！");
            try {
                copy(getClassLoader().getResource("mpr.exe").openStream(),mprfolder.getAbsolutePath()+File.separator+"mpr.exe");
            } catch (IOException e) {
                sendError(e.getLocalizedMessage());
            }
            copy(getResource("mprdb.ini"),mprfolder.getAbsolutePath()+File.separator+"mprdb.ini");

            copy(getResource("dirmngr.exe"),gpgfolder.getAbsolutePath()+File.separator+"dirmngr.exe");
            copy(getResource("dirmngr_ldap.exe"),gpgfolder.getAbsolutePath()+File.separator+"dirmngr_ldap.exe");
            copy(getResource("gpg.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpg.exe");
            copy(getResource("gpg-agent.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpg-agent.exe");
            copy(getResource("gpg-check-pattern.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpg-check-pattern.exe");
            copy(getResource("gpgconf.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpgconf.exe");
            copy(getResource("gpg-connect-agent.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpg-connect-agent.exe");
            copy(getResource("gpgme-w32spawn.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpgme-w32spawn.exe");
            copy(getResource("gpg-preset-passphrase.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpg-preset-passphrase.exe");
            copy(getResource("gpgsm.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpgsm.exe");
            copy(getResource("gpgtar.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpgtar.exe");
            copy(getResource("gpgv.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpgv.exe");
            copy(getResource("gpg-wks-client.exe"),gpgfolder.getAbsolutePath()+File.separator+"gpg-wks-client.exe");
            copy(getResource("libassuan-0.dll"),gpgfolder.getAbsolutePath()+File.separator+"libassuan-0.dll");
            copy(getResource("libgcrypt-20.dll"),gpgfolder.getAbsolutePath()+File.separator+"libgcrypt-20.dll");
            copy(getResource("libgpg-error-0.dll"),gpgfolder.getAbsolutePath()+File.separator+"libgpg-error-0.dll");
            copy(getResource("libgpgme-11.dll"),gpgfolder.getAbsolutePath()+File.separator+"libgpgme-11.dll");
            copy(getResource("libksba-8.dll"),gpgfolder.getAbsolutePath()+File.separator+"libksba-8.dll");
            copy(getResource("libnpth-0.dll"),gpgfolder.getAbsolutePath()+File.separator+"libnpth-0.dll");
            copy(getResource("libsqlite3-0.dll"),gpgfolder.getAbsolutePath()+File.separator+"libsqlite3-0.dll");
            copy(getResource("pinentry-basic.exe"),gpgfolder.getAbsolutePath()+File.separator+"pinentry-basic.exe");
            copy(getResource("scdaemon.exe"),gpgfolder.getAbsolutePath()+File.separator+"scdaemon.exe");
            copy(getResource("zlib1.dll"),gpgfolder.getAbsolutePath()+File.separator+"zlib1.dll");
            sendInfo("Copy Done！");
        }

        if(!(loadstate||debug)) {
            sendError("Config file or Data file went wrong! Please check it manually!");
            sendInfo("-----------------------------------------------------------------");
        } else {

            sendInfo("MC Player Reputation Database begins to init!");
            try {
                init();
            } catch (InterruptedException e) {
                sendWarn("Initialization may go wrong, best ask for key_normal and register again manually.");
            }

            Check_Score_Thread check_score = new Check_Score_Thread(update_interval);
            check_score.start();

            sendInfo("MC Player Reputation Database loaded!");
            sendInfo("-----------------------------------------------------------------");
        }

    }

    static void init() throws InterruptedException {

        String key_normal;
        if (!config.contains("key_id.normal")) {
            sendInfo("Found that you never asked for a key.");
            sendInfo("Asking for key...May cost time(less than 60s)...");
            key_normal = addKey(name, email, passphrase, "NORMAL");
            config.set("key_id.normal", key_normal);
        } else {
            key_normal = config.getString("key_id.normal");
        }

        if (!config.contains("server.uuid")) {
            sendInfo("Registering...May cost time(less than 60s)...");
            uuid = reg();
            if(uuid!=null) config.set("server.uuid", uuid);
            else sendError("注册失败，请稍后自行重新注册！");
        } else {
            uuid = config.getString("server.uuid");
        }

        try {
            config.save(config_f);
        } catch (IOException e) {
            sendError(e.getMessage());
        }

        if (uuid!=null) {
            File file = new File(folder.getAbsolutePath() + File.separator + "pubkeys", "public_key_" + key_normal + ".asc");
            File a = null;
            if (!file.exists()) {
                sendInfo("Asking for public key...May cost time(less than 60s)...");
                a = getKeys(key_normal);
            }
            if (a != null) {
                pu_key = a.getAbsolutePath();
            } else sendError("公钥获取失败，请稍后重试！");
        }
    }


    @SuppressWarnings("All")
    private static File getKeys(String uuid) throws InterruptedException {
        List<String> cmds = new ArrayList<>();
        cmds.add("--key");
        cmds.add("-m");
        cmds.add("export");
        cmds.add("-u");
        cmds.add(uuid);
        CLIRuntime cliRuntime = new CLIRuntime(cmds);
        cliRuntime.start();
        cliRuntime.join();
        List<String> outs = cliRuntime.getOuts();
        if(outs.size()==0)return null;
        cliRuntime.sendOuts(Reputation_Database::sendDebug);
        File file = null;
        try {
            file = new File(new File(folder.getAbsolutePath()+File.separator+"mpr"),outs.get(0).split(": ")[1]);
        } catch (Exception e) {
            sendError(e.getMessage());
        }
        if(file!=null){
            DataInputStream dataInputStream;
            try {
                dataInputStream = new DataInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                return null;
            }

            StringBuilder sb = new StringBuilder();
            while (true){
                try {
                    sb.append((char) dataInputStream.readByte());
                } catch (EOFException e){
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            pu_key = sb.toString();
            if(pu_key.split("-----").length > 2) pu_key = pu_key.split("-----")[2];
            sendDebug("public_key:"+pu_key);
            pu_key = file.getAbsolutePath();

            File pubkeys_folder = new File(folder.getAbsolutePath()+File.separator+"pubkeys");
            boolean bp = pubkeys_folder.mkdirs();
            try {
                copyFile(file,new File(pubkeys_folder,file.getName()));
            } catch (IOException e) {
                sendError(e.getLocalizedMessage());
            }
            return file;
        }
        return null;
    }

    @SuppressWarnings("all")
    public static String reg() throws InterruptedException {
        List<String> cmds = new ArrayList<>();
        cmds.add("--reg");
        cmds.add("-n");
        cmds.add(name);
        CLIRuntime cliRuntime = new CLIRuntime(cmds);
        cliRuntime.start();
        cliRuntime.join();
        List<String> outs = cliRuntime.getOuts();
        if(outs.size()==0)return null;
        cliRuntime.sendOuts(Reputation_Database::sendDebug);
        if (outs.get(outs.size()-1).contains(": ")) {
            return outs.get(outs.size()-1).split(": ")[1];
        } else return null;
    }

    @SuppressWarnings("All")
    static String addKey(String name, String email, String passphrase, String remark) throws InterruptedException {
        List<String> cmds = new ArrayList<>();
        cmds.add("--key");
        cmds.add("-n");
        cmds.add(name);
        cmds.add("-e");
        cmds.add(email);
        cmds.add("-c");
        cmds.add("y");
        cmds.add("-p");
        cmds.add(passphrase);
        if(remark != null){
            cmds.add("-p");
            cmds.add(remark);
        }
        CLIRuntime cliRuntime = new CLIRuntime(cmds);
        cliRuntime.start();
        cliRuntime.join();
        List<String> outs = cliRuntime.getOuts();
        if(outs.size()==0)return null;
        cliRuntime.sendOuts(Reputation_Database::sendDebug);
        return outs.get(outs.size()-2).split(": ")[1];
    }

    static void saveCon() {
        config.set("server.name", name);
        config.set("server.email", email);
        config.set("server.passphrase", passphrase);
        config.set("server.uuid", uuid);

        config.set("debug",debug);

        config.set("data.update_interval", update_interval);
        config.set("data.period", period_use);
        config.set("data.trust", server_trust);

        List<Map<String, Object>> tmp = null;
        for (RDAction action : actions) {
            tmp = new ArrayList<>();
            tmp.add(action.serialize());
        }
        config.set("action", tmp);

    }

    @SuppressWarnings("all")
    static boolean deleteSubmit(String submit_uuid, String reason) throws InterruptedException {
        List<String> cmds = new ArrayList<>();
        cmds.add("--delete");
        cmds.add("-u");
        cmds.add(submit_uuid);
        cmds.add("-r");
        cmds.add(reason);
        CLIRuntime cliRuntime = new CLIRuntime(cmds);
        cliRuntime.start();
        cliRuntime.join();
        List<String> outs = cliRuntime.getOuts();
        if(outs.size()==0)return false;
        cliRuntime.sendOuts(Reputation_Database::sendDebug);
        return true;
    }

    @SuppressWarnings("all")
    boolean shut(String reason) throws InterruptedException {
        List<String> cmds = new ArrayList<>();
        cmds.add("--shut");
        cmds.add("-r");
        cmds.add(reason);
        CLIRuntime cliRuntime = new CLIRuntime(cmds);
        cliRuntime.start();
        cliRuntime.join();
        List<String> outs = cliRuntime.getOuts();
        if(outs.size()==0)return false;
        cliRuntime.sendOuts(Reputation_Database::sendDebug);
        return true;
    }

    @SuppressWarnings("all")
    public static List<String> getListFromServer(String server_uuid) throws InterruptedException {
        List<String> cmds = new ArrayList<>();
        cmds.add("--listfrom");
        cmds.add("-u");
        cmds.add(server_uuid);
        CLIRuntime cliRuntime = new CLIRuntime(cmds);
        cliRuntime.start();
        cliRuntime.join();
        List<String> outs = cliRuntime.getOuts();
        if(outs.size()==0) return null;
        cliRuntime.sendOuts(Reputation_Database::sendDebug);
        List<String> ret = new ArrayList<>();
        for (int i = 1; i < outs.size(); i++) {
            if(outs.get(i).split(" ").length>=4){
                ret.add(getP(outs.get(i),3));
            }
        }
        return ret;
    }

    static void loadEverySubmitFromServer(String server_uuid){
        Submit_Getting_Thread esfs = new Submit_Getting_Thread(server_uuid);
        esfs.start();
    }

    static String getP(String s, int i) {
        if(s==null||i<=0)return null;
        if(s.equals(""))return null;
        if (s.split(" ")[0].equals("")) {
            return getP(s.substring(1),i);
        } else if(i == 1) return s.split(" ")[0];
        else return getP(s.substring(s.split(" ")[0].length()),i-1);
    }

    public static void reloadData(){
        loadData(data);
    }

    public static boolean loadData(YamlConfiguration data) {

        for (OfflinePlayer each : Bukkit.getOfflinePlayers()) {
            String name = each.getUniqueId().toString();
            if(!data.contains(name)){
                player_data.put(each, new RDData(name));
                data.set(name+".weighted_score", 0);
                continue;
            }
            List<Map<?, ?>> list = data.getMapList(name+".submits");

            for (Map<?, ?> map : list) {
                player_data.get(each).addSubmit(
                        (String) map.get("uuid"),
                        (long) map.get("timestamp"),
                        (String) map.get("player_uuid"),
                        (double) map.get("points"),
                        (String) map.get("comments")
                );
            }
            player_data.get(each).countScore();
        }
        return true;
    }

    private boolean loadConfig(FileConfiguration configuration) {

        name = configuration.getString("server.name", "not_set");
        email = configuration.getString("server.email", "not_set");
        passphrase = configuration.getString("server.passphrase", "not_set");

        if (name.equals("not_set")) {
            sendWarn("请使用/mprd name <name>来设置服务器名！");
            return false;
        }
        if (email.equals("not_set")) {
            sendWarn("请使用/mprd email <email>来设置服务器邮箱！");
            return false;
        }
        if (passphrase.equals("not_set")) {
            sendWarn("请使用/mprd passphrase <passphrase>来设置服务器密码！");
            return false;
        }

        List<Map<?, ?>> loadtemp;
        if (configuration.contains("remote")) {
            loadtemp = configuration.getMapList("remote");
            for (Map<?, ?> map : loadtemp) {
                for (Object key : map.keySet()) {
                    server_trust.put(UUID.fromString((String) key), (Double) map.get(key));
                }
            }
        }

        update_interval.setTime(configuration.getLong("data.update_interval", 3600000));
        long year = Long.parseLong("31536000000");
        if (configuration.getLong("data.period_use", year) == -1) period_use.setTime(100 * year);
        else period_use.setTime(configuration.getLong("data.period_use", year));

        loadtemp = configuration.getMapList("data.trust");
        for (Map<?, ?> map : loadtemp) {
            @SuppressWarnings("unchecked")
            Map<String, Double> trust = (Map<String, Double>) map;
            for (String s : trust.keySet()) {
                server_trust.put(UUID.fromString(s),trust.get(s));
            }
        }


        loadtemp = configuration.getMapList("action");
        for (Map<?, ?> map : loadtemp) {
            for (Object key : map.keySet()) {

                @SuppressWarnings("unchecked")
                Map<String, String> action = (Map<String, String>) map.get(key);

                String cmds = action.get("operation");

                RDAction tmpacton = new RDAction(
                        cmds.split(" "),
                        Boolean.getBoolean(action.get("enable")),
                        Double.parseDouble(action.get("weighted_score")),
                        action.get("name")
                );

                actions.add(tmpacton);
            }
        }

        return true;
    }

    @SuppressWarnings("all")
    private static void copyFile(File source, File dest) throws IOException {
        if(!dest.exists()){
            boolean b = dest.createNewFile();
        }
        FileChannel in = null, out = null;
        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();
            out.transferFrom(in, 0, in.size());
        } finally {
            if(in==null||out==null) {
                sendError("文件NULL:");
            } else {
                in.close();
                out.close();
            }
        }
    }

    @Override
    public void onDisable() {

        // Plugin shutdown logic
    }
}
