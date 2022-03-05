package com.cashback.models.response;

import com.cashback.models.BankOfferCategory;
import com.cashback.models.DebitCard;
import com.cashback.models.DonationDetails;
import com.cashback.models.EWallet;
import com.cashback.models.UserDetails;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetUserProfileResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foUserDetails")
    private UserDetails userDetails;

    @SerializedName("foWalletList")
    private ArrayList<EWallet> walletList;

    @SerializedName("foBankCategory")
    private ArrayList<BankOfferCategory> bankOfferCategoryList;

    @SerializedName("foUserCardList")
    private ArrayList<DebitCard> debitCardList;

    @SerializedName("foDonationDetails")
    private DonationDetails donationDetails;

    @SerializedName("foPaymentDetails")
    private PaymentDetails paymentDetails;

    public GetUserProfileResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public ArrayList<EWallet> getWalletList() {
        return walletList;
    }

    public ArrayList<BankOfferCategory> getBankOfferCategoryList() {
        return bankOfferCategoryList;
    }

    public ArrayList<DebitCard> getDebitCardList() {
        return debitCardList;
    }

    public DonationDetails getDonationDetails() {
        return donationDetails;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public class PaymentDetails{
        @SerializedName("fiPaymentMode")
        private int paymentMode;

        @SerializedName("fsUPILink")
        private String upiLink;

        @SerializedName("fsIFSCCode")
        private String fsIFSCCode;

        @SerializedName("fsAccountNo")
        private String fsAccountNo;

        @SerializedName("fsPaytmMobile")
        private String fsPaytmMobile;

        public int getPaymentMode() {
            return paymentMode;
        }

        public String getUpiLink() {
            return upiLink;
        }

        public String getFsIFSCCode() {
            return fsIFSCCode;
        }

        public void setFsIFSCCode(String fsIFSCCode) {
            this.fsIFSCCode = fsIFSCCode;
        }

        public String getFsAccountNo() {
            return fsAccountNo;
        }

        public void setFsAccountNo(String fsAccountNo) {
            this.fsAccountNo = fsAccountNo;
        }

        public String getFsPaytmMobile() {
            return fsPaytmMobile;
        }

        public void setFsPaytmMobile(String fsPaytmMobile) {
            this.fsPaytmMobile = fsPaytmMobile;
        }
    }
}
