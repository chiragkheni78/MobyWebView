package com.cashback.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.adgyde.android.AdGyde;
import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.activities.HomeActivity;
import com.cashback.models.request.SyncTokenRequest;
import com.cashback.models.response.SyncTokenResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.FirebaseEvents;
import com.cashback.utils.LogV2;
import com.cashback.utils.SharedPreferenceManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cashback.utils.Constants.IntentKey.Action.ACTIVITY_LIST;
import static com.cashback.utils.Constants.IntentKey.Action.MESSAGE_LIST;
import static com.cashback.utils.Constants.IntentKey.Action.OFFER_LIST;
import static com.cashback.utils.Constants.IntentKey.Action.WALLET_SCREEN;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    public class NotificationType {
        public static final int OFFER_LIST = 0;
        public static final int MESSAGE_LIST = 1;
        public static final int ACTIVITY_LIST = 2;
        public static final int PURCHASED = 21;
        public static final int BILL_VERIFIED = 22;
        public static final int CASH_BACK_PAID = 23;
        public static final int COUPON_EXPIRING = 20;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(this);
            if (loSharedPreferenceManager.isUserLogin()) {
                handleNotification(remoteMessage.getData());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void handleNotification(Map<String, String> foData) {

        try {
            String loBody = foData.get("body");
            Log.d(TAG, loBody);

            Intent loIntent = null;
            PendingIntent loPendingIntent = null;

            JSONObject loJsonBody = new JSONObject(loBody);
            int liNotifyID = 0;
            String lsTitle = loJsonBody.getString("title");
            String lsMessage = loJsonBody.getString("message");
            long llAdID = 0, llLocationID = 0, llActivityID = 0, llMessageID = 0;
            int liCategoryId = 0;

            if (loJsonBody.has("fbIsTrack")) {
                boolean fbIsTrack = loJsonBody.getBoolean("fbIsTrack");
                if (fbIsTrack) {
                    try {
                        AdGydeEvents.billTracked(getApplicationContext());
                        Bundle bundle = new Bundle();
                        bundle.putString("mobile", AppGlobal.getPhoneNumber());
                        FirebaseEvents.trigger(getApplicationContext(), bundle, FirebaseEvents.BILL_TRACKED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (loJsonBody.has("type"))
                liNotifyID = loJsonBody.getInt("type");

            if (loJsonBody.has("ad_id"))
                llAdID = loJsonBody.getLong("ad_id");

            if (loJsonBody.has("location_id"))
                llLocationID = loJsonBody.getLong("location_id");

            if (loJsonBody.has("category_id"))
                liCategoryId = loJsonBody.getInt("category_id");

            if (loJsonBody.has("activity_id"))
                llActivityID = loJsonBody.getInt("activity_id");

            if (loJsonBody.has("message_id"))
                llMessageID = loJsonBody.getInt("message_id");


            switch (liNotifyID) {
                case NotificationType.OFFER_LIST:
                    loIntent = new Intent(this, HomeActivity.class);
                    loIntent.setAction(OFFER_LIST);
                    loIntent.putExtra(Constants.IntentKey.CATEGORY_ID, liCategoryId);
                    loIntent.putExtra(Constants.IntentKey.OFFER_ID, llAdID);
                    loIntent.putExtra(Constants.IntentKey.LOCATION_ID, llLocationID);
                    loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, loIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    break;

                case NotificationType.ACTIVITY_LIST:
                case NotificationType.COUPON_EXPIRING:
                    loIntent = new Intent(this, HomeActivity.class);
                    loIntent.setAction(ACTIVITY_LIST);
                    loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, llActivityID);
                    loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, loIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    break;

                case NotificationType.PURCHASED:
                case NotificationType.BILL_VERIFIED:
                case NotificationType.CASH_BACK_PAID:

//                    if (liNotifyID == NotificationType.PURCHASED) {
//                        AdGydeEvents.purchased(this, llAdID);
//                    }
                    loIntent = new Intent(this, HomeActivity.class);
                    loIntent.setAction(WALLET_SCREEN);
                    loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, llActivityID);
                    loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, loIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    break;

                case NotificationType.MESSAGE_LIST:
                    loIntent = new Intent(this, HomeActivity.class);
                    loIntent.setAction(MESSAGE_LIST);
                    loIntent.putExtra(Constants.IntentKey.MESSAGE_ID, llMessageID);
                    loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, loIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    break;

                default:
                    loIntent = new Intent(this, HomeActivity.class);
                    loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, loIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    break;
            }

            sendNotification(lsTitle, lsMessage, loPendingIntent);
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (!TextUtils.isEmpty(token)) {
            AdGyde.onTokenRefresh(token);
//            AppsFlyerLib.getInstance().updateServerUninstallToken(getApplicationContext(), token);
            // Instance ID token to your app server.
            sendRegistrationToServer(token);
        }
    }

    private void sendNotification(String fsTitle, String fsMessage, PendingIntent foPendingIntent) {

        String lsChannelId = getString(R.string.default_notification_channel_id);
        String lsChannel = "default-channel";
        Uri loDefaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder loNotificationBuilder =
                new NotificationCompat.Builder(this, lsChannelId)
                        .setSmallIcon(R.drawable.ic_notification_small)
                        .setContentTitle(fsTitle)
                        .setContentText(fsMessage)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(fsMessage))
                        .setAutoCancel(true)
                        .setSound(loDefaultSoundUri)
                        .setContentIntent(foPendingIntent);

        NotificationManager loNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel loChannel = new NotificationChannel(lsChannelId, lsChannel, NotificationManager.IMPORTANCE_DEFAULT);
            loNotificationManager.createNotificationChannel(loChannel);
        }
        int oneTimeID = (int) SystemClock.uptimeMillis();
        loNotificationManager.notify(oneTimeID /* ID of notification */, loNotificationBuilder.build());
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferenceManager moSharedPreferenceManager = new SharedPreferenceManager(this);
        moSharedPreferenceManager.setFcmToken(token);
        moSharedPreferenceManager.setFcmTokenSynch(false);
        // server call
        syncTokenToServer(this);
    }

    public static void syncTokenToServer(Context foContext) {

        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(foContext);
        String lsToken = loSharedPreferenceManager.getFcmToken();

        SyncTokenRequest loRequestObject = new SyncTokenRequest();
        loRequestObject.setAction(Constants.API.SYNC_FB_TOKEN_TO_SERVER.getValue());
        loRequestObject.setDeviceId(Common.getDeviceUniqueId(foContext));
        loRequestObject.setMobileNumber(AppGlobal.getPhoneNumber());
        loRequestObject.setFcmToken(lsToken);

        if (loSharedPreferenceManager.isUserLogin()) {

            Common.printReqRes(loRequestObject, "syncTokenToServer", Common.LogType.REQUEST);

            Call<SyncTokenResponse> loRequest = APIClient.getInterface().syncTokenToServer(loRequestObject);

            loRequest.enqueue(new Callback<SyncTokenResponse>() {
                @Override
                public void onResponse(Call<SyncTokenResponse> call, Response<SyncTokenResponse> foResponse) {

                    if (foResponse.isSuccessful()) {
                        Common.printReqRes(foResponse.body(), "syncTokenToServer", Common.LogType.RESPONSE);
                        SyncTokenResponse loJsonObject = foResponse.body();
                        if (!loJsonObject.isError()) {
                            loSharedPreferenceManager.setFcmTokenSynch(true);
                        }
                    } else {
                        String fsMessage = Common.getErrorMessage(foResponse);
                        LogV2.i(TAG, "syncTokenToServer-Error: " + fsMessage);
                    }
                }

                @Override
                public void onFailure(Call<SyncTokenResponse> call, Throwable t) {
                    Common.printReqRes(t, "syncTokenToServer", Common.LogType.ERROR);
                }
            });
        }
    }
}
