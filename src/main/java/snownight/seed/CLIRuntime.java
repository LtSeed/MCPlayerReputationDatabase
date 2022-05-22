package snownight.seed;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static snownight.seed.Reputation_Database.*;

public class CLIRuntime extends Thread {

    List<String> commands;
    File dir;
    List<String> outs = new ArrayList<>();
    Consumer<List<String>> consumer;

    @Override
    public void run() {

        String[] cmd_run = new String[commands.size()+1];
        cmd_run[0] = dir.getAbsolutePath()+File.separator+"mpr"+File.separator+"mpr.exe";
        for (int i = 1; i < commands.size()+1; i++) {
            cmd_run[i] = commands.get(i-1);
        }
        try {
            cmdRun(cmd_run);
        } catch (Exception e) {
            sendError("Error occurred when processing:"+ Arrays.toString(new List[]{commands}));
            sendError(e.getLocalizedMessage());
        }
        if(outs.size()==0){
            sendWarn("本次提交没有结果!这可能会导致错误！");
            return;
        } else if(outs.get(0).contains("400")){
            sendError("400 Bad Request：提交内容格式有误，或者网络连接异常！");
            return;
        } else if(outs.get(0).contains("401")){
            sendError("401 Unauthorized：请重新使用init或reg进行注册！");
            return;
        } else if(outs.get(0).contains("502")){
            sendError("502 Internal Server Error：服务器状态异常！");
            return;
        } else if(outs.get(0).contains("404")){
            sendError("404 Not Found：未能找到相关信息，插件数据可能出现错误！");
            return;
        }
        if(consumer!=null){
            consumer.accept(this.getOuts());
        }
        sendDebug("终止进程");
        this.interrupt();
    }

    CLIRuntime(List<String> commands){
        this.dir = folder;
        this.commands = commands;
        this.consumer = null;
    }
    CLIRuntime(List<String> commands,Consumer<List<String>> consumer){
        this.dir = folder;
        this.commands = commands;
        this.consumer =consumer;
    }

    public List<String> getOuts(){
        return outs;
    }

    public void sendOuts(Consumer<String> to){
        outs.forEach(to);
    }

    private void cmdRun(String[] commands) throws IOException, InterruptedException {

        sendDebug("开始执行cmd指令！");
        sendDebug(Arrays.toString(commands));
        Date time = new Date();

        ProcessBuilder builder = new ProcessBuilder(commands);
        Process process = builder.directory(new File(dir.getAbsolutePath() + File.separator +"mpr")).start();
        process.waitFor();
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(process.getInputStream()));
        int n_cnt = 0;
        while(true){

            String line = reader.readLine();
            if(time.getTime()- new Date().getTime() < -60000) {
                process.destroy();
                sendWarn("CLIRuntime运行超时！");
                break;
            }

            if(line == null) {
                if(n_cnt>=100&&!process.isAlive()){
                    break;
                }
                if(!process.isAlive()) n_cnt++;
                continue;
            }
            this.outs.add(line);
            sendDebug(line);
        }
        sendDebug(Arrays.toString(commands));
        sendDebug("任务执行完成！");

    }

}
