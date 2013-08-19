package ch.sebastianhaeni.contacts.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class Item {
	public abstract int getViewType();

	public abstract View getView(LayoutInflater inflater, View convertView, Context context);

	public void click(DetailActivity context) {
	}

	public void longClick(DetailActivity detailActivity) {
	}
}