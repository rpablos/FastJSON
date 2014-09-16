//  Author: Ronald Pablos
//  Year: 2013

package fjson;

import java.io.IOException;
import java.io.OutputStream;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonWriter;

/**
 * A {@link JsonWriter} implementation for fjson encoding
 * 
 * @author rpablos
 */
public class FJsonWriter extends Encoder implements JsonWriter {

    public FJsonWriter(OutputStream out) {
        this(out,true);
    }

    public FJsonWriter(OutputStream out,boolean utf8encoding) {
        super(utf8encoding);
        setOutputStream(out);
    }

    @Override
    public void writeArray(JsonArray ja) {
        write(ja);
    }

    @Override
    public void writeObject(JsonObject jo) {
        write(jo);
    }

    @Override
    public void write(JsonStructure js) {
        try {
            encodeJsonDocument(js);
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }

    @Override
    public void close() {
        try {
            _out.close();
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }
    
}
