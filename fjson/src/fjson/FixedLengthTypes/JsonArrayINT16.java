//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayINT16 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayINT16(short[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberINT16(((short[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.INT16;
    }

}
