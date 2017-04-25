package magic.slocksocket.mina;

import android.content.Context;
import android.content.Intent;

public class BroadcastHelper {

	public static void sendBroadCast(Context context, String action,
			String key, String value) {
		Intent intent = new Intent();
		intent.setAction(action);
//		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(key, value);
		context.sendBroadcast(intent);
	}

	public static void sendBroadCast(Context context, String action,
			String key, int value) {
		Intent intent = new Intent();
		intent.setAction(action);
//		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(key, value);
		context.sendBroadcast(intent);
	}

	// public static void toPage(Context context,int index) {
	// String action = Constants.WHERE_PAGER_ACTION;
	// String key = Constants.WHERE_PAGER_KEY;
	// BroadcastHelper.sendBroadCast(context, action, key, index);
	// }
}
