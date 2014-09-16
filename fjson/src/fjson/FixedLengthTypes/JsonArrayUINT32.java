//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayUINT32 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayUINT32(int[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberUINT32(((int[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.UINT32;
    }

}
