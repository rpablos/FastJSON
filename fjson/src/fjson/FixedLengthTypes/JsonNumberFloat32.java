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
public class JsonNumberFloat32 implements JsonFixedLengthNumberType {
    float value;
    BigDecimal valueBig;

    public JsonNumberFloat32(float value) {
        this.value = value;
        this.valueBig = new BigDecimal(value);
    }
    
    @Override
    public boolean isIntegral() { return false; }

    @Override
    public int intValue() { return (int) value; }

    @Override
    public int intValueExact() {return valueBig.intValueExact();   }

    @Override
    public long longValue() { return (long) value;  }

    @Override
    public long longValueExact() { return valueBig.longValueExact();   }

    @Override
    public BigInteger bigIntegerValue() { return valueBig.toBigInteger();   }

    @Override
    public BigInteger bigIntegerValueExact() { return valueBig.toBigIntegerExact();  }

    @Override
    public double doubleValue() {return value;  }

    @Override
    public BigDecimal bigDecimalValue() { return valueBig; }

    @Override
    public ValueType getValueType() {return ValueType.NUMBER;  }

    @Override
    public String toString() {
        return Float.toString(value);
    }

    @Override
    public int sizeInBits() {
        return Float.SIZE;
    }

    @Override
    public byte[] toByteArray() {
        return PrimitiveArraySerializer.floatToByteArray(value);
    }

    @Override
    public NumberType getType() {
        return NumberType.FLOAT32;
    }

}
