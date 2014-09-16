//  Author: Ronald Pablos
//  Year: 2013

package fjson.Types;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * Implementation of {@link JsonArray} interface.
 * <p>
 * In order to create a JsonArray with this implementation
 * use a {@link List} object to put the JSON values
 * and then construct with {@link #JsonArray_Impl(java.util.List) }
 * <p>For example:
 * <pre>{@code
 * List<JsonValue> list = ....
 * list.add(new JsonString_impl("string"));
 * list.add(JsonValue.TRUE);
 * list.add(new JsonArrayINT8(new byte[] {1,2,3});
 * JsonArray jsonArray = new JsonArray_Impl(list);
 * //use jsonArray
 * ...}</pre>
 * 
 * @author rpablos
 */
public class JsonArray_Impl extends AbstractList<JsonValue> implements JsonArray {
    List<JsonValue> list;
    public JsonArray_Impl(List<JsonValue> list){
        this.list = list;
    }
    @Override
    public JsonValue get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public JsonObject getJsonObject(int index) {
        return (JsonObject) list.get(index);
    }

    @Override
    public JsonArray getJsonArray(int index) {
        return (JsonArray) list.get(index);
    }

    @Override
    public JsonNumber getJsonNumber(int index) {
        return (JsonNumber) list.get(index);
    }

    @Override
    public JsonString getJsonString(int index) {
        return (JsonString) list.get(index);
    }

    @Override
    public <T extends JsonValue> List<T> getValuesAs(Class<T> type) {
        int len = size();
        List<T> list = new ArrayList<T>(len);
        for (int i = 0; i < len; i++)
            list.add((T)get(i));
        return list;
    }

    @Override
    public String getString(int index) {
         return getJsonString(index).getString();
    }

    @Override
    public String getString(int index, String defaultValue) {
        try {
            return getString(index);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    @Override
    public int getInt(int index) {
        return getJsonNumber(index).intValue();
    }

    @Override
    public int getInt(int index, int defaultValue) {
        try {
            return getInt(index);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    @Override
    public boolean getBoolean(int index) {
        JsonValue jsonValue = list.get(index);
        if (JsonValue.TRUE.equals(jsonValue)) {
            return true;
        } else if (JsonValue.FALSE.equals(jsonValue)) {
            return false;
        } else {
            throw new ClassCastException();
        }

    }

    @Override
    public boolean getBoolean(int index, boolean defaultValue) {
        try {
            return getBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    @Override
    public boolean isNull(int index) {
        return JsonValue.NULL.equals(list.get(index));

    }

    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }

    @Override
    public String toString() {
        return toJSONText(this);
    }
    
    public static String toJSONText(JsonArray jsonArray) {
        StringBuilder sb = new StringBuilder(jsonArray.size()*3);
        sb.append('[');
        Iterator<JsonValue> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            sb.append(iterator.hasNext()?",":"");
        }
        sb.append(']');
        return sb.toString();
    }
}
