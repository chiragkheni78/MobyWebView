package com.cashback.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.activities.HomeActivity;
import com.cashback.models.request.SyncTokenRequest;
import com.cashback.models.response.SyncTokenResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;
import com.cashback.utils.SharedPreferenceManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    public class NotificationType {
        public static final int OFFER_LIST = 1;
        public static final int ACTIVITY_LIST = 3;
        public static final int MESSAGE_LIST = 5;
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
            if (loSharedPreferenceManager.getUserId() != 0) {
                handleNotification(remoteMessage.getData());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void handleNotification(Map<String, String> foData) {

        String loBody = foData.get("body");
        Log.d(TAG, loBody);

        Intent loIntent = null;
        PendingIntent loPendingIntent = null;

        try {
            JSONObject loJsonBody = new JSONObject(loBody);
            int liNotifyID = 0;
            String lsTitle = loJsonBody.getString("title");
            String lsMessage = loJsonBody.getString("message");
            long llAdID = 0, llLocationID = 0, llActivityID = 0, llMessageID = 0;
            int liCategoryId = 0;

            if (loJsonBody.has("type"))
                liNotifyID = loJsonBody.getInt("type");

            if (loJsonBody.has("ad_id"))
                llAdID = loJsonBody.getLong("ad_id");

            if (loJsonBody.has("location_id"))
                llLocationID = loJsonBody.getLong("location_id");

            if (loJsonBody.has("categoryId"))
                liCategoryId = loJsonBody.getInt("categoryId");

            if (loJsonBody.has("activityId"))
                llActivityID = loJsonBody.getInt("activityId");

            if (loJsonBody.has("messageId"))
                llMessageID = loJsonBody.getInt("messageId");


            switch (liNotifyID) {
                case NotificationType.OFFER_LIST:

                    loIntent = new Intent(this, HomeActivity.class);
                    loIntent.setAction(String.valueOf(NotificationType.OFFER_LIST));
                    loIntent.putExtra(Constants.IntentKey.CATEGORY_ID, liCategoryId);
                    loIntent.putExtra(Constants.IntentKey.OFFER_ID, llAdID);
                    loIntent.putExtra(Constants.IntentKey.LOCATION_ID, llLocationID);
                    loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, loIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    break;

                case NotificationType.ACTIVITY_LIST:

                    loIntent = new Intent(this, HomeActivity.class);
                    loIntent.setAction(String.valueOf(NotificationType.ACTIVITY_LIST));
                    loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, llActivityID);
                    loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, loIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    break;

                case NotificationType.MESSAGE_LIST:

                    loIntent = new Intent(this, HomeActivity.class);
                    loIntent.setAction(String.valueOf(NotificationType.MESSAGE_LIST));
                    loIntent.putExtra(Constants.IntentKey.MESSAGE_ID, llMessageID);
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
        sendRegistrationToServer(token);
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