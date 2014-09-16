//  Author: Ronald Pablos
//  Year: 2013

package fjson.Alphabet;

import java.util.Arrays;

/**
 * Alphabet for representing numeric character strings.
 * 
 * <p>This alphabet is "0123456789-+.eE"
 * 
 * @author rpablos
 */
public class Numeric extends Alphabet {
    public static int id = 0;

    
    private Numeric() {
        super("0123456789-+.eE");
    }

    // specializations for better performance
    static private int[] indextable = new int['e'-'+'+1];
    static {
        Arrays.fill(indextable, -1);
        for (int i = '0'; i <= '9';i++)
            indextable[i-'+'] = i-'0';
        indextable['-'-'+'] = 10;
        indextable['+'-'+'] = 11;
        indextable['.'-'+'] = 12;
        indextable['e'-'+'] = 13;
        indextable['E'-'+'] = 14;
    }
    @Override
    public int charPosition(int ch) {
        int offset = ch - '+';
        if ((offset >= indextable.length) || (offset < 0))
            return -1;
        return indextable[offset];
    }

    @Override
    public byte[] getEncodedOctetString(String str) throws CharacterOutOfRangeException {
        int len = str.length();
        byte[] result = new byte[(len+1)/2];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < 2; j++) {
                if (i*2+j<len) {
                    int pos = charPosition(str.charAt(i*2+j));
                    if (pos < 0) 
                        throw new CharacterOutOfRangeException("character "+str.charAt(i*2+j)+" out of alphabet range");
                    result[i] |= (byte) (pos << ((j==0)?4:0));
                } else
                    result [i] |= 0xf;
            }
        }
        return result;
    }
    
    
    public static Numeric instance = new Numeric();
    
    
}
