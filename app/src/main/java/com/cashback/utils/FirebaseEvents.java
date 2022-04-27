package com.cashback.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.cashback.AppGlobal;
import com.facebook.appevents.AppEventsLogger;

public class FirebaseEvents {

    public static String SAVE_PROFILE = "save_profile";

    public static String OFFER_LOADED_PAGE = "offer_loaded";
    public static String SELECT_DEAL_CLICKED = "select_deal";

    public static String SHOP_ONLINE_CLICKED = "shop_now";
    public static String GET_OFFER_CLICKED = "get_offer";
    public static String CASHBACK_ACTIVATE_OK_CLICKED = "cashback_activate_ok";
    public static String SHOP_ONLINE_BACK_PRESS = "shop_online_back_press";

    public static String VIEW_AND_SHOP_CLICKED = "view_and_shop";
    public static String REGISTER_BILL_CLICKED = "register_bill";

    public static String BILL_UPLOAD_SUCCEED = "bill_upload_succeed";

    public static String SHARE_OPTION = "share_option";

    public static String PICTURE_PRESS = "picture_press";
    public static String HELP_OPTION = "help_option";

    public static String OTP_VERIFIED_ALL = "otp_verified_all";
    public static String USER_PROFILE_VERIFIED = "user_profile_otp_verified";
    public static String SHOP_ONLINE_VERIFIED = "shop_online_otp_verified";
    public static String NEAR_BY_OTP_VERIFIED = "near_by_otp_verified";
    public static String SHARE_APP_OTP_VERIFIED = "share_app_otp_verified";

    public static String MESSAGE_TRACKING = "message_tracking";
    public static String MY_COUPON_PAGE = "coupon_page_loaded";
    public static String DOWNLOAD_USING_REFERRAL_CODE = "download_using_referral_code";
    public static String SUBMIT_QUIZ = "submit_quiz";

    public static String OPEN_WHATSAPP_SHARE = "open_whatsapp_share";
    public static String OPEN_FB_SHARE = "open_FB_for_share";
    public static String OPEN_MESSENGER_SHARE = "open_messenger_share";
    public static String OPEN_INSTAGRAM_SHARE = "open_instagram_share";
    public static String OPEN_SMS_SHARE = "open_SMS_share";
    public static String OPEN_EMAIL_SHARE = "open_email_share";
    public static String OPEN_TWITTER_SHARE = "open_twitter_share";
    public static String OPEN_TELEGRAM_SHARE = "open_telegram_share";
    public static String OPEN_COPY_TEXT_SHARE = "open_copy_text_share";

    public static String BILL_TRACKED = "bill_tracked";


    public static void trigger(Context context, Bundle bundle, String eventName) {
        eventName = "AND_" + eventName;

        if (bundle == null)
            bundle = new Bundle();

        bundle.putString("mobile", AppGlobal.getPhoneNumber());

        // TODO: 27-04-2022 payal
       // AppGlobal.getFirebaseAnalytics().logEvent(eventName, bundle);
        //triggerFacebookEvent(context, eventName, bundle);
    }

    private static void triggerFacebookEvent(Context context, String eventName, Bundle bundle) {
        try {
            AppEventsLogger logger = AppEventsLogger.newLogger(context);
            logger.logEvent(eventName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
