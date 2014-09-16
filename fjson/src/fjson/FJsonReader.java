//  Author: Ronald Pablos
//  Year: 2013

package fjson;

import java.io.IOException;
import java.io.InputStream;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.stream.JsonParsingException;

/**
 * A {@link JsonReader} implementation for fjson decoding.
 * 
 * @author rpablos
 */
public class FJsonReader extends Decoder implements JsonReader {



    public FJsonReader(InputStream in) {
         this._in = in;
    }
    
    @Override
    public JsonStructure read() {
        JsonStructure js;
        try {
            js = decodeJsonDocument();
        } catch (Exception ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
        return (JsonStructure) js;
    }

    @Override
    public JsonObject readObject() {
        JsonStructure js = read();
        if (! (js instanceof JsonObject))
            throw new JsonParsingException("Not a JsonObject", jsonLocation);
        return (JsonObject) js;
    }

    @Override
    public JsonArray readArray() {
        JsonStructure js = read();
        if (! (js instanceof JsonArray))
            throw new JsonParsingException("Not a JsonArray", jsonLocation);
        return (JsonArray) js;
    }

    @Override
    public void close() {
        try {
            _in.close();
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }
    
}
