//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

/**
 *
 * @author rpablos
 */
public class JsonBoolean implements JsonFixedLengthType {
    boolean value;

    public JsonBoolean(boolean value) {
        this.value = value;
    }
    
    @Override
    public ValueType getValueType() {
        return (value)?ValueType.TRUE:ValueType.FALSE;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return (value)?TRUE.toString():FALSE.toString();
    }

    
    @Override
    public int sizeInBits() {
        return 1;
    }
    
}
