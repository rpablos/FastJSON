//  Author: Ronald Pablos
//  Year: 2016

package fjson;

import fjson.Algorithm.Builtin.HEXADECIMAL;
import fjson.Alphabet.Numeric;
import fjson.FixedLengthTypes.JsonArrayBoolean;
import fjson.Types.JsonAlgorithmEncodingString;
import fjson.Types.JsonAlphabetConstrainedString;
import fjson.util.InitialVocabulary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;

/**
 *
 * @author Ronald
 */
public class Test {
    public static void main(String[] args) {

        JsonArray jsonArray = Json.createArrayBuilder().
                add(Json.createObjectBuilder().
                        add("id", 0).
                        add("Vendor", "vendor1").
                        add("MSISDN","1234567890").
                        add("Power",new BigDecimal("1234567890.123456789")).
                        add("Kc","BA3DE1F4AABBCCDD").
                        add("Bitmask",Json.createArrayBuilder().
                                add(true).add(false).add(true).add(true).add(true).add(true).add(false).add(true))).
                add(Json.createObjectBuilder().
                        add("id", 1).
                        add("Vendor", "vendor2").
                        add("MSISDN","7890123456").
                        add("Power",new BigDecimal("9876543210.123456789")).
                        add("Kc","FFEEDDCCBBAABBCC").
                        add("Bitmask",Json.createArrayBuilder().
                                add(false).add(false).add(true).add(false).add(true).add(true).add(false).add(true))).build();
        System.out.println("JSON: "+jsonArray.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FJsonWriter jsonEncoder = new FJsonWriter(baos);
        jsonEncoder.write(jsonArray);
        System.out.println("Size fjson without primitives and algorithms: "+baos.size());
        System.out.println("Size JSON string: "+jsonArray.toString().length());
        
        jsonArray = Json.createArrayBuilder().
                add(Json.createObjectBuilder().
                        add("id", 0).
                        add("Vendor", "vendor1").
                        add("MSISDN",new JsonAlphabetConstrainedString("1234567890",Numeric.instance)).
                        add("Power",new BigDecimal("1234567890.123456789")).
                        add("Kc",new JsonAlgorithmEncodingString("BA3DE1F4AABBCCDD",HEXADECIMAL.instance)).
                        add("Bitmask",new JsonArrayBoolean(new boolean[] {true,false,true,true,true,true,false,true})
                                            )).
                add(Json.createObjectBuilder().
                        add("id", 1).
                        add("Vendor", "vendor2").
                        add("MSISDN",new JsonAlphabetConstrainedString("7890123456",Numeric.instance)).
                        add("Power",new BigDecimal("9876543210.123456789")).
                        add("Kc",new JsonAlgorithmEncodingString("FFEEDDCCBBAABBCC",HEXADECIMAL.instance)).
                        add("Bitmask",new JsonArrayBoolean(new boolean[] {false,false,true,false,true,true,false,true})
                                )).build();
        baos.reset();
        jsonEncoder.reset();
        jsonEncoder.write(jsonArray);
        System.out.println("Size fjson with primitives and algorithms: "+baos.size());
        System.out.println("Size JSON string: "+jsonArray.toString().length());
        
//        InitialVocabulary iniVoc = new InitialVocabulary();
//        InitialVocabulary extVoc = new InitialVocabulary();
//        extVoc.keys.addAll(Arrays.asList(new String[]{"id","Vendor","MSISDN","Power","Kc","Bitmask"}));
//        iniVoc.setExternalVocabulary("external", extVoc);
//        baos.reset();
//        jsonEncoder.reset();
//        jsonEncoder.setInitialVocabulary(iniVoc);
//        jsonEncoder.write(jsonArray);
//        System.out.println("Size fjson with primitives and algorithms and external vocabulary: "+baos.size());
//        System.out.println("Size JSON string: "+jsonArray.toString().length());
        
        PruebaRedimientoCodificandoJson(jsonArray, 100000);
        PruebaRedimientoCodificandoFJson(jsonArray, 100000);
        ByteArrayInputStream bais = new ByteArrayInputStream(jsonArray.toString().getBytes());
        PruebaRedimientoDecodificandoJson(bais, 100000);
        bais = new ByteArrayInputStream(baos.toByteArray());
        PruebaRedimientoDecodificandoFJson(bais, 100000);
    }

    private static void PruebaRedimientoCodificandoJson(JsonStructure v, int count) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long t0 = System.currentTimeMillis();
        for (int i =0; i< count; i++) {
            JsonWriter jsonEncoder = Json.createWriter(baos);
            jsonEncoder.write(v);
            jsonEncoder.close();
            baos.reset();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo en codificar "+count+" veces con JSON:"+ (t1-t0));
    }

    private static void PruebaRedimientoCodificandoFJson(JsonStructure v,int count) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FJsonWriter jsonEncoder = new FJsonWriter(baos);
        long t0 = System.currentTimeMillis();
        for (int i =0; i< count; i++) {
            jsonEncoder.write(v);
            jsonEncoder.reset();
            baos.reset();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo en codificar "+count +" veces con FJSON:"+ (t1-t0));
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
        System.out.println("Tiempo en decodificar "+count+" veces con JSON:"+ (t1-t0));
    }

    private static void PruebaRedimientoDecodificandoFJson(ByteArrayInputStream bais, int count) {
        JsonStructure read;
        long t0 = System.currentTimeMillis();
        FJsonReader jsonDecoder = new FJsonReader(bais);
        for (int i =0; i< count; i++) {
            
            read = jsonDecoder.read();
            jsonDecoder.reset();
            bais.reset();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo en decodificar "+count+" veces con FJSON:"+ (t1-t0));
    }
}
