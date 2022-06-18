package com.mobycashback.utils;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.mobycashback.webview.MobyWebViewBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import retrofit2.Response;

public class Common {
    public static String URL = "url";
    public static String ACCESS_GPS = "isAccessGPS";
    public static String ACCESS_STORAGE = "isAccessStorage";
    public static String PRIMARY_COLOR = "setPrimaryColor";
    public static String SECONDARY_COLOR = "setSecondaryColor";
    public static String PRIMARY_TEXT_COLOR = "setPrimaryTextColor";
    public static String SECONDARY_TEXT_COLOR = "setSecondaryTextColor";
    public static String CATEGORY = "isCategories";

    public static MobyWebViewBuilder builder;

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

    public static void loadImageBank(ImageView loImageView, String fsURL, Drawable fdError, Drawable placeholder) {

        try {
            if (fsURL != null) {
                //  fsURL = (fsURL).replace("https", "http");
                // Log.d("TTT", "fsURL..." + fsURL);
                RequestCreator loRequest = Picasso.get().load(fsURL);

                if (fdError != null && placeholder != null) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
