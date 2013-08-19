package ch.sebastianhaeni.contacts.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallActionsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		TelephonyManager manager = (TelephonyManager) arg0.getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(new ContactsPhoneStateListener(arg0), android.telephony.PhoneStateListener.LISTEN_CALL_STATE);

	}

}