//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import fjson.util.PrimitiveArraySerializer;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author rpablos
 */
public class JsonNumberINT16 implements JsonFixedLengthNumberType {
    short value;

    public JsonNumberINT16(short value) {
        this.value = value;
    }
    
    @Override
    public boolean isIntegral() { return true; }

    @Override
    public int intValue() { return value;  }

    @Override
    public int intValueExact() { return intValue(); }

    @Override
    public long longValue() { return intValue(); }

    @Override
    public long longValueExact() { return longValue(); }

    @Override
    public BigInteger bigIntegerValue() { return BigInteger.valueOf(intValue()); }

    @Override
    public BigInteger bigIntegerValueExact() {return bigIntegerValue();}

    @Override
    public double doubleValue() { return intValue(); }

    @Override
    public BigDecimal bigDecimalValue() {return BigDecimal.valueOf(intValue()); }

    @Override
    public ValueType getValueType() { return ValueType.NUMBER;  }

    @Override
    public String toString() {
        return Integer.toString(intValue());
    }

    @Override
    public int sizeInBits() {
        return Short.SIZE;
    }

    @Override
    public byte[] toByteArray() {
        return PrimitiveArraySerializer.shortToByteArray(value);
    }

    @Override
    public NumberType getType() {
        return NumberType.INT16;
    }
    
}
