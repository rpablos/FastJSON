//  Author: Ronald Pablos
//  Year: 2014

package fjson.util;

import fjson.FJsonReader;
import fjson.FJsonWriter;
import fjson.FixedLengthTypes.JsonArrayBoolean;
import fjson.FixedLengthTypes.JsonArrayFloat32;
import fjson.FixedLengthTypes.JsonArrayFloat64;
import fjson.FixedLengthTypes.JsonArrayINT16;
import fjson.FixedLengthTypes.JsonArrayINT32;
import fjson.FixedLengthTypes.JsonArrayINT64;
import fjson.FixedLengthTypes.JsonArrayINT8;
import fjson.FixedLengthTypes.JsonNumberFloat32;
import fjson.FixedLengthTypes.JsonNumberFloat64;
import fjson.FixedLengthTypes.JsonNumberINT16;
import fjson.FixedLengthTypes.JsonNumberINT32;
import fjson.FixedLengthTypes.JsonNumberINT64;
import fjson.FixedLengthTypes.JsonNumberINT8;
import fjson.Types.JsonArray_Impl;
import fjson.Types.JsonNumber_Impl;
import fjson.Types.JsonObject_Impl;
import fjson.Types.JsonString_Impl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author rpablos
 */
public class JsonOrgUtilities {
    public static byte [] JSONObjectToFJson(JSONObject jsonObject) throws JsonException {
        return toFJson(JSONObjectToJsonObject(jsonObject));
    }
    public static byte [] JSONArrayToFJson(JSONArray jsonArray) throws JsonException {
        return toFJson(JSONArrayToJsonArray(jsonArray));
    }
     
    private static byte [] toFJson(JsonStructure json) throws JsonException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FJsonWriter jsonEncoder = new FJsonWriter(out);
        jsonEncoder.write(json);
        jsonEncoder.close();
        return out.toByteArray();
    }
    public static void writeJSONObject(JSONObject jsonObject, OutputStream out) throws JsonException {
        writeJsonStructure(JSONObjectToJsonObject(jsonObject),out);
    }
    public static void writeJSONArray(JSONArray jsonArray, OutputStream out) throws JsonException {
        writeJsonStructure(JSONArrayToJsonArray(jsonArray),out);
    }
    private static void writeJsonStructure(JsonStructure jsonStructure, OutputStream out) throws JsonException {
        FJsonWriter jsonEncoder = new FJsonWriter(out);
        jsonEncoder.write(jsonStructure);
    }
    
    private static JsonObject JSONObjectToJsonObject(JSONObject jsonObject) throws JsonException {
        Map<String,JsonValue> data = new LinkedHashMap<String,JsonValue>(jsonObject.length());
        try {
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object value = jsonObject.get(key);
                    data.put(key,toJsonValue(value));
            }
        } catch (Exception ex) {
                throw new JsonException(ex.getLocalizedMessage(), ex);
        }
        return new JsonObject_Impl(data);
    }

    private static JsonArray JSONArrayToJsonArray(JSONArray jsonArray) throws JsonException {
        List<JsonValue> data = new ArrayList<JsonValue>(jsonArray.length());
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++)
                data.add(toJsonValue(jsonArray.get(i)));
        } catch (Exception ex) {
            throw new JsonException(ex.getLocalizedMessage(), ex);
        }
        return new JsonArray_Impl(data);
    }
    
    private static JsonValue toJsonValue(Object value) throws JSONException {
        if (value instanceof String) 
            return new JsonString_Impl((String) (value));
        else if (JSONObject.NULL.equals(value))
            return JsonValue.NULL;
        else if (value instanceof Boolean) 
            return ((Boolean)value)?JsonValue.TRUE:JsonValue.FALSE;
        else if (value instanceof Number) {
            if (value instanceof Byte)
                return new JsonNumberINT8((Byte)value);
            else if (value instanceof Short)
                return new JsonNumberINT16((Short)value);
            else if (value instanceof Integer)
                return new JsonNumberINT32((Integer)value);
            else if (value instanceof Long)
                return new JsonNumberINT64((Long)value);
            else if (value instanceof Float)
                return new JsonNumberFloat32((Float)value);
            else if (value instanceof Double)
               return new JsonNumberFloat64((Double)value);
            else
                return new JsonNumber_Impl(new BigDecimal(value.toString()));
        } else if (value instanceof JSONObject) 
            return JSONObjectToJsonObject((JSONObject) value);
        else if (value instanceof Map) 
           return JSONObjectToJsonObject(new JSONObject((Map)value));
        else if (value instanceof JSONArray) 
            return JSONArrayToJsonArray((JSONArray)value);
        else if (value instanceof Collection)
            return JSONArrayToJsonArray(new JSONArray((Collection)value));
        else if (value.getClass().isArray()) {
            if (value.getClass().getComponentType().isPrimitive()) {
                Class<?> componentType = value.getClass().getComponentType();
                if (Byte.TYPE.equals(componentType))
                    return new JsonArrayINT8((byte[]) value);
                else if (Short.TYPE.equals(componentType))
                    return new JsonArrayINT16((short[]) value);
                else if (Integer.TYPE.equals(componentType))
                    return new JsonArrayINT32((int[]) value);
                else if (Long.TYPE.equals(componentType))
                    return new JsonArrayINT64((long[]) value);
                else if (Float.TYPE.equals(componentType))
                    return new JsonArrayFloat32((float[]) value);
                else if (Double.TYPE.equals(componentType))
                    return new JsonArrayFloat64((double[]) value);
                else if (Boolean.TYPE.equals(componentType))
                    return new JsonArrayBoolean((boolean[]) value);
            }
            return JSONArrayToJsonArray(new JSONArray(value));
        }
        else
            return new JsonString_Impl(value.toString());
    }
    
    private static JsonStructure fromFJson(byte[] fjson) throws JsonException {
        ByteArrayInputStream in = new ByteArrayInputStream(fjson);
        FJsonReader jsonDecoder = new FJsonReader(in);
        JsonStructure result = jsonDecoder.read();
        jsonDecoder.close();
        return result;
    }
    public static JSONObject JSONObjectFromFJson(byte[] fjson) throws JsonException {
        return JSONObjectFromJsonObject((JsonObject) fromFJson(fjson));
    }
    public static JSONArray JSONArrayFromFJson(byte[] fjson) throws JsonException {
        return JSONArrayFromJsonArray((JsonArray) fromFJson(fjson));
    }
    public static JSONObject readJSONObject(InputStream in) throws JsonException {
        return JSONObjectFromJsonObject((JsonObject) readJsonStructure(in));
    }
    public static JSONArray readJSONArray(InputStream in) throws JsonException {
        return JSONArrayFromJsonArray((JsonArray) readJsonStructure(in));
    }
    private static JsonStructure readJsonStructure(InputStream in) throws JsonException {
        FJsonReader jsonDecoder = new FJsonReader(in);
        return jsonDecoder.read();
    }
    
    private static JSONObject JSONObjectFromJsonObject(JsonObject jsonObject) throws JsonException {
        JSONObject result = new JSONObject();
        try {
            Iterator<Map.Entry<String, JsonValue>> iterator = jsonObject.entrySet().iterator();
            while (iterator.hasNext()) {
                    Map.Entry<String, JsonValue> next = iterator.next();
                    result.put(next.getKey(), fromJsonValue(next.getValue()));
            }
        } catch (Exception ex) {
                throw new JsonException(ex.getLocalizedMessage(), ex);
        }
        return result;
    }

    private static JSONArray JSONArrayFromJsonArray(JsonArray jsonArray) {
        JSONArray result = new JSONArray();
        try {
            Iterator<JsonValue> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JsonValue next = iterator.next();
                result.put(fromJsonValue(next));
            }
        } catch (Exception ex) {
                throw new JsonException(ex.getLocalizedMessage(), ex);
        }
        return result;
    }
    private static Object fromJsonValue(JsonValue value) {
        switch (value.getValueType()){
            case STRING:
                return value.toString();
            case TRUE:
                return true;
            case FALSE:
                return false;
            case NULL:
                return JSONObject.NULL;
            case NUMBER:
                JsonNumber number = (JsonNumber)value;
                return number.isIntegral()?number.longValue():number.doubleValue();
            case ARRAY:
                return JSONArrayFromJsonArray((JsonArray) value);
            case OBJECT:
                return JSONObjectFromJsonObject((JsonObject) value);
            default:
                return value.toString();   
        }
    }
}
