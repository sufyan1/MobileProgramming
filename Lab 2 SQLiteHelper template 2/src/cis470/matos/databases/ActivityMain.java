// demonstrates the reading of XML resource files using 
// a SAX XmlPullParser
// ---------------------------------------------------------------------
package cis470.matos.databases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityMain extends Activity {

	private TextView txtMsg;
	Button btnGoParser;
	private Button btnWriteSDFile;
	private Button dbBtn;
	private String mySdPath;
	MySQLiteHelper dbs;
	SQLiteDatabase db;
	//private String myDbPath1 = "data/data/cis470.matos.databases/";
	Map <String,String> myMap  = new HashMap <String,String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		txtMsg = (TextView) findViewById(R.id.txtMsg);

		btnGoParser = (Button) findViewById(R.id.btnReadXml);
		btnGoParser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnGoParser.setEnabled(false);
				// do slow XML reading in a separated thread
				//Integer xmlResFile = R.xml.manakiki_golf_course;/*************************/
				Integer xmlResFile = R.xml.employees;

				new backgroundAsyncTask().execute(xmlResFile);
				Toast.makeText(getBaseContext(),
						"Done reading xml file employees list updated " ,
						Toast.LENGTH_SHORT).show();

			}
		});

		mySdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		btnWriteSDFile = (Button) findViewById(R.id.btnWriteSDFile);
		btnWriteSDFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
//					dbs.addBook(new Book("Android Application Development Cookbook", "Wei Meng Lee"));
//					dbs.addBook(new Book("Android Programming: The Big Nerd Ranch Guide", "Bill Phillips and Brian Hardy"));
//					dbs.addBook(new Book("Learn Android App Development", "Wallace Jackson"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}// onClick
		}); // btnWriteSDFile

		dbBtn = (Button) findViewById(R.id.buttondb);
		dbBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent dbIntent = new Intent (ActivityMain.this,
						Activity2.class);
				// create a Bundle (MAP) container to ship data
				// call Activity1, tell your local listener to wait a
				// response sent to a listener known as 101
				startActivityForResult(dbIntent, 101);
			}
		});
		//dbs = new MySQLiteHelper(this);
	}// onCreate

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			if ((requestCode == 101) && (resultCode == Activity.RESULT_OK)) {
				//Bundle myResultBundle = data.getExtras();
				//Double myResult = myResultBundle.getDouble("result");
				txtMsg.setText("returned from database handler ");
			}
		}
			catch(Exception e){
				txtMsg.setText("Problems - " + requestCode + " " + resultCode);
			}
	}
	/*******************************************************************************************
	*SQL methods
    *******************************************************************************************/



	/*******************************************************************************************
	 *reading XML
	 *******************************************************************************************/
	public class backgroundAsyncTask extends
			AsyncTask<Integer, Void, List> {

		
		ProgressDialog dialog = new ProgressDialog(ActivityMain.this);

		public List<Employee> getEmpList() {
			return empList;
		}

		List<Employee> empList = new ArrayList<>();

		String content = null;

		@Override
		protected void onPostExecute(List empList1) { // after reading xml
			super.onPostExecute(empList1);//
			dialog.dismiss();

			for(Employee emp: empList) {
				txtMsg.append(emp.toString()+"\n");
			}

			openDatabase(); // open (create if needed) database
			dropTable(); // if needed drop table tblAmigos
			insertSomeDbData();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected List doInBackground(Integer... params) {
			Employee emp = null;
			int xmlResFile = params[0];
			XmlPullParser parser = getResources().getXml(xmlResFile);
					
			StringBuilder stringBuilder = new StringBuilder();
			String nodeText = "";
			String nodeName = "";


			try {
				int eventType = -1;
				while (eventType != XmlPullParser.END_DOCUMENT) {

					eventType = parser.next();

					if (eventType == XmlPullParser.START_DOCUMENT) {
						stringBuilder.append("\nSTART_DOCUMENT");

					} else if (eventType == XmlPullParser.END_DOCUMENT) {
						stringBuilder.append("\nEND_DOCUMENT");

					} else if (eventType == XmlPullParser.START_TAG) {
						nodeName = parser.getName();
						stringBuilder.append("\nSTART_TAG: " + nodeName);
						//stringBuilder.append(getAttributes(parser));

						switch(nodeName){
							//Create a new Employee object when the start tag is found
							case "employee":
								emp = new Employee();
								emp.attributes = getAttributes(parser);
								break;
						}
					}
					else if (eventType == XmlPullParser.END_TAG) {
						nodeName = parser.getName();
						switch(nodeName) {
							//For all other end tags the employee has to be updated.
							case "employee":
								empList.add(emp);
								break;
							case "FNAME":
								emp.firstName = nodeText;
								break;
							case "MINIT":
								emp.minit = nodeText;
								break;
							case "LNAME":
								emp.lastName = nodeText;
								break;
							case "SSN":
								emp.ssn = nodeText;
								break;
							case "BDATE":
								emp.bdate = nodeText;
								break;
							case "ADDRESS":
								emp.address = nodeText;
								break;
							case "SEX":
								emp.sex = nodeText;
								break;
							case "SALARY":
								emp.salary = nodeText;

							//	ssalary

								break;
							case "SUPERSSN":
								emp.superssn = nodeText;
								break;
							case "DNO":
								emp.dno = nodeText;
								break;
						}
						stringBuilder.append("\nEND_TAG:   " + nodeName );

					} else if (eventType == XmlPullParser.TEXT) {
						nodeText = parser.getText();
						stringBuilder.append("\n    TEXT: " + nodeText);
					}
				}
			} catch (Exception e) {
				Log.e("<<PARSING ERROR>>", e.getMessage());
			}

			return empList;
		}// doInBackground

		private String[] getAttributes(XmlPullParser parser) {
			StringBuilder stringBuilder = new StringBuilder();
			// trying to detect inner attributes nested inside a node tag
			String name = parser.getName();
			String[] attributesList = null;
			if (name != null) {
				int size = parser.getAttributeCount();
				attributesList = new String[size];
				for (int i = 0; i < size; i++) {
					String attrName = parser.getAttributeName(i);

					String SALARY = parser.getAttributeValue(i);

					//myMap.put(attrName, attrValue);
					attributesList[i] = (" " + attrName + "=" + SALARY + "\n");
					//stringBuilder.append("key =" + attrName + " value = " + attrValue + "\n");
				}
			}
			return attributesList;
		}// innerElements

		private void dropTable() {
			// (clean start) action query to drop table
			try {
				db.execSQL("DROP TABLE IF EXISTS tableDB;");
				txtMsg.append("\n-dropTable - dropped!!");
			} catch (Exception e) {
				txtMsg.append("\nError dropTable: " + e.getMessage());
				finish();
			}
		}

		private void openDatabase() {
			try {
				String myDbPath = mySdPath  + "/myDB1.db";
				txtMsg.append("\n-openDatabase - DB Path: " + myDbPath);

				db = SQLiteDatabase.openDatabase(myDbPath, null,
						SQLiteDatabase.CREATE_IF_NECESSARY);

				txtMsg.append("\n-openDatabase - DB was opened");
			} catch (SQLiteException e) {
				txtMsg.append("\nError openDatabase: " + e.getMessage());
				finish();
			}
		}// createDatabase

		private void insertSomeDbData() {
			// create table: tblAmigo
			db.beginTransaction();
			try {
				// create table
				db.execSQL("create table tableDB ("
						+ " ID integer PRIMARY KEY autoincrement, "
						+ " FirstName  text, " + " Minit text , "
						+ " Lastname  text, " + " ssn text , "
						+ " bdate  text, " + " address text , "
						+ " sex  text, " + " salary text , "
						+ " superssn  text, "
						+ " dno text );  ");
				// commit your changes
				db.setTransactionSuccessful();

				txtMsg.append("\n-insertSomeDbData - Table was created");

			} catch (SQLException e1) {
				txtMsg.append("\nError insertSomeDbData: " + e1.getMessage());
				finish();
			} finally {
				db.endTransaction();
			}
			// populate table: tblAmigo
			db.beginTransaction();
			try {
				for(Employee emp: empList) {
//					db.execSQL("insert into tableDB(firstName, lastName, location) "
//							+ " values ('"+ (emp.getFirstName())+
//							"', '" + (emp.getFirstName()) + "', '"
//							+ (emp.getLocation())+"', );");

					ContentValues values = new ContentValues();
					values.put("FirstName", emp.getFirstName()); // get first name
					values.put("Minit", emp.getMinit()); // get minit
					values.put("Lastname", emp.getLastName()); // get lastname
					values.put("ssn", emp.getSsn()); // get lastname
					values.put("bdate", emp.getBdate()); // get lastname
					values.put("address", emp.getAddress()); // get lastname
					values.put("sex", emp.getSex()); // get lastname
					values.put("salary", emp.getSalary()); // get lastname
					values.put("superssn", emp.getSuperssn()); // get lastname
					values.put("dno", emp.getDno()); // get lastname
					//gethighestSalary()

/*
public String getSsn(){
			return ssn;
		}
		public String getBdate(){
			return bdate;
		}
		public String getAddress(){
			return address;
		}
		public String getSex(){
			return sex;
		}
		public String getSalary(){
			return salary;
		}
		public String getSuperssn(){
			return superssn;
		}
		public String getDno(){
			return dno;
		}

 */
					// 3. insert
					db.insert("tableDB", // table
							null, //nullColumnHack
							values); // key/value -> keys = column names/ values = column values
				}


				// insert rows
//				db.execSQL("insert into tblAMIGO(name, phone) "
//						+ " values ('BBB', 'aaaaaaaaa' );");
//				db.execSQL("insert into tblAMIGO(name, phone) "
//						+ " values ('BBB', 'bbbbbbbbbb' );");
//				db.execSQL("insert into tblAMIGO(name, phone) "
//						+ " values ('CCC', 'zzzzzzzzzzz' );");



				// commit your changes
				db.setTransactionSuccessful();
				txtMsg.append("\n-insertSomeDbData - 3 rec. were inserted");

			} catch (SQLiteException e2) {
				txtMsg.append("\nError insertSomeDbData: " + e2.getMessage());
			} finally {
				db.endTransaction();
			}
		}// insertSomeData
	}// backroundAsyncTask

	class Employee {
		String id;
		String firstName;
		String minit;
		String lastName;
		String ssn;
		String bdate;
		String address;
		String sex;
		String salary;
		String superssn;
		String dno;
		int hsalary=0;
		int ssalary=0;

		String [] attributes;

		@Override
		public String toString() {
			return "Employee "+ "\n" + " \nFN:" +firstName + " \nMINIT: " + minit + " \nLNAME: " + lastName
					+ " \nSSN: " + ssn+ " \nBDATE: " + bdate+ " \nAddress: " + address
					+ " \nSEX: " + sex+ " \nSALARY: " + salary+ " \nSuperssn: " + superssn
					+ " \nDNO: " + dno+ " \n"+firstName + " salary is "+salary+ " \n";
		}
		public String getID(){
			return id;
		}
		public String getFirstName(){
			return firstName;
		}
		public String getMinit(){
			return minit;
		}
		public String getLastName(){
			return lastName;
		}
		public String getSsn(){
			return ssn;
		}
		public String getBdate(){
			return bdate;
		}
		public String getAddress(){
			return address;
		}
		public String getSex(){
			return sex;
		}
		public String getSalary(){
			return salary;
		}
		public int gethighestSalary(){
			if(ssalary>=hsalary){
				hsalary=ssalary;
			}
			return hsalary;
		}
		public String getSuperssn(){
			return superssn;
		}
		public String getDno(){
			return dno;
		}


	}
}// ActivityMain