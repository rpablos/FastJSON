//  Author: Ronald Pablos
//  Year: 2013

package fjson;

import fjson.Algorithm.Algorithm;
import fjson.Alphabet.Alphabet;
import fjson.Alphabet.Numeric;
import fjson.FixedLengthTypes.JsonArrayBoolean;
import fjson.FixedLengthTypes.JsonArrayFloat32;
import fjson.FixedLengthTypes.JsonArrayFloat64;
import fjson.FixedLengthTypes.JsonArrayINT16;
import fjson.FixedLengthTypes.JsonArrayINT32;
import fjson.FixedLengthTypes.JsonArrayINT64;
import fjson.FixedLengthTypes.JsonArrayINT8;
import fjson.FixedLengthTypes.JsonArrayNull;
import fjson.FixedLengthTypes.JsonArrayOfFixedLength;
import fjson.FixedLengthTypes.JsonArrayOfFixedLength.ArrayType;
import fjson.FixedLengthTypes.JsonArrayUINT16;
import fjson.FixedLengthTypes.JsonArrayUINT32;
import fjson.FixedLengthTypes.JsonArrayUINT64;
import fjson.FixedLengthTypes.JsonArrayUINT8;
import fjson.FixedLengthTypes.JsonNumberFloat32;
import fjson.FixedLengthTypes.JsonNumberFloat64;
import fjson.FixedLengthTypes.JsonNumberINT16;
import fjson.FixedLengthTypes.JsonNumberINT32;
import fjson.FixedLengthTypes.JsonNumberINT64;
import fjson.FixedLengthTypes.JsonNumberINT8;
import fjson.FixedLengthTypes.JsonNumberUINT16;
import fjson.FixedLengthTypes.JsonNumberUINT32;
import fjson.FixedLengthTypes.JsonNumberUINT64;
import fjson.FixedLengthTypes.JsonNumberUINT8;
import fjson.Types.JsonAlgorithmEncodingString;
import fjson.Types.JsonAlphabetConstrainedString;
import fjson.Types.JsonArray_Impl;
import fjson.Types.JsonNumber_Impl;
import fjson.Types.JsonObject_Impl;
import fjson.Types.JsonString_Impl;
import fjson.util.Additional_datum;
import fjson.util.ArrayIndex;
import fjson.util.DecoderVocabulary;
import fjson.util.EncodedString;
import fjson.util.InitialVocabulary;
import fjson.util.PrimitiveArrayDeserializer;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParsingException;

/**
 *
 * @author rpablos
 */
public class Decoder {
    protected JsonLocation jsonLocation = new JsonLocation_Impl();
//    static JsonBuilderFactory jsonFactory = Json.createBuilderFactory(null);
    protected int current_octet;
    protected InputStream _in;
    protected boolean halfOctetLastTermination = false;
    protected DecoderVocabulary vocabulary = new DecoderVocabulary();
    List<Additional_datum> additional_data = null;
    Map<String, InitialVocabulary> registredExternalVocabulary = new HashMap<String, InitialVocabulary>();
    Map<String, Algorithm> registredAlgorithms = new HashMap<String, Algorithm>();
    
    protected JsonStructure decodeJsonDocument() throws IOException, FjsonException {
        JsonStructure result;
        decodeHeader();
        result = decodeJsonStructure();
//        decodeDocumentTermination();
        return result;
    }
    protected JsonStructure decodeJsonStructure() throws IOException, FjsonException {
        JsonStructure result = null;
        readOctet();
        if ((current_octet & FjsonConstants.OBJECT_IDENTIFICATION_MASK) == FjsonConstants.OBJECT_IDENTIFICATION) {
            result = decodeJsonObject();
        } else if 
            ((current_octet & FjsonConstants.ARRAY_IDENTIFICATION_MASK) == FjsonConstants.ARRAY_IDENTIFICATION) {
            result = decodeJsonArray();
        } else
            throw new JsonParsingException("Root element is not a Json structure", jsonLocation);
        return result;
    }
//    protected void decodeDocumentTermination() throws IOException {
//        if (
//            ((halfOctetLastTermination) && ((current_octet & 0xF) != FjsonConstants.TERMINATION_PATTERN)) ||
//             ((!halfOctetLastTermination) && (readOctet() != (FjsonConstants.TERMINATION_PATTERN << 4)))
//            )
//            throw new JsonParsingException("Incorrect document termination",jsonLocation);       
//    }

    
    protected void decodeHeader() throws IOException, FjsonException {
        if (readOctet() != FjsonConstants.MAGIC_NUMBER)
            throw new JsonParsingException("Not a FJSON document",jsonLocation);
        
        int mascara = readOctet(); //mascara
        if ((mascara >>> 4) != FjsonConstants.FJSON_VERSION)
            throw new JsonParsingException("FJSON version not supported",jsonLocation);
        if ((mascara & FjsonConstants.ADDITIONAL_DATA_FLAG) != 0) {
            additional_data = decodeAdditionalData();
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_FLAG) != 0) {
            decodeInitialVocabulary();
        }
    }
    protected JsonObject decodeJsonObject() throws IOException, FjsonException {
//        JsonObjectBuilder objectBuilder = jsonFactory.createObjectBuilder();
        Map<String, JsonValue> map = new LinkedHashMap<String, JsonValue>();
        boolean _terminate = false;
        boolean terminateOnHalf = true;
        while (!_terminate) {
            readOctet();
            if ((current_octet >> 4) == FjsonConstants.TERMINATION_PATTERN ) {
                //_terminate = true;
                break;
            }
            String key = decodeIdentifyingStringOrIndex(vocabulary.keys);
            readOctet();
            JsonValue value = decodeJsonValue();
//            objectBuilder.add(key,value);
            map.put(key, value);
            if ((value.getValueType() == ValueType.OBJECT) || (value.getValueType() == ValueType.ARRAY))
                if ((halfOctetLastTermination) && ((current_octet & 0xF) == FjsonConstants.TERMINATION_PATTERN)) {
                    _terminate = true;
                    terminateOnHalf = false;
                }
        }
        halfOctetLastTermination = terminateOnHalf;
//        return objectBuilder.build();
        return new JsonObject_Impl(map);
    }
    protected JsonArray decodeJsonArray() throws IOException, FjsonException {
        
        if ((current_octet & FjsonConstants.TYPED_ARRAY_FLAG) != 0) {
            halfOctetLastTermination = false;
            return decodeArrayOfFixedLength(JsonArrayOfFixedLength.ArrayType.values()[current_octet & 0xF]);
        }
        boolean terminateOnHalf = true;
//        JsonArrayBuilder arrayBuilder = jsonFactory.createArrayBuilder();
        List<JsonValue> list = new ArrayList<JsonValue>();
        boolean _terminate = false;
        while (!_terminate) {
            readOctet();
            if ((current_octet >>4) ==FjsonConstants.TERMINATION_PATTERN ) {
                //_terminate = true;
                break;
            }
            JsonValue value = decodeJsonValue();
//            arrayBuilder.add(value);
            list.add(value);
            if ((value.getValueType() == ValueType.OBJECT) || (value.getValueType() == ValueType.ARRAY))
                if ((halfOctetLastTermination) && ((current_octet & 0xF) == FjsonConstants.TERMINATION_PATTERN)) {
                    _terminate = true;
                    terminateOnHalf = false;
                }
        }
        halfOctetLastTermination = terminateOnHalf;
//        return arrayBuilder.build();
        return new JsonArray_Impl(list);
    }
    protected JsonArrayOfFixedLength decodeArrayOfFixedLength(JsonArrayOfFixedLength.ArrayType type) throws IOException {
        int len;
        Object array;
        // decode length
        readOctet();
        if (((1 << 7) & current_octet) == 0) {
            len =  (current_octet & ((1 << 7) - 1));
        } else if ((current_octet & 0xFF) == FjsonConstants.OCTET_STRING_LENGTH_2_OCTETS_FLAG) {
            len = readOctet() + (1 << 7);
        } else {
            len = (readOctet() << 24);
            len |= (readOctet() << 16);
            len |= (readOctet() << 8);
            len |= readOctet();
            len += (1 << 7) + 256;
        }
        switch (type) {
            case INT8:
            case UINT8:
                array = new byte[len];
                PrimitiveArrayDeserializer.readFrom(_in, array, 0, len);
                return (type == ArrayType.INT8)?new JsonArrayINT8((byte[])array):new JsonArrayUINT8((byte[])array);
            case INT16:
            case UINT16:
                array = new short[len];
                PrimitiveArrayDeserializer.readFrom(_in, array, 0, len);
                return (type == ArrayType.INT16)?new JsonArrayINT16((short[])array):new JsonArrayUINT16((short[])array);
            case INT32:
            case UINT32:
                array = new int[len];
                PrimitiveArrayDeserializer.readFrom(_in, array, 0, len);
                return (type == ArrayType.INT32)?new JsonArrayINT32((int[])array):new JsonArrayUINT32((int[])array);
            case INT64:
            case UINT64:
                array = new long[len];
                PrimitiveArrayDeserializer.readFrom(_in, array, 0, len);
                return (type == ArrayType.INT64)?new JsonArrayINT64((long[])array):new JsonArrayUINT64((long[])array);
            case BOOLEAN:
                array = new boolean[len];
                PrimitiveArrayDeserializer.readFrom(_in, array, 0, len);
                return new JsonArrayBoolean((boolean[])array);
            case NULL:
                return new JsonArrayNull(len);
            case FLOAT32:
                array = new float[len];
                PrimitiveArrayDeserializer.readFrom(_in, array, 0, len);
                return new JsonArrayFloat32((float[])array);
            case FLOAT64:
                array = new double[len];
                PrimitiveArrayDeserializer.readFrom(_in, array, 0, len);
                return new JsonArrayFloat64((double[])array);
        }
        throw new JsonParsingException("Incorrect fixed-length array type", jsonLocation);
    }
    protected JsonString decodeJsonString() throws IOException, FjsonException {
        EncodedString estr = decodeNonIdentifyingStringOrIndexOnSecondBit(vocabulary.string_values);
        switch (estr.type) {
            case Algorithm:
                return new JsonAlgorithmEncodingString(estr.algorithm.objectFromByteArray(estr.theData), estr.algorithm);
            case Alphabet:
                return new JsonAlphabetConstrainedString(estr.getString(), (Alphabet) estr.algorithm);
            default:
                return new JsonString_Impl(estr.getString());
        }
    }
    protected JsonNumber decodeJsonNumber() throws FjsonException, IOException {
        if ((current_octet & FjsonConstants.NUMBER_LITTLEUINT_IDENTIFICATION) != 0)
            return new JsonNumberINT8((byte)(current_octet & FjsonConstants.LITTLEUINT_NUMBER_MASK));
        if ((current_octet & FjsonConstants.NUMBER_VARIABLE_IDENTIFICATION) != 0)
            return new JsonNumber_Impl(new BigDecimal(Numeric.instance.fromByteArray(decodeNonEmptyOctetStringOnFifthBit())));
        switch(current_octet & FjsonConstants.TYPE_OF_NUMBER_MASK ) {
            case FjsonConstants.NUMBER_UINT8_IDENTIFICATION:
                return new JsonNumberUINT8((byte)readOctet());
            case FjsonConstants.NUMBER_INT8_IDENTIFICATION:
                return new JsonNumberINT8((byte)readOctet());
            case FjsonConstants.NUMBER_UINT16_IDENTIFICATION:
                return new JsonNumberUINT16(PrimitiveArrayDeserializer.readShort(_in));
            case FjsonConstants.NUMBER_INT16_IDENTIFICATION:
                return new JsonNumberINT16(PrimitiveArrayDeserializer.readShort(_in));
            case FjsonConstants.NUMBER_UINT32_IDENTIFICATION:
                return new JsonNumberUINT32(PrimitiveArrayDeserializer.readInt(_in));
            case FjsonConstants.NUMBER_INT32_IDENTIFICATION:
                return new JsonNumberINT32(PrimitiveArrayDeserializer.readInt(_in));
            case FjsonConstants.NUMBER_UINT64_IDENTIFICATION:
                return new JsonNumberUINT64(PrimitiveArrayDeserializer.readLong(_in));
            case FjsonConstants.NUMBER_INT64_IDENTIFICATION:
                return new JsonNumberINT64(PrimitiveArrayDeserializer.readLong(_in));
            case FjsonConstants.NUMBER_FLOAT32_IDENTIFICATION:
                return new JsonNumberFloat32(PrimitiveArrayDeserializer.readFloat(_in));
            case FjsonConstants.NUMBER_FLOAT64_IDENTIFICATION:
                return new JsonNumberFloat64(PrimitiveArrayDeserializer.readDouble(_in));
            default:
                throw new FjsonException();
        }
    }
    protected JsonValue decodeBoolean() {
        return ((current_octet & 1) !=0)?JsonValue.TRUE:JsonValue.FALSE;
        
    }
    protected JsonValue decodeNull() {
        return JsonValue.NULL;
    }
    protected JsonValue decodeJsonValue() throws IOException, FjsonException {
        //readOctet();
        if ((current_octet & FjsonConstants.STRING_IDENTIFICATION_MASK) == FjsonConstants.STRING_IDENTIFICATION) {
            return decodeJsonString();
        } else if ((current_octet & FjsonConstants.NUMBER_IDENTIFICATION_MASK) == FjsonConstants.NUMBER_IDENTIFICATION) {
            return decodeJsonNumber();
        } else if ((current_octet & FjsonConstants.OBJECT_IDENTIFICATION_MASK) == FjsonConstants.OBJECT_IDENTIFICATION) {
            return decodeJsonObject();
        } else if ((current_octet & FjsonConstants.ARRAY_IDENTIFICATION_MASK) == FjsonConstants.ARRAY_IDENTIFICATION) {
            return decodeJsonArray();
        } else if ((current_octet & FjsonConstants.BOOLEAN_IDENTIFICATION_MASK) == FjsonConstants.BOOLEAN_IDENTIFICATION) {
            return decodeBoolean();
        } else if ((current_octet & FjsonConstants.NULL_IDENTIFICATION_MASK) == FjsonConstants.NULL_IDENTIFICATION) {
            return decodeNull();
        }
        throw new JsonParsingException("Unknown type", jsonLocation);
    }
    
    protected String decodeIdentifyingStringOrIndex(ArrayIndex<String> table) throws IOException, FjsonException {
        if (current_octet == 0xFF) //empty string
            return "";
        if ((current_octet & FjsonConstants.IDENTIFYINGSTRINGORINDEX_INDEX) != 0) {
            int index = decodeIndexOnSecondBit();
            return table.get(index);
        } else {
            String str = decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit());
            table.add(str);
            return str;
        }
    }
    
    protected int decodeIndexOnSecondBit() throws IOException {
        if ((current_octet & 64) == 0) {
            return current_octet & 63;
        } else if ((current_octet & 32) == 0) {
            int b1 = current_octet & 31;
            return ((b1 << 8) | readOctet()) + 64;
        } else {
            int b1 = current_octet & 15;
            int b2 = readOctet();
            return ((b1 << 16) | (b2 << 8) | readOctet()) + 8256;
        }
    }
    protected byte[] decodeNonEmptyOctetStringOnSecondBit() throws IOException {
        return decodeNonEmptyOctetString(2);
    }
    protected byte[] decodeNonEmptyOctetString(int startingBit) throws IOException {
        int length;
        // decode length
        if (((1 << (8 - startingBit)) & current_octet) == 0) {
            length = 1 + (current_octet & ((1 << (8 - startingBit)) - 1));
        } else if (((current_octet << (startingBit - 1)) & 255) == FjsonConstants.OCTET_STRING_LENGTH_2_OCTETS_FLAG) {
            length = readOctet() + 1 + (1 << (8 - startingBit));
        } else {
            length = (readOctet() << 24);
            length |= (readOctet() << 16);
            length |= (readOctet() << 8);
            length |= readOctet();
            length += 1 + (1 << (8 - startingBit)) + 256;
        }
        //decode octet_string
        byte[] result = new byte[length];
        readFully(result);
        return result;
    }
    
    EncodedString encodedStringBuffer = new EncodedString();
    protected EncodedString decodeNonIdentifyingStringOrIndexOnSecondBit(ArrayIndex<String> table) throws IOException, FjsonException {
               
        if ((current_octet & FjsonConstants.NONIDENTIFYINGSTRINGORINDEX_INDEX) != 0) {
            String str = "";
            if ((current_octet & FjsonConstants.ZERO_LENGTH_STRING_PATTERN_ON_3RD_BIT) != FjsonConstants.ZERO_LENGTH_STRING_PATTERN_ON_3RD_BIT) { //no es la cadena vacia
                int index = decodeIndexOnThirdBit();
                str = table.get(index);
            }
            encodedStringBuffer.setUTF8(str);
            return encodedStringBuffer;
        } else {
            boolean addtotable = (current_octet & FjsonConstants.NONIDENTIFYINGSTRING_ADDTOTABLE) != 0;
            EncodedString str = decodeEncodedCharacterStringOnFourthbit();
            if (addtotable) {
                table.add(str.getString());
            }
            return str;
        }
    }
    protected int decodeIndexOnThirdBit() throws IOException {
        return decodeIndex(3);
    }
    protected int decodeIndex(int startingbit) throws IOException {
        if ((current_octet & (1 << (8 - startingbit))) == 0) {
            return current_octet & ((1 << (8 - startingbit)) - 1);
        } else if ((current_octet & (56 >> (startingbit - 3))) == (32 >> (startingbit - 3))) {
            int b1 = current_octet & ((1 << (6 - startingbit)) - 1);
            return ((b1 << 8) | readOctet()) + (1 << (8 - startingbit));
        } else if ((current_octet & (56 >> (startingbit - 3))) == (40 >> (startingbit - 3))) {
            int b1 = current_octet & ((1 << (6 - startingbit)) - 1);
            int b2 = readOctet();
            return ((b1 << 16) | (b2 << 8) | readOctet()) + (1 << (8 - startingbit)) + (1 << (8 + 6 - startingbit));
        } else {
            int b1 = readOctet();
            int b2 = readOctet();
            return ((b1 << 16) | (b2 << 8) | readOctet()) + (1 << (8 - startingbit)) + (1 << (8 + 6 - startingbit)) + (1 << (16 + 6 - startingbit));
        }
    }
    
    protected EncodedString decodeEncodedCharacterStringOnFourthbit() throws IOException, FjsonException {
        switch (current_octet & FjsonConstants.ENCODED_CHARACTER_STRING_4TH_BIT_MASK_DISCRIMINANT) {
            case FjsonConstants.ENCODED_CHARACTER_STRING_4TH_BIT_UTF8_DISCRIMINANT:
                encodedStringBuffer.setUTF8(decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSixthBit()));
                return encodedStringBuffer;
            case FjsonConstants.ENCODED_CHARACTER_STRING_4TH_BIT_UTF16_DISCRIMINANT:
                encodedStringBuffer.setUTF16(decodeUTF16inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSixthBit()));
                return encodedStringBuffer;
            case FjsonConstants.ENCODED_CHARACTER_STRING_4TH_BIT_ALGORITHM_DISCRIMINANT:
                int index = decodeOctetInteger(6);
                Algorithm algo = vocabulary.algorithms.get(index);
                encodedStringBuffer.setAlgorithm(index, algo, decodeNonEmptyOctetStringOnSixthBit());
                return encodedStringBuffer;
            case FjsonConstants.ENCODED_CHARACTER_STRING_4TH_BIT_ALPHABET_DISCRIMINANT:
                int index2 = decodeOctetInteger(6);
                Algorithm algo2 = vocabulary.alphabets.get(index2);
                encodedStringBuffer.setAlphabet(index2, algo2, algo2.fromByteArray(decodeNonEmptyOctetStringOnSixthBit()));
                return encodedStringBuffer;
        }
        throw new FjsonException();
    }
    protected byte[] decodeNonEmptyOctetStringOnSixthBit() throws IOException {
        return decodeNonEmptyOctetString(6);
    }
    protected byte[] decodeNonEmptyOctetStringOnFifthBit() throws IOException {
        return decodeNonEmptyOctetString(5);
    }
    protected int decodeOctetInteger(int startingBit) throws IOException {
        int result = (current_octet << (startingBit - 1)) & 255;
        readOctet();
        return result |= current_octet >>> 8 - (startingBit - 1);
    }
    //read from input operations
    protected int readOctet() throws IOException {
        if ((current_octet = _in.read()) == -1) {
            throw new EOFException();
        }
        return current_octet;
    }
    void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    void readFully(byte[] b, int off, int len) throws IOException {
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
    
    //UTF operations
    char[] _decodingBuffer = new char[1024];
    public String decodeUTF8inInternalEncodingBufferAsString(byte[] data) throws IOException {
        int len= decodeUTF8inInternalEncodingBuffer(data, 0, data.length);
        return new String(_decodingBuffer,0,len);
    }
    public int decodeUTF8inInternalEncodingBuffer(byte[] data) throws IOException {
        return decodeUTF8inInternalEncodingBuffer(data, 0, data.length);
    }
    protected int decodeUTF8inInternalEncodingBuffer(byte[] data, int offset, int length) throws IOException {
        ensureDecodingBufferSizeForUtf8String(length);
        int charlen = 0, b1;
        int end = offset+length;
        while (offset < end) {
            b1 = data[offset++] & 0xFF;
            if (b1 < 0x80)
                _decodingBuffer[charlen++] = (char) b1;
            else {
                checkEndUTF8String(offset,end);
                int b2 = data[offset++] & 0xFF;
                checkContinuationUTF8octet(b2, offset-1);
                if (b1 < 0xE0) {  // dos octetos
                    _decodingBuffer[charlen++] = (char) (((b1 & 0x1F) << 6) | (b2 & 0x3F));
                } else {
                    checkEndUTF8String(offset,end);
                    int b3 = data[offset++] & 0xFF;
                    checkContinuationUTF8octet(b3, offset-1);
                    if (b1 < 0xF0){  // tres octetos
                        _decodingBuffer[charlen++] = (char) ((b1 & 0x0F) << 12 | (b2 & 0x3F) << 6 | (b3 & 0x3F));
                    } else {  // 4 octetos
                        checkEndUTF8String(offset,end);
                        int b4 = data[offset++] & 0xFF;
                        checkContinuationUTF8octet(b4, offset-1);
                        int uc = ((b1 & 0x7) << 18) | ((b2 & 0x3f) << 12) | ((b3 & 0x3f) << 6) | (b4 & 0x3f);
                        if (uc < 0 || uc >= 0x200000) {
                                throw new IOException("hign surrogate and low surrogate expected");
                        }
                        _decodingBuffer[charlen++] = (char)( ( ( (uc - 0x10000) >> 10) & 0x3FF)  +0xd800); //high
                        _decodingBuffer[charlen++] = (char)( ( ( (uc - 0x10000)) & 0x3FF)  +0xdc00); //low
                    }
                }
            }
        }
        return charlen;
    }
    private void checkContinuationUTF8octet(int b, int pos) throws IOException {
        if ((b & 0xC0) != 0x80) 
                throw new IOException("Illegal state at position "+pos);
    }
    private void checkEndUTF8String(int offset, int end) throws IOException {
        if (offset == end)
             throw new IOException("Unexpected end of string");
    }
    private void ensureDecodingBufferSizeForUtf8String(int length) {
        if (_decodingBuffer.length < length) {
            _decodingBuffer = new char[length];
        }
    }
    public String decodeUTF16inInternalEncodingBufferAsString(byte[] data) throws IOException {
        int len= decodeUTF16inInternalEncodingBuffer(data, 0, data.length);
        return new String(_decodingBuffer,0,len);
    }
    protected int decodeUTF16inInternalEncodingBuffer(byte[] data, int offset, int length) throws IOException {
        ensureDecodingBufferSizeForUtf16String(length);
        if ((length & 0x1) != 0) //impar
            throw new IOException("not a valid utf16");
        int len = length/2;
        for (int i = 0; i < len; i++)
            _decodingBuffer[i] = (char) ((data[2*i] << 8) | data[2*i+1]);
        return len;
    }

    private void ensureDecodingBufferSizeForUtf16String(int length) {
        if (_decodingBuffer.length < length/2) {
            _decodingBuffer = new char[length/2];
        }
    }

    protected List<Additional_datum> decodeAdditionalData() throws IOException {
        int len = decodeSequenceOfLength();
        List<Additional_datum> result = new ArrayList<Additional_datum>(len);
        for (; len > 0; len--) {
            readOctet();
            int l = decodeUTF8inInternalEncodingBuffer(decodeNonEmptyOctetStringOnSecondBit());
            String id = new String(_decodingBuffer, 0, l);
            readOctet();
            byte[] data = decodeNonEmptyOctetStringOnSecondBit();
            result.add(new Additional_datum(id, data));
        }
        return result;
    }
    protected int decodeSequenceOfLength() throws IOException {
        readOctet();
        if (current_octet < 128) {
            return current_octet + 1;
        } else {
            int b1 = current_octet & 15;
            int b2 = readOctet();
            return ((b1 << 16) | (b2 << 8) | readOctet()) + 129;
        }
    }

    private void decodeInitialVocabulary() throws IOException, FjsonException {
        int mascara = readOctet();
        
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG) != 0) {
            readOctet();
            String externalURI = decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit());
            vocabulary.setExternalVocabulary(registredExternalVocabulary.get(externalURI));
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_ALPHABETS_FLAG) != 0) {
            decodeAlphabetVocabularyTable(vocabulary.alphabets);
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_ALGORITHMS_FLAG) != 0) {
            decodeAlgorithmVocabularyTable(vocabulary.algorithms);
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_KEYS_FLAG) != 0) {
            decodeNonEmptyOctetStringVocabularyTable(vocabulary.keys);
        }
        if ((mascara & FjsonConstants.INITIAL_VOCABULARY_STRING_VALUES_FLAG) != 0) {
            decodeEncodedCharacterStringVocabularyTable(vocabulary.string_values);
        }
    }
    
    protected void decodeAlphabetVocabularyTable(ArrayIndex<Alphabet> table) throws IOException {
        int len = decodeSequenceOfLength();
        table.ensureCapacity(len);
        for (int i = 0; i < len; i++) {
            readOctet();
            table.add(new Alphabet(decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit())));
        }
    }
    protected void decodeAlgorithmVocabularyTable(ArrayIndex<Algorithm> table) throws IOException {
        int len = decodeSequenceOfLength();
        table.ensureCapacity(len);
        for (int i = 0; i < len; i++) {
            readOctet();
            String algorithmURI = decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit());
            table.add(registredAlgorithms.get(algorithmURI));
        }
    }
    protected void decodeNonEmptyOctetStringVocabularyTable(ArrayIndex<String> table) throws IOException {
        int len = decodeSequenceOfLength();
        table.ensureCapacity(len);
        for (int i = 0; i < len; i++) {
            readOctet();
            table.add(decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit()));
        }
    }
    protected void decodeEncodedCharacterStringVocabularyTable(ArrayIndex<String> table) throws IOException, FjsonException {
        int len = decodeSequenceOfLength();
        table.ensureCapacity(len);
        for (int i = 0; i < len; i++) {
            readOctet();
            table.add(decodeEncodedCharacterStringOnFourthbit().getString());
        }
    }
    
    public void registerAlgorithms(List<Algorithm> algorithms) {
        for (Algorithm algo: algorithms)
            registredAlgorithms.put(algo.getURI(), algo);
    }
    public List<Algorithm> getRegistredAlgorithms() {
        return new ArrayList<Algorithm>(registredAlgorithms.values());
    }
    public void registerExternalVocabularies(Map<String, InitialVocabulary> vocabularies) {
        registredExternalVocabulary.putAll(vocabularies);
    }
    public Map<String, InitialVocabulary> getRegistredVocabularies() {
        return registredExternalVocabulary;
    }
    public List<Additional_datum> getAdditional_Data() {
        return additional_data;
    }
    
    static class JsonLocation_Impl implements JsonLocation {

        @Override
        public long getLineNumber() { return -1; }

        @Override
        public long getColumnNumber() { return -1; }

        @Override
        public long getStreamOffset() { return -1; }
    }
}
