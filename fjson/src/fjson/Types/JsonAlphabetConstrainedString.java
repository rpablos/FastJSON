//  Author: Ronald Pablos
//  Year: 2013


package fjson.Types;

import fjson.Alphabet.Alphabet;

/**
 *
 * @author rpablos
 */
public class JsonAlphabetConstrainedString extends JsonAlgorithmEncodingString {
    
    public JsonAlphabetConstrainedString(String theString, Alphabet alphabet) {
        super(theString,alphabet);
        data = theString;
    }
    public Alphabet getAlphabet() {
        return (Alphabet) algorithm;
    }
}
