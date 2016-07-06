//  Author: Ronald Pablos
//  Year: 2016

package fjson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;

/**
 *
 * @author Ronald
 */
public class TestSpeed {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        JsonReader reader = Json.createReader(new FileInputStream(args[0]));
        JsonStructure structure = reader.read();
        
        int count = 10000;
        System.out.println("Repeticiones: "+count);
        ByteArrayOutputStream baos = PruebaRedimientoCodificandoJson(structure, count);
        System.out.println("Tamaño json: "+baos.size());
        baos = PruebaRedimientoCodificandoFJson(structure, count);
        System.out.println("Tamaño fast json: "+baos.size());
        ByteArrayInputStream bais = new ByteArrayInputStream(structure.toString().getBytes());
        PruebaRedimientoDecodificandoJson(bais, count);
        bais = new ByteArrayInputStream(baos.toByteArray());
        PruebaRedimientoDecodificandoFJson(bais, count);
    }

    private static ByteArrayOutputStream PruebaRedimientoCodificandoJson(JsonStructure v, int count) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonEncoder;
        long t0 = System.currentTimeMillis();
        for (int i =0; i< count; i++) {
            jsonEncoder = Json.createWriter(baos);
            jsonEncoder.write(v);
            jsonEncoder.close();
            baos.reset();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo en codificar "+count+" veces con JSON: "+ (t1-t0));
        jsonEncoder = Json.createWriter(baos);
        jsonEncoder.write(v);
        return baos;
    }

    private static ByteArrayOutputStream PruebaRedimientoCodificandoFJson(JsonStructure v,int count) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FJsonWriter jsonEncoder = new FJsonWriter(baos);
        long t0 = System.currentTimeMillis();
        for (int i =0; i< count; i++) {
            jsonEncoder.write(v);
            jsonEncoder.reset();
            baos.reset();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo en codificar "+count +" veces con FJSON: "+ (t1-t0));
        
        jsonEncoder.write(v);
        return baos;
    }

    private static void PruebaRedimientoDecodificandoJson(ByteArrayInputStream bais, int count) {
        JsonStructure read;
        long t0 = System.currentTimeMillis();
        for (int i =0; i< count; i++) {
            JsonReader jsonDecoder = Json.createReader(bais);
            read = jsonDecoder.read();
            jsonDecoder.close();
            bais.reset();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo en decodificar "+count+" veces con JSON: "+ (t1-t0));
    }

    private static void PruebaRedimientoDecodificandoFJson(ByteArrayInputStream bais, int count) {
        JsonStructure read = null;
        long t0 = System.currentTimeMillis();
        FJsonReader jsonDecoder = new FJsonReader(bais);
        for (int i =0; i< count; i++) {
            
            read = jsonDecoder.read();
            jsonDecoder.reset();
            bais.reset();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo en decodificar "+count+" veces con FJSON: "+ (t1-t0));

    }
}
