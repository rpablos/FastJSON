//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

/**
 *
 * @author rpablos
 */
public class JsonNumberUINT8 extends JsonNumberINT8 {

    public JsonNumberUINT8(byte value) {
        super(value);
    }

    @Override
    public int intValue() {
        return value & 0xFF;
    }

    @Override
    public NumberType getType() {
        return NumberType.UINT8;
    }
    
}
