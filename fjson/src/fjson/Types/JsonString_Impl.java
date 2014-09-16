//  Author: Ronald Pablos
//  Year: 2013

package fjson.Types;

import javax.json.JsonString;

/**
 * Implementation of {@link JsonString} interface.
 *
 * @author rpablos
 */
public class JsonString_Impl implements JsonString{
    protected String theString;

    public JsonString_Impl(String theString) {
        this.theString = theString;
    }
    
    @Override
    public String getString() {
        return theString;
    }

    @Override
    public CharSequence getChars() {
        return getString();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

    @Override
    public String toString() {
        return toJSONText(theString);
    }

    @Override
    public int hashCode() {
        return theString.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof JsonString)  && getString().equals(((JsonString)o).getString());
    }
    
    public static String toJSONText(String str) {
        StringBuilder sb = new StringBuilder(str.length()+2);
        sb.append('"');

        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isSurrogate(c) && c >= 0x20 && c != 0x22 && c != 0x5c && c != 0x2F) {
                sb.append(c);
            } else {
                sb.append('\\');
                switch (c) {
                    case '"':
                    case '\\':
                    case '/':
                        sb.append(c);
                        break;
                    case '\b':
                        sb.append('b');
                        break;
                    case '\f':
                        sb.append('f');
                        break;
                    case '\n':
                        sb.append('n');
                        break;
                    case '\r':
                        sb.append('r');
                        break;
                    case '\t':
                        sb.append('t');
                        break;
                    default:
                        String hex = "000" + Integer.toHexString(c);
                        sb.append("u").append(hex.substring(hex.length() - 4));
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
