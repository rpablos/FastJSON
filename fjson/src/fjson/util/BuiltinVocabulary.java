//  Author: Ronald Pablos
//  Year: 2013

package fjson.util;

import fjson.Algorithm.Algorithm;
import fjson.Algorithm.Builtin.BASE64;
import fjson.Algorithm.Builtin.HEXADECIMAL;
import fjson.Alphabet.Alphabet;
import fjson.Alphabet.DateAndTime;
import fjson.Alphabet.Numeric;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rpablos
 */
public class BuiltinVocabulary {
    final static List<Algorithm> algorithms_builtin = new ArrayList<Algorithm>();
    final static List<Alphabet> alphabets_builtin = new ArrayList<Alphabet>();
     
  
    static {

        //builtin algorithms
        Algorithm[] builtinAlgorithmsArray = new Algorithm[31];
        builtinAlgorithmsArray[HEXADECIMAL.id] = HEXADECIMAL.instance;
        builtinAlgorithmsArray[BASE64.id] = BASE64.instance;
//        builtinAlgorithmsArray[UUID.id] = UUID.instance;
//        builtinAlgorithmsArray[CDATA.id] = CDATA.instance;
        algorithms_builtin.addAll(Arrays.asList(builtinAlgorithmsArray));
        Alphabet[] builtinAlphabetArray = new Alphabet[15];
        builtinAlphabetArray[Numeric.id] = Numeric.instance;
        builtinAlphabetArray[DateAndTime.id] = DateAndTime.instance;
        alphabets_builtin.addAll(Arrays.asList(builtinAlphabetArray));
    }
}
