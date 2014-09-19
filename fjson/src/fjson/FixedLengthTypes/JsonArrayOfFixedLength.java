//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import fjson.Types.JsonArray_Impl;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public abstract class JsonArrayOfFixedLength  extends AbstractList<JsonValue> implements JsonArray {
    public enum ArrayType {
        UINT8,INT8,UINT16,INT16,UINT32,INT32,UINT64,INT64,FLOAT32,FLOAT64,BOOLEAN,NULL
    }
    Object array;
    
    
    @Override
    public abstract JsonValue get(int i) ;

    @Override
    public int size() { return Array.getLength(array); }

    @Override
    public JsonObject getJsonObject(int i) { return (JsonObject)get(i); }

    @Override
    public JsonArray getJsonArray(int i) { return (JsonArray)get(i); }

    @Override
    public JsonNumber getJsonNumber(int i) { return (JsonNumber)get(i); }

    @Override
    public JsonString getJsonString(int i) { return (JsonString)get(i); }

    @Override
    public <T extends JsonValue> List<T> getValuesAs(Class<T> type) {
        int len = size();
        List<T> list = new ArrayList<T>(len);
        for (int i = 0; i < len; i++)
            list.add((T)get(i));
        return Collections.unmodifiableList(list);
    }

    @Override
    public String getString(int i) { return getJsonString(i).getString(); }

    @Override
    public String getString(int i, String defaultvalue) {
        try {
            return getString(i);
        } catch (Exception ex) {
            return defaultvalue;
        }
    }

    @Override
    public int getInt(int i) { return getJsonNumber(i).intValue(); }

    @Override
    public int getInt(int i, int defaultValue) {
        try {
            return getInt(i);  
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolean(int i) {
        JsonValue jsonValue = get(i);
        if (jsonValue == JsonValue.TRUE) {
              return true;
         } else if (jsonValue == JsonValue.FALSE) {
             return false;
         } else {
             throw new ClassCastException();
         }
    }

    @Override
    public boolean getBoolean(int i, boolean defaultValue) { 
        try {
            return getBoolean(i); 
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public boolean isNull(int i) { return false; }

    @Override
    public ValueType getValueType() { return ValueType.ARRAY; }

    
    abstract public ArrayType getArrayType();

    
    public Object getArray() {
        return array;
    }

    @Override
    public String toString() {
        return JsonArray_Impl.toJSONText(this);
    }

}
