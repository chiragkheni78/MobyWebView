package com.cashback.utils.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.cashback.databinding.DialogCommonBinding;

public class MessageDialog extends Dialog {

    DialogCommonBinding moBinding;

    private Context moContext;
    private String msTitle, msMessage, msButtonName;
    private boolean isFinish;

    View.OnClickListener moListener;

    public MessageDialog(@NonNull Context context, String fsTitle, String fsMessage, String fsButtonName, boolean isFinish) {
        super(context);
        moContext = context;
        msTitle = fsTitle;
        msMessage = fsMessage;
        msButtonName = fsButtonName;
        this.isFinish = isFinish;
    }

    public void setClickListener(View.OnClickListener listener) {
        this.moListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(false);

        moBinding = DialogCommonBinding.inflate(getLayoutInflater());
        setContentView(moBinding.getRoot());
        initContent();
    }

    private void initContent() {

        if (moListener != null) {
            moBinding.btnOk.setOnClickListener(moListener);
        } else {
            moBinding.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (isFinish) {
                        ((Activity) moContext).finish();
                    }
                }
            });
        }

        if (msTitle == null || msTitle.isEmpty())
            moBinding.tvTitle.setVisibility(View.GONE);
        else {
            moBinding.tvTitle.setText(msTitle);
        }

        moBinding.tvMessage.setText(msMessage);

        if (msButtonName != null)
            moBinding.btnOk.setText(msButtonName);

    }
}
