package com.example.chap6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity 
	implements OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);
		
		int wc = LinearLayout.LayoutParams.WRAP_CONTENT;
		
		Button btnw = new Button(this);
		btnw.setText("Write");
		btnw.setTag("W");
		btnw.setLayoutParams(new LinearLayout.LayoutParams(wc,wc));
		btnw.setOnClickListener(this); // �C�x���g���X�i�[�̓o�^
		layout.addView(btnw);
		
		Button btnr = new Button(this);
		btnr.setText("Read");
		btnr.setTag("R");
		btnr.setLayoutParams(new LinearLayout.LayoutParams(wc,wc));
		btnr.setOnClickListener(this); // �C�x���g���X�i�[�̓o�^����
		layout.addView(btnr);
		
		EditText et = new EditText(this);
		et.setText("???");
		et.setTag("et");
		layout.addView(et);
		
		TextView tv = new TextView(this);
		tv.setText("???");
		tv.setTag("tv");
		layout.addView(tv);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if((String)v.getTag() == "W"){
			View parent = (View)v.getParent();
			EditText et = (EditText)parent.findViewWithTag("et");
			TextView tv  = (TextView)parent.findViewWithTag("tv");
			try {
	//			writeToFile(this, et.getText().toString()); // �t�@�C���ւ̏������ݏ���
				//writeToTable(et.getText().toString(), false); // �e�[�u���ɒǉ�
				SharedPreferences prefs
					= getSharedPreferences("myprefs", MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("text", et.getText().toString());
				editor.putString("Title", "Java�̊G�{");
				editor.putInt("Pages", 213);
				editor.putInt("Price", 1580);
				editor.commit();
			} catch (Exception e) {
				tv.setText("ERROR:" + e.getMessage());
			}
		}else if((String)v.getTag() == "R"){
			View parent = (View)v.getParent();
			TextView tv  = (TextView)parent.findViewWithTag("tv");
			try {
//				tv.setText(readFromFile(this)); // �t�@�C���̓ǂݍ��ݏ���
				//tv.setText(readFromTable());	// �e�[�u������̓ǂݏo��
				tv.setText(readFromPrefs()); // �v���t�@�����X����̓ǂݍ���
			} catch (Exception e) {
				tv.setText("ERROR:" + e.getMessage());
			}
		}
		
	}
	
	private void writeToFile(Context c, String s) throws Exception{
		s = s + "\n";
		byte [] data = s.getBytes();
		OutputStream stream = null;
		try {
			stream = c.openFileOutput	// �t�@�C���̃I�[�v��
					("test.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
			stream.write(data); // ��������
			Log.v("writeToFile", s);
			stream.close();	// �t�@�C���̃N���[�Y

		} catch (Exception e) {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e1) {
					throw e1;
				}
			}
		}
	}
	
	private String readFromFile(Context c) throws Exception{
		byte[] data = new byte[100];
		InputStream stream = null;
		ByteArrayOutputStream stream2 = null;
		try {
			stream = c.openFileInput("test.txt");
			
			stream2 = new ByteArrayOutputStream();
			
			int size = stream.read(data);
			while(size > 0){
				stream2.write(data, 0, size);
				size = stream.read(data);
			}
			stream2.close();
			stream.close();
			
			String s = new String(stream2.toByteArray());
			return s;
		} catch (Exception e) {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e1) {
					throw e1;
				}
			}
		}
		return "";
	}
	
	private void writeToTable(String data, boolean overwrite){
		ContentValues vals = new ContentValues();
		vals.put("data", data);
		String whereClause = "id = 1";
		String [] whereArgs = null;
		
		DBHelper dbh = new DBHelper(this);
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		try{
			if(overwrite){
				db.update(DBHelper.TABLENAME, vals, whereClause, whereArgs);
			}else{
                db.insert(DBHelper.TABLENAME, "", vals);
			}
		}finally{
			db.close();
		}
	}
	
	private String readFromTable(){
		String[] columns = {"id", "data"};
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		
		StringBuffer sb = new StringBuffer();
		Cursor cur = null;
		
		DBHelper dbh = new DBHelper(this);
		SQLiteDatabase db = dbh.getReadableDatabase();
		try{
				cur = db.query(DBHelper.TABLENAME, columns, selection, 
                        selectionArgs, groupBy, having, orderBy);
				while(cur.moveToNext()){
					sb.append(cur.getInt(0) + "," + cur.getString(1) + "\n");
				}
		}finally{
			if(cur != null){
				cur.close();
			}
			db.close();
		}
		return sb.toString();
	}
	
	private String readFromPrefs(){
		StringBuffer sb = new StringBuffer();
		
		SharedPreferences prefs
			= getSharedPreferences("myprefs", MODE_PRIVATE);
		Map<String, ?> map = prefs.getAll();
		for(Entry<String, ?> entry : map.entrySet()){
			String key = entry.getKey();
			Object value = entry.getValue();
			sb.append(key + "|" + value.toString() + "\n");
		}
		return  sb.toString();
	}
}
