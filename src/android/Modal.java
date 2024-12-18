package kr.co.purpleworks.cordova.modal;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;

public class Modal extends CordovaPlugin {
	private static final int ACTIVITY_MODAL = 1001;							// modal activity request code
	public static final String PARAM_LOAD_URL = "loadUrl";					// passing data

	private CallbackContext callbackContext;

	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
        if (action.equals("open")) {
            String url = args.getString(0);
            Intent intent = new Intent(this.cordova.getActivity(), ModalActivity.class);
            intent.putExtra(PARAM_LOAD_URL, url);
            this.cordova.setActivityResultCallback(this);
            this.cordova.getActivity().startActivityForResult(intent, ACTIVITY_MODAL);
            return true;
        } else if(action.equals("close")) {
        	if (cordova.getActivity() instanceof ModalActivity) {
        		Intent intent = new Intent();
        		intent.putExtra("param", args.getString(0));

        		this.cordova.getActivity().setResult(Activity.RESULT_OK, intent);
        		this.cordova.getActivity().finish();
			} else {
				callbackContext.error("Not ModalActivity");
			}
        	return true;
        }
        return false;
    }

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == ACTIVITY_MODAL) {
			if (resultCode == Activity.RESULT_OK) {
				String param = intent.getStringExtra("param");
				if (param != null) {
					this.callbackContext.success(param); // Pass the result back to the JavaScript layer
				} else {
					this.callbackContext.success("closed_without_data"); // Default case for closure without specific data
				}
			} else {
				this.callbackContext.error("modal_dismissed_with_error"); // Error case
			}
		}
	}
}
