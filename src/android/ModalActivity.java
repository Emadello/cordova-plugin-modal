package kr.co.purpleworks.cordova.modal;

import org.apache.cordova.CordovaActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class ModalActivity extends CordovaActivity {

    private int dismissOption = 2; // 0=undismissable, 1=X-only, 2=X+back
    private int xPosition = 0;     // 0=hidden, 1=left, 2=right
    private ImageButton closeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        int bottomInAnim = res.getIdentifier("bottom_in", "anim", getPackageName());
        int holdAnim = res.getIdentifier("hold", "anim", getPackageName());
        if (bottomInAnim != 0 && holdAnim != 0) {
            this.overridePendingTransition(bottomInAnim, holdAnim);
        }

        // Make window visible immediately
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

        // White placeholder with spinner
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFFFFFFFF); // white background

        ProgressBar progress = new ProgressBar(this);
        progress.getIndeterminateDrawable().setColorFilter(
                Color.parseColor("#1A1A2E"), // bluish-black
                PorterDuff.Mode.SRC_IN
        );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        root.addView(progress, lp);

        setContentView(root);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Defer Cordova init
        getWindow().getDecorView().post(() -> {
            super.init();

            Intent intent = getIntent();
            String url = intent.getStringExtra(Modal.PARAM_LOAD_URL);
            dismissOption = intent.getIntExtra(Modal.PARAM_DISMISS_OPTION, 3);
            xPosition = intent.getIntExtra(Modal.PARAM_X_POSITION, 0);

            if (url != null) {
                super.loadUrl(url);
            }
            if (xPosition != 0) {
                addCloseButton(xPosition);
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });
    }

    private void addCloseButton(int pos) {
        final FrameLayout root = findViewById(android.R.id.content);
        if (root == null) return;

        final ImageButton closeBtn = new ImageButton(this);
        closeBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeBtn.setBackground(null); // no default gray background

        int dpMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        int dpTop = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());

        final FrameLayout.LayoutParams params;
        if (pos == 1) { // left
            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.START | Gravity.TOP
            );
            params.setMargins(dpMargin, dpTop, 0, 0);
        } else { // default right
            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.END | Gravity.TOP
            );
            params.setMargins(0, dpTop, dpMargin, 0);
        }

        closeBtn.setLayoutParams(params);

        closeBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("param", "x_button_pressed");
            setResult(RESULT_OK, intent);
            finish();
        });

        runOnUiThread(() -> root.addView(closeBtn));
    }

    @Override
    public void onBackPressed() {
        if (dismissOption == 2) {
            Intent intent = new Intent();
            intent.putExtra("param", "back_button_pressed");
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }
        // If dismissOption = 1 or 2 → ignore back press
    }

    @Override
    public void finish() {
        Resources res = getResources();
        int holdAnim = res.getIdentifier("hold", "anim", getPackageName());
        int bottomOutAnim = res.getIdentifier("bottom_out", "anim", getPackageName());

        super.finish();

        if (holdAnim != 0 && bottomOutAnim != 0) {
            this.overridePendingTransition(holdAnim, bottomOutAnim);
        }
    }
}
