//  Author: Ronald Pablos
//  Year: 2013

package fjson.Types;

import fjson.Algorithm.Algorithm;

/**
 *
 * @author rpablos
 */
public class JsonAlgorithmEncodingString extends JsonString_Impl {
    protected Algorithm algorithm;
    protected Object data = null;
    public JsonAlgorithmEncodingString(String theString, Algorithm algorithm) {
        super(theString);
        this.algorithm = algorithm;
    }
    
    public JsonAlgorithmEncodingString(Object data, Algorithm algorithm) {
        super(null);
        this.algorithm = algorithm;
        this.data = data;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String getString() {
        if (theString == null)
            theString = algorithm.stringFromObject(data);
        return theString;
    }
    
}
