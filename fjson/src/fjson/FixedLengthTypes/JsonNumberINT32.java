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
public class JsonNumberINT32 implements JsonFixedLengthNumberType {
    int value;

    public JsonNumberINT32(int value) {
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
    public BigInteger bigIntegerValue() { return BigInteger.valueOf(longValue()); }

    @Override
    public BigInteger bigIntegerValueExact() {return bigIntegerValue();}

    @Override
    public double doubleValue() { return longValue(); }

    @Override
    public BigDecimal bigDecimalValue() {return BigDecimal.valueOf(longValue()); }

    @Override
    public ValueType getValueType() { return ValueType.NUMBER;  }

    @Override
    public String toString() {
        return Long.toString(longValue());
    }

    @Override
    public int sizeInBits() {
        return Integer.SIZE;
    }

    @Override
    public byte[] toByteArray() {
        return PrimitiveArraySerializer.intToByteArray(value);
    }

    @Override
    public NumberType getType() {
        return NumberType.INT32;
    }
    
}
