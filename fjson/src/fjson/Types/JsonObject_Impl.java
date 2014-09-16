//  Author: Ronald Pablos
//  Year: 2013

package fjson.Types;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * Implementation of {@link JsonObject} interface.
 * <p>
 * In order to create a JsonObject with this implementation
 * use a {@link Map} object to put the keys and JSON values
 * and then construct with {@link #JsonObject_Impl(java.util.Map) }
 * <p>For example:
 * <pre>{@code
 * Map<String,JsonValue> map = ....
 * map.put(key1,new JsonString_impl("string"));
 * map.put(key2, JsonValue.TRUE);
 * map.put(key3, new JsonArrayINT8(new byte[] {1,2,3});
 * JsonObject jsonObject = new JsonObject_Impl(map);
 * //use jsonObject
 * ...
 * }</pre>
 * @author rpablos
 */
public class JsonObject_Impl extends AbstractMap<String, JsonValue> implements JsonObject {
    Map<String, JsonValue> map;
    
    public JsonObject_Impl(Map<String, JsonValue> map) {
        this.map = map;
    }
    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return Collections.unmodifiableSet(map.entrySet());
    }

    @Override
    public JsonArray getJsonArray(String key) {
        return (JsonArray) map.get(key);
    }

    @Override
    public JsonObject getJsonObject(String key) {
        return (JsonObject) map.get(key);
    }

    @Override
    public JsonNumber getJsonNumber(String key) {
        return (JsonNumber) map.get(key);
    }

    @Override
    public JsonString getJsonString(String key) {
        return (JsonString) map.get(key);
    }

    @Override
    public String getString(String key) {
        return getJsonString(key).getString();
    }

    @Override
    public String getString(String key, String defaultValue) {
        try {
            return getString(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public int getInt(String key) {
        return getJsonNumber(key).intValue();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        try {
            return getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolean(String key) {
        JsonValue value = map.get(key);
        if (value == null) {
            throw new NullPointerException();
        } else if (JsonValue.TRUE.equals(value)) {
            return true;
        } else if (JsonValue.FALSE.equals(value)) {
            return false;
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public boolean isNull(String key) {
        return JsonValue.NULL.equals(get(key));
    }

    @Override
    public ValueType getValueType() {
        return ValueType.OBJECT;
    }

    @Override
    public String toString() {
        return toJSONText(this);
    }
    
    public static String toJSONText(JsonObject jsonObject) {
        StringBuilder sb = new StringBuilder(jsonObject.size()*10);
        sb.append('{');
        Iterator<Entry<String, JsonValue>> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, JsonValue> next = iterator.next();
            sb.append(JsonString_Impl.toJSONText(next.getKey()));
            sb.append(':');
            sb.append(next.getValue());
            sb.append(iterator.hasNext()?",":"");
        }
        sb.append('}');
        return sb.toString();
    }
}
