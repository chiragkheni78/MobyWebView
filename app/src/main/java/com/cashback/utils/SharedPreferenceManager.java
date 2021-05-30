package com.cashback.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    SharedPreferences moSharedPreferences;

    private static final String IS_LOGIN = "isLogin";
    private static final String IS_MARKETING_AD = "isMarketingAd";
    public static final String REFERRAL_LINK = "referralLink";
    public static final String REFERRAL_CODE = "referralCode";
    public static final String ADVERT_BANNER_POSITION = "advertBannerPosition";
    public static final String QUIZ_TIME_PERIOD = "quizTimePeriod";

    public static final String APP_DOWNLOAD_SOURCE = "app_download_source";
    public static final String APP_DOWNLOAD_MEDIUM = "app_download_medium";
    public static final String APP_DOWNLOAD_CAMPAIGN = "app_download_campaign";

    private static final String IS_FCM_SYNC = "is_fcm_sync";
    private static final String FCM_TOKEN = "fcm_token";

    public static final String MAP_ZOOM_LEVEL = "mapZoomLevel";

    public static final String OFFER_LIST_PAGE_SIZE = "offerListPageSize";

    private static final String TERMS_CONDITION = "terms_conditions";

    private static final String IS_BLINK_MOBY_ICON = "isBlinkMobyIcon";

    private static final String PHONE_ID = "phoneID";


    private static final String IS_PROFILE_SAVE = "is_profile_save";
    private static final String LANG_ID = "language_id";
    private static final String COUNTRY_ID = "country_id";
    private static final String USER_ID = "user_id";
    private static final String USER_ROLE = "user_role";
    private static final String OAUTH = "oauth";

    //consumer data
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String ADHAR_NUM = "aadhaar_num";
    private static final String STATE_ID = "state_id";
    private static final String STATE_NAME = "state_name";
    private static final String CITY_ID = "city_id";
    private static final String CITY_NAME = "city_name";
    private static final String USER_PROFILE_PIC = "profile_pic";


    public SharedPreferenceManager(Context foContext) {
        moSharedPreferences = android.preference.PreferenceManager
                .getDefaultSharedPreferences(foContext.getApplicationContext());
    }

    public void clear() {
        moSharedPreferences.edit().clear().commit();
    }


    public String getDynamicValue(String key) {
        return moSharedPreferences.getString(key, "");
    }

    public void setDynamicValue(String key, String value) {
        try {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(key, value);
            loEditor.commit();
        } catch (Exception e) {
        }
    }

    //START isLogIN
    public void setUserLogIn(boolean fbIsUserLogin) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putBoolean(IS_LOGIN, fbIsUserLogin);
            loEditor.commit();
        }
    }

    public boolean isUserLogin() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getBoolean(IS_LOGIN, false);
        }
        return false;
    }
    //END isLogIn

    //START REFERRAL_LINK
    public void setReferralLink(String fsReferralLink) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(REFERRAL_LINK, fsReferralLink);
            loEditor.commit();
        }
    }

    public String getReferralLink() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(REFERRAL_LINK, "");
        }
        return null;
    }
    //END REFERRAL_LINK

    // START REFERRAL_CODE
    public void setReferralCode(String fsReferralCode) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(REFERRAL_CODE, fsReferralCode);
            loEditor.commit();
        }
    }

    public String getReferralCode() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(REFERRAL_CODE, null);
        }
        return null;
    }
    //END REFERRAL_CODE

    //START APP_DOWNLOAD_CAMPAIGN
    public void setAppDownloadCampaign(String fsAppDownloadCampaign) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(APP_DOWNLOAD_CAMPAIGN, fsAppDownloadCampaign);
            loEditor.commit();
        }
    }

    public String getAppDownloadCampaign() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(APP_DOWNLOAD_CAMPAIGN, "");
        }
        return "";
    }
    //END APP_DOWNLOAD_CAMPAIGN

    //START APP_DOWNLOAD_MEDIUM
    public void setAppDownloadMedium(String fsAppDownloadMedium) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(APP_DOWNLOAD_MEDIUM, fsAppDownloadMedium);
            loEditor.commit();
        }
    }

    public String getAppDownloadMedium() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(APP_DOWNLOAD_MEDIUM, "");
        }
        return null;
    }
    //END APP_DOWNLOAD_MEDIUM

    //START APP_DOWNLOAD_SOURCE
    public void setAppDownloadSource(String fsAppDownloadSource) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(APP_DOWNLOAD_SOURCE, fsAppDownloadSource);
            loEditor.commit();
        }
    }

    public String getAppDownloadSource() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(APP_DOWNLOAD_SOURCE, "");
        }
        return null;
    }
    //END APP_DOWNLOAD_SOURCE

    public void setAdvertBannerPosition(int fiAdvertBannerPosition) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(ADVERT_BANNER_POSITION, fiAdvertBannerPosition);
            loEditor.commit();
        }
    }

    public int getAdvertBannerPosition() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(ADVERT_BANNER_POSITION, 0);
        }
        return 0;
    }

    //START isMarketingAd
    public void setMarketingAd(boolean fbIsMarketingAd) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putBoolean(IS_MARKETING_AD, fbIsMarketingAd);
            loEditor.commit();
        }
    }

    public boolean isMarketingAd() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getBoolean(IS_MARKETING_AD, false);
        }
        return false;
    }
    //END isMarketingAd


    //START QUIZ_TIME_PERIOD
    public void setQuizTimePeriod(int fiQuizTimePeriod) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(QUIZ_TIME_PERIOD, fiQuizTimePeriod);
            loEditor.commit();
        }
    }

    public int getQuizTimePeriod() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(QUIZ_TIME_PERIOD, 80);
        }
        return 0;
    }
    //END QUIZ_TIME_PERIOD


    //START MAP_ZOOM_LEVEL
    public void setMapZoomLevel(int fiZoomLevel) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(MAP_ZOOM_LEVEL, fiZoomLevel);
            loEditor.commit();
        }
    }

    public int getMapZoomLevel() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(MAP_ZOOM_LEVEL, 11);
        }
        return 11;
    }
    //END MAP_ZOOM_LEVEL

    //START OFFER_LIST_PAGE_SIZE
    public void setOfferListPageSize(int fiOfferListPageSize) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(OFFER_LIST_PAGE_SIZE, fiOfferListPageSize);
            loEditor.commit();
        }
    }

    public int getOfferListPageSize() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(OFFER_LIST_PAGE_SIZE, 100);
        }
        return 100;
    }
    //END OFFER_LIST_PAGE_SIZE

    //START IS_BLINK_MOBY_ICON
    public void setBlinkMobyIcon(boolean fbIsBlinkMobyIcon) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putBoolean(IS_BLINK_MOBY_ICON, fbIsBlinkMobyIcon);
            loEditor.commit();
        }
    }

    public boolean isBlinkMobyIcon() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getBoolean(IS_BLINK_MOBY_ICON, true);
        }
        return true;
    }
    //END IS_BLINK_MOBY_ICON


    private static final String IS_BANK_OFFER_MESSAGE_SHOWN = "isBankOfferMessageShown";

    //START IS_BANK_OFFER_MESSAGE_SHOWN
    public void setBankOfferMessageShown(boolean fbIsBankOfferMessageShown) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putBoolean(IS_BANK_OFFER_MESSAGE_SHOWN, fbIsBankOfferMessageShown);
            loEditor.commit();
        }
    }

    public boolean isBankOfferMessageShown() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getBoolean(IS_BANK_OFFER_MESSAGE_SHOWN, false);
        }
        return false;
    }
    //END IS_BANK_OFFER_MESSAGE_SHOWN


    //START PHONE_ID
    public void setPhoneID(String fsPhoneID) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(PHONE_ID, fsPhoneID);
            loEditor.commit();
        }
    }

    public String getPhoneID() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(PHONE_ID, "");
        }
        return "";
    }
    //END PHONE_ID

//=====================================================



    //START TERMS_CONDITION
    public void setTermsAccepted(boolean fbIsAcceptedTC) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putBoolean(TERMS_CONDITION, fbIsAcceptedTC);
            loEditor.commit();
        }
    }

    public boolean isTermsAccepted() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getBoolean(TERMS_CONDITION, false);
        }
        return false;
    }
    //END TERMS_CONDITION


    //START IS_PROFILE_SAVE
    public void setProfileSave(boolean fbIsProfileSave) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putBoolean(IS_PROFILE_SAVE, fbIsProfileSave);
            loEditor.commit();
        }
    }

    public boolean isProfileSave() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getBoolean(IS_PROFILE_SAVE, false);
        }
        return false;
    }
    //END IS_PROFILE_SAVE

    //START LANG_ID
    public void setLanguageId(int fiLangId) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(LANG_ID, fiLangId);
            loEditor.commit();
        }
    }

    public int getLanguageId() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(LANG_ID, 1);
        }
        return 1;
    }
    //END LANG_ID

    //START USER_ID
    public void setUserId(int fiuserId) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(USER_ID, fiuserId);
            loEditor.commit();
        }
    }

    public int getUserId() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(USER_ID, 0);
        }
        return 0;
    }
    //END USER_ID

    //START USER_ROLE
    public void setUserRole(int fiUserRole) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(USER_ROLE, fiUserRole);
            loEditor.commit();
        }
    }

    public int getUserRole() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(USER_ROLE, 2);
        }
        return 2;
    }
    //END USER_ROLE

    //START OAUTH
    public void setAuth(String fsOauth) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(OAUTH, fsOauth);
            loEditor.commit();
        }
    }

    public String getAuth() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(OAUTH, "");
        }
        return "";
    }
    //END OAUTH

    //START COUNTRY_ID
    public void setCountryId(int fiId) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(COUNTRY_ID, fiId);
            loEditor.commit();
        }
    }

    public int getCountryId() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(COUNTRY_ID, 105);
        }
        return 105;
    }
    //END COUNTRY_ID

    //START FIRST_NAME
    public void setFirstName(String fsFirstName) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(FIRST_NAME, fsFirstName);
            loEditor.commit();
        }
    }

    public String getFirstName() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(FIRST_NAME, "");
        }
        return "";
    }
    //END FIRST_NAME

    //START LAST_NAME
    public void setLastName(String fsLastName) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(LAST_NAME, fsLastName);
            loEditor.commit();
        }
    }

    public String getLastName() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(LAST_NAME, "");
        }
        return "";
    }
    //END LAST_NAME


    //START EMAIL
    public void setEmail(String fsEmail) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(EMAIL, fsEmail);
            loEditor.commit();
        }
    }

    public String getEmail() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(EMAIL, "");
        }
        return "";
    }
    //END EMAIL


    //START PHONE
    public void setPhone(String fsPhone) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(PHONE, fsPhone);
            loEditor.commit();
        }
    }

    public String getPhone() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(PHONE, "");
        }
        return "";
    }
    //END PHONE

    //START ADHAR_NUM
    public void setAadhaar(String fsAadhaar) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(ADHAR_NUM, fsAadhaar);
            loEditor.commit();
        }
    }

    public String getAadhaar() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(ADHAR_NUM, "");
        }
        return "";
    }
    //END ADHAR_NUM

    //START STATE_ID
    public void setStateId(int fiStateId) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(STATE_ID, fiStateId);
            loEditor.commit();
        }
    }

    public int getStateId() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(STATE_ID, 0);
        }
        return 0;
    }
    //END STATE_ID

    //START STATE_NAME
    public void setStateName(String fsState) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(STATE_NAME, fsState);
            loEditor.commit();
        }
    }

    public String getStateName() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(STATE_NAME, "");
        }
        return "";
    }
    //END STATE_NAME

    //START CITY_ID
    public void setCityId(int fiCityId) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putInt(CITY_ID, fiCityId);
            loEditor.commit();
        }
    }

    public int getCityId() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getInt(CITY_ID, 0);
        }
        return 0;
    }
    //END CITY_ID

    //START CITY_NAME
    public void setCityName(String fsCity) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(CITY_NAME, fsCity);
            loEditor.commit();
        }
    }

    public String getCityName() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(CITY_NAME, "");
        }
        return "";
    }
    //END CITY_NAME

    //START USER_PROFILE_PIC
    public void setUserProfilePic(String fsUserProfilePic) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(USER_PROFILE_PIC, fsUserProfilePic);
            loEditor.commit();
        }
    }

    public String getUserProfilePic() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(USER_PROFILE_PIC, "http://pronksiapartments.ee/wp-content/uploads/2015/10/placeholder-face-big.png");
        }
        return "http://pronksiapartments.ee/wp-content/uploads/2015/10/placeholder-face-big.png";
    }
    //END USER_PROFILE_PIC


    //START IS_FCM_SYNC
    public void setFcmTokenSynch(boolean fbIsSync) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putBoolean(IS_FCM_SYNC, fbIsSync);
            loEditor.commit();
        }
    }

    public boolean isFcmTokenSynch() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getBoolean(IS_FCM_SYNC, false);
        }
        return false;
    }
    //END IS_FCM_SYNC


    //START FCM_TOKEN
    public void setFcmToken(String fsFcmToken) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(FCM_TOKEN, fsFcmToken);
            loEditor.commit();
        }
    }

    public String getFcmToken() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(FCM_TOKEN, "");
        }
        return "";
    }
    //END FCM_TOKEN

}
