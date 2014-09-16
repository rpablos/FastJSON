//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

/**
 *
 * @author rpablos
 */
public class JsonNumberUINT32 extends JsonNumberINT32 {

    public JsonNumberUINT32(int value) {
        super(value);
    }

    @Override
    public int intValueExact() {
        long intvalue = value & 0xFFFFFFFFL;
        if (intvalue != value)
            throw new ArithmeticException();
        return value; 
    }

    @Override
    public long longValue() {
        return value & 0xFFFFFFFFL;
    }

    @Override
    public NumberType getType() {
        return NumberType.UINT32;
    }
    
}
