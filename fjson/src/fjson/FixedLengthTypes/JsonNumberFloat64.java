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
public class JsonNumberFloat64 implements JsonNumber, JsonFixedLengthNumberType {
    double value;
    BigDecimal valueBig;

    public JsonNumberFloat64(double value) {
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
    public String toString() { return Double.toString(value);  }

    @Override
    public int sizeInBits() { return Double.SIZE; }

    @Override
    public byte[] toByteArray() {
        return PrimitiveArraySerializer.doubleToByteArray(value);
    }

    @Override
    public NumberType getType() {
        return NumberType.FLOAT64;
    }

}
