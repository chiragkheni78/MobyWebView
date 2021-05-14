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

    public enum API
    {
        GET_USER_PROFILE("getUserProfile"),
        SAVE_MINI_PROFILE("saveMiniProfile"),
        GET_OFFER_FILTER("getOfferFilter"),
        GET_OFFER_LIST("getOfferList");

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
        MAP_VIEW(2),  OFFER_LIST(4);

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
