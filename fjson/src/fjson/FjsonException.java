//  Author: Ronald Pablos
//  Year: 2013


package fjson;

/**
 *
 * @author rpablos
 */
public class FjsonException extends Exception {

    public FjsonException() {
    }

    public FjsonException(String message) {
        super(message);
    }

    public FjsonException(Throwable cause) {
        super(cause);
    }

    public FjsonException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
