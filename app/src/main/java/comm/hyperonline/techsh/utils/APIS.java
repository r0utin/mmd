package comm.hyperonline.techsh.utils;

import com.ciyashop.library.apicall.URLS;

public class APIS {

    public static final String CUSTOM_API_KEY = "adsaaaaaaaaaaaaaadadadlakdlakflakslfwefowefoweuyrtadagvbbbcvzznczczbnmnbvdadddddadgsdhgsdgahgdhqjgdjqhdjaddccccccbn";

    public static final String APP_URL = "http://mashhadmtech.ir/";
    public static final String WOO_MAIN_URL = APP_URL + "wp-json/wc/v2/";
    public static final String MAIN_URL = APP_URL + "wp-json/pgs-woo-api/v1/";

    public static final String CONSUMERKEY = "SoENBsS5Jo8s";
    public static final String CONSUMERSECRET = "kGoGTtJ6wW4VSE5ILfSA4qUl922QGwhcfrnS1DwXRo5PsFod";
    public static final String OAUTH_TOKEN = "zE2Dnco7pTICRW1Y57IvqEKa";
    public static final String OAUTH_TOKEN_SECRET = "UvZ5KvIY2Xm7LPzOywpYOxX0KxsvVS3wKf64IWDzI1GPJExC";

    public static final String WOOCONSUMERKEY = "ck_09a20d2362bb2ecbedb169705326af1d6bb559e6";
    public static final String WOOCONSUMERSECRET = "cs_0978fa61348935121c4423e2401196d8266410df";
    public static final String version="3.5";


    public static final String HOME_BANNER_IMAGE = "https://androidsrc.ir/wp-content/uploads/2021/05/bndigi.png";
    public static final String HOME_BANNER_LINK = "https://androidsrc.ir/product/woocommerce-wordpress-app-2020-4-level/";

    

    public APIS() {
        URLS.APP_URL = APP_URL;
        URLS.NATIVE_API = APP_URL + "wp-json/wc/v3/";
        URLS.WOO_MAIN_URL = WOO_MAIN_URL;
        URLS.MAIN_URL = MAIN_URL;
        URLS.version = version;
        URLS.CONSUMERKEY = CONSUMERKEY;
        URLS.CONSUMERSECRET = CONSUMERSECRET;
        URLS.OAUTH_TOKEN = OAUTH_TOKEN;
        URLS.OAUTH_TOKEN_SECRET = OAUTH_TOKEN_SECRET;
        URLS.WOOCONSUMERKEY = WOOCONSUMERKEY;
        URLS.WOOCONSUMERSECRET = WOOCONSUMERSECRET;
    }
}