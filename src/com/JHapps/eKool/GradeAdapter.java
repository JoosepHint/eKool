package com.JHapps.eKool;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GradeAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;
	
	private String[] mStrings;
	private String[] mType;
	private String[] mGrade;
	Context context;
	
	private int mViewResourceId;
	
	public GradeAdapter(Context ctx, int viewResourceId,
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
		
		TextView tv = (TextView)convertView.findViewById(R.id.option_text);
		tv.setText(mStrings[position]);
		
		TextView grade_type = (TextView)convertView.findViewById(R.id.grade_type);
		grade_type.setText(mType[position]);
		
		TextView grade = (TextView)convertView.findViewById(R.id.grade);
		grade.setText(mGrade[position]);
		
		if (grade.getText().toString().equals("")){
			LinearLayout date = (LinearLayout) convertView.findViewById(R.id.date);
			date.setVisibility(View.GONE);
		}else{
			//grade.setPaintFlags(grade.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
			}
		
		return convertView;
	}
}
