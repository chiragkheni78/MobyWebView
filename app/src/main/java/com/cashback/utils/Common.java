package com.cashback.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.BuildConfig;
import com.cashback.R;
import com.cashback.activities.ShortProfileActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

public class Common {

    private static final String TAG = Common.class.getSimpleName();

    public static String getDynamicText(Context foContext, String fsKey) {

        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(foContext);

        String lsValue = null;

        try {
            if (!loSharedPreferenceManager.getDynamicValue(fsKey).isEmpty()) {
                lsValue = loSharedPreferenceManager.getDynamicValue(fsKey).replace("&amp;", "&");
            } else {
                int liResID = getResId(fsKey, R.string.class);
                lsValue = foContext.getResources().getString(liResID);
            }
        } catch (Exception e) {
            int liResID = getResId(fsKey, R.string.class);
            lsValue = foContext.getResources().getString(liResID);
        } finally {
            return lsValue;
        }
    }

    public static void setDynamicText(Context foContext, JSONObject foJsonObject) {
        try {
            Iterator<String> iterator = foJsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                try {
                    String value = (String) foJsonObject.get(key);
                    SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(foContext);
                    sharedPreferenceManager.setDynamicValue(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static void showErrorDialog(final Context foContext, String lsMessage, boolean isFinish) {
        if (isFinish) {
            new AlertDialog.Builder(foContext).setMessage(lsMessage)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity) foContext).finish();
                        }
                    }).show();
        } else {
            new AlertDialog.Builder(foContext).setMessage(lsMessage)
                    .setPositiveButton("Ok", null).show();
        }
    }

    public static void showErrorDialog(Context foContext, String lsMessage, DialogInterface.OnClickListener foListener) {

        new AlertDialog.Builder(foContext).setMessage(lsMessage)
                .setPositiveButton("Ok", foListener).show();
    }


    public static int getLayoutManagerOrientation(int activityOrientation) {
        if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return LinearLayoutManager.VERTICAL;
        } else {
            return LinearLayoutManager.HORIZONTAL;
        }
    }

    public static String getErrorMessage(Response<?> foResponse) {
        String fsMessage = "Something went wrong..!!, Please try again..";

        switch (foResponse.code()) {
            case 401:
                break;
            case 404:
                fsMessage = "404 Page Not Found";
                break;
            default:
                break;
        }
        return fsMessage;
    }

    public static enum LogType {
        REQUEST,
        RESPONSE,
        ERROR
    }

    public static void printReqRes(Object foObject, String fsMethodName, LogType fiType) {
//        try {
//            String lsLog = null;
//            if (fiType == LogType.REQUEST) {
//                lsLog = "REQUEST: " + fsMethodName + "  >> " + getJsonFromObject(foObject);
//            } else if (fiType == LogType.RESPONSE) {
//                lsLog = "RESPONSE: " + fsMethodName + "  >> " + getJsonFromObject(foObject);
//            } else {
//                lsLog = "ERROR: " + fsMethodName + "  >> " + getJsonFromObject(foObject);
//            }
//            LogV2.i(TAG, lsLog);
//        } catch (Exception e) {
//            LogV2.logException(TAG, e);
//        }
    }

    public static String getJsonFromObject(Object foObject) {
        try {
            Gson gson = new Gson();
            return gson.toJson(foObject);
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
        return null;
    }

    public static ProgressDialog showProgressDialog(Context foContext) {

        ProgressDialog loDialog = null;
        try {
            loDialog = new ProgressDialog(foContext);
            loDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loDialog.setCancelable(false);
            loDialog.show();
            loDialog.setContentView(R.layout.progressbar);
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
        return loDialog;
    }

    public static void dismissProgressDialog(Dialog foProgress) {
        try {
            if ((foProgress != null) && foProgress.isShowing()) {
                foProgress.dismiss();
            }
        } catch (IllegalArgumentException e) {
            LogV2.logException(TAG, e);
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        } finally {
            foProgress = null;
        }
    }

    public static JSONObject getDeviceDetails(Context foContext) {
        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(foContext);
        Calendar loCalender = Calendar.getInstance();
        JSONObject loJsonObject = new JSONObject();
        try {
            loJsonObject.put("versionName", BuildConfig.VERSION_NAME);
            loJsonObject.put("versionCode", BuildConfig.VERSION_CODE);
            loJsonObject.put("deviceDateTime", getFormattedDateTime(loCalender.getTimeInMillis()));
            loJsonObject.put("manufacturer", getDeviceName());
            loJsonObject.put("deviceId", getDeviceUniqueId(foContext));
            loJsonObject.put("platformId", 1); // Android = 1; iOS = 2;
            loJsonObject.put("osVersion", android.os.Build.VERSION.SDK_INT);
            return loJsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getDeviceUniqueId(Context foContext) {
        return Settings.Secure.getString(foContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getFormattedDateTime(long foTimeInMillis) {
        Date loDate = new Date(foTimeInMillis);
        Calendar loCalendar = Calendar.getInstance();
        loCalendar.setTime(loDate);
        return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss aa", Locale.ENGLISH).format(loCalendar.getTime());
    }

    public static void hideKeyboard(Activity foContext) {
        //foContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) foContext.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);

            if (foContext.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(foContext.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void replaceFragment(Context foContext, Fragment foFragment, String fsTag, boolean isBackStack) {
        FragmentManager fragmentManager = ((AppCompatActivity) foContext).getSupportFragmentManager();
        FragmentTransaction loTransaction = fragmentManager.beginTransaction();
        loTransaction.replace(R.id.fragment_container_view, foFragment, fsTag);
        if (isBackStack)
            loTransaction.addToBackStack(fsTag);
        loTransaction.commit();
    }

    public static void shareApp(Context foContext, String fsMessage) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, fsMessage);
        sendIntent.setType("text/plain");
        foContext.startActivity(sendIntent);
    }

    public static Spannable getColorText(String fsText, int fiColor) {
        Spannable word = new SpannableString(fsText);
        word.setSpan(new ForegroundColorSpan(fiColor), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return word;
    }

    public static boolean isLocationFromMockProvider(Context context, Location location) {
        boolean isMock = false;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            //isMock = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            if (Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                return false;
            else {
                return true;
            }
        }
        return isMock;
    }

    public static void blinkAnimation(View foView) {
        Animation anim = new AlphaAnimation(1, 0);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        foView.startAnimation(anim);
    }

    public static void openBrowser(Context foContext, String fsURL) {
        if (fsURL != null && !fsURL.isEmpty()) {
            try {
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse(fsURL));
                foContext.startActivity(httpIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadImage(ImageView loImageView, String fsURL, Drawable fdError, Drawable placeholder) {
        if (fsURL != null) {
            fsURL = (Constants.IMAGE_BASE_URL + fsURL).replace("https", "http");


            RequestCreator loRequest = Picasso.get().load(fsURL);

            if (fdError != null && placeholder != null){
                loRequest.error(fdError)
                        .placeholder(placeholder)
                        .into(loImageView);
            } else if (fdError != null && placeholder == null) {
                loRequest.error(fdError)
                        .into(loImageView);
            } else if (fdError == null && placeholder != null) {
                loRequest.placeholder(placeholder)
                        .into(loImageView);
            } else {
                loRequest.into(loImageView);
            }
        }
    }

    public static String getLinkifiedMyText(String mytext) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] splitted = mytext.split(" ");
        for (int i = 0; i < splitted.length; i++) {
            String link = splitted[i];
            if ((splitted[i]).contains("www.") || (splitted[i]).contains("https") || (splitted[i]).contains("http")) { // use more statements for
                System.out.println(splitted[i]); //just checking the output
                //link = "<a href=\"" + splitted[i] + "\" >" + splitted[i] + "</a>"; //style=color:#2196F3;
                link = splitted[i];
                return link;
            }
            stringBuilder.append(link);
            stringBuilder.append(" ");
        }
        return "";
    }

    public static void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static String getRealPathFromURI(Context foContext, Uri foURI) {

        String lsPath = null;
        Cursor loCursor = null;
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            loCursor = foContext.getContentResolver().query(foURI, filePathColumn, null, null, null);
            loCursor.moveToFirst();
            int columnIndex = loCursor.getColumnIndex(filePathColumn[0]);
            lsPath = loCursor.getString(columnIndex);
            loCursor.close();
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        } finally {
            if (loCursor != null)
                loCursor.close();
            return lsPath;
        }
    }


    public static String validatePhoneNumber(String fsPhoneNumber) {

        if (TextUtils.isEmpty(fsPhoneNumber)) {
            return "Invalid phone number.";
        } else if (fsPhoneNumber.length() != 10) {
            return "Enter valid 10 digit number";
        }
        return "";
    }


    public static boolean isValidEmail(String fsEmail) {
        String EMAIL_ADDRESS_EXPRESSIONS = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?";
        Pattern pattern = Pattern.compile(EMAIL_ADDRESS_EXPRESSIONS);
        Matcher matcher = pattern.matcher(fsEmail);

        boolean matchFound = matcher.matches();
        return matchFound;
    }

    public static boolean isGPSEnabled(Context foContext) {
        LocationManager locManager = (LocationManager) foContext.getSystemService(Context.LOCATION_SERVICE);
        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
