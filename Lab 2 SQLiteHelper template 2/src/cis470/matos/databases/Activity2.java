package cis470.matos.databases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Activity2 extends Activity implements OnClickListener{
	EditText txtMsg;
	Button  btnDone;
	private Button btnReadSDFile;
	//private Button btnUpdate;
	private Button btnDelete;
	private Button btnAdd;
	private  String mySdPath;
	private EditText f1;
	private EditText f2;
	private EditText f3;
	private EditText f4;
	SQLiteDatabase db;
	MySQLiteHelper dbs;
	//private String myDbPath1 = "data/data/cis470.matos.databases/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		txtMsg = (EditText) findViewById(R.id.etDataReceived);
		btnDone = (Button) findViewById(R.id.btnDone);
		//btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnDone.setOnClickListener(this);
		mySdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		Intent myLocalIntent = getIntent();
		f1 = (EditText)findViewById(R.id.editText1);
		f2 = (EditText)findViewById(R.id.editText2);
		f3= (EditText)findViewById(R.id.editText3);
		f4= (EditText)findViewById(R.id.editText4);

		dbs = new MySQLiteHelper(this);
		/**********************************************/

		btnReadSDFile = (Button) findViewById(R.id.buttonsd);
		btnReadSDFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Double v1 = Double.parseDouble(txtValue1.getText().toString());
				String tableName = f1.getText().toString();
				String tableName2 = f2.getText().toString();
				String tableName3 = f3.getText().toString();
				String tableName4 = f4.getText().toString();

				//showTable(tableName);
			}// onClick
		});

	/*	btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useUpdateMethod();
				//updateDB();
			}// onClick
		});
*/
		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String tableName = f1.getText().toString();
				String tableName2 = f2.getText().toString();
				dbs.helperUseDeleteMethod(tableName);
			}// onClick
		});

		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String tableName = f1.getText().toString();
				String tableName2 = f2.getText().toString();
				String tableName3 = f3.getText().toString();
				String tableName4 = f4.getText().toString();

				//addMethod(tableName, tableName2);
				dbs.helperAddMethod(tableName,tableName2,tableName3,tableName4);
			}// onClick
		});
		//openDatabase();
		setResult(Activity.RESULT_OK, myLocalIntent);
	}

	/*********************************************************************************************/
	// button methods
	/*******************************************************************************/

	private void showTable(String tableName) {
		try {
			String sql = "select * from " + tableName ;
			Cursor c = db.rawQuery(sql, null);
			txtMsg.append("\n-showTable: " + tableName + showCursor(c));
		} catch (Exception e) {
			txtMsg.append("\nError showTable: " + e.getMessage());

		}
	}// useCursor1

	private void openDatabase() {
		try {
			// path to private memory:data/data/cis470.matos.databases/myfriendsDB2.db
			//myDbPath = "data/data/cis470.matos.databases/";
			//mySdPath = Environment.getExternalStorageDirectory().getPath();
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

	private void dropTable() {
		try {
			db.execSQL("DROP TABLE IF EXISTS tblAmigo;");
			txtMsg.append("\n-dropTable - dropped!!");
		} catch (Exception e) {
			txtMsg.append("\nError dropTable: " + e.getMessage());
			finish();
		}
	}



	private void useUpdateMethod() {
		try {
			// using the 'update' method to change name of selected friend
			String[] whereArgs = { "2" };

			ContentValues updValues = new ContentValues();
			updValues.put("name", "UPDATE");

			int recAffected = db.update("tblAMIGO", updValues,
					"recID = ? ", whereArgs);

			txtMsg.append("\n-useUpdateMethod - Rec Affected " + recAffected);
			showTable("tblAmigo");

		} catch (Exception e) {
			txtMsg.append("\n-useUpdateMethod - Error: " + e.getMessage());
		}
	}



	private String showCursor( Cursor cursor) {
		// show SCHEMA (column names & types)
		cursor.moveToPosition(-1); //reset cursor's top
		String cursorData = "\nCursor: [";

		try {
			// get column names
			String[] colName = cursor.getColumnNames();
			for(int i=0; i<colName.length; i++){
				String dataType = getColumnType(cursor, i);
				cursorData += colName[i] + dataType;

				if (i<colName.length-1){
					cursorData+= ", ";
				}
			}
		} catch (Exception e) {
			Log.e("<<SCHEMA>>", e.getMessage());
		}
		cursorData += "]";

		// now get the rows
		cursor.moveToPosition(-1); //reset cursor's top
		while (cursor.moveToNext()) {
			String cursorRow = "\n[";
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				cursorRow += cursor.getString(i);
				if (i<cursor.getColumnCount()-1)
					cursorRow +=  ", ";
			}
			cursorData += cursorRow + "]";
		}
		return cursorData + "\n";
	}

	private String getColumnType(Cursor cursor, int i) {
		try {
			//peek at a row holding valid data
			cursor.moveToFirst();
			int result = cursor.getType(i);
			String[] types = {":NULL", ":INT", ":FLOAT", ":STR", ":BLOB", ":UNK" };
			//backtrack - reset cursor's top
			cursor.moveToPosition(-1);
			return types[result];
		} catch (Exception e) {
			return " ";
		}
	}

	private void useCursor1() {
		try {
			// this is similar to showCursor(...)
			// obtain a list of records[recId, name, phone] from DB
			String[] columns = { "ID", "firstName", "lastName","address" };
			// using simple parametric cursor
			Cursor c = db.query("tblAMIGO", columns, null, null, null, null,
					"recID");

			int theTotal = c.getCount();
			txtMsg.append("\n-useCursor1 - Total rec " + theTotal);
			txtMsg.append("\n");
			int idCol = c.getColumnIndex("ID");
			int nameCol = c.getColumnIndex("firstName");
			int phoneCol = c.getColumnIndex("lastName");
			int addressCol = c.getColumnIndex("address");

			c.moveToPosition(-1);
			while (c.moveToNext()) {
				columns[0] = Integer.toString((c.getInt(idCol)));
				columns[1] = c.getString(nameCol);
				columns[2] = c.getString(phoneCol);
				columns[3] = c.getString(addressCol);

				txtMsg.append(columns[0] + " " + columns[1] + " " + columns[2]+ columns[3] + " "
						+ "\n");
			}

		} catch (Exception e) {
			txtMsg.append("\nError useCursor1: " + e.getMessage());
			finish();
		}
	}// useCursor1



	@Override
	public void onClick(View v) {
		// close current screen - terminate Activity1
		finish();
	}

}
