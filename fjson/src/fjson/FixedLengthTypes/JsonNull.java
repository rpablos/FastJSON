//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

/**
 *
 * @author rpablos
 */
public class JsonNull implements JsonFixedLengthType {

    @Override
    public int sizeInBits() {
        return 0;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.NULL;
    }

    @Override
    public String toString() {
        return NULL.toString();
    }
    
}
