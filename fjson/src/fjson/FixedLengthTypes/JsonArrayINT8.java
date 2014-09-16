//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayINT8 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayINT8(byte[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberINT8(((byte[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.INT8;
    }

}
