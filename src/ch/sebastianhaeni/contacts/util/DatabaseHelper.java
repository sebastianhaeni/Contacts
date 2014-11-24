package ch.sebastianhaeni.contacts.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String DB_NAME = "contacts.s3db";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	private boolean _preloadingEmployees;

	protected List<Person> _persons;

	private static DatabaseHelper _instance;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Returns singleton instance.
	 */
	public static DatabaseHelper getInstance(Context context) {
		if (_instance == null) {
			_instance = new DatabaseHelper(context);
		}
		return _instance;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDatabase() throws IOException {

		boolean dbExist = checkDatabase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path of your application so we are gonna be
			// able to overwrite that database with our database.
			this.getReadableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDatabase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = getDbPath();
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transferring bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = getDbPath();

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public boolean openDataBase() throws SQLException {
		if (myDataBase != null) {
			return true;
		}
		// Open the database
		String myPath = getDbPath();
		if (myPath != null) {
			myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
			return true;
		}
		return false;
	}

	private String getDbPath() {
		File externalFilesDir = myContext.getExternalFilesDir(null);

		if (externalFilesDir == null) {
			Log.e("CONTACTS", "No external storage device found!");
			AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
			builder.setTitle("Database not found!").setMessage("External storage is required to use this app.").setCancelable(false)
					.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int buttonId) {
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					}).create().show();

			return null;
		}

		String path = myContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + DB_NAME;
		File file = new File(path);
		if (!file.exists()) {
			Log.e("CONTACTS", "Database not found where expected: " + path);

			AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
			builder.setTitle("Database not found!").setMessage("Please copy database manually to this location:\n" + path)
					.setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int buttonId) {
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					}).create().show();

			return null;
		}
		return path;
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("CONTACTS", "Database onCreate called");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public List<Person> getEmployees() {
		if (_persons != null) {
			return _persons;
		}
		if (_preloadingEmployees) {
			while (_persons == null) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return _persons;
		}

		_persons = loadEmployees();
		return _persons;
	}

	public List<Person> getEmployees(String string) {
		List<Person> persons = new ArrayList<Person>();

		if (string.length() >= 3) {
			Cursor cur = myDataBase.rawQuery("SELECT * FROM employee WHERE employee match ? ORDER BY firstname, lastname LIMIT 100",
					new String[] { string + "*" });

			while (cur.moveToNext()) {
				persons.add(getPerson(cur));
			}
			cur.close();
		}
		return persons;
	}

	private Person getPerson(Cursor cur) {
		return new Person(cur.getString(0), cur.getString(1), cur.getString(2), cur.getString(3), cur.getString(4), cur.getString(5),
				cur.getString(6), cur.getString(7), cur.getString(8), cur.getString(9), cur.getString(10), cur.getString(11),
				1 == cur.getInt(12), 1 == cur.getInt(13), cur.getString(14), cur.getString(15), cur.getString(16), cur.getString(17),
				cur.getString(18), cur.getString(19), cur.getString(20), cur.getString(21), cur.getString(22));
	}

	public Person getEmployee(String personId) {
		Cursor cur = myDataBase.rawQuery("SELECT * FROM employee WHERE idUser = ?", new String[] { personId });

		if (cur.getCount() != 1) {
			return null;
		}
		cur.moveToFirst();
		Person person = getPerson(cur);
		cur.close();
		return person;
	}

	public Person getEmployeeByNumber(String incomingNumber) {
		Log.i("CONTACTS", "Looking up " + incomingNumber);
		Cursor cur = myDataBase.rawQuery(
				"SELECT * FROM employee WHERE replace(phone, ' ', '') LIKE ? OR replace(mobile_phone, ' ', '') LIKE ?", new String[] {
						incomingNumber, incomingNumber });

		if (cur.getCount() == 0) {
			return null;
		}

		cur.moveToFirst();
		Person person = getPerson(cur);
		cur.close();
		return person;
	}

	public List<Person> getFavorites() {
		Cursor cur = myDataBase.rawQuery(
				"SELECT * FROM employee INNER JOIN favorite ON employee.idUser = favorite.idUser ORDER BY idFavorite DESC LIMIT 50",
				new String[] {});

		List<Person> persons = new ArrayList<Person>();
		while (cur.moveToNext()) {
			persons.add(getPerson(cur));
		}
		cur.close();
		return persons;
	}

	public void setFavorite(Person person) {
		String[] args = new String[] { person.getId() };
		removeFavorite(person);
		myDataBase.execSQL("INSERT INTO favorite (idUser) VALUES (?)", args);
	}

	public void preloadEmployees() {
		new Thread(new Runnable() {
			public void run() {
				_preloadingEmployees = true;
				_persons = loadEmployees();
			}
		}).start();
	}

	protected List<Person> loadEmployees() {
		Cursor cur = myDataBase.rawQuery("SELECT * FROM employee ORDER BY firstname", new String[] {});

		List<Person> persons = new ArrayList<Person>();
		while (cur.moveToNext()) {
			persons.add(getPerson(cur));
		}
		cur.close();
		return persons;
	}

	public boolean isFavorite(String id) {
		Cursor cur = myDataBase.rawQuery("SELECT COUNT(*) FROM favorite WHERE idUser = ?", new String[] { id });
		cur.moveToFirst();
		boolean result = cur.getInt(0) == 1;
		cur.close();
		return result;
	}

	public void removeFavorite(Person person) {
		myDataBase.execSQL("DELETE FROM favorite WHERE idUser LIKE ?", new String[] { person.getId() });
	}

	public Bitmap getProfilePicture(Person person) {
		Cursor cur = myDataBase.rawQuery("SELECT * FROM image WHERE idUser LIKE ?", new String[] { person.getId() });
		if (cur.getCount() != 1) {
			return null;
		}
		cur.moveToFirst();
		byte[] blob = cur.getBlob(2);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
		cur.close();
		return BitmapFactory.decodeStream(inputStream);
	}

	// Add your public helper methods to access and get content from the
	// database. You could return cursors by doing
	// "return myDataBase.query(....)" so it'd
	// be easy to you to create adapters for your views.

}