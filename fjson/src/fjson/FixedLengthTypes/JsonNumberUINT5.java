//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import fjson.FjsonConstants;

/**
 *
 * @author rpablos
 */
public class JsonNumberUINT5 extends JsonNumberINT8 {

    public JsonNumberUINT5(byte value) {
        super((byte)(value & FjsonConstants.LITTLEUINT_NUMBER_MASK));
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public NumberType getType() {
        return NumberType.UINT5;
    }

    @Override
    public int sizeInBits() {
        return 5;
    }
    
}
