package taokdao.plugins.apk.signer.key;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[4096]; //buff用于存放循环读取的临时数据
        int rc;
        while ((rc = input.read(buff, 0, 4096)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] array = swapStream.toByteArray();
        try {
            swapStream.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return array;
    }

}
