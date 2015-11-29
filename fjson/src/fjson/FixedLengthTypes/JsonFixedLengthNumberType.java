//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonNumber;

/**
 *
 * @author rpablos
 */
public interface JsonFixedLengthNumberType extends JsonNumber {
    public enum NumberType {UINT8,INT8,UINT16,INT16,UINT32,INT32,UINT64,INT64,FLOAT32,FLOAT64,UINT5}
    public byte[] toByteArray();
    public NumberType getType();
    public int sizeInBits();
}
