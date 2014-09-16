//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author rpablos
 */
public class JsonNumberUINT64 extends JsonNumberINT64 {

    public JsonNumberUINT64(long value) {
        super(value);
    }

    @Override
    public int intValueExact() {
        if ((value & 0xFFFFFFFF) != value)
            throw new ArithmeticException();
        return (int) value;
    }

    @Override
    public long longValueExact() {
        if ((value & 0x8000000000000000L) != 0)
            throw new ArithmeticException();
        return value;
    }

    @Override
    public BigInteger bigIntegerValue() {
        return new BigInteger(1, new byte[] {
                        (byte) (value >>>56),
                        (byte) (value >>>48),
                        (byte) (value >>>40),
                        (byte) (value >>>32),
                        (byte) (value >>>24),
                        (byte) (value >>>16),
                        (byte) (value >>>8),
                        (byte) (value &0xFF) }
                );
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return new BigDecimal(bigIntegerValue());
    }

    @Override
    public String toString() {
        return bigIntegerValue().toString();
    }

    @Override
    public NumberType getType() {
        return NumberType.UINT64;
    }
    
}
