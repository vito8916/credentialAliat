package com.vicxbox.micredencial.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.widget.AppCompatButton;
import com.vicxbox.micredencial.R;

public class DialogNetworkChecker {

    Context mcontext;
    Dialog dialog;

    public DialogNetworkChecker(Context mcontext) {
        this.mcontext = mcontext;
    }

    public void showDialogHelpRequest(){
        dialog = new Dialog(mcontext);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);

        dialog.show();

    }

    public void DimmisNetworkDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

}
