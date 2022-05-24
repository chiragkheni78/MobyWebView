package app.dev.myapplication;

import android.app.Application;
import android.content.Context;

public class LibApp extends Application {

    /**
     * Instance of the current application.
     */
    private static LibApp instance;

    /**
     * Constructor.
     */
    public LibApp() {
        instance = this;
    }

    /**
     * Gets the application context.
     *
     * @return the application context
     */
    public static Context getContext() {
        return instance;
    }

}