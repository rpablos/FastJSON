//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import fjson.util.PrimitiveArraySerializer;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonNumber;

/**
 *
 * @author rpablos
 */
public class JsonNumberINT64 implements JsonNumber,JsonFixedLengthNumberType {
    long value;

    public JsonNumberINT64(long value) {
        this.value = value;
    }
    
    @Override
    public boolean isIntegral() { return true; }

    @Override
    public int intValue() { return  (int) value;  }

    @Override
    public int intValueExact() { 
        long intvalue = ((int)value);
        if (intvalue != value)
            throw new ArithmeticException();
        return (int) value; 
    }

    @Override
    public long longValue() { return value; }

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
        return Long.SIZE;
    }

    @Override
    public byte[] toByteArray() {
        return PrimitiveArraySerializer.longToByteArray(value);
    }

    @Override
    public NumberType getType() {
        return NumberType.INT64;
    }
    
}
