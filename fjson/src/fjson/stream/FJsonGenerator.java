//  Author: Ronald Pablos
//  Year: 2013

package fjson.stream;

import fjson.Encoder;
import fjson.FixedLengthTypes.JsonNumberFloat64;
import fjson.FixedLengthTypes.JsonNumberINT32;
import fjson.FixedLengthTypes.JsonNumberINT64;
import fjson.FjsonConstants;
import fjson.Types.JsonNumber_Impl;
import fjson.Types.JsonString_Impl;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonException;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;

/**
 *
 * @author rpablos
 */
public class FJsonGenerator extends Encoder implements JsonGenerator{

    boolean firstTime = true;
    ContextStack contextStack = new ContextStack();
    public FJsonGenerator(OutputStream out) {
        this(out, true);
    }
    public FJsonGenerator(OutputStream out, boolean utf8encoding){
        super(utf8encoding);
        setOutputStream(out);
    }
    
    
    void writeStructureStart(boolean IsObject) {
        try {
            if (firstTime) {
                encodeHeader();
                firstTime = false;
            }
            alignToOctet();
            current_octet |= IsObject?FjsonConstants.OBJECT_IDENTIFICATION:FjsonConstants.ARRAY_IDENTIFICATION;
            flush_currentoctet();
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }
    @Override
    public JsonGenerator writeStartObject() {
        if (contextStack.currentContextIsObject())
            throw new JsonGenerationException("Object start within an object context");
        writeStructureStart(true);
        contextStack.push(true);
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String key) {
        if (!contextStack.currentContextIsObject())
            throw new JsonGenerationException("key/object value pair not within an object context");
        writeKey(key);
        writeStructureStart(true);
        contextStack.push(true);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        if (contextStack.currentContextIsObject())
            throw new JsonGenerationException("Array start within an object context");
        writeStructureStart(false);
        contextStack.push(false);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String key) {
        if (!contextStack.currentContextIsObject())
            throw new JsonGenerationException("key/array value pair not within an object context");
        writeKey(key);
        writeStructureStart(false);
        contextStack.push(false);
        return this;
    }
    
    protected void writeKey(String key) {
        try {
            alignToOctet();
            encodeIdentifyingStringOrIndex(key, vocabulary.key);
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }
    @Override
    public JsonGenerator write(String key, JsonValue jv) {
        if (!contextStack.currentContextIsObject())
            throw new JsonGenerationException("key/value pair not within an object context");
        try {
            writeKey(key);
            encodeJsonValue(jv);
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
        return this;
    }

    @Override
    public JsonGenerator write(String key, String value) {
        return write(key, new JsonString_Impl((value)));
    }

    @Override
    public JsonGenerator write(String key, BigInteger bi) {
        return write(key, new BigDecimal(bi));
    }

    @Override
    public JsonGenerator write(String key, BigDecimal bd) {
        return write(key,new JsonNumber_Impl(bd));
    }

    @Override
    public JsonGenerator write(String key, int i) {
        return write(key, new JsonNumberINT32(i));
    }

    @Override
    public JsonGenerator write(String key, long l) {
        return write (key, new JsonNumberINT64(l));
    }

    @Override
    public JsonGenerator write(String key, double d) {
        return write(key, new JsonNumberFloat64(d));
    }

    @Override
    public JsonGenerator write(String key, boolean bln) {
        return write(key,bln?JsonValue.TRUE:JsonValue.FALSE);
    }

    @Override
    public JsonGenerator writeNull(String key) {
        return write(key,JsonValue.NULL);
    }

    @Override
    public JsonGenerator writeEnd() {
        if (contextStack.noContext())
            throw new JsonGenerationException("no context");
        try {
            encodeStructureTermination();
            contextStack.pop();
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
        return this;
    }

    @Override
    public JsonGenerator write(JsonValue jv) {
        if (!contextStack.currentContextIsArray())
            throw new JsonGenerationException("value not within an array context");
        try {
            if (firstTime) {
                encodeHeader();
                firstTime = false;
            }
            encodeJsonValue(jv);
            return this;
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }

    @Override
    public JsonGenerator write(String string) {
        return write(new JsonString_Impl(string));
    }

    @Override
    public JsonGenerator write(BigDecimal bd) {
        return write(new JsonNumber_Impl(bd));
    }

    @Override
    public JsonGenerator write(BigInteger bi) {
        return write(new BigDecimal(bi));
    }

    @Override
    public JsonGenerator write(int i) {
        return write(new JsonNumberINT32(i));
    }

    @Override
    public JsonGenerator write(long l) {
        return write(new JsonNumberINT64(l));
    }

    @Override
    public JsonGenerator write(double d) {
        return write(new JsonNumberFloat64(d));
    }

    @Override
    public JsonGenerator write(boolean bln) {
        return write(bln?JsonValue.TRUE:JsonValue.FALSE);
    }

    @Override
    public JsonGenerator writeNull() {
        return write(JsonValue.NULL);
    }

    @Override
    public void close() {
        try {
            flush();
            _out.close();
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }

    @Override
    public void flush() {
        try {
            alignToOctet();
            _out.flush();
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }
}
