package com.cashback.utils;

/**
 * Created by Chirag on 6/24/2016.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A utility that downloads a file from a URL.
 *
 * @author www.codejava.net
 */
public class HttpDownloadUtility {
    private static final String TAG = "HttpDownloadUtility";
    private static final int BUFFER_SIZE = 4096;

    /**
     * Downloads a file from a URL
     *
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @param fsFileName file name
     * @throws IOException
     */
    public static String downloadFile(String fileURL, String saveDir, String fsFileName)
            throws IOException {


        File loDir = new File(saveDir);
        if (!loDir.exists()) {
            loDir.mkdirs();
        }

        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        String loSaveFilePath = "";
        try {
            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                            fileURL.length());
                }

//                System.out.println("Content-Type = " + contentType);
//                System.out.println("Content-Disposition = " + disposition);
//                System.out.println("Content-Length = " + contentLength);
//                System.out.println("fileName = " + fileName);

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                fsFileName = fsFileName + fileName.substring(fileName.lastIndexOf("."));
                loSaveFilePath = saveDir + fsFileName;
                // opens an output stream to save into file

                FileOutputStream outputStream = new FileOutputStream(loSaveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                System.out.println("File downloaded");
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        } finally {
            httpConn.disconnect();
        }
        return loSaveFilePath;
    }
}