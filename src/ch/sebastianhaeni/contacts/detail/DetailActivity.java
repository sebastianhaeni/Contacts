package ch.sebastianhaeni.contacts.detail;

import java.util.ArrayList;
import java.util.List;

import ch.sebastianhaeni.contacts.MainActivity;
import ch.sebastianhaeni.contacts.util.DatabaseHelper;
import ch.sebastianhaeni.contacts.util.Person;
import ch.sebastianhaeni.contacts.R;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class DetailActivity extends ListActivity {

	private DatabaseHelper _db;
	private Person _person;
	private List<Item> _items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		// Show the Up button in the action bar.
		setupActionBar();

		getListView().setLongClickable(true);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				_items.get(position).longClick(DetailActivity.this);
				return true;
			}
		});

		// Get the message from the intent
		Intent intent = getIntent();
		String personId = intent.getStringExtra(MainActivity.PERSON_ID);

		_db = DatabaseHelper.getInstance(this);

		try {
			_db.openDataBase();
			Person person = _db.getEmployee(personId);
			_person = person;
			setTitle(person.getFullname());
			if (_db.isFavorite(personId)) {
				getActionBar().setIcon(R.drawable.btn_rating_star_on_normal_holo_dark);
			}

			_db.setFavorite(person);
			setDetails(person);
		} catch (SQLException sqle) {
			throw sqle;
		}

	}

	private void setDetails(Person person) {
		Bitmap profilePicture = _db.getProfilePicture(person);
		ImageView image = (ImageView) findViewById(R.id.profile_picture);
		if (profilePicture != null) {
			BitmapDrawable pic = new BitmapDrawable(getResources(), profilePicture);
			image.setImageDrawable(pic);
		}

		((TextView) findViewById(R.id.detail_fullname)).setText(person.getFullname());
		((TextView) findViewById(R.id.detail_function)).setText(person.getFunction());

		_items = new ArrayList<Item>();
		_items.add(new Header("Company"));
		_items.add(new DetailListItem("Name", person.getCompany(), DetailListItem.TYPES.SEARCH));
		_items.add(new DetailListItem("Function", person.getFunction(), DetailListItem.TYPES.SEARCH));
		_items.add(new DetailListItem("Department", person.getDepartment(), DetailListItem.TYPES.SEARCH));

		_items.add(new Header("Phone"));
		_items.add(new DetailListItem("Work", person.getPhone(), DetailListItem.TYPES.PHONE));
		if (person.getMobilePhone().length() > 0 && !person.getPhone().equals(person.getMobilePhone())) {
			_items.add(new DetailListItem("Mobile", person.getMobilePhone(), DetailListItem.TYPES.PHONE));
		}

		_items.add(new Header("Address"));
		_items.add(new DetailListItem("E-Mail", person.getEmail(), DetailListItem.TYPES.EMAIL));
		_items.add(new DetailListItem("Postal", getPostalAddress(person), DetailListItem.TYPES.MAP, getGeoAddress(person)));
		_items.add(new DetailListItem("Site", person.getSite(), DetailListItem.TYPES.SEARCH));
		_items.add(new DetailListItem("Building", person.getBuilding(), DetailListItem.TYPES.SEARCH));

		if (person.getFloor().length() > 0) {
			_items.add(new DetailListItem("Floor", person.getFloor()));
		}
		if (person.getRoom().length() > 0) {
			_items.add(new DetailListItem("Office", person.getRoom()));
		}

		_items.add(new Header("Misc"));
		_items.add(new DetailListItem("Language", person.getLanguage(), DetailListItem.TYPES.SEARCH));
		_items.add(new DetailListItem("Manager", person.isManager() ? "Yes" : "No"));
		_items.add(new DetailListItem("External", person.isExternal() ? "Yes" : "No"));
		_items.add(new DetailListItem("Deputy", getDeputy(person), DetailListItem.TYPES.PERSON));

		DetailAdapter adapter = new DetailAdapter(this, _items);
		setListAdapter(adapter);
	}

	private String getGeoAddress(Person person) {
		return person.getStreet() + ", " + person.getZip() + " " + person.getCity();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		_items.get(position).click(DetailActivity.this);
	}

	private String getDeputy(Person person) {
		if (person.getDeputy().length() > 0) {
			Person deputy = _db.getEmployee(person.getDeputy());
			if (deputy != null) {
				return deputy.getFullname();
			}
		}
		return "No";
	}

	private String getPostalAddress(Person person) {
		String address = person.getAddress() + "\n" + person.getStreet() + "\n";
		if (person.getMailbox().length() > 0) {
			address += person.getMailbox() + "\n";
		}
		address += person.getZip() + " " + person.getCity() + "\n" + person.getCountry();
		return address;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(R.drawable.ic_contact_picture);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public Person getPerson() {
		return _person;
	}

}
