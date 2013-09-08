package ch.sebastianhaeni.contacts;

import java.util.Iterator;
import java.util.List;

import ch.sebastianhaeni.contacts.detail.DetailActivity;
import ch.sebastianhaeni.contacts.util.DatabaseHelper;
import ch.sebastianhaeni.contacts.util.ContactsAdapter;
import ch.sebastianhaeni.contacts.util.Person;
import ch.sebastianhaeni.contacts.R;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends ListActivity {

	public final static String PERSON_ID = "ch.sebastianhaeni.contacts.PERSON_ID.MESSAGE";

	private DatabaseHelper _db;
	private ContactsAdapter _adapter;

	protected boolean _foundEasteregg = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_db = DatabaseHelper.getInstance(this);
		final List<Person> persons;

		try {
			if (!_db.openDataBase()) {
				return;
			}
			persons = _db.getFavorites();
		} catch (SQLException sqle) {
			throw sqle;
		}

		_adapter = new ContactsAdapter(this, persons, _db);
		setListAdapter(_adapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(MainActivity.this, DetailActivity.class);
				intent.putExtra(PERSON_ID, ((Person) getListView().getItemAtPosition(position)).getId());
				startActivity(intent);
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				if (_db.isFavorite(_adapter.getItem(position).getId())) {
					AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
					dialog.setTitle("Delete from favorites?");
					dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int buttonId) {
						}
					});
					dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int buttonId) {
							_db.removeFavorite(_adapter.getItem(position));
							_adapter.clear();
							_adapter.addAll(_db.getFavorites());
						}
					});
					dialog.show();
				} else {
					AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
					dialog.setTitle("Add to favorites?");
					dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int buttonId) {
						}
					});
					dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int buttonId) {
							_db.setFavorite(_adapter.getItem(position));
							_adapter.clear();
							_adapter.addAll(_db.getFavorites());
						}
					});
					dialog.show();
				}
				return true;
			}
		});
		((ImageButton) findViewById(R.id.btnClearTxtSearch)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText txtSearch = (EditText) findViewById(R.id.txtSearch);
				(txtSearch).setText("");
				txtSearch.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(txtSearch, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		((EditText) findViewById(R.id.txtSearch)).requestFocus();
		final EditText filter = (EditText) findViewById(R.id.txtSearch);
		filter.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String filterText = filter.getText().toString();
				UpdateList(filterText);
				if (filterText.equals("rocket science") && !_foundEasteregg) {
					_foundEasteregg = true;
					Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					long[] pattern = { 0, 50, 100, 50, 100, 50, 100, 400, 100, 300, 100, 350, 50, 200, 100, 100, 50, 600 };
					v.vibrate(pattern, -1);
				} else {
					_foundEasteregg = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		Intent intent = getIntent();
		String query = intent.getStringExtra(SearchManager.QUERY);
		if (query != null && query.length() > 0) {
			((EditText) findViewById(R.id.txtSearch)).setText(query);
			UpdateList(query);
		}
	}

	protected void UpdateList(String filter) {
		_adapter.clear();
		_adapter.addAll(_db.getEmployees(filter));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (((EditText) findViewById(R.id.txtSearch)).getText().toString().length() > 0) {
			return;
		}
		if (_adapter == null) {
			return;
		}
		_adapter.clear();
		_adapter.addAll(_db.getFavorites());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clear_favorites:
			clearFavorites();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void clearFavorites() {
		List<Person> favorites = _db.getFavorites();
		Iterator<Person> i = favorites.iterator();
		while (i.hasNext()) {
			_db.removeFavorite(i.next());
		}
		_adapter.clear();
	}

}
