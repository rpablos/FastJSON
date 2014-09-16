//  Author: Ronald Pablos
//  Year: 2013

package fjson.FixedLengthTypes;

import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public interface JsonFixedLengthType extends JsonValue {
    public int sizeInBits();
}
