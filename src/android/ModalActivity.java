package kr.co.purpleworks.cordova.modal;

import org.apache.cordova.CordovaActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

public class ModalActivity extends CordovaActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dynamically fetch animation resources
        Resources res = getResources();
        int bottomInAnim = res.getIdentifier("bottom_in", "anim", getPackageName());
        int holdAnim = res.getIdentifier("hold", "anim", getPackageName());

        if (bottomInAnim != 0 && holdAnim != 0) {
            this.overridePendingTransition(bottomInAnim, holdAnim);
        }

        super.init();

        String url = getIntent().getStringExtra(Modal.PARAM_LOAD_URL);
        super.loadUrl(url);
    }

    @Override
    public void finish() {
        // Dynamically fetch animation resources
        Resources res = getResources();
        int holdAnim = res.getIdentifier("hold", "anim", getPackageName());
        int bottomOutAnim = res.getIdentifier("bottom_out", "anim", getPackageName());

        super.finish();

        if (holdAnim != 0 && bottomOutAnim != 0) {
            this.overridePendingTransition(holdAnim, bottomOutAnim);
        }
    }

    @Override
    public void onBackPressed() {
        // Handle the back button press
        Intent intent = new Intent();
        intent.putExtra("param", "back_button_pressed"); // Custom identifier for back button dismiss
        setResult(RESULT_OK, intent); // Pass the result back to the Cordova Plugin
        super.onBackPressed(); // Finish the activity
    }
}
