//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayUINT16 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayUINT16(short[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberUINT16(((short[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.UINT16;
    }

}
