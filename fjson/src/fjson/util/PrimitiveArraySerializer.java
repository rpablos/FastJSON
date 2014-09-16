//  Author: Ronald Pablos
//  Year: 2013

package fjson.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;

/**
 *
 * @author rpablos
 */
public class PrimitiveArraySerializer {

    public static void writeTo(OutputStream out, Object array, int offset, int length) throws IOException {
        if (array == null)
            return; // NULL
       Class<?> componentType = array.getClass().getComponentType();
       if (Byte.TYPE.equals(componentType)) {
           out.write((byte[])array,offset,length);
           return;
       }
       if (Boolean.TYPE.equals(componentType)) {
           boolean[] barray = (boolean[]) array;
           for (int i = offset; i < offset+length; i += 8) {
               int octet = 0;
               for (int j = i; j < Math.min(i+8,length+offset); j++) {
                   octet |= (barray[j]?1:0) << 7-(j-i);
               }
               out.write(octet);
           }
           return;
       }
        
        for (int i = offset; i < offset+length; i++) {
            if (Short.TYPE.equals(componentType))
                out.write(shortToByteArray(Array.getShort(array, i)));
            else if (Integer.TYPE.equals(componentType))
                out.write(intToByteArray(Array.getInt(array, i)));
            else if (Long.TYPE.equals(componentType))
                out.write(longToByteArray(Array.getLong(array, i)));
            else if (Float.TYPE.equals(componentType))
                out.write(floatToByteArray(Array.getFloat(array, i)));
            else if (Double.TYPE.equals(componentType))
                out.write(doubleToByteArray(Array.getDouble(array, i)));
            else 
                throw new IllegalArgumentException("Not a valid array");
        }
    }
    public static byte[] shortToByteArray(short data) {
        return new byte[] {
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }
    public static byte[] intToByteArray(int data) {
        return new byte[] {
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }
    public static byte[] longToByteArray(long data) {
        return new byte[] {
            (byte)((data >> 56) & 0xff),
            (byte)((data >> 48) & 0xff),
            (byte)((data >> 40) & 0xff),
            (byte)((data >> 32) & 0xff),
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }
    public static byte[] floatToByteArray(float data) {
        return intToByteArray(Float.floatToIntBits(data));
    }
    public static byte[] doubleToByteArray(double data) {
        return longToByteArray(Double.doubleToLongBits(data));
    }
}
