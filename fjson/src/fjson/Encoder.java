//  Author: Ronald Pablos
//  Year: 2013

package fjson;

import fjson.Algorithm.Algorithm;
import fjson.Algorithm.EncodingAlgorithmException;
import fjson.Alphabet.Alphabet;
import fjson.Alphabet.CharacterOutOfRangeException;
import fjson.Alphabet.Numeric;
import fjson.FixedLengthTypes.JsonArrayOfFixedLength;
import fjson.FixedLengthTypes.JsonFixedLengthNumberType;
import fjson.Types.JsonAlgorithmEncodingString;
import fjson.util.Additional_datum;
import fjson.util.AllowIndexMap;
import fjson.util.HashMapObjectInt;
import fjson.util.IndexMap;
import fjson.util.InitialVocabulary;
import fjson.util.PrimitiveArraySerializer;
import fjson.util.Vocabulary;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 *
 * @author rpablos
 */
public class Encoder {
    final int DEFAULT_MAXIMUM_LENGTH = 60;
    AllowLimitedStringLenghts allowLimitedStringLenghts = new AllowLimitedStringLenghts(DEFAULT_MAXIMUM_LENGTH);
    protected OutputStream _out;
    protected int current_octet = 0;
    boolean utf8encoding = true;
    List<Additional_datum> additional_data = null;
    protected Vocabulary vocabulary = new Vocabulary();

    public Encoder() {
        this(true);
    }

    public Encoder(boolean utf8encoding) {
        this.utf8encoding = utf8encoding;
        vocabulary.string_values.setAllowIndexMap(allowLimitedStringLenghts);
    }
    public void setDefaultAllowPolicyMaximumStringLengthForIndexing(int length) {
        allowLimitedStringLenghts.setMaximumLength(length);
        setAllowPolicyForStrings(allowLimitedStringLenghts);
    }
    public void setAllowPolicyForStrings(AllowIndexMap<String> allowPolicy) {
        vocabulary.string_values.setAllowIndexMap(allowPolicy);
    }
    protected void clear_currentoctet() {
        current_octet = 0;
    }

    public void setOutputStream(OutputStream out) {
        _out = out;
    }

    protected void flush_currentoctet() throws IOException {
        _out.write(current_octet);
        current_octet = 0;
    }
    protected void alignToOctet() throws IOException {
        if (current_octet != 0)
                flush_currentoctet();
    }
    protected void encodeStructureTermination() throws IOException {
         if (current_octet == 0) {
                current_octet |= FjsonConstants.TERMINATION_PATTERN << 4;
            } else {
                current_octet |= FjsonConstants.TERMINATION_PATTERN;
                flush_currentoctet();
            }

    }
//    protected void encodeDocumentTermination() throws IOException {
//        current_octet |= (current_octet == 0) ? FjsonConstants.TERMINATION_PATTERN << 4 : FjsonConstants.TERMINATION_PATTERN;
//        flush_currentoctet();
//    }
    
    void encodeJsonDocument(JsonStructure jsonStructure) throws IOException {
        encodeHeader();
        encodeJsonStructure(jsonStructure);
        alignToOctet();
//        encodeDocumentTermination();
    }
    void encodeJsonStructure(JsonStructure jsonStructure) throws IOException {
        if (jsonStructure.getValueType().equals(JsonValue.ValueType.OBJECT)) {
            encodeJsonObject((JsonObject)jsonStructure);
        } else {
            encodeJsonArray((JsonArray)jsonStructure);
        }
    }
    void encodeJsonObject(JsonObject jsonObject) throws IOException {
        alignToOctet();
        current_octet |= FjsonConstants.OBJECT_IDENTIFICATION;
        flush_currentoctet();

        for(Map.Entry<String, JsonValue> pair : jsonObject.entrySet()) {
            alignToOctet();
            encodeIdentifyingStringOrIndex(pair.getKey(), vocabulary.key);
            encodeJsonValue(pair.getValue());
        }
        encodeStructureTermination();
    }
    void encodeJsonArray(JsonArray jsonArray) throws IOException {
        alignToOctet();
        current_octet |= FjsonConstants.ARRAY_IDENTIFICATION;
        if (jsonArray instanceof JsonArrayOfFixedLength) {
            current_octet |= FjsonConstants.TYPED_ARRAY_FLAG;
            current_octet |= ((JsonArrayOfFixedLength)jsonArray).getArrayType().ordinal();
            flush_currentoctet();
            encodeArrayOfFixedLength((JsonArrayOfFixedLength)jsonArray);
        }
        else {
            flush_currentoctet();
            for (JsonValue value: jsonArray) {
                encodeJsonValue(value);
            }
            encodeStructureTermination();
        }
    }
    void encodeJsonString(JsonString value) throws IOException {
        Algorithm algo = null;
        if (value instanceof JsonAlgorithmEncodingString)
            algo = ((JsonAlgorithmEncodingString)value).getAlgorithm();
        encodeJsonString(value, algo);
    }
    void encodeJsonString(JsonString value, Algorithm algo) throws IOException {
        alignToOctet();
        current_octet |= FjsonConstants.STRING_IDENTIFICATION;
        encodeNonIdentifyingStringOrIndexOnSecondBit(value.getString(), vocabulary.string_values,algo);
        
    }
    void encodeJsonNull() throws IOException {
        alignToOctet();
        current_octet |= FjsonConstants.NULL_IDENTIFICATION;
        flush_currentoctet();
    }
    void encodeJsonBoolean(boolean value) throws IOException {
        alignToOctet();
        current_octet |= FjsonConstants.BOOLEAN_IDENTIFICATION | (value?1:0);
        flush_currentoctet();
    }
    void encodeJsonNumber(JsonNumber value) throws IOException {
        alignToOctet();
        current_octet |= FjsonConstants.NUMBER_IDENTIFICATION;
        if (!(value instanceof JsonFixedLengthNumberType)) {
            if (value.isIntegral()) {
                int exactvalue = -1;
                try {
                    exactvalue = value.intValueExact();
                } catch (ArithmeticException e) {}
                if ((exactvalue & FjsonConstants.LITTLEUINT_NUMBER_MASK) == exactvalue) {
                    current_octet |= FjsonConstants.NUMBER_LITTLEUINT_IDENTIFICATION;
                    current_octet |= exactvalue;
                    flush_currentoctet();
                    return;
                }
            }
            current_octet |= FjsonConstants.NUMBER_VARIABLE_IDENTIFICATION;
            try {
                encodeNonEmptyOctetStringOnFifthBit(Numeric.instance.toByteArray(value.toString()));
            } catch (CharacterOutOfRangeException ex) {
                throw new IOException(ex);
            }
        } else {
            JsonFixedLengthNumberType flnvalue = (JsonFixedLengthNumberType) value;
            switch (flnvalue.getType()) {
                case UINT5:
                    current_octet |= FjsonConstants.NUMBER_LITTLEUINT_IDENTIFICATION;
                    current_octet |= flnvalue.intValue();
                    flush_currentoctet();
                    break;
                default:
                    current_octet |= flnvalue.getType().ordinal();
                    flush_currentoctet();
                    _out.write(flnvalue.toByteArray());
                    break;
            }
        }
    }
    protected void encodeJsonValue(JsonValue value) throws IOException {
        switch (value.getValueType()) {
            case STRING: 
                encodeJsonString((JsonString)value); 
                break;
            case NUMBER:
                encodeJsonNumber((JsonNumber)value);
                break;
            case OBJECT:
                encodeJsonObject((JsonObject)value);
                break;
            case ARRAY:
                encodeJsonArray((JsonArray)value);
                break;
            case TRUE:
                encodeJsonBoolean(true);
                break;
            case FALSE:
                encodeJsonBoolean(false);
                break;
            case NULL:
                encodeJsonNull();
                break;
        }
    }
    public void reset() {
        clear_currentoctet();
        vocabulary.reset();
    }
    protected void encodeHeader() throws IOException {
       _out.write(FjsonConstants.MAGIC_NUMBER);
       int mascara = 0;
       mascara |= FjsonConstants.FJSON_VERSION << 4;
        if ((additional_data != null) && (!additional_data.isEmpty())) {
            mascara |= FjsonConstants.ADDITIONAL_DATA_FLAG;
        }
        if ((vocabulary.getInitialVocabulary() != null) && (!vocabulary.getInitialVocabulary().isEmpty())) {
            mascara |= FjsonConstants.INITIAL_VOCABULARY_FLAG;
        }
        current_octet = mascara;
        flush_currentoctet(); //escribe máscara
        if ((mascara & FjsonConstants.ADDITIONAL_DATA_FLAG) != 0) {
            encodeAdditionalData(additional_data);
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_FLAG) != 0) {
            encodeInitialVocabulary(vocabulary.getInitialVocabulary());
        }
        
    }
    protected int encodeIdentifyingStringOrIndex(String str,  IndexMap<String> map) throws IOException {
        if (str.isEmpty()) {
            current_octet |= FjsonConstants.IDENTIFYINGSTRINGORINDEX_INDEX | FjsonConstants.ZERO_LENGTH_STRING_PATTERN_ON_2ND_BIT;
            flush_currentoctet();
            return map.NO_INDEX;
        }
        return encodeIdentifyingStringOrIndex(str, map.get(str),map);
    }
    protected int encodeIdentifyingStringOrIndex(String str, int index, IndexMap<String> map) throws IOException {
        if (index != HashMapObjectInt.NO_INDEX) {
            current_octet |= FjsonConstants.IDENTIFYINGSTRINGORINDEX_INDEX;
            encodeIndexOnSecondbit(index);
        } else {
            int len = encodeUTF8inInternalEncodingBuffer(str);
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
            index = map.addNewIndexEntry(str);
        }
        return index;
    }
    protected void encodeIndexOnSecondbit(int index) throws IOException {
        if (index < 64) {
            current_octet |= index;
            flush_currentoctet();
        } else if (index < 8256) {
            index -= 64;
            current_octet |= FjsonConstants.INTEGER_2ND_BIT_2_OCTETS_FLAG | (index >> 8);
            flush_currentoctet();
            _out.write(index & 255);
        } else {
            index -= 8256;
            current_octet |= FjsonConstants.INTEGER_2ND_BIT_3_OCTETS_FLAG;
            current_octet |= (15 & (index >> 16));
            flush_currentoctet();
            _out.write(255 & (index >> 8));
            _out.write(255 & index);
        }
    }
    protected void encodeIndexOnThirdbit(int index) throws IOException {
        if (index < 32) {
            
            current_octet |= index;
            flush_currentoctet();
        } else if (index < 2080) {
            index -= 32;
            current_octet |= FjsonConstants.INTEGER_3RD_BIT_2_OCTETS_FLAG | (index >> 8);
            flush_currentoctet();
            _out.write(index & 0xFF);
        } else if (index < 526368) {
            
            index -= 2080;
            current_octet |= FjsonConstants.INTEGER_3RD_BIT_3_OCTETS_FLAG | (index >> 16);
            flush_currentoctet();
            _out.write(0xFF & (index >> 8));
            _out.write(0xFF & index);
        } else {
            index -= 526368;
            current_octet |= FjsonConstants.INTEGER_3RD_BIT_4_OCTETS_FLAG;
            flush_currentoctet();
            _out.write(15 & (index >> 16));
            _out.write(255 & (index >> 8));
            _out.write(255 & index);
        }
    }
    
    protected void encodeNonEmptyOctetStringOnFifthBit(byte[] octet_string) throws IOException {
        encodeNonEmptyOctetString(octet_string, 5);
    }
    protected void encodeNonEmptyOctetStringOnFifthBit(byte[] octet_string,int offset, int length) throws IOException {
        encodeNonEmptyOctetString(octet_string,offset, length, 5);
    }
    protected void encodeNonEmptyOctetStringOnSecondBit(byte[] octet_string) throws IOException {
        encodeNonEmptyOctetString(octet_string, 2);
    }
    protected void encodeNonEmptyOctetStringOnSecondBit(byte[] octet_string,int offset, int length) throws IOException {
        encodeNonEmptyOctetString(octet_string,offset, length, 2);
    }
    protected void encodeNonEmptyOctetString(byte[] octet_string,int startingBit) throws IOException {
        encodeNonEmptyOctetString(octet_string, 0, octet_string.length, startingBit);
    }
    protected void encodeNonEmptyOctetString(byte[] octet_string,int offset, int length,int startingBit) throws IOException {
        // encode length
        int firstLimit =  1 + (1 << (8-startingBit));
        if (length < firstLimit) {
            current_octet |= (length - 1);
            flush_currentoctet();
        } else if (length < (firstLimit+256)) {
            current_octet |= FjsonConstants.OCTET_STRING_LENGTH_2_OCTETS_FLAG >>> (startingBit-1);
            flush_currentoctet();
            _out.write(length - firstLimit);
        } else {
            current_octet |= FjsonConstants.OCTET_STRING_LENGTH_3_OCTETS_FLAG >>> (startingBit-1);
            flush_currentoctet();
            int len = length - (firstLimit+256);
            _out.write(len >>> 24);
            _out.write((len >> 16) & 255);
            _out.write((len>> 8) & 255);
            _out.write(len & 255);
        }
        //encode octet_string
        _out.write(octet_string,offset,length);
    }
    protected void encodeNonIdentifyingStringOrIndexOnSecondBit(String str, IndexMap<String> map) throws IOException {
        encodeNonIdentifyingStringOrIndexOnSecondBit(str, map, null);
    }
    protected void encodeNonIdentifyingStringOrIndexOnSecondBit(String str, IndexMap<String> map, Algorithm algo) throws IOException {
        if (str.isEmpty()) {
            current_octet |= FjsonConstants.NONIDENTIFYINGSTRINGORINDEX_INDEX | FjsonConstants.ZERO_LENGTH_STRING_PATTERN_ON_3RD_BIT;
            flush_currentoctet();
            return;
        }
        int index = map.get(str);
        if (index != HashMapObjectInt.NOT_FOUND) {
            current_octet |= FjsonConstants.NONIDENTIFYINGSTRINGORINDEX_INDEX;
            encodeIndexOnThirdbit(index);
        } else {
            index = map.addNewIndexEntry(str);
            if (index != HashMapObjectInt.NO_INDEX) {
                //add-to-table equals true
                current_octet |= FjsonConstants.NONIDENTIFYINGSTRING_ADDTOTABLE;
            }
            encodeEncodedCharacterStringOnFourthbit(str,algo);
        }
    }
    
    protected void encodeEncodedCharacterStringOnFourthbit(String str) throws IOException {
        encodeEncodedCharacterStringOnFourthbit(str, null);
    }
    protected void encodeEncodedCharacterStringOnFourthbit(String str, Algorithm algo) throws IOException {
        if (algo == null) { 
            if (!utf8encoding)
                current_octet |= FjsonConstants.ENCODED_CHARACTER_STRING_4TH_BIT_UTF16_DISCRIMINANT;
            int len = encodeUTFinInternalEncodingBuffer(str);
            encodeNonEmptyOctetStringOnSixthBit(_encodingBuffer, 0, len);
        } else {
            int algo_index = HashMapObjectInt.NOT_FOUND;
            if (algo instanceof Alphabet) {
                current_octet |= FjsonConstants.ENCODED_CHARACTER_STRING_4TH_BIT_ALPHABET_DISCRIMINANT;
                algo_index = vocabulary.alphabets.get(algo);
            } else {
                current_octet |= FjsonConstants.ENCODED_CHARACTER_STRING_4TH_BIT_ALGORITHM_DISCRIMINANT;
                algo_index = vocabulary.algorithms.get(algo);
            }
            
            if (algo_index == HashMapObjectInt.NOT_FOUND)
                throw new IOException("algorithm not found");
            encodeOctetInteger(algo_index, 6);
            try {
                encodeNonEmptyOctetStringOnSixthBit(algo.toByteArray(str));
            } catch (EncodingAlgorithmException ex) {
                throw new IOException(ex);
            }
        }

    }
    protected void encodeNonEmptyOctetStringOnSixthBit(byte[] octet_string) throws IOException {
        encodeNonEmptyOctetString(octet_string, 6);
    }
    protected void encodeNonEmptyOctetStringOnSixthBit(byte[] octet_string,int offset, int length) throws IOException {
        encodeNonEmptyOctetString(octet_string,offset,length, 6);
    }
    protected void encodeOctetInteger(int octet, int startingBit) throws IOException {
        octet &= 0xFF;
        current_octet |= octet >> (startingBit-1);
        flush_currentoctet();
        current_octet |= octet << 8-(startingBit-1);
    }
    
    
    protected void encodeArrayOfFixedLength(JsonArrayOfFixedLength arrayOfFixedLength) throws IOException {
        // encode length
        int length = arrayOfFixedLength.size();
        int firstLimit = (1 << 7);
        if (length < firstLimit) {
            current_octet |= (length);
            flush_currentoctet();
        } else if (length < (firstLimit+256)) {
            current_octet |= FjsonConstants.OCTET_STRING_LENGTH_2_OCTETS_FLAG;
            flush_currentoctet();
            _out.write(length - firstLimit);
        } else {
            current_octet |= FjsonConstants.OCTET_STRING_LENGTH_3_OCTETS_FLAG;
            flush_currentoctet();
            int len = length - (firstLimit+256);
            _out.write(len >>> 24);
            _out.write((len >> 16) & 255);
            _out.write((len>> 8) & 255);
            _out.write(len & 255);
        }
        //encode array
        PrimitiveArraySerializer.writeTo(_out,arrayOfFixedLength.getArray(),0,length);
        
    }
    
    // UTF encoding
    byte[] _encodingBuffer = new byte[1024];
    char[] _charBuffer = new char[1024];
    protected int encodeUTF8inInternalEncodingBuffer(String str) throws IOException {
        return encodeUTFinInternalEncodingBuffer(str, true);
    }
    protected int encodeUTFinInternalEncodingBuffer(String str) throws IOException {
        return encodeUTFinInternalEncodingBuffer(str, utf8encoding);
    }
    protected int encodeUTFinInternalEncodingBuffer(String str,boolean utf8encoding) throws IOException {
        final int length = str.length();
            if (length < _charBuffer.length) {
                str.getChars(0, length, _charBuffer, 0);
                return utf8encoding?encodeUTF8String(_charBuffer, 0, length):
                                    encodeUTF16String(_charBuffer, 0, length);
            } else {
                char[] ch = str.toCharArray();
                return utf8encoding?encodeUTF8String(ch, 0, length):
                                    encodeUTF16String(ch, 0, length);
            }
    }
    protected int encodeUTFinInternalEncodingBuffer(char[] ca, int offset, int length) throws IOException {
        return utf8encoding?encodeUTF8String(ca, offset, length):
                                    encodeUTF16String(ca, offset, length);
    }
    private final int encodeUTF8String(char[] ch, int offset, int length) throws IOException {
        int bpos = 0;

        ensureEncodingBufferSizeForUtf8String(length);

        final int end = offset + length;
        char c;
        while (end != offset) {
            c = ch[offset++];
            if (c < 0x80) {
                // 1 byte, 7 bits
                _encodingBuffer[bpos++] = (byte) c;
            } else if (c < 0x800) {
                // 2 bytes, 11 bits
                _encodingBuffer[bpos++] =
                    (byte) (0xC0 | (c >> 6));    // first 5
                _encodingBuffer[bpos++] =
                    (byte) (0x80 | (c & 0x3F));  // second 6
            } else if (c <= '\uFFFF') { 
                if (!Character.isHighSurrogate(c) && !Character.isLowSurrogate(c)) {
                    // 3 bytes, 16 bits
                    _encodingBuffer[bpos++] =
                        (byte) (0xE0 | (c >> 12));   // first 4
                    _encodingBuffer[bpos++] =
                        (byte) (0x80 | ((c >> 6) & 0x3F));  // second 6
                    _encodingBuffer[bpos++] =
                        (byte) (0x80 | (c & 0x3F));  // third 6
                } else {
                    // 4 bytes, high and low surrogate
                    encodeCharacterAsUtf8FourByte(c, ch, offset, end, bpos);
                    
                    bpos += 4;
                    offset++;
                }
            }
        }

        return bpos;
    }
    private void encodeCharacterAsUtf8FourByte(int c, char[] ch, int chpos, int chend, int bpos) throws IOException {
        if (chpos == chend) {
            throw new IOException("Unexpected end of string");
        }
        
        final char d = ch[chpos];
        if (!Character.isLowSurrogate(d)) {
            throw new IOException("Illegal character. Low surrogate expected");
        }
        
        final int uc = (((c & 0x3ff) << 10) | (d & 0x3ff)) + 0x10000;
        if (uc < 0 || uc >= 0x200000) {
            throw new IOException("");
        }

        _encodingBuffer[bpos++] = (byte)(0xF0 | ((uc >> 18)));
        _encodingBuffer[bpos++] = (byte)(0x80 | ((uc >> 12) & 0x3F));
        _encodingBuffer[bpos++] = (byte)(0x80 | ((uc >> 6) & 0x3F));
        _encodingBuffer[bpos++] = (byte)(0x80 | (uc & 0x3F));
    }
    private void ensureEncodingBufferSizeForUtf8String(int length) {
        final int newLength = 4 * length;
        if (_encodingBuffer.length < newLength) {
            _encodingBuffer = new byte[newLength];
        }
    }
    protected final int encodeUTF16String(char[] ch, int offset, int length) throws IOException {
        int byteLength = 0;

        ensureEncodingBufferSizeForUtf16String(length);

        final int n = offset + length;
        for (int i = offset; i < n; i++) {
            final int c = (int) ch[i];
            _encodingBuffer[byteLength++] = (byte)(c >> 8);
            _encodingBuffer[byteLength++] = (byte)(c & 0xFF);
        }

        return byteLength;
    }
    private void ensureEncodingBufferSizeForUtf16String(int length) {
        final int newLength = 2 * length;
        if (_encodingBuffer.length < newLength) {
            _encodingBuffer = new byte[newLength];
        }
    }

    protected void encodeAdditionalData(List<Additional_datum> additional_data) throws IOException {
        encodeSequenceOfLength(additional_data.size());
        for (Additional_datum additional_datum : additional_data) {
            int len = encodeUTF8inInternalEncodingBuffer(additional_datum.getId());
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
            encodeNonEmptyOctetStringOnSecondBit(additional_datum.getData());
        }
    }
    protected void encodeSequenceOfLength(int length) throws IOException {
        length--;
        if (length < 128) {
            current_octet = length;
            flush_currentoctet();
        }
        else { 
            length -= 128;
            current_octet = 0x80 | ((length >> 16) & 0xF);
            flush_currentoctet();
            _out.write((length >> 8) & 0xFF);
            _out.write(length & 0xFF);
        }
    }

    private void encodeInitialVocabulary(InitialVocabulary initialVocabulary) throws IOException {
        int mascara = 0;
        
        String externalURI = initialVocabulary.getExternalVocabularyURI();
        if (externalURI != null && !externalURI.isEmpty()) {
            mascara |= FjsonConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG;
        }
        if (!initialVocabulary.alphabets.isEmpty()) {
            mascara |= FjsonConstants.INITIAL_VOCABULARY_ALPHABETS_FLAG;
        }
        if (!initialVocabulary.algorithms.isEmpty()) {
            mascara |= FjsonConstants.INITIAL_VOCABULARY_ALGORITHMS_FLAG;
        }
        if (!initialVocabulary.keys.isEmpty()) {
            mascara |= FjsonConstants.INITIAL_VOCABULARY_KEYS_FLAG;
        }
        if (!initialVocabulary.string_values.isEmpty()) {
            mascara |= FjsonConstants.INITIAL_VOCABULARY_STRING_VALUES_FLAG;
        }
        current_octet = mascara;
        flush_currentoctet();
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG) != 0) {
            int len = encodeUTF8inInternalEncodingBuffer(externalURI);
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_ALPHABETS_FLAG) != 0) {
            encodeAlphabetVocabularyTable(initialVocabulary.alphabets);
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_ALGORITHMS_FLAG) != 0) {
            encodeAlgorithmVocabularyTable(initialVocabulary.algorithms);
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_KEYS_FLAG) != 0) {
            encodeNonEmptyOctetStringVocabularyTable(initialVocabulary.keys);
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_STRING_VALUES_FLAG) != 0) {
            encodeEncodedCharacterStringVocabularyTable(initialVocabulary.string_values);
        }
        
    }
    protected void encodeAlphabetVocabularyTable(List<Alphabet> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (Alphabet alphabet : table) {
            int len = encodeUTF8inInternalEncodingBuffer(alphabet.toString());
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
        }
    }
    protected void encodeAlgorithmVocabularyTable(List<Algorithm> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (Algorithm algo : table) {
            int len = encodeUTF8inInternalEncodingBuffer(algo.getURI());
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
        }
    }
    protected void encodeNonEmptyOctetStringVocabularyTable(List<String> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (String str : table) {
            int len = encodeUTF8inInternalEncodingBuffer(str);
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
        }
    }
    protected void encodeEncodedCharacterStringVocabularyTable(List<String> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (String str : table) {
            encodeEncodedCharacterStringOnFourthbit(str);
        }
    }
    
    public void setAdditional_Data(List<Additional_datum> additional_data) {
        this.additional_data = additional_data;
    }
    public void setInitialVocabulary(InitialVocabulary initialVocabulary) {
        vocabulary.setInitialVocabulary(initialVocabulary);
    }
    public InitialVocabulary getDynamicallyGeneratedVocabularyAsInitial() {
        return vocabulary.toInitialVocabularyExcludingInitialVocabulary();
    }
    public InitialVocabulary getAllVocabularyAsInitial() {
        return vocabulary.toInitialVocabularyIncludingInitialVocabulary();
    }
    private class AllowLimitedStringLenghts implements AllowIndexMap<String> {
        int maximumLength;
        public AllowLimitedStringLenghts(int maximumLength) {
            this.maximumLength = maximumLength;
        }

        public void setMaximumLength(int maximumLength) {
            this.maximumLength = maximumLength;
        }

        public int getMaximumLength() {
            return maximumLength;
        }
        @Override
        public boolean isInsertionAllowed(String str, Algorithm algo) {
            return str.length() <= maximumLength;
        }

        @Override
        public boolean isObtentionAllowed(Algorithm algo) {
            return true;
        }
        
    }
}
