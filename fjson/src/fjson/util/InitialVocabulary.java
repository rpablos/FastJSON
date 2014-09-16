//  Author: Ronald Pablos
//  Year: 2013

package fjson.util;


import fjson.Algorithm.Algorithm;
import fjson.Alphabet.Alphabet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author rpablos
 */
public class InitialVocabulary {
    String external_vocabulary_URI;
    InitialVocabulary external_vocabulary;
    
    public List<Algorithm> algorithms = new ArrayList<Algorithm>();
    public List<Alphabet> alphabets = new ArrayList<Alphabet>();
    
    public List<String> keys = new ArrayList<String>();
    public List<String> string_values = new ArrayList<String>();
    
    
    public void setExternalVocabulary(String uri, InitialVocabulary external) {
        external_vocabulary_URI = uri;
        external_vocabulary = external;
    }
    public InitialVocabulary getExternalVocabulary() {
        return external_vocabulary;
    }
    public String getExternalVocabularyURI() {
        return external_vocabulary_URI;
    }
    
    public boolean isEmpty() {
        if ((external_vocabulary != null) && (external_vocabulary_URI != null))
            return false;
        if (    !keys.isEmpty() || !string_values.isEmpty() || 
                !alphabets.isEmpty() || !algorithms.isEmpty() 
           )
            return false;
        return true;
    }
    public void addInitialVocabulary(InitialVocabulary iv) {
        algorithms.addAll(iv.algorithms);
        alphabets.addAll(iv.alphabets);
        
        keys.addAll(iv.keys);
        string_values.addAll(iv.string_values);
        
    }
}
