package com.cashback.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.R;
import com.cashback.adapters.QuizOptionAdapter;
import com.cashback.databinding.ActivityQuizDetailsBinding;
import com.cashback.models.Quiz;
import com.cashback.models.QuizAnswer;
import com.cashback.models.QuizDetailsViewModel;
import com.cashback.models.response.QuizDetailsResponse;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.util.ArrayList;

public class QuizDetailsActivity extends BaseActivity implements View.OnClickListener, QuizOptionAdapter.OnOptionSelectListener {

    private static final String TAG = QuizDetailsActivity.class.getSimpleName();
    private static final int REQUEST_QR = 1000;
    ActivityQuizDetailsBinding moBinding;
    QuizDetailsViewModel moQuizDetailsViewModel;

    ArrayList<Quiz> moQuizList;
    private long miOfferID, miLocationID;
    private int miCurrentQuestion = 0;

    ArrayList<QuizAnswer> moQuizAnswerList = new ArrayList<>();

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

        setToolbar();
        moBinding.btnNext.setOnClickListener(this);

        if (getIntent() != null) {
            miOfferID = getIntent().getLongExtra(Constants.IntentKey.OFFER_ID, 0);
            miLocationID = getIntent().getLongExtra(Constants.IntentKey.LOCATION_ID, 0);
        }

        getQuizDetails();
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
        loTvToolbarTitle.setVisibility(View.GONE);
    }

    private void getQuizDetails() {
        showProgressDialog();
        moQuizDetailsViewModel.fetchQuizDetails(getContext(), "", miOfferID, miLocationID);
    }

    Observer<QuizDetailsResponse> fetchOfferDetailsObserver = new Observer<QuizDetailsResponse>() {
        @Override
        public void onChanged(QuizDetailsResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                moQuizList = loJsonObject.getQuizList();
                setUpQuizDetailView();
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }

        private void setUpQuizDetailView() {
            if (moQuizList != null) {

                setQuestion(getContext(), moQuizList, miCurrentQuestion, moBinding);
            }
        }
    };


    private void setQuestion(Context context, ArrayList<Quiz> foQuizList, int fiCurrentQuestion, ActivityQuizDetailsBinding foBinding) {

        Quiz quiz = foQuizList.get(fiCurrentQuestion);

        foBinding.rvQuizOptions.setVisibility(View.GONE);
        foBinding.cvYouTube.setVisibility(View.GONE);
        foBinding.cvQRCode.setVisibility(View.GONE);
        foBinding.cvTextAnswer.setVisibility(View.GONE);

        foBinding.tvQuestion.setText(quiz.getQuestion());
        foBinding.tvNumber.setText((fiCurrentQuestion + 1) + " of " + foQuizList.size());

        if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.BARCODE.getValue())) {
            foBinding.cvQRCode.setVisibility(View.VISIBLE);
            setQRCodeScanner(context, quiz, foBinding);
        } else if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.MULTI_CHOICE.getValue())) {
            setUpMultiChoice(context, quiz, foBinding);
            foBinding.rvQuizOptions.setVisibility(View.VISIBLE);
        } else if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.YOUTUBE_VIDEO.getValue())) {
            setYouTubeLink(context, quiz, foBinding);
            foBinding.cvYouTube.setVisibility(View.VISIBLE);
        } else if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.TEXABLE.getValue())) {
            setTexable(context, quiz, foBinding);
            foBinding.cvTextAnswer.setVisibility(View.VISIBLE);
        } else if (quiz.getAnswerType().equalsIgnoreCase(Constants.AnswerType.CAMPAIGN.getValue())) {

        }
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
            case R.id.cvYouTube:
                btnYoutubePressed();
            default:
                break;
        }
    }

    private void btnYoutubePressed() {

        Quiz loQuiz = moQuizList.get(miCurrentQuestion);
        if (loQuiz.getOptionList() != null &&
                loQuiz.getOptionList().size() > 0) {

            Intent loIntent = new Intent(getContext(), VideoViewActivity.class);
            loIntent.putExtra("video_url", loQuiz.getOptionList().get(0).getValue());
            loIntent.putExtra("ad_name", "ad.getAdname()");
            startActivity(loIntent);
        }
    }

    private void btnQRCodePressed() {
        if (moQuizList.get(miCurrentQuestion).getOptionList() != null &&
                moQuizList.get(miCurrentQuestion).getOptionList().size() > 0) {

            Intent loIntent = new Intent(getContext(), QrScannerActivity.class);
            loIntent.putExtra("video_url", moQuizList.get(miCurrentQuestion).getOptionList().get(0).getValue());
            loIntent.putExtra("ad_name", "ad.getAdname()");
            startActivityForResult(loIntent, REQUEST_QR);
        }
    }

    private void btnNextPressed() {


        if (miCurrentQuestion == moQuizList.size() - 1) {


        } else {
            miCurrentQuestion = miCurrentQuestion + 1;
            if (miCurrentQuestion == moQuizList.size() - 1) {
                moBinding.btnNext.setText(Common.getDynamicText(getContext(), "submit_quiz"));
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

        if (requestCode == REQUEST_QR && resultCode == Activity.RESULT_OK){
            String lsScannedText = data.getStringExtra(Constants.IntentKey.QR_DATA);
            if (!lsScannedText.isEmpty()) {
                
                Quiz loQuiz = moQuizList.get(miCurrentQuestion);

                String lsQRString = "";
                if (loQuiz.getOptionList() != null &&
                        loQuiz.getOptionList().size() > 0) {
                    lsQRString  = loQuiz.getOptionList().get(0).getValue();
                }

//                if (lsQRString.isEmpty() || (!lsQRString.equalsIgnoreCase(lsScannedText)
//                        && ad.getFsPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue()))) {
//                    Common.showErrorDialog(getContext(), Common.getDynamicText(getContext(), "scanned_qr_is_not_correct"), false);
//                } else {
//                    setAnswer(lsScannedText);
//                }

                setButtonText();

                if (lsScannedText.startsWith("http://") || lsScannedText.startsWith("https://")) {
                    Common.openBrowser(getContext(), lsScannedText);
                }
            }
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
        moQuizAnswerList.add(loQuizAnswer);
    }
}
