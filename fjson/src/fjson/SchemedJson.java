//  Author: Ronald Pablos
//  Year: 2016

package fjson;

import fjson.util.InitialVocabulary;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;

/**
 *
 * @author rpablos
 */
public class SchemedJson {
    static public void main(String args[]) throws  Exception {
        if ((args.length < 3) || ((args.length == 4) && !args[0].equals("-d"))) {
            System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("USOESQUEMA1"));
            System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("USOESQUEMA2"));
            System.exit(0);
        }
        boolean decode = args[0].equals("-d");
        File config = new File(args[decode?1:0]);
        File source = new File(args[decode?2:1]);
        File destination = new File(args[decode?3:2]);
        Properties properties = new Properties();
        properties.load(new FileInputStream(config));
        String maxStringLenStr = (String) properties.get("MAXIMUMSTRINGLENGTH");
        int maxStringLen = -1;
        try {
            maxStringLen = Integer.parseInt(maxStringLenStr);
        } catch (Exception e) {}
        if (maxStringLen < 0)
            maxStringLen = 60;
        InitialVocabulary externalVocabulary = new InitialVocabulary();
        String externalURI = (String) properties.get("EXTERNALURI");
        PopulateVocabulary((String)properties.get("EXTERNALKEYS"),externalVocabulary.keys);
        PopulateVocabulary((String)properties.get("EXTERNALSTRINGVALUES"),externalVocabulary.string_values);
        if (!decode) {
            InitialVocabulary initialVocabulary = new InitialVocabulary();
            initialVocabulary.setExternalVocabulary(externalURI, externalVocabulary);
            JsonReader jsonReader = Json.createReader(new FileInputStream(source));
            JsonStructure model = jsonReader.read();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(destination),1<<16);
            FJsonWriter jsonEncoder = new FJsonWriter(out);
            if (!initialVocabulary.isEmpty())
                jsonEncoder.setInitialVocabulary(initialVocabulary);
            jsonEncoder.setDefaultAllowPolicyMaximumStringLengthForIndexing(maxStringLen);
            long time = System.currentTimeMillis();
            jsonEncoder.write(model);
            System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("TIEMPO")+": "+(System.currentTimeMillis()-time)+" ms");
            jsonEncoder.close();
        }
        else {
            Map<String, InitialVocabulary> externalVocabularies = new HashMap<String, InitialVocabulary>();
            externalVocabularies.put(externalURI, externalVocabulary);
            
            InputStream in = new BufferedInputStream(new FileInputStream(source),1<<16);
            FJsonReader jsonDecoder = new FJsonReader(in);
            jsonDecoder.registerExternalVocabularies(externalVocabularies);
            long time = System.currentTimeMillis();
            JsonStructure js = jsonDecoder.read();
            System.out.println(java.util.ResourceBundle.getBundle("fjson/locale/strings").getString("TIEMPO")+": "+(System.currentTimeMillis()-time)+" ms");
            in.close();
            JsonWriter jsonwriter =Json.createWriter(new BufferedOutputStream(new FileOutputStream(destination),1<<16));
            jsonwriter.write(js);
            jsonwriter.close();
        }
    }
    
    private static void PopulateVocabulary(String str, List<String> table) {
        if (str == null) return;
        StringTokenizer st = new StringTokenizer(str,",");
        while (st.hasMoreTokens()) {
            table.add(st.nextToken());
        }
    }
}
