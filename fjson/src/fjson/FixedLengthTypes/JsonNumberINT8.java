//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonNumber;

/**
 *
 * @author rpablos
 */
public class JsonNumberINT8 implements JsonNumber,JsonFixedLengthNumberType {
    byte value;

    public JsonNumberINT8(byte value) {
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
        return Byte.SIZE;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[] { value };
    }

    @Override
    public NumberType getType() {
        return NumberType.INT8;
    }
    
}
