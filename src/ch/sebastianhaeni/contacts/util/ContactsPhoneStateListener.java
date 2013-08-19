package ch.sebastianhaeni.contacts.util;

import ch.sebastianhaeni.contacts.MainActivity;
import ch.sebastianhaeni.contacts.detail.DetailActivity;
import ch.sebastianhaeni.contacts.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsPhoneStateListener extends PhoneStateListener {
	public final static String PERSON_PHONE = "ch.sebastianhaeni.contacts.PERSON_PHONE.MESSAGE";

	private Context context;

	public ContactsPhoneStateListener(Context context) {
		this.context = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		if (state == TelephonyManager.CALL_STATE_RINGING) {
			DatabaseHelper myDbHelper = new DatabaseHelper(context);

			try {
				myDbHelper.openDataBase();
				Person person = myDbHelper.getEmployeeByNumber(incomingNumber);
				if (person != null) {
					Log.d("CONTACTS", "Incoming: " + incomingNumber);

					showNotification(person, myDbHelper.getProfilePicture(person), incomingNumber);
				}
			} catch (SQLException sqle) {
				throw sqle;
			}

		}

	}

	private void showNotification(final Person person, final Bitmap picture, String incomingNumber) {

		showToast(person, picture);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setContentTitle(person.getFullname())
				.setContentText(incomingNumber + " called you").setSmallIcon(R.drawable.ic_contact_picture);

		if (picture != null) {
			mBuilder.setLargeIcon(picture);
		}

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, DetailActivity.class);

		resultIntent.putExtra(MainActivity.PERSON_ID, person.getId());

		// The stack builder object will contain an artificial back stack for
		// the started Activity. This ensures that navigating backward from the
		// Activity leads out of your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(DetailActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// mId allows you to update the notification later on.
		mNotificationManager.notify(person.getId().hashCode(), mBuilder.build());
	}

	private void showToast(Person person, Bitmap picture) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.toast, null);
		TextView text = (TextView) layout.findViewById(R.id.toast_text);
		text.setText(person.getFullname());

		ImageView image = (ImageView) layout.findViewById(R.id.toast_image);
		if (picture != null) {
			image.setImageBitmap(picture);
		} else {
			image.setVisibility(View.INVISIBLE);
		}

		final Toast toast = new Toast(context);

		// Set The layout as Toast View
		toast.setView(layout);

		// Position you toast here toast position is 35 dp from bottom you can
		// give any integral value
		toast.setGravity(Gravity.BOTTOM, 0, 35);

		// Show toast for 10 seconds (renewing every second)
		new CountDownTimer(10000, 1000) {
			public void onTick(long millisUntilFinished) {
				toast.show();
			}

			public void onFinish() {
				toast.cancel();
			}
		}.start();

	}
}