//  Author: Ronald Pablos
//  Year: 2013

package fjson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;


/**
 *
 * @author rpablos
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws Exception {

        if (args.length < 2 || args.length > 3) {
           System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("USO"));
           System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("USO1"));
           System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("USO2"));
           System.exit(0);
       }
       boolean decode = false;
       File origen;
       File destino;
       if (args.length == 3) {
           decode = (args[0].equals("-d"));
           origen = new File (args[1]);
           destino = new File (args[2]);
       } else {
           origen = new File (args[0]);
           destino = new File (args[1]);
       }
       if (!decode) {
            JsonReader jsonReader = Json.createReader(new FileInputStream(origen));
            JsonStructure model = jsonReader.read();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(destino),1<<16);
            FJsonWriter jsonEncoder = new FJsonWriter(out);
            long time = System.currentTimeMillis();
            jsonEncoder.write(model);
            System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("TIEMPO")+": "+(System.currentTimeMillis()-time)+" ms");
            jsonEncoder.close();
       } else {
           
           InputStream in = new BufferedInputStream(new FileInputStream(origen),1<<16);
           FJsonReader jsonDecoder = new FJsonReader(in);
           long time = System.currentTimeMillis();
           JsonStructure js = jsonDecoder.read();
           System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("TIEMPO")+": "+(System.currentTimeMillis()-time)+" ms");
           in.close();
           JsonWriter jsonwriter =Json.createWriter(new BufferedOutputStream(new FileOutputStream(destino),1<<16));
           jsonwriter.write(js);
           jsonwriter.close();
       }
    }
}
