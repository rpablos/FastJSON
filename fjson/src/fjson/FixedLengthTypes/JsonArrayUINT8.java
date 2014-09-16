//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayUINT8 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayUINT8(byte[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberUINT8(((byte[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.UINT8;
    }

}
