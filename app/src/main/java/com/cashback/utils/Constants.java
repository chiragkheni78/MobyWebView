package com.cashback.utils;

import com.cashback.BuildConfig;

public class Constants {

    public static final int SPLASH_TIME = 3 * 1000;

    public static String BASE_URL = BuildConfig.BASE_URL;
    public static String ADGYDE_APP_KEY = BuildConfig.ADGYDE_APP_KEY;

    public static String IMAGE_BASE_URL = "https://mobyads.in/moby/";
    public static String DEFAULT_REFERRAL_CODE = "SAMSUNG"; //SAMSUNG


    public class FragmentTag {
        public static final String TAG_MY_OFFER_LIST = "my_offer_list";
        public static final String TAG_OFFER_LIST = "offer_list";
        public static final String TAG_MAP_VIEW = "map_view";
    }

    public class IntentKey {
        public static final String OFFER_OBJECT = "offer_object";
        public static final String OFFER_ID = "offerId";
        public static final String LOCATION_ID = "locationId";
        public static final String CATEGORY_ID = "categoryId";
        public static final String BANNER_ID = "bannerId";
        public static final String REPORT_ID = "reportId";
        public static final String QR_DATA = "qrData";
        public static final String ACTIVITY_ID = "activityId";
        public static final String MESSAGE_ID = "messageId";
        public static final String ENGAGED_DATE = "engagedDate";
        public static final String PIN_COLOR = "pinColor";
        public static final String ADVERT_SCREEN_TYPE = "advertScreenType";
        public static final String SCREEN_TITLE = "screenTitle";
        public static final String WEBVIEW_PAGE_NAME = "webviewURL";
        public static final String VIDEO_URL = "videoURL";
        public static final String IS_FROM = "isFrom";
        public static final String adId = "adId";
        public static final String FROM_COUPON = "fromCoupon";


        public class Action {
            public static final String MAP_SCREEN = "MAP_SCREEN";
            public static final String OFFER_LIST = "OFFER_LIST";
            public static final String ACTIVITY_LIST = "ACTIVITY_LIST";
            public static final String MESSAGE_LIST = "MESSAGE_LIST";
            public static final String WALLET_SCREEN = "WALLET_SCREEN";


            public static final String OPEN_BILL_UPLOAD = "OPEN_BILL_UPLOAD";
            public static final String CLICK_SHOP_ONLINE = "CLICK_SHOP_ONLINE";
        }
    }

    public enum API
    {
        GET_ALL_STATIC_LABEL("getAllStaticLabels"),
        GET_GLOBAL_SETTING("getGlobalSetting"),
        SYNC_FB_TOKEN_TO_SERVER("syncTokenToServer"),
        GET_MINI_PROFILE("getUserProfile"),
        SAVE_MINI_PROFILE("saveMiniProfile"),
        GET_OFFER_FILTER("getOfferFilter"),
        GET_OFFER_LIST("getOfferList"),
        GET_OFFER_DETAILS("getOfferDetails"),
        GET_QUIZ_DETAILS("getQuizDetails"),
        SUBMIT_QUIZ_ANSWER("submitQuiz"),
        GET_ACTIVITY_LIST("getActivityList"),
        GET_ACTIVITY_DETAILS("getActivityDetails"),
        COUPON_MARK_AS_USED("couponMarkAsUsed"),
        UPLOAD_TRANSACTION_BILL("uploadTrasactionBill"),
        UPLOAD_SHOP_ONLINE_BLINK("updateShopOnlineBlink"),
        GET_MESSAGE_LIST("getMessages"),
        UPDATE_MESSAGE_AS_READ("updateMessageAsRead"),
        CHECK_CONNECTED_DEVICE("checkMultipleDevicesWithMobile"),
        PROCEED_DEVICE("proceedWithDevice"),
        GET_USER_TRANSACTION("getUserTransaction"),
        GET_ADVERT_IMAGES("getAdvertisementImage"),
        GET_LOAD_WEBVIEW("loadWebView"),
        GET_LOAD_WEBVIEW_FAQ("faqs"),
        LOAD_WEB_VIEW_DATA("loadWebView"),
        GET_USER_DETAILS("getUserDetails"),
        SAVE_USER_DETAILS("saveFullProfile"),
        DELETE_USER_CARD("deleteUserCard"),
        ;



        private String type;

        API (String type)
        {
            this.type = type;
        }

        public String getValue()
        {
            return type;
        }
    }

    public enum AnswerType
    {
        BARCODE("barCode"),
        YOUTUBE_VIDEO("youtubeVideo"),
        MULTI_CHOICE("multiChoice"),
        TEXABLE("texable"),
        CAMPAIGN("campaign")
        ;

        private String type;

        AnswerType (String type)
        {
            this.type = type;
        }

        public String getValue()
        {
            return type;
        }
    }

    public enum Gender
    {
        MALE("male"),  FEMALE("female");

        private String type;

        Gender (String type)
        {
            this.type = type;
        }

        public String getValue()
        {
            return type;
        }
    }

    public enum PinColor
    {
        RED("red"),  GREEN("green"), YELLOW("yellow");

        private String type;

        PinColor (String type)
        {
            this.type = type;
        }

        public String getValue()
        {
            return type;
        }
    }

    public enum AdType
    {
        BANK_OFFER("Bank Offer"),  RETAIL("Retail");

        private String type;

        AdType (String type)
        {
            this.type = type;
        }

        public String getValue()
        {
            return type;
        }
    }

    public enum AdvertScreenType
    {
        HELP_SCREEN("help-screen"),
        ADVERTISEMENT_SCREEN("advertiesment-popup"),
        ONGOING_DEALS("ongoing-deals"),
        TRANSACTION_SCREEN("trasaction-screen"),
        PROFILE_SCREEN("profile-screen")
        ;

        private String type;

        AdvertScreenType (String type)
        {
            this.type = type;
        }

        public String getValue()
        {
            return type;
        }
    }

    public enum WebViewPage
    {
        TERMS_CONDITION("tandc"),
        ;

        private String type;

        WebViewPage (String type)
        {
            this.type = type;
        }

        public String getValue()
        {
            return type;
        }
    }

    public enum OfferPage
    {
        MAP_VIEW(2),  OFFER_LIST(1);

        private int type;

        OfferPage (int type)
        {
            this.type = type;
        }

        public int getValue()
        {
            return type;
        }
    }

}
