//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayFloat64 extends JsonArrayOfFixedLength {
    
    
    public JsonArrayFloat64(double[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { return new JsonNumberFloat64(((double[])array)[i]); }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.FLOAT64;
    }

}
