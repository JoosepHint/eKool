package com.JHapps.eKool;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AssignAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;
	
	private String[] mStrings;
	private String[] mType;
	private String[] mGrade;
	Context context;
	boolean expanded = false;
	
	private int mViewResourceId;
	
	public AssignAdapter(Context ctx, int viewResourceId,
			String[] strings, String[] type, String[] grade) {
		super(ctx, viewResourceId, strings);
		mInflater = (LayoutInflater)ctx.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		context = ctx;
		mStrings = strings;
		mType = type;
		mGrade = grade;
		
		mViewResourceId = viewResourceId;
	}

	@Override
	public int getCount() {
		return mStrings.length;
	}
	@Override
	public String getItem(int position) {
		return mStrings[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(mViewResourceId, null);
		
		final TextView tv = (TextView)convertView.findViewById(R.id.option_text);
		tv.setText(mStrings[position]);
		tv.isClickable();
		final String text = tv.getText().toString();
		final ImageButton btn = (ImageButton) convertView.findViewById(R.id.expand);
		String shrt;
		if (tv.length() > 80){
			shrt = tv.getText().toString().substring(0, 67) + "...";
		    tv.setText(tv.getText().toString().substring(0, 67) + "...");
		}else{
			shrt = tv.getText().toString();
			btn.setVisibility(View.GONE);
		}
		final String shorter = shrt;
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (expanded == true){
					tv.setText(shorter);
					btn.setImageResource(R.drawable.ic_action_expand);
					expanded = false;
				}else{
					expanded = true;
				tv.setText(text);
				btn.setImageResource(R.drawable.ic_action_collapse);
				}
			}
		});
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (expanded == true){
					tv.setText(shorter);
					btn.setImageResource(R.drawable.ic_action_expand);
					expanded = false;
				}else{
					expanded = true;
				tv.setText(text);
				btn.setImageResource(R.drawable.ic_action_collapse);
				}
			}
		});
		TextView grade_type = (TextView)convertView.findViewById(R.id.grade_type);
		grade_type.setText(mType[position]);
		TextView grade = (TextView)convertView.findViewById(R.id.grade);
		grade.setText(mGrade[position]);
				final String combined = (text + grade_type.getText().toString());
				final CheckBox done = (CheckBox) convertView.findViewById(R.id.done);
				final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				try {
					JSONArray dones = JSONSharedPreferences.loadJSONArray(prefs, "done");
					 if(dones.toString().contains(combined)){
						 done.setChecked(true);
						 }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				done.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if(done.isChecked()){
							try {
								JSONArray dones = JSONSharedPreferences.loadJSONArray(prefs, "done");
								 if(!dones.toString().contains(combined)){
									 dones.put(combined);
								       JSONSharedPreferences.saveJSONArray(prefs, "done", dones);
									 }
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							try {
								JSONArray dones = JSONSharedPreferences.loadJSONArray(prefs, "done");
									 int i;
									 for(i = 0; i < dones.length(); i++){
										 if (dones.getString(i).equals(combined)){
										       JSONSharedPreferences.replaceItem(prefs, "done", combined);
										 }
									 }
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
				
		
		
		if (grade.getText().toString().equals("")){
			LinearLayout date = (LinearLayout) convertView.findViewById(R.id.date);
			date.setVisibility(View.GONE);
		}else{
			//grade.setPaintFlags(grade.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
			}
		
		return convertView;
	}
}