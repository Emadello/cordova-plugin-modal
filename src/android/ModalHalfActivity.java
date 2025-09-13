package kr.co.purpleworks.cordova.modal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import org.apache.cordova.CordovaActivity;

public class ModalHalfActivity extends CordovaActivity {
    private int dismissOption = 2; // 0=undismissable, 1=X-only, 2=X+back
    private int xPosition = 0;     // 0=hidden, 1=left, 2=right

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;
        int themeId = context.getResources().getIdentifier("Theme_ModalHalf", "style", context.getPackageName());
        setTheme(themeId); // transparent half-sheet theme

        // Animations
        Resources res = getResources();
        int bottomInAnim = res.getIdentifier("bottom_in", "anim", getPackageName());
        int holdAnim = res.getIdentifier("hold", "anim", getPackageName());
        if (bottomInAnim != 0 && holdAnim != 0) {
            this.overridePendingTransition(bottomInAnim, holdAnim);
        }

        super.init();

        // Parameters
        Intent i = getIntent();
        String url = i.getStringExtra(Modal.PARAM_LOAD_URL);
        dismissOption = i.getIntExtra("dismissOption", 3);
        xPosition = i.getIntExtra("xPosition", 0);

        if (url != null && !url.isEmpty()) {
            super.loadUrl(url);
        }

        // Half height + bottom
        getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDisplayMetrics().heightPixels / 2
        );
        getWindow().setGravity(Gravity.BOTTOM);

        // Add X button if required
        if (xPosition != 0) {
            addCloseButton(xPosition);
        }
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
        // dismissOption 1 or 2: ignore back press
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
