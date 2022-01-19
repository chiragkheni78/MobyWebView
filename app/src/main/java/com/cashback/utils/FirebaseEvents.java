package com.cashback.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.cashback.AppGlobal;
import com.facebook.appevents.AppEventsLogger;

public class FirebaseEvents {

    public static String REGISTER = "register";
    public static String MAIN_OFFER_LOADED = "main_offer_loaded";
    public static String PICTURE_PRESS = "picture_press";
    public static String HELP_OPTION = "help_option";
    public static String SHARE_OPTION = "share_option";
    public static String SHOP_ONLINE = "shop_online";
    public static String USER_PROFILE_VERIFIED = "user_profile_verified";
    public static String SHOP_ONLINE_VERIFIED = "shop_online_verified";
    public static String PHONE_VERIFIED_FOR_NEAR_ADS = "phone_verified_for_nearAds";
    public static String MESSAGE_TRACKING = "message_tracking";
    public static String MY_COUPON_PAGE = "coupon_page_Load";
    public static String DOWNLOAD_USING_REFERRAL_CODE = "download_using_referral_code";
    public static String SUBMIT_QUIZ = "submit_quiz";
    public static String GET_OFFER = "get_offer";
    public static String SELECT_DEAL = "select_deal_press";
    public static String SHOP_ONLINE_BACK_PRESS = "shop_online_backpress";
    public static String OPEN_WHATSAPP_SHARE = "open_whatsapp_share";
    public static String OPEN_FB_SHARE = "open_FB_for_share";
    public static String OPEN_MESSENGER_SHARE = "open_messenger_share";
    public static String OPEN_INSTAGRAM_SHARE = "open_instagram_share";
    public static String OPEN_SMS_SHARE = "open_SMS_share";
    public static String OPEN_EMAIL_SHARE = "open_email_share";
    public static String OPEN_TWITTER_SHARE = "open_twitter_share";
    public static String OPEN_TELEGRAM_SHARE = "open_telegram_share";
    public static String OPEN_COPY_TEXT_SHARE = "open_copy_text_share";
    public static String PHONE_VERIFIED_MY_CASH = "phone_verified_for_mycash";
    public static String BILL_TRACKED = "bill_tracked";

    public static void FirebaseEvent(Context context, Bundle bundle, String eventName) {
        AppGlobal.getFirebaseAnalytics().logEvent(eventName, bundle);

        triggerFacebookEvent(context, eventName);
    }

    private static void triggerFacebookEvent(Context context, String eventName) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent(eventName);
    }
}
