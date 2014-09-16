//  Author: Ronald Pablos
//  Year: 2013

package fjson.stream;

import fjson.Decoder;
import fjson.FixedLengthTypes.JsonArrayOfFixedLength;
import fjson.FjsonConstants;
import fjson.FjsonException;
import fjson.Types.JsonString_Impl;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;

/**
 *
 * @author rpablos
 */
public class FJsonParser extends Decoder implements JsonParser {
    JsonParser.Event nextState = null;
    JsonParser.Event currentState = null;
    JsonValue nextValue = null;
    JsonValue currentValue = null;
    boolean firstTime = true;
    ContextStack contextStack = new ContextStack();
    JsonArrayOfFixedLength fixedTypeArray = null;
    int posInFixedTypeArray = 0;
    JsonArrayOfFixedLength.ArrayType typeFixedTypeArray = null;
    
    public FJsonParser(InputStream in) {
        _in = in;
    }
    
    
    @Override
    public boolean hasNext() {
        return firstTime || (nextState != null);
    }

    @Override
    public Event next() {
        try {
            if (firstTime) {
                decodeHeader();
                firstTime = false;
                readOctet();
                if ((current_octet & FjsonConstants.OBJECT_IDENTIFICATION_MASK) == FjsonConstants.OBJECT_IDENTIFICATION) {
                    nextState = Event.START_OBJECT;
                    contextStack.push(true);
                } else if 
                    ((current_octet & FjsonConstants.ARRAY_IDENTIFICATION_MASK) == FjsonConstants.ARRAY_IDENTIFICATION) {
                    nextState = Event.START_ARRAY;
                    contextStack.push(false);
                } else
                    throw new JsonParsingException("Root element is not a Json structure", jsonLocation);
            }
        
            if (nextState == null)
                throw new NoSuchElementException();
            currentState = nextState;
            currentValue = nextValue;
            if (contextStack.noContext()) { //FIN
                nextState = null;
                return currentState;
            }
            //leer siguiente estado
            if ((nextState.equals(Event.END_OBJECT)|| nextState.equals(Event.END_ARRAY)) &&
                ((halfOctetLastTermination) && ((current_octet & 0xF) == FjsonConstants.TERMINATION_PATTERN))) {
                            nextState = contextStack.currentContextIsObject()?Event.END_OBJECT:Event.END_ARRAY;
                            contextStack.pop();
                            halfOctetLastTermination = false;

            } else {
                if (fixedTypeArray != null) {
                    if (posInFixedTypeArray < fixedTypeArray.size()) {
                        nextValue = fixedTypeArray.get(posInFixedTypeArray++);
                        switch (typeFixedTypeArray) {
                            case BOOLEAN:
                                nextState = nextValue.equals(JsonValue.TRUE)?Event.VALUE_TRUE:Event.VALUE_FALSE;
                                break;
                            case NULL:
                                nextState = Event.VALUE_NULL;
                                break;
                            default:
                                nextState = Event.VALUE_NUMBER;
                        }

                    } else {
                        nextState = Event.END_ARRAY;
                        fixedTypeArray = null;
                        contextStack.pop();
                    }
                    
                } else {
                    readOctet();
                    if ((current_octet >> 4) == FjsonConstants.TERMINATION_PATTERN ) {
                        nextState = contextStack.currentContextIsObject()?Event.END_OBJECT:Event.END_ARRAY;
                        contextStack.pop();
                        halfOctetLastTermination = true;
                    } else {
                        if (contextStack.currentContextIsObject() && (!nextState.equals(Event.KEY_NAME))) {
                                // leer key
                                String key = decodeIdentifyingStringOrIndex(vocabulary.keys);
                                nextState = Event.KEY_NAME;
                                nextValue = new JsonString_Impl(key);
                        } else {
                            // leer value
                            parseJsonValue();
                            if (nextState.equals(Event.START_ARRAY) &&
                                ((current_octet & FjsonConstants.TYPED_ARRAY_FLAG) != 0)) {
                                halfOctetLastTermination = false;
                                typeFixedTypeArray = JsonArrayOfFixedLength.ArrayType.values()[current_octet & 0xF];
                                fixedTypeArray = decodeArrayOfFixedLength(typeFixedTypeArray);
                                posInFixedTypeArray = 0;
                            }
                        }
                    }
                }
            }
            
            return currentState;
        }  catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        } catch (FjsonException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
        
    }
    void parseJsonValue() throws FjsonException, IOException {
        
        
        if ((current_octet & FjsonConstants.STRING_IDENTIFICATION_MASK) == FjsonConstants.STRING_IDENTIFICATION) {
            nextValue = decodeJsonString();
            nextState = Event.VALUE_STRING;
        } else if ((current_octet & FjsonConstants.NUMBER_IDENTIFICATION_MASK) == FjsonConstants.NUMBER_IDENTIFICATION) {
            nextValue = decodeJsonNumber();
            nextState = Event.VALUE_NUMBER;
        } else if ((current_octet & FjsonConstants.OBJECT_IDENTIFICATION_MASK) == FjsonConstants.OBJECT_IDENTIFICATION) {
            nextValue = null;
            nextState = Event.START_OBJECT;
            contextStack.push(true);
        } else if ((current_octet & FjsonConstants.ARRAY_IDENTIFICATION_MASK) == FjsonConstants.ARRAY_IDENTIFICATION) {
            nextValue = null;
            nextState = Event.START_ARRAY;
            contextStack.push(false);
        } else if ((current_octet & FjsonConstants.BOOLEAN_IDENTIFICATION_MASK) == FjsonConstants.BOOLEAN_IDENTIFICATION) {
            nextValue = decodeBoolean();
            nextState = nextValue.equals(JsonValue.TRUE)?Event.VALUE_TRUE:Event.VALUE_FALSE;
        } else if ((current_octet & FjsonConstants.NULL_IDENTIFICATION_MASK) == FjsonConstants.NULL_IDENTIFICATION) {
            nextValue = decodeNull();
            nextState = Event.VALUE_NULL;
        } else 
            throw new JsonParsingException("Unknown type", jsonLocation);
    }
    @Override
    public String getString() {
        switch (currentState) {
            case KEY_NAME: case VALUE_STRING: case VALUE_NUMBER:
                return currentValue.toString();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public boolean isIntegralNumber() {
        if (Event.VALUE_NUMBER.equals(currentState))
            return ((JsonNumber)currentValue).isIntegral();
        throw new IllegalStateException();
    }

    @Override
    public int getInt() {
        if (Event.VALUE_NUMBER.equals(currentState))
            return ((JsonNumber)currentValue).intValue();
        throw new IllegalStateException();
    }

    @Override
    public long getLong() {
        if (Event.VALUE_NUMBER.equals(currentState))
            return ((JsonNumber)currentValue).longValue();
        throw new IllegalStateException();
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (Event.VALUE_NUMBER.equals(currentState))
            return ((JsonNumber)currentValue).bigDecimalValue();
        throw new IllegalStateException();
    }

    @Override
    public JsonLocation getLocation() {
        return jsonLocation;
    }
    
    public boolean isArrayOfFixedLengthType() {
        if (Event.START_ARRAY.equals(currentState))
            return fixedTypeArray != null;
        throw new IllegalStateException();
    }
    public JsonArrayOfFixedLength.ArrayType getArrayTypeOfFixedLengthType() {
        if (isArrayOfFixedLengthType())
            return ((JsonArrayOfFixedLength)fixedTypeArray).getArrayType();
        throw new IllegalStateException();
    }
    public int getArrayOfFixedLengthTypeLength() {
        if (isArrayOfFixedLengthType())
            return fixedTypeArray.size();
        throw new IllegalStateException();
    }
    public JsonArrayOfFixedLength getArrayOfFixedLengthType() {
        if (isArrayOfFixedLengthType()) {
            nextState = Event.END_ARRAY;
            fixedTypeArray = null;
            contextStack.pop();
            return fixedTypeArray;
        }
        throw new IllegalStateException();
    }
    @Override
    public void close() {
        try {
            _in.close();
        } catch (IOException ex) {
            throw new JsonException(ex.getLocalizedMessage(),ex);
        }
    }
    
}
