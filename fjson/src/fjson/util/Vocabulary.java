//  Author: Ronald Pablos
//  Year: 2013

package fjson.util;


import fjson.Algorithm.Algorithm;
import fjson.Alphabet.Alphabet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author rpablos
 */
public class Vocabulary extends BuiltinVocabulary {
    InitialVocabulary initialVocabulary = null;
    InternalInitialVocabulary internalInitialVocabulary =null;
    
    public static int INITIAL_CAPACITY_ALGORITHMS = 31;
    public static int INITIAL_CAPACITY_ALPHABETS = 31;
    
    public static int INITIAL_CAPACITY_KEY = 1000;
    public static int INITIAL_CAPACITY_STRING_VALUES = 1000;

    
    public IndexMap<Algorithm> algorithms = new IndexMap<Algorithm>(INITIAL_CAPACITY_ALGORITHMS); 
    public IndexMap<Alphabet> alphabets = new IndexMap<Alphabet>(INITIAL_CAPACITY_ALPHABETS); 
    
    public IndexMap<String> key = new IndexMap<String>(INITIAL_CAPACITY_KEY); 
    public IndexMap<String> string_values = new IndexMap<String>(INITIAL_CAPACITY_STRING_VALUES); 
    
    
    public Vocabulary() {
        this(null);
    }

    public Vocabulary(InitialVocabulary initialVocabulary) {
        init(initialVocabulary);
    }
    private void init(InitialVocabulary initialVocabulary) {
        this.initialVocabulary = initialVocabulary;
        addBuiltinEntries();
        addInitialVocabulary(initialVocabulary);
        

            internalInitialVocabulary = new InternalInitialVocabulary();
            
            internalInitialVocabulary.algorithms = (IndexMap<Algorithm>) algorithms.clone();
            internalInitialVocabulary.alphabets = (IndexMap<Alphabet>) alphabets.clone();
            
            internalInitialVocabulary.key = (IndexMap<String>) key.clone();
            internalInitialVocabulary.string_values = (IndexMap<String>) string_values.clone();
    }
    
    private void addInitialVocabulary(InitialVocabulary initialVocabulary) {
        if (initialVocabulary == null)
            return;
        InitialVocabulary external = initialVocabulary.getExternalVocabulary();
        if (external != null) {
            addInitialVocabulary(external);

        }
        addToMap(algorithms, initialVocabulary.algorithms);
        addToMap(alphabets, initialVocabulary.alphabets);
        
        addToMap(key, initialVocabulary.keys);
        addToMap(string_values, initialVocabulary.string_values);
        
    }
    private void addBuiltinEntries() {
        addToMap(algorithms, algorithms_builtin);
        addToMap(alphabets, alphabets_builtin);
    }
    <T> void addToMap(IndexMap<T> map, List<T> list) {
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T t = it.next();
            if (t == null)
                map.index++; //null especial, para hacer correr el índice
            else 
                map.addNewIndexEntry(t);
        }
    }

    public InitialVocabulary getInitialVocabulary() {
        return initialVocabulary;
    }
    public void setInitialVocabulary(InitialVocabulary initialVocabulary) {
        algorithms.clear();
        alphabets.clear();
        key.clear();
        string_values.clear();
        init(initialVocabulary);
    }
    public InitialVocabulary toInitialVocabularyExcludingInitialVocabulary() {
        InitialVocabulary result = new InitialVocabulary();
//        result.algorithms.addAll(Arrays.asList(algorithms.getKeysInOrder(new Algorithm[0],internalInitialVocabulary.algorithms.index)));
//        result.alphabets.addAll(Arrays.asList(alphabets.getKeysInOrder(new Alphabet[0],internalInitialVocabulary.alphabets.index)));
        result.keys.addAll(Arrays.asList(key.getKeysInOrder(new String[0],internalInitialVocabulary.key.index)));
        result.string_values.addAll(Arrays.asList(string_values.getKeysInOrder(new String[0],internalInitialVocabulary.string_values.index)));
        
        return result;
    }
    public InitialVocabulary toInitialVocabularyIncludingInitialVocabulary() {
        InitialVocabulary result = new InitialVocabulary();
        result.algorithms.addAll(Arrays.asList(algorithms.getKeysInOrder(new Algorithm[0],algorithms_builtin.size())));
        result.alphabets.addAll(Arrays.asList(alphabets.getKeysInOrder(new Alphabet[0],alphabets_builtin.size())));
        result.keys.addAll(Arrays.asList(key.getKeysInOrder(new String[0])));
        result.string_values.addAll(Arrays.asList(string_values.getKeysInOrder(new String[0])));
        return result;
    }

    public void reset() {
        key = (IndexMap<String>) internalInitialVocabulary.key.clone();
        string_values = (IndexMap<String>) internalInitialVocabulary.string_values.clone();
    }
    
    static private class InternalInitialVocabulary {
        public IndexMap<Algorithm> algorithms; 
        public IndexMap<Alphabet> alphabets; 

        public IndexMap<String> key; 
        public IndexMap<String> string_values; 
        
    }
}
