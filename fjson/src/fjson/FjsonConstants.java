//  Author: Ronald Pablos
//  Year: 2013

package fjson;

/**
 *
 * @author rpablos
 */
public class FjsonConstants {
    
    public static final int MAGIC_NUMBER = 0xFB;
    public static final int FJSON_VERSION = 1;
    public static final int INITIAL_VOCABULARY_FLAG = 0x04;
    public static final int ADDITIONAL_DATA_FLAG = 0x08;
    
    public static final int MAXIMUM_TABLE_ENTRIES = 1 << 20;
    
    public static final int STRING_IDENTIFICATION = 0x00;
    public static final int STRING_IDENTIFICATION_MASK = 0x80;
    public static final int ARRAY_IDENTIFICATION = 0xE0;
    public static final int ARRAY_IDENTIFICATION_MASK = 0xE0;
    public static final int OBJECT_IDENTIFICATION = 0xCC;
    public static final int OBJECT_IDENTIFICATION_MASK = 0xFC;
    public static final int BOOLEAN_IDENTIFICATION = 0xC4;
    public static final int BOOLEAN_IDENTIFICATION_MASK = 0xFC;
    public static final int NULL_IDENTIFICATION = 0xC8;
    public static final int NULL_IDENTIFICATION_MASK = 0xFC;
    public static final int NUMBER_IDENTIFICATION = 0x80;
    public static final int NUMBER_IDENTIFICATION_MASK = 0xC0;
    
    public static final int TYPED_ARRAY_FLAG = 0x10;
    
    public static final int TERMINATION_PATTERN = 0xf;
    
    public static final int IDENTIFYINGSTRINGORINDEX_STRING = 0x00;
    public static final int IDENTIFYINGSTRINGORINDEX_INDEX = 0x80;
    public static final int NONIDENTIFYINGSTRINGORINDEX_INDEX = 0x40;
    public static final int NONIDENTIFYINGSTRING_ADDTOTABLE = 0x20;
    
    public static final int INTEGER_2ND_BIT_2_OCTETS_FLAG = 0x40;
    public static final int INTEGER_2ND_BIT_3_OCTETS_FLAG = 0x60;
    public static final int INTEGER_3RD_BIT_2_OCTETS_FLAG = 0x20;
    public static final int INTEGER_3RD_BIT_3_OCTETS_FLAG = 0x28;
    public static final int INTEGER_3RD_BIT_4_OCTETS_FLAG = 0x30;
    
    public static final int OCTET_STRING_LENGTH_2_OCTETS_FLAG = 0x80;
    public static final int OCTET_STRING_LENGTH_3_OCTETS_FLAG = 0xC0;
    
    public static final int ZERO_LENGTH_STRING_PATTERN_ON_2ND_BIT = 0x7F;
    public static final int ZERO_LENGTH_STRING_PATTERN_ON_3RD_BIT = 0x3F;
    
    public static final int ENCODED_CHARACTER_STRING_4TH_BIT_UTF8_DISCRIMINANT = 0x00;
    public static final int ENCODED_CHARACTER_STRING_4TH_BIT_UTF16_DISCRIMINANT = 0x08;
    public static final int ENCODED_CHARACTER_STRING_4TH_BIT_ALPHABET_DISCRIMINANT = 0x10;
    public static final int ENCODED_CHARACTER_STRING_4TH_BIT_ALGORITHM_DISCRIMINANT = 0x18;
    public static final int ENCODED_CHARACTER_STRING_4TH_BIT_MASK_DISCRIMINANT = 0x18;
    
    public static final int TYPE_OF_NUMBER_MASK = 0x1F;
    public static final int LITTLEUINT_NUMBER_MASK = 0x1F;
    public static final int NUMBER_UINT8_IDENTIFICATION = 0;
    public static final int NUMBER_INT8_IDENTIFICATION = 1;
    public static final int NUMBER_UINT16_IDENTIFICATION = 2;
    public static final int NUMBER_INT16_IDENTIFICATION = 3;
    public static final int NUMBER_UINT32_IDENTIFICATION = 4;
    public static final int NUMBER_INT32_IDENTIFICATION = 5;
    public static final int NUMBER_UINT64_IDENTIFICATION = 6;
    public static final int NUMBER_INT64_IDENTIFICATION = 7;
    public static final int NUMBER_FLOAT32_IDENTIFICATION = 8;
    public static final int NUMBER_FLOAT64_IDENTIFICATION = 9;
    public static final int NUMBER_VARIABLE_IDENTIFICATION = 0x10;
    public static final int NUMBER_LITTLEUINT_IDENTIFICATION = 0x20;
    
    public static final int INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG = 0x10;
    public static final int INITIAL_VOCABULARY_ALPHABETS_FLAG = 0x08;
    public static final int INITIAL_VOCABULARY_ALGORITHMS_FLAG = 0x04;
    public static final int INITIAL_VOCABULARY_KEYS_FLAG = 0x02;
    public static final int INITIAL_VOCABULARY_STRING_VALUES_FLAG = 0x01;
    
}
