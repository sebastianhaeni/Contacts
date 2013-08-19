package ch.sebastianhaeni.contacts.detail;

import ch.sebastianhaeni.contacts.detail.DetailAdapter.RowType;
import ch.sebastianhaeni.contacts.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class Header extends Item {
	private final String name;

	public Header(String name) {
		this.name = name;
	}

	@Override
	public int getViewType() {
		return RowType.HEADER_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView, Context context) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.detail_header, null);
			// Do some initialization
		} else {
			view = convertView;
		}

		TextView text = (TextView) view.findViewById(R.id.separator);
		text.setText(name);

		return view;
	}

}