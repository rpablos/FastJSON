//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayUINT64 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayUINT64(long[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberUINT64(((long[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.UINT64;
    }

}
