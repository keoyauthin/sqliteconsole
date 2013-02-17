package name.brumla.sqliteconsole;

import java.util.ArrayList;
import java.util.List;

import name.brumla.sqliteconsole.db.BaseDbHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConsoleActivity extends Activity {

	private BaseDbHelper baseDbHelper;
	private SQLiteDatabase writableDatabase;
	private Button btExecute;
	private EditText teQuery;
	private EditText teResult;

	private List<String> sqlQueries = new ArrayList<String>();
	int pos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);

		baseDbHelper = new BaseDbHelper(this, null, null, 1);
		writableDatabase = baseDbHelper.getWritableDatabase();

		Log.d(ConsoleActivity.class.getName(), "Writeble database at "
				+ writableDatabase);

		btExecute = (Button) findViewById(R.id.btExecute);
		teQuery = (EditText) findViewById(R.id.etQuery);
		teResult = (EditText) findViewById(R.id.etResult);

		initializeActions();
	}

	private void initializeActions() {
		btExecute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				teResult.setText("");
				String sql = teQuery.getText().toString();
				Log.d(this.getClass().getName(), "Query: " + sql);

				try {
					Cursor cr = ConsoleActivity.this.writableDatabase.rawQuery(
							sql, null);
					Log.d(this.getClass().getName(), "Result is " + cr);

					if (cr.getCount() == 0) {
						teResult.getText().append("OK\n");
						teResult.getText().append("-- no result --");
					} else {
						teResult.getText().append("OK\n");

						for (int i = 0; i < cr.getColumnCount(); i++) {
							teResult.getText().append(
									cr.getColumnName(i) + " |");
						}
						teResult.getText().append("\n");
						cr.moveToFirst();

						do {
							for (int i = 0; i < cr.getColumnCount(); i++) {
								teResult.getText().append(
										cr.getString(i) + " |");
							}
							teResult.getText().append("\n");
						} while (cr.moveToNext());
					}

					if (ConsoleActivity.this.writableDatabase.inTransaction()) {
						Log.d(this.getClass().getName(), "COMMIT");
						ConsoleActivity.this.writableDatabase
								.setTransactionSuccessful();
						ConsoleActivity.this.writableDatabase.endTransaction();
					}
				} catch (SQLiteException e) {
					if (ConsoleActivity.this.writableDatabase.inTransaction()) {
						Log.d(this.getClass().getName(), "ROLLBACK");
						ConsoleActivity.this.writableDatabase.endTransaction();
					}
					teResult.getText().append("ERROR\n");
					teResult.getText().append("Exception :" + e.getMessage());
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.console, menu);
		return true;
	}

	public SQLiteDatabase getWritableDatabase() {
		return writableDatabase;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(this.getClass().getName(), "Menu item id = " + item.getItemId());

		String sql = "";
		switch (item.getItemId()) {
		case R.id.sqlPK:
			sql = teQuery.getText().toString();
			teQuery.setText(sql + " INTEGER PRIMARY KEY AUTOINCREMENT ");
			break;
		case R.id.sqlNotNull:
			sql = teQuery.getText().toString();
			teQuery.setText(sql + " NOT NULL ");
			break;
		case R.id.sqlVarchar:
			sql = teQuery.getText().toString();
			teQuery.setText(sql + " VARCHAR( ");
			break;
		case R.id.action_reset:
			sqlQueries.add(teQuery.getText().toString());

			if (sqlQueries.size() > 1000) {
				sqlQueries.remove(0);
			}
			teQuery.setText("");
			pos = sqlQueries.size() - 1;
			Log.d(this.getClass().getName(), "Position updated: " + pos);
			break;
		case R.id.sqlNext:
			if (pos < sqlQueries.size() - 1) {
				pos++;
				teQuery.setText(sqlQueries.get(pos));
				Log.d(this.getClass().getName(), "position updated");
			} else {
				Log.d(this.getClass().getName(), "position not updated");
			}
			break;
		case R.id.sqlPrev:
			if (pos > 0) {
				pos--;
				teQuery.setText(sqlQueries.get(pos));
				Log.d(this.getClass().getName(), "position updated");
			} else {
				Log.d(this.getClass().getName(), "position not updated");
			}
			break;
		case R.id.action_sample_db:
			baseDbHelper = new BaseDbHelper(this, null, null, 1);
			writableDatabase = baseDbHelper.getWritableDatabase();
			teQuery.setText("");
			teResult.setText(R.string.msg_database_created);
			sqlQueries.clear();
			break;
		case R.id.action_about:
			AlertDialog.Builder bld = new Builder(this);
			bld.setTitle(R.string.about_application);
			bld.setMessage(R.string.about_text);
			bld.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}});
			bld.create().show();
		}

		return true;
	}
}
