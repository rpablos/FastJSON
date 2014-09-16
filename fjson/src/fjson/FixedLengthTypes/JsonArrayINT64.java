//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayINT64 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayINT64(long[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberINT64(((long[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.INT64;
    }

}
