package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.models.Transaction;
import com.cashback.models.response.BillUploadResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillUploadViewModel extends ViewModel {

    public MutableLiveData<BillUploadResponse> uploadBillStatus = new MutableLiveData<>();

    public void uploadTransactionBill(Context foContext, String phoneNumber, long fiActivityId, String transactionDate, int transactionAmount, String fsBillImagePath_1, String fsBillImagePath_2) {
        Transaction loTransactionData = new Transaction(phoneNumber, fiActivityId, transactionDate, transactionAmount);
        loTransactionData.setDeviceId(Common.getDeviceUniqueId(foContext));

        String lsMessage = loTransactionData.validateData(foContext);
        if (lsMessage != null) {
            uploadBillStatus.postValue(new BillUploadResponse(true, lsMessage));
            return;
        }

        RequestBody loRequestData = RequestBody.create(Common.getJsonFromObject(loTransactionData), MediaType.parse("application/json"));
        RequestBody loAction = RequestBody.create(Constants.API.UPLOAD_TRANSACTION_BILL.getValue(), okhttp3.MultipartBody.FORM);

        List<MultipartBody.Part> loMultiPartList = getMultipartList(fsBillImagePath_1, fsBillImagePath_2);

        //API Call
        Call<BillUploadResponse> loRequest = APIClient.getInterface().uploadTransactionBill(loAction, loRequestData, loMultiPartList);
        Common.printReqRes(loRequest, "uploadTransactionBill", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<BillUploadResponse>() {
            @Override
            public void onResponse(Call<BillUploadResponse> call, Response<BillUploadResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "uploadTransactionBill", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    BillUploadResponse loJsonObject = foResponse.body();
                    uploadBillStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    uploadBillStatus.postValue(new BillUploadResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<BillUploadResponse> call, Throwable t) {
                Common.printReqRes(t, "uploadTransactionBill", Common.LogType.ERROR);
                uploadBillStatus.postValue(new BillUploadResponse(true, t.getMessage()));
            }
        });
    }

    public String formatTransactionDate(int Year, int Month, int Date) {
        try {
            return (new StringBuilder()
                    .append(Year).append("-")
                    .append((Month <= 9 ? "0" : "")).append(Month + "-")
                    .append((Date <= 9 ? "0" : "")).append(Date)).toString();
        } catch (Exception e) {
            return "";
        }
    }

    public List<MultipartBody.Part> getMultipartList(String fsBillImagePath_1, String fsBillImagePath_2) {

        List<MultipartBody.Part> loMultiPartList = new ArrayList<>();
        long lsImageName = System.currentTimeMillis();
        if (fsBillImagePath_1 != null) {
            File loFile = new File(fsBillImagePath_1);
            RequestBody loRequestBodyImage = RequestBody.create(loFile, MediaType.parse("image/*"));
            MultipartBody.Part loPart = MultipartBody.Part.createFormData("fsFirstImage", lsImageName+"1.png", loRequestBodyImage);
            loMultiPartList.add(loPart);
        }

        if (fsBillImagePath_2 != null) {
            File loFile = new File(fsBillImagePath_2);
            RequestBody loRequestBodyImage = RequestBody.create(loFile, MediaType.parse("image/*"));
            MultipartBody.Part loPart = MultipartBody.Part.createFormData("fsSecondImage", lsImageName+"1.png", loRequestBodyImage);
            loMultiPartList.add(loPart);
        }
        return loMultiPartList;
    }
}
