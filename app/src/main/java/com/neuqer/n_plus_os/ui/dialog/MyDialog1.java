package com.neuqer.n_plus_os.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neuqer.n_plus_os.R;
import com.neuqer.n_plus_os.databinding.ItemDialogBinding;

import androidx.databinding.DataBindingUtil;

public class MyDialog1 extends Dialog {

    private EditText editTitle;
    private EditText editContent;
    private TextView btnConfirm;
    private TextView btnCancel;

    private Activity activity;
    private OnCenterItemClickListener listener;
    public MyDialog1(Activity context){
        super(context, R.style.CustomDialog);//加载dialog的样式
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中
        //dialogWindow.setWindowAnimations();设置动画效果
        setContentView(R.layout.item_dialog);

        editTitle = findViewById(R.id.item_dialog_title_edit);
        editContent = findViewById(R.id.item_dialog_content_edit);
        btnConfirm = findViewById(R.id.item_dialog_btn_confirm);
        btnCancel = findViewById(R.id.item_dialog_btn_cancel);

        btnConfirm.setOnClickListener(v -> {
            try {
                listener.onConfirmClick(editTitle.getText().toString().trim(),
                        editContent.getText().toString().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnCancel.setOnClickListener(v -> {
            listener.onCancelClick();
        });

        WindowManager windowManager = ((Activity)activity).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失


    }

    public interface OnCenterItemClickListener {
        void onConfirmClick(String title, String content) throws Exception;
        void onCancelClick();
    }
    //很明显我们要在这里面写个接口，然后添加一个方法
    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }


}
