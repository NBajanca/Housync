package pt.nb_web.housync.utils;

/**
 * Created by Nuno on 23/02/2016.
 */
public class Commons {


    public static boolean DEBUG = true;

    public static final int NO_EXTRA = -404;

    public static final int ONLINE_UPDATE = 101;
    public static final int LOCAL_UPDATE = 102;

    public static final int NO_INTERNET = -101;
    public static final int NOT_SIGNED_IN = -102;
    public static final int USER_NOT_FOUND = -103;
    public static final int HOUSE_NOT_FOUND = -104;
    public static final int ERROR_IN_API = -105;

    public static final String HOUSE_LOCAL_ID_PARAMETER = "house_local_id";

    public static final String HOUSE_DETAILS_ACTIVIY_PARAMETER = "house_details_activity";
    public static final int HOUSE_DETAILS_ACTIVIY_REQUEST = 2000;
    public static final int HOUSE_DETAILS_ACTIVIY_RESULT_DELETE = 2001;

    public static final String HOUSE_ADD_ACTIVIY_PARAMETER = "house_add_activity";
    public static final int HOUSE_ADD_ACTIVIY_REQUEST = 3000;
    public static final int HOUSE_ADD_ACTIVIY_RESULT_ADD = 3001;

    public static final String HOUSE_EDIT_ACTIVIY_PARAMETER = "house_edit_activity";
    public static final int HOUSE_EDIT_ACTIVIY_REQUEST = 4000;
    public static final int HOUSE_EDIT_ACTIVIY_RESULT_DELETE = 4001;
    public static final int HOUSE_EDIT_ACTIVIY_RESULT_EDIT = 4002;


}
