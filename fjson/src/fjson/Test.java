/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fjson;

import fjson.Algorithm.Builtin.HEXADECIMAL;
import fjson.Alphabet.Numeric;
import fjson.FixedLengthTypes.JsonArrayBoolean;
import fjson.Types.JsonAlgorithmEncodingString;
import fjson.Types.JsonAlphabetConstrainedString;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import javax.json.Json;
import javax.json.JsonArray;

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
        
        
    }
}
