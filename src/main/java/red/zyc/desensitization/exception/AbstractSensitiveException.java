package red.zyc.desensitization.exception;

/**
 * @author zyc
 */
public abstract class AbstractSensitiveException extends RuntimeException {

    public AbstractSensitiveException(String message) {
        super(message);
    }
}
