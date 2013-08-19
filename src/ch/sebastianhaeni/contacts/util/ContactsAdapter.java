package ch.sebastianhaeni.contacts.util;

import java.util.List;

import ch.sebastianhaeni.contacts.detail.DetailAdapter.RowType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ContactsAdapter extends ArrayAdapter<Person> {
	private LayoutInflater _inflater;
	private DatabaseHelper _db;

	public ContactsAdapter(Context context, List<Person> items, DatabaseHelper db) {
		super(context, 0, items);
		_inflater = LayoutInflater.from(context);
		_db = db;
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;

	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItem(position).getView(_inflater, convertView, _db);
	}

}
