package snownight.seed;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.Objects;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;
import static snownight.seed.Reputation_Database.sendError;

public class CopyDir {


    public static void copyFileB(File a ,String endsrc) {
        BufferedInputStream dis = null;
        BufferedOutputStream dos = null;
        try {
            FileInputStream fis = new FileInputStream(a);
            FileOutputStream fos = new FileOutputStream(endsrc);

            dis = new BufferedInputStream(fis);
            dos = new BufferedOutputStream(fos);
            byte[] b = new byte[900000000];
            int len = 0;
            while((len = dis.read(b)) != -1){
                dos.write(b, 0, len);
            }
        } catch (IOException e) {
            sendError(e.getMessage());
        } finally{
            try {
                if(dos != null){
                    dos.flush();
                    dos.close();
                }
            } catch (IOException e) {
                sendError(e.getMessage());
            }
            try {
                if(dis != null){
                    dis.close();
                }
            } catch (IOException e) {
                sendError(e.getMessage());
            }
        }
    }

    public static void copy(InputStream fis,String endsrc) {
        BufferedInputStream dis = null;
        BufferedOutputStream dos = null;
        try {
            FileOutputStream fos = new FileOutputStream(endsrc);

            dis = new BufferedInputStream(fis);
            dos = new BufferedOutputStream(fos);
            byte[] b = new byte[900000000];
            int len = 0;
            while((len = dis.read(b)) != -1){
                dos.write(b, 0, len);
            }
        } catch (IOException e) {
            sendError(e.getMessage());
        } finally{
            try {
                if(dos != null){
                    dos.flush();
                    dos.close();
                }
            } catch (IOException e) {
                sendError(e.getMessage());
            }
            try {
                if(dis != null){
                    dis.close();
                }
            } catch (IOException e) {
                sendError(e.getMessage());
            }
        }
    }
}

