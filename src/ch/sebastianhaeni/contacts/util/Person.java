package ch.sebastianhaeni.contacts.util;

import ch.sebastianhaeni.contacts.R;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class Person {
	private String _id;
	private String _firstname;
	private String _lastname;
	private String _email;
	private String _company;
	private String _phone;
	private String _mobilePhone;
	private String _site;
	private String _building;
	private String _floor;
	private String _room;
	private String _language;
	private boolean _manager;
	private boolean _external;
	private String _deputy;
	private String _address;
	private String _street;
	private String _mailbox;
	private String _zip;
	private String _city;
	private String _country;
	private String _function;
	private String _department;

	public Person(String id, String firstname, String lastname, String email, String company, String phone, String mobilePhone,
			String site, String building, String floor, String room, String language, boolean manager, boolean external, String deputy,
			String address, String street, String mailbox, String zip, String city, String country, String function, String department) {
		_id = id;
		_firstname = firstname;
		_lastname = lastname;
		_email = email;
		_company = company;
		_phone = phone;
		_mobilePhone = mobilePhone;
		_site = site;
		_building = building;
		_floor = floor;
		_room = room;
		_language = language;
		_manager = manager;
		_external = external;
		_deputy = deputy;
		_address = address;
		_street = street;
		_mailbox = mailbox;
		_zip = zip;
		_city = city;
		_country = country;
		_function = function;
		_department = department;
	}

	public View getView(LayoutInflater inflater, View convertView, DatabaseHelper db) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.main_list_item, null);
			// Do some initialization
		} else {
			view = convertView;
		}

		TextView txtFullname = (TextView) view.findViewById(R.id.fullname);
		TextView txtIsManager = (TextView) view.findViewById(R.id.isManager);
		txtFullname.setText(getFullname());
		if (isManager()) {
			txtIsManager.setText("Manager");
		} else if (isExternal()) {
			txtIsManager.setText("External");
		} else {
			txtIsManager.setText("");
		}
		// ImageView picture = (ImageView) view
		// .findViewById(R.id.list_item_picture);
		// Bitmap pic = db.getProfilePicture(this);
		// if (pic != null) {
		// picture.setImageBitmap(pic);
		// }

		return view;
	}

	public String getFirstname() {
		return _firstname;
	}

	public String getLastname() {
		return _lastname;
	}

	public String getFullname() {
		return getFirstname() + " " + getLastname();
	}

	@Override
	public String toString() {
		return getFullname();
	}

	public String getId() {
		return _id;
	}

	public String getPhone() {
		return _phone;
	}

	public String getMobilePhone() {
		return _mobilePhone;
	}

	public String getEmail() {
		return _email;
	}

	public String getCompany() {
		return _company;
	}

	public String getSite() {
		return _site;
	}

	public String getBuilding() {
		return _building;
	}

	public String getFloor() {
		return _floor;
	}

	public String getRoom() {
		return _room;
	}

	public String getLanguage() {
		return _language;
	}

	public boolean isManager() {
		return _manager;
	}

	public boolean isExternal() {
		return _external;
	}

	public String getDeputy() {
		return _deputy;
	}

	public String getAddress() {
		return _address;
	}

	public String getStreet() {
		return _street;
	}

	public String getMailbox() {
		return _mailbox;
	}

	public String getZip() {
		return _zip;
	}

	public String getCity() {
		return _city;
	}

	public String getCountry() {
		return _country;
	}

	public String getFunction() {
		return _function;
	}

	public String getDepartment() {
		return _department;
	}

}
