//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayFloat32 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayFloat32(float[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberFloat32(((float[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.FLOAT32;
    }

}
