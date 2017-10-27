package constants;

/**
 * Created by chirag on 24/8/17.
 */
public class ErrorConstant {

    private ErrorConstant() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SESSION_USERNAME_ERROR = "Couldn't get user name from the session";
    public static final String CANT_ADAPT_USER_ERROR = "User cannot be adapted to ";
    public static final String NO_SUCH_USER_IN_ZOOM = "No such user found within zoom: ";
}
