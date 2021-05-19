package com.cashback.activities;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;
import com.cashback.databinding.ActivityBillUploadBinding;
import com.cashback.models.response.ActivityMarkAsUsedResponse;
import com.cashback.models.response.BillUploadResponse;
import com.cashback.models.viewmodel.ActivityDetailsViewModel;
import com.cashback.models.viewmodel.BillUploadViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.view.View.GONE;

public class BillUploadActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = BillUploadActivity.class.getSimpleName();
    ActivityBillUploadBinding moBinding;
    BillUploadViewModel moBillUploadViewModel;

    private long miActivityId;
    private String msEngagedDate, msPinColor;
    private String msTransactionDate;

    private int mYear, mMonth, mDay;

    private String msBillImagePath_1, msBillImagePath_2;
    private int imageCounter = 0;

    CompoundButton.OnCheckedChangeListener moCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                moBinding.llBillUpload.setVisibility(View.VISIBLE);
            } else {
                moBinding.llBillUpload.setVisibility(GONE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityBillUploadBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moBillUploadViewModel = new ViewModelProvider(this).get(BillUploadViewModel.class);
        moBillUploadViewModel.uploadBillStatus.observe(this, uploadBillObserver);


        if (getIntent() != null) {
            miActivityId = getIntent().getLongExtra(Constants.IntentKey.ACTIVITY_ID, 0);
            msEngagedDate = getIntent().getStringExtra(Constants.IntentKey.ENGAGED_DATE);
            msPinColor = getIntent().getStringExtra(Constants.IntentKey.PIN_COLOR);

        }

        moBinding.btnLater.setOnClickListener(this);
        moBinding.btnRegisterNow.setOnClickListener(this);
        moBinding.tvDate.setOnClickListener(this);
        moBinding.cbUploadBill.setOnCheckedChangeListener(moCheckChangeListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLater:
                onBackPressed();
                break;
            case R.id.btnRegisterNow:
                uploadTransactionBill();
                break;
            case R.id.tvDate:
                openDatePicker();
                break;
            case R.id.ivOne:
                if (msBillImagePath_1 == null)
                    imageClick();
                break;
            case R.id.ivTwo:
                if (msBillImagePath_2 == null)
                    imageClick();
                break;
        }
    }

    private void imageClick() {
        try {
            if (imageCounter < 2)
                fileChooser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerFragmentDialog loDialog = DatePickerFragmentDialog.newInstance(new DatePickerFragmentDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerFragmentDialog view, int year, int monthOfYear, int dayOfMonth) {
                moBinding.tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                msTransactionDate = moBillUploadViewModel.formatTransactionDate(year, (monthOfYear + 1), dayOfMonth);
            }
        }, mYear, mMonth, mDay);


        msEngagedDate = msEngagedDate.replace("th", "");

        DateFormat df = new SimpleDateFormat("dd MMM, yyyy HH:mm a");
        try {
            Date d = df.parse(msEngagedDate);
            loDialog.setMinDate(d.getTime());
            loDialog.setMaxDate(c.getTimeInMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        loDialog.show(getSupportFragmentManager(), "datePicker");
    }

    private void uploadTransactionBill() {
        showProgressDialog();
        int liTransactionAmount = moBinding.etAmount.getText().length() == 0 ? 0 : Integer.parseInt(moBinding.etAmount.getText().toString());
        moBillUploadViewModel.uploadTransactionBill(getContext(), "", miActivityId, msTransactionDate, liTransactionAmount, msBillImagePath_1, msBillImagePath_2);
    }

    Observer<BillUploadResponse> uploadBillObserver = new Observer<BillUploadResponse>() {
        @Override
        public void onChanged(BillUploadResponse loJsonObject) {
            if (!loJsonObject.isError()) {

                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), BillUploadActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
                    }
                });

                if (msPinColor.equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                    ActivityDetailsViewModel moActivityDetailsViewModel = new ViewModelProvider(BillUploadActivity.this).get(ActivityDetailsViewModel.class);
                    moActivityDetailsViewModel.updateMarkAsUsedStatus.observe(BillUploadActivity.this, updateMarkAdUsedObserver);
                    moActivityDetailsViewModel.updateMarkAsUsed(getContext(), "", miActivityId);
                }
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    Observer<ActivityMarkAsUsedResponse> updateMarkAdUsedObserver = new Observer<ActivityMarkAsUsedResponse>() {
        @Override
        public void onChanged(ActivityMarkAsUsedResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                LogV2.i(TAG, "GREEN: Register Bill: SUCCESS");
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    /*
    *
    Image Chooser Methods
    *
    */
    private void fileChooser() {
        try {
            final Dialog loDialog = new Dialog(this);
            loDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loDialog.setContentView(R.layout.dialog_file_chooser);
            loDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            loDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loDialog.getWindow().setGravity(Gravity.CENTER);


            if (!isFinishing())
                loDialog.show();

            Button loBtnCamera = loDialog.findViewById(R.id.btnCamera);
            Button loBtnGallery = loDialog.findViewById(R.id.btnGallery);
            ImageView loIvClose = loDialog.findViewById(R.id.ivClose);


            loBtnGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loDialog.dismiss();
                    choosePhotoFromGallery();
                }
            });

            loBtnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loDialog.dismiss();
                    captureImage();
                }
            });

            loIvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loDialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TTT", "Permission is granted2");
                return true;
            } else {
                Log.v("TTT", "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TTT", "Permission is granted2");
            return true;
        }
    }


    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TTT", "Permission is granted: WRITE_EXTERNAL_STORAGE");
                return true;
            } else {
                Log.v("TTT", "Permission is revoked: WRITE_EXTERNAL_STORAGE");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TTT", "Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TTT", "permission granted...");
                    captureImage();
                } else {
                    openAlertDialog(0);
                }
                break;

            case 22:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TTT", "permission granted: WRITE_EXTERNAL_STORAGE");
                    choosePhotoFromGallery();
                } else {
                    openAlertDialog(1);
                }
                break;
        }
    }

    private void openAlertDialog(int fiType) {
        String lsMessage = (fiType == 1) ? getResources().getString(R.string.gallery_access_permission) : getResources().getString(R.string.camera_access_permission);

        Common.showErrorDialog(getContext(), lsMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (fiType == 1) {
                    isCameraPermissionGranted();
                } else {
                    isWriteStoragePermissionGranted();
                }
            }
        });
    }

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_GALLERY_IMAGE = 110;

    private void captureImage() {
        if (isCameraPermissionGranted()) {

            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    public void choosePhotoFromGallery() {
        if (isWriteStoragePermissionGranted()) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri foUri = data.getData();
                    String lsFilePath = Common.getRealPathFromURI(this, foUri);

                    setImageView(lsFilePath);
                } else {
                    Common.showErrorDialog(this, "Unable to get Image", false);
                }
                break;
            case REQUEST_CAPTURE_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        if (null != data) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Title", null);
                            Uri foUri = Uri.parse(path);
                            String lsFilePath = Common.getRealPathFromURI(this, foUri);
                            setImageView(lsFilePath);
                        } else {
                            Common.showErrorDialog(this, "Unable to get Image", false);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                break;
        }
    }

    private void setImageView(String fsFilePath) {
        File loFile = new File(fsFilePath);
        if (imageCounter == 0) {
            msBillImagePath_1 = fsFilePath;
            Picasso.get().load(loFile).into(moBinding.ivOne);
        } else {
            msBillImagePath_2 = fsFilePath;
            Picasso.get().load(loFile).into(moBinding.ivTwo);
        }
        imageCounter++;
    }
}