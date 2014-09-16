//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

/**
 *
 * @author rpablos
 */
public class JsonNumberUINT16 extends JsonNumberINT16 {

    public JsonNumberUINT16(short value) {
        super(value);
    }

    @Override
    public int intValue() {
        return value & 0xFFFF;
    }

    @Override
    public NumberType getType() {
        return NumberType.UINT16;
    }
    
}
