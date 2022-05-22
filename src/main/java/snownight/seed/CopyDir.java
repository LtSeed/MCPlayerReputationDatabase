package snownight.seed;

import java.io.*;

import static snownight.seed.Reputation_Database.sendError;

public class CopyDir {


    public static void copy(InputStream fis,String endsrc) {
        BufferedInputStream dis = null;
        BufferedOutputStream dos = null;
        try {
            FileOutputStream fos = new FileOutputStream(endsrc);

            dis = new BufferedInputStream(fis);
            dos = new BufferedOutputStream(fos);
            byte[] b = new byte[900000000];
            int len;
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

