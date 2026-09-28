package kr.co.purpleworks.cordova.modal;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import org.apache.cordova.CordovaActivity;

public class ModalActivity extends CordovaActivity {

  @Override
  protected boolean showInitialSplashScreen() {
    return false;
  }

  private int dismissOption = 2; // 0=undismissable, 1=X-only, 2=X+back
  private int xPosition = 0; // 0=hidden, 1=left, 2=right
  private ImageButton closeButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Resources res = getResources();

    int bottomInAnim = res.getIdentifier("bottom_in", "anim", getPackageName());

    int holdAnim = res.getIdentifier("hold", "anim", getPackageName());

    if (bottomInAnim != 0 && holdAnim != 0) {
      overridePendingTransition(bottomInAnim, holdAnim);
    }

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    // Temporary white loading view
    FrameLayout loadingRoot = new FrameLayout(this);
    loadingRoot.setBackgroundColor(Color.WHITE);

    ProgressBar progress = new ProgressBar(this);
    progress
      .getIndeterminateDrawable()
      .setColorFilter(Color.parseColor("#1A1A2E"), PorterDuff.Mode.SRC_IN);

    FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT,
      Gravity.CENTER
    );

    loadingRoot.addView(progress, progressParams);
    setContentView(loadingRoot);

    /*
     * This must run synchronously in onCreate().
     * SystemWebChromeClient registers an ActivityResultLauncher,
     * which must happen before the Activity reaches STARTED.
     *
     * super.init() also attaches appView to the Activity, so do not
     * call setContentView(appView.getView()) afterward.
     */
    super.init();

    Intent intent = getIntent();

    String url = intent.getStringExtra(Modal.PARAM_LOAD_URL);

    dismissOption = intent.getIntExtra(Modal.PARAM_DISMISS_OPTION, 2);

    xPosition = intent.getIntExtra(Modal.PARAM_X_POSITION, 0);

    appView.getView().setAlpha(0f);

    if (url != null && !url.isEmpty()) {
      super.loadUrl(url);
    }

    if (xPosition != 0) {
      addCloseButton(xPosition);
    }

    appView.getView().animate().alpha(1f).setDuration(150).start();

    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
  }

  private void addCloseButton(int pos) {
    final FrameLayout root = findViewById(android.R.id.content);
    if (root == null) return;

    final ImageButton closeBtn = new ImageButton(this);
    closeBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
    closeBtn.setBackground(null); // no default gray background

    int dpMargin = (int) TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      16,
      getResources().getDisplayMetrics()
    );
    int dpTop = (int) TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      24,
      getResources().getDisplayMetrics()
    );

    final FrameLayout.LayoutParams params;
    if (pos == 1) {
      // left
      params = new FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT,
        Gravity.START | Gravity.TOP
      );
      params.setMargins(dpMargin, dpTop, 0, 0);
    } else {
      // default right
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
    int bottomOutAnim = res.getIdentifier(
      "bottom_out",
      "anim",
      getPackageName()
    );

    super.finish();

    if (holdAnim != 0 && bottomOutAnim != 0) {
      this.overridePendingTransition(holdAnim, bottomOutAnim);
    }
  }
}
