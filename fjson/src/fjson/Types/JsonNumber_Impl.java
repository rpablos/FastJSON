//  Author: Ronald Pablos
//  Year: 2013

package fjson.Types;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonNumber;

/**
 *
 * @author rpablos
 */
public class JsonNumber_Impl implements JsonNumber {
    BigDecimal theNumber;
    
    public JsonNumber_Impl(BigDecimal theNumber) {
        this.theNumber = theNumber;
        if (theNumber == null)
            throw new NullPointerException();
    }
    @Override
    public boolean isIntegral() {
        return (theNumber.scale() == 0);
    }

    @Override
    public int intValue() {
        return theNumber.intValue();
    }

    @Override
    public int intValueExact() {
        return theNumber.intValueExact();
    }

    @Override
    public long longValue() {
        return theNumber.longValue();
    }

    @Override
    public long longValueExact() {
        return theNumber.longValueExact();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return theNumber.toBigInteger();
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return theNumber.toBigIntegerExact();
    }

    @Override
    public double doubleValue() {
        return theNumber.doubleValue();
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return theNumber;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.NUMBER;
    }

    @Override
    public int hashCode() {
        return theNumber.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return theNumber.equals(o);
    }

    @Override
    public String toString() {
        return theNumber.toString();
    }
    
    
}
