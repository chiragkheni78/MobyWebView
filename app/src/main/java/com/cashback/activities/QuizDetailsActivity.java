package com.cashback.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.R;
import com.cashback.adapters.QuizOptionAdapter;
import com.cashback.databinding.ActivityQuizDetailsBinding;
import com.cashback.models.Ad;
import com.cashback.models.Quiz;
import com.cashback.models.QuizAnswer;
import com.cashback.models.viewmodel.QuizDetailsViewModel;
import com.cashback.models.response.QuizDetailsResponse;
import com.cashback.models.response.SubmitQuizResponse;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.dialog.MessageDialog;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static com.cashback.models.viewmodel.QuizDetailsViewModel.REQUEST_CAMERA;

@SuppressWarnings("All")
public class QuizDetailsActivity extends BaseActivity implements View.OnClickListener, QuizOptionAdapter.OnOptionSelectListener {

    private static final String TAG = QuizDetailsActivity.class.getSimpleName();
    private static final int REQUEST_QR = 1000;
    ActivityQuizDetailsBinding moBinding;
    QuizDetailsViewModel moQuizDetailsViewModel;

    ArrayList<Quiz> moQuizList;
    private Ad moOffer;
    private int miCurrentQuestion = 0;

    ArrayList<QuizAnswer> moQuizAnswerList = new ArrayList<>();

    TextWatcher moTextChangeListener = new TextWatcher() {

        public void onTextChanged(CharSequence s, int start, int before,
        int count) {
            if (s.length() > 0) {
                setAnswer(String.valueOf(s.toString()));
            } else {
                moBinding.btnNext.setVisibility(GONE);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityQuizDetailsBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moQuizDetailsViewModel = new ViewModelProvider(this).get(QuizDetailsViewModel.class);
        moQuizDetailsViewModel.fetchQuizDetailsStatus.observe(this, fetchOfferDetailsObserver);
        moQuizDetailsViewModel.submitQuizAnswerStatus.observe(this, submitQuizAnswerObserver);

        if (getIntent() != null) {
            moOffer = (Ad) getIntent().getExtras().getSerializable(Constants.IntentKey.OFFER_OBJECT);

            setToolbar();
            moBinding.btnNext.setOnClickListener(this);
            moBinding.cvQRCode.setOnClickListener(this);
            moBinding.cvYouTube.setOnClickListener(this);
            moBinding.btnNext.setOnClickListener(this);
            moBinding.etTextAnswer.addTextChangedListener(moTextChangeListener);

            getQuizDetails();
        }
    }

    private void setToolbar() {

        Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);

        ImageButton loIbNavigation = loToolbar.findViewById(R.id.ibNavigation);
        loIbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView loTvToolbarTitle = loToolbar.findViewById(R.id.tvToolbarTitle);
        loTvToolbarTitle.setText(moOffer.getAdName());
        loTvToolbarTitle.setVisibility(View.VISIBLE);
    }

    private void getQuizDetails() {
        showProgressDialog();
        moQuizDetailsViewModel.fetchQuizDetails(getContext(), "", moOffer.getAdID(), moOffer.getLocationList().get(0).getLocationID());
    }

    Observer<QuizDetailsResponse> fetchOfferDetailsObserver = new Observer<QuizDetailsResponse>() {
        @Override
        public void onChanged(QuizDetailsResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                moQuizList = loJsonObject.getQuizList();
                preDefineQuizAnswer();
                setUpQuizDetailView();
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    private void preDefineQuizAnswer() {
        for (Quiz loQuiz: moQuizList){
            QuizAnswer loQuizAnswer = new QuizAnswer(loQuiz.getQuizID(), 0, "");
            moQuizAnswerList.add(loQuizAnswer);
        }
    }

    private void setUpQuizDetailView() {
        if (moQuizList != null) {
            setQuestion(getContext(), moQuizList, miCurrentQuestion, moBinding);
        }
    }

    private void setQuestion(Context context, ArrayList<Quiz> foQuizList, int fiCurrentQuestion, ActivityQuizDetailsBinding foBinding) {

        Quiz quiz = foQuizList.get(fiCurrentQuestion);

        foBinding.rvQuizOptions.setVisibility(View.GONE);
        foBinding.cvYouTube.setVisibility(View.GONE);
        foBinding.cvQRCode.setVisibility(View.GONE);
        foBinding.cvTextAnswer.setVisibility(View.GONE);
        moBinding.btnNext.setVisibility(GONE);

        foBinding.tvQuestion.setText(quiz.getQuestion());
        foBinding.tvNumber.setText((fiCurrentQuestion + 1) + " of " + foQuizList.size());

        if (moOffer.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())){
            foBinding.tvNumber.setTextColor(ActivityCompat.getColor(getContext(), R.color.colorPrimary));
            foBinding.llQuestion.setBackground(ActivityCompat.getDrawable(getContext(), R.drawable.half_circle_light_red));
        } else if (moOffer.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())){
            foBinding.tvNumber.setTextColor(ActivityCompat.getColor(getContext(), R.color.green_primary));
            foBinding.llQuestion.setBackground(ActivityCompat.getDrawable(getContext(), R.drawable.half_circle_light_green));
        }

        if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.BARCODE.getValue())) {
            foBinding.cvQRCode.setVisibility(View.VISIBLE);
            showTimer(false);
            setQRCodeScanner(context, quiz, foBinding);
        } else if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.MULTI_CHOICE.getValue())) {
            setUpMultiChoice(context, quiz, foBinding);
            showTimer(true);
            foBinding.rvQuizOptions.setVisibility(View.VISIBLE);
        } else if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.YOUTUBE_VIDEO.getValue())) {
            setYouTubeLink(context, quiz, foBinding);
            showTimer(false);
            foBinding.cvYouTube.setVisibility(View.VISIBLE);
        } else if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.TEXABLE.getValue())) {
            setTexable(context, quiz, foBinding);
            foBinding.cvTextAnswer.setVisibility(View.VISIBLE);
            showTimer(true);
        } else if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.CAMPAIGN.getValue())) {

        }
        firstTimestamp = System.currentTimeMillis();
    }

    private void setTexable(Context context, Quiz quiz, ActivityQuizDetailsBinding foBinding) {
        foBinding.etTextAnswer.setText("");
    }

    private void setQRCodeScanner(Context context, Quiz quiz, ActivityQuizDetailsBinding foBinding) {

    }

    private void setYouTubeLink(Context context, Quiz quiz, ActivityQuizDetailsBinding foBinding) {

    }

    private void setUpMultiChoice(Context context, Quiz quiz, ActivityQuizDetailsBinding foBinding) {
        LinearLayoutManager loLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        foBinding.rvQuizOptions.setLayoutManager(loLayoutManager);
        QuizOptionAdapter moCategoryAdapter = new QuizOptionAdapter(context, quiz.getOptionList(), this);
        foBinding.rvQuizOptions.setAdapter(moCategoryAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                btnNextPressed();
                break;
            case R.id.cvQRCode:
                btnQRCodePressed();
                break;
            case R.id.cvYouTube:
                btnYoutubePressed();
                break;
            default:
                break;
        }
    }

    private void btnYoutubePressed() {

        Quiz loQuiz = moQuizList.get(miCurrentQuestion);
        if (loQuiz.getOptionList() != null &&
                loQuiz.getOptionList().size() > 0) {

            Intent loIntent = new Intent(getContext(), VideoViewActivity.class);
            loIntent.putExtra(Constants.IntentKey.VIDEO_URL, loQuiz.getOptionList().get(0).getValue());
            loIntent.putExtra(Constants.IntentKey.SCREEN_TITLE, moOffer.getAdName());
            startActivity(loIntent);
        }
        setAnswer(String.valueOf(loQuiz.getOptionList().get(0).getValue()));
    }

    private void btnQRCodePressed() {
        if (moQuizDetailsViewModel.isCameraPermissionGranted(this)) {
            if (moQuizList.get(miCurrentQuestion).getOptionList() != null &&
                    moQuizList.get(miCurrentQuestion).getOptionList().size() > 0) {

                Intent loIntent = new Intent(getContext(), QrScannerActivity.class);
                startActivityForResult(loIntent, REQUEST_QR);
            }
        }
    }

    @Override
    public void onOptionSelect(int fiAnswerIndex) {
        setAnswer(String.valueOf(fiAnswerIndex));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_QR && resultCode == Activity.RESULT_OK) {
            String lsScannedText = data.getStringExtra(Constants.IntentKey.QR_DATA);
            if (!lsScannedText.isEmpty()) {

                Quiz loQuiz = moQuizList.get(miCurrentQuestion);

                String lsQRString = "";
                if (loQuiz.getOptionList() != null &&
                        loQuiz.getOptionList().size() > 0) {
                    lsQRString = loQuiz.getOptionList().get(0).getValue();
                }

                if (lsQRString.isEmpty() || (!lsQRString.equalsIgnoreCase(lsScannedText)
                        && moOffer.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue()))) {
                    Common.showErrorDialog(getContext(), Common.getDynamicText(getContext(), "scanned_qr_is_not_correct"), false);
                } else {
                    setAnswer(lsScannedText);
                }

                if (lsScannedText.startsWith("http://") || lsScannedText.startsWith("https://")) {
                    Common.openBrowser(getContext(), lsScannedText);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted...");
                    btnQRCodePressed();
                } else {
                    Common.showErrorDialog(getContext(), "", false);
                }
                break;
        }
    }

    private void setButtonText() {
        if (miCurrentQuestion == (moQuizList.size() - 1)) {
            moBinding.btnNext.setText(Common.getDynamicText(getContext(), "submit_quiz"));
        } else {
            moBinding.btnNext.setText(Common.getDynamicText(getContext(), "next_question"));
        }
        moBinding.btnNext.setVisibility(View.VISIBLE);
    }

    private void setAnswer(String fsAnswer) {
        QuizAnswer loQuizAnswer = new QuizAnswer(moQuizList.get(miCurrentQuestion).getQuizID(), 12, String.valueOf(fsAnswer));
        moQuizAnswerList.set(miCurrentQuestion, loQuizAnswer);
//        LogV2.i(TAG, "size: "+ moQuizAnswerList.size() + " miCurrentQuestion: "+ miCurrentQuestion);
        setButtonText();
    }

    private void btnNextPressed() {
        if (moQuizAnswerList.size() > 0){
            moQuizAnswerList.get(miCurrentQuestion).setAnsInterval(getQuizInterval());
        }

        if (miCurrentQuestion == moQuizList.size() - 1) {
            submitQuizAnswer();
        } else {
            miCurrentQuestion = miCurrentQuestion + 1;
            setUpQuizDetailView();
        }
    }

    private void submitQuizAnswer() {
        if (moQuizAnswerList != null & moQuizAnswerList.size() > 0) {
            showProgressDialog();
            moQuizDetailsViewModel.submitQuizAnswer(getContext(), "",
                    moOffer.getAdID(),
                    moOffer.getLocationList().get(0).getLocationID(),
                    moQuizAnswerList);
        }
    }

    Observer<SubmitQuizResponse> submitQuizAnswerObserver = new Observer<SubmitQuizResponse>() {
        @Override
        public void onChanged(SubmitQuizResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                handleQuizAnswerResponse(loJsonObject);
            } else {
                if (loJsonObject.isMarketingAd())
                {
//                    Common.showErrorDialog(getContext(), loJsonObject.getMessage(), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                            Intent intent = new Intent(getContext(), HomeActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        }
//                    });

                    MessageDialog loDialog = new MessageDialog(getContext(), null, loJsonObject.getMessage(), null, false);
                    loDialog.setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loDialog.cancel();
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                    loDialog.show();
                } else {
                    Common.showErrorDialog(getContext(), loJsonObject.getMessage(), true);
                }
            }
            dismissProgressDialog();
        }
    };

    private void handleQuizAnswerResponse(SubmitQuizResponse foJsonObject) {
        showSuccessMessage(foJsonObject);
        AdGydeEvents.offerEngaged(getContext(), moOffer);
    }


    private void showTimer(boolean isShowTimer) {
        if (isShowTimer && !getPreferenceManager().isMarketingAd()) {
            moBinding.toolbar.flTimer.setVisibility(View.VISIBLE);
            resetTimer();
        } else {
            moBinding.toolbar.flTimer.setVisibility(GONE);
            if (countDownTimer != null)
                countDownTimer.cancel();
        }
    }

    CountDownTimer countDownTimer;

    private void resetTimer() {
        int liSeconds = getPreferenceManager().getQuizTimePeriod();
        if (liSeconds > 0) {
            moBinding.toolbar.pbTimer.setMax(liSeconds);
            moBinding.toolbar.pbTimer.setProgress(0);
            // call to start the count down timer
            if (countDownTimer != null)
                countDownTimer.cancel();
            countDownTimer = new CountDownTimer(liSeconds * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    moBinding.toolbar.tvTimer.setText(String.valueOf((int) (millisUntilFinished / 1000)));
                    moBinding.toolbar.pbTimer.setProgress((int) (millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {

                }

            }.start();
            countDownTimer.start();
        } else moBinding.toolbar.flTimer.setVisibility(GONE);
    }


    long firstTimestamp = 0, secondTimestamp = 0;

    private long getQuizInterval() {

        if (!getPreferenceManager().isMarketingAd()) {
            secondTimestamp = System.currentTimeMillis();

            long first = TimeUnit.MILLISECONDS.toSeconds(firstTimestamp);
            long second = TimeUnit.MILLISECONDS.toSeconds(secondTimestamp);

            Log.d(TAG, "firstTimestamp:: " + first + "\t secondTimestamp:: " + second);

            long llInterval = second - first;
            return llInterval;
        }
        return 0;
    }

    private void showSuccessMessage(SubmitQuizResponse foJsonObject) {
        try {
            if (!isFinishing()) {
                final Dialog moDialog = new Dialog(getContext());
                moDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                moDialog.setContentView(R.layout.dialog_quiz_success);

                TextView loTvTitle = moDialog.findViewById(R.id.tvTitle);
                WebView loTvMessage = moDialog.findViewById(R.id.tvMessage);
                loTvMessage.setBackgroundColor(0);
                Button loBtnPlayQuiz = moDialog.findViewById(R.id.btnPlayQuiz);
                Button loBtnTimeline = moDialog.findViewById(R.id.btnTimeline);

                String lsTitle = foJsonObject.getSuccessMessage().getTitle();
                String lsMessage = foJsonObject.getSuccessMessage().getMessage();
                loTvTitle.setText(Html.fromHtml(lsTitle));
                loTvMessage.getSettings().setJavaScriptEnabled(true);
                loTvMessage.loadDataWithBaseURL(null,lsMessage,"text/html", "utf-8", null);

                if (getPreferenceManager().isMarketingAd()) {
                    loBtnTimeline.setVisibility(GONE);
                }

                loBtnPlayQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moDialog.dismiss();
                        Intent intent = new Intent(moContext, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        moContext.startActivity(intent);
                    }
                });

                loBtnTimeline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Common.stCouponId = "" + moOffer.getAdID();
                        Intent intent = new Intent(moContext, HomeActivity.class);
                        intent.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.FROM_COUPON);
                        //intent.putExtra("foGiftCard", foGiftCard);
                        moContext.startActivity(intent);
                        finishAffinity();
                        moDialog.dismiss();
                    }
                });

                if (moDialog.getWindow() != null) {
                    moDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    moDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    moDialog.getWindow().setGravity(Gravity.CENTER);
                    moDialog.setCancelable(false);
                    moDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
