//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class JsonArrayNull extends JsonArrayOfFixedLength {
    
    int size;
    public JsonArrayNull(int size) {
        this.array = null;
        this.size = size;
    }
    
    @Override
    public JsonValue get(int i) { return JsonValue.NULL; }

    @Override
    public int size() {
       return size;
    }


    @Override
    public ArrayType getArrayType() {
        return ArrayType.NULL;
    }

}
