//  Author: Ronald Pablos
//  Year: 2013

package fjson.util;

import fjson.Algorithm.Algorithm;
import fjson.Alphabet.Alphabet;


/**
 *
 * @author rpablos
 */
public class DecoderVocabulary extends BuiltinVocabulary {
    InitialVocabulary externalVocabulary;
    
    public ArrayIndex<Algorithm> algorithms = new ArrayIndex<Algorithm>();
    public ArrayIndex<Alphabet> alphabets = new ArrayIndex<Alphabet>();
    
    public ArrayIndex<String> keys = new ArrayIndex<String>();
    public ArrayIndex<String> string_values = new ArrayIndex<String>();
    
    
    ArrayIndex[] internalTables = new ArrayIndex[]{alphabets,algorithms, keys, string_values };
    int[] internalInitialindexes = new int[internalTables.length];

    public DecoderVocabulary() {
        this(null);
    }
    public DecoderVocabulary(InitialVocabulary externalVocabulary) {
        this.externalVocabulary = externalVocabulary;
        addBuiltinEntries();
        
        for (int i = 0; i < internalTables.length; i++) {
            internalInitialindexes[i] = internalTables[i].getSize();
        }
        addInitialVocabulary(externalVocabulary);
    }

    private void addBuiltinEntries() {
        algorithms.addAll(algorithms_builtin);
        alphabets.addAll(alphabets_builtin);
    }

    private void addInitialVocabulary(InitialVocabulary initialVocabulary) {
        if (initialVocabulary == null)
            return;
        InitialVocabulary external = initialVocabulary.getExternalVocabulary();
        if (external != null) 
            addInitialVocabulary(external);
        algorithms.addAll(initialVocabulary.algorithms);
        alphabets.addAll(initialVocabulary.alphabets);
        keys.addAll(initialVocabulary.keys);
        string_values.addAll(initialVocabulary.string_values);
    }
    
    
    public void reset() {
        for (int i = 0; i < internalTables.length; i++)
            internalTables[i].truncate(internalInitialindexes[i]);
    }

    public InitialVocabulary getExternalVocabulary() {
        return externalVocabulary;
    }

    public void setExternalVocabulary(InitialVocabulary external) {
        reset();
        addInitialVocabulary(external);
    }
    
}
