package kr.co.purpleworks.cordova.modal;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;

public class Modal extends CordovaPlugin {
	private static final int ACTIVITY_MODAL = 1001; // modal activity request code
	public static final String PARAM_LOAD_URL = "loadUrl";
	public static final String PARAM_DISMISS_OPTION = "dismissOption";
	public static final String PARAM_X_POSITION = "xPosition";
	public static final String PARAM_IS_HALF = "isHalf";

	private CallbackContext callbackContext;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;

		if (action.equals("open")) {
			String url = args.getString(0);
			int dismissOption = args.length() > 1 ? args.getInt(1) : 2;
			int xPosition = args.length() > 2 ? args.getInt(2) : 0;

			Intent intent = new Intent(this.cordova.getActivity(), ModalActivity.class);
			intent.putExtra(PARAM_LOAD_URL, url);
			intent.putExtra(PARAM_DISMISS_OPTION, dismissOption);
			intent.putExtra(PARAM_X_POSITION, xPosition);

			this.cordova.setActivityResultCallback(this);
			this.cordova.getActivity().startActivityForResult(intent, ACTIVITY_MODAL);
			return true;

		} else if (action.equals("openHalf")) {
			String url = args.getString(0);
			int dismissOption = args.length() > 1 ? args.getInt(1) : 2;
			int xPosition = args.length() > 2 ? args.getInt(2) : 0;

			Intent intent = new Intent(this.cordova.getActivity(), ModalHalfActivity.class);
			intent.putExtra(PARAM_LOAD_URL, url);
			intent.putExtra(PARAM_DISMISS_OPTION, dismissOption);
			intent.putExtra(PARAM_X_POSITION, xPosition);

			this.cordova.setActivityResultCallback(this);
			this.cordova.getActivity().startActivityForResult(intent, ACTIVITY_MODAL);
			return true;
		} else if (action.equals("close")) {
			if (cordova.getActivity() instanceof ModalActivity) {
				Intent intent = new Intent();
				intent.putExtra("param", args.length() > 0 ? args.getString(0) : "closed_by_js");

				this.cordova.getActivity().setResult(Activity.RESULT_OK, intent);
				this.cordova.getActivity().finish();
			} else if (cordova.getActivity() instanceof ModalHalfActivity) {
				Intent intent = new Intent();
				intent.putExtra("param", args.length() > 0 ? args.getString(0) : "closed_by_js");

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
					this.callbackContext.success(param);
				} else {
					this.callbackContext.success("closed_without_data");
				}
			} else {
				this.callbackContext.error("modal_dismissed_with_error");
			}
		}
	}
}
