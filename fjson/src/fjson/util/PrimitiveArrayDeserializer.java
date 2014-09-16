//  Author: Ronald Pablos
//  Year: 2013

package fjson.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;


/**
 *
 * @author rpablos
 */
public class PrimitiveArrayDeserializer {
    public static void readFrom(InputStream in, Object array, int offset, int length) throws IOException {
        if (array == null)
            return; // NULL
       Class<?> componentType = array.getClass().getComponentType();
       if (Byte.TYPE.equals(componentType)) {
           readFully(in,(byte[])array,offset,length);
           return;
       }
       if (Boolean.TYPE.equals(componentType)) {
           boolean[] barray = (boolean[]) array;
           for (int i = offset; i < offset+length; i += 8) {
               int octet = in.read();
               if (octet < 0)
                   throw new EOFException();
               for (int j = i; j < Math.min(i+8,length+offset); j++) {
                   barray[j] = (octet & (1 << (7-(j-i)))) != 0;
               }
           }
           return;
       }
       for (int i = offset; i < offset+length; i++) {
            if (Short.TYPE.equals(componentType))
                Array.setShort(array, i,readShort(in));
            else if (Integer.TYPE.equals(componentType))
                Array.setInt(array, i,readInt(in));
            else if (Long.TYPE.equals(componentType))
                Array.setLong(array, i,readLong(in));
            else if (Float.TYPE.equals(componentType))
                Array.setFloat(array, i,Float.intBitsToFloat(readInt(in)));
            else if (Double.TYPE.equals(componentType))
                Array.setDouble(array, i,Double.longBitsToDouble(readLong(in)));
            else
                throw new IllegalArgumentException("Not a valid array");
        }
    }
    static void readFully(InputStream _in, byte[] b, int off, int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            int count = _in.read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }
    public static short readShort(InputStream in) throws IOException {
        final byte[] bytea = new byte[2];
        readFully(in, bytea, 0, bytea.length);
        return (short) (((bytea[0] &0xFF) << 8) | (bytea[1] & 0xFF));
    }
    public static int readInt(InputStream in) throws IOException {
        final byte[] bytea = new byte[4];
        readFully(in, bytea, 0, bytea.length);
        return (((bytea[0] &0xFF) << 24) | ((bytea[1] & 0xFF)<<16) | ((bytea[2] & 0xFF)<<8) | (bytea[3] & 0xFF));
    }
    public static long readLong(InputStream in) throws IOException {
        final byte[] bytea = new byte[8];
        readFully(in, bytea, 0, bytea.length);
        return (((bytea[0] &0xFFL) << 56) | ((bytea[1] & 0xFFL)<<48) | ((bytea[2] & 0xFFL)<<40) | ((bytea[3] & 0xFFL)<<32) |
                ((bytea[4] &0xFFL) << 24) | ((bytea[5] & 0xFFL)<<16) | ((bytea[6] & 0xFFL)<<8) | (bytea[7] & 0xFFL));
    }
    public static float readFloat(InputStream in) throws IOException {
        return Float.intBitsToFloat(readInt(in));
    }
    public static double readDouble(InputStream in) throws IOException {
        return Double.longBitsToDouble(readLong(in));
    }
}
