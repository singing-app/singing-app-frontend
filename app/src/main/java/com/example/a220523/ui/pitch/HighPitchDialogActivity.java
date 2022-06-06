package com.example.a220523.ui.pitch;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a220523.R;

public class HighPitchDialogActivity extends Dialog {

    HighPitchInterface customDialogInterface;
    Context context;

    public HighPitchDialogActivity(@NonNull Context context, HighPitchInterface inputInterface) {
        super(context);
        this.context = context;
        customDialogInterface = inputInterface;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highpitch_dialog);

        Button yesButton = findViewById(R.id.dialogButtonYes);
        Button noButton = findViewById(R.id.dialogButtonNo);
        CheckBox neverCheckBox = findViewById(R.id.dialogLastTimeCheckbox);

        getWindow().setBackgroundDrawable(new ColorDrawable(0xfff3f3f3));

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialogInterface.onYesButtonClicked();
                dismiss();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialogInterface.onNoButtonClicked();
                dismiss();
            }
        });

        neverCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.getId() == neverCheckBox.getId()){
                    customDialogInterface.onNevCheckBoxChanged(b);
                }


            }
        });


    }


}
