//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayBoolean extends JsonArrayOfFixedLength {
    
    
    public JsonArrayBoolean(boolean[] array) {
        this.array = array;
    }
    
    @Override
    public JsonValue get(int i) { 
        boolean value = ((boolean[])array)[i];
        return value?JsonValue.TRUE:JsonValue.FALSE;
    }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.BOOLEAN;
    }

}
