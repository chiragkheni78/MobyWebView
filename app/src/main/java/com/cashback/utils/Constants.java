package com.cashback.utils;

import com.cashback.BuildConfig;

public class Constants {

    public static final int SPLASH_TIME = 3 * 1000;

    public static String BASE_URL = BuildConfig.BASE_URL;
    public static String IMAGE_BASE_URL = "https://mobyads.in/moby/";


    public class FragmentTag {
        public static final String TAG_OFFER_LIST = "offer_list";
        public static final String TAG_MAP_VIEW = "map_view";
    }

    public class IntentKey {
        public static final String OFFER_OBJECT = "offer_object";
        public static final String OFFER_ID = "offerId";
        public static final String LOCATION_ID = "locationId";
        public static final String CATEGORY_ID = "categoryId";
        public static final String REPORT_ID = "reportId";
        public static final String QR_DATA = "qrData";
        public static final String ACTIVITY_ID = "activityId";
        public static final String MESSAGE_ID = "messageId";
        public static final String ENGAGED_DATE = "engagedDate";
        public static final String PIN_COLOR = "pinColor";
    }

    public enum API
    {
        GET_GLOBAL_SETTING("getGlobalSetting"),
        SYNC_FB_TOKEN_TO_SERVER("syncTokenToServer"),
        GET_USER_PROFILE("getUserProfile"),
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
        UPLOAD_SHOP_ONLINE_BLINK("updateShopOnlineBlink")
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
