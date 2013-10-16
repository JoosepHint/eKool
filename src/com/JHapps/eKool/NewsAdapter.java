package com.JHapps.eKool;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;
	
	private String[] mStrings;
	private String[] mType;
	private String[] mDate;
	private String[] mGrade;
	Context context;
	
	private int mViewResourceId;
	
	public NewsAdapter(Context ctx, int viewResourceId,
			String[] strings, String[] type, String[] date, String[] grade) {
		super(ctx, viewResourceId, strings);
		mInflater = (LayoutInflater)ctx.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		context = ctx;
		mStrings = strings;
		mType = type;
		mDate = date;
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
		RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout);
		final TextView tv = (TextView)convertView.findViewById(R.id.option_text);
		tv.setText(mStrings[position]);
		if(tv.toString().contains(".removed")){
			tv.setPaintFlags(tv.getPaintFlags() |   Paint.STRIKE_THRU_TEXT_FLAG);
			tv.setText(tv.getText().toString().replace(".removed", ""));
		}

		TextView grade_type = (TextView)convertView.findViewById(R.id.grade_type);
		grade_type.setText(mType[position]);
		//grade_type.setText(grade_type.getText().toString().toLowerCase());
		
		TextView date = (TextView)convertView.findViewById(R.id.date);
		date.setText(mDate[position]);
		
		TextView grade = (TextView)convertView.findViewById(R.id.grade);
		grade.setText(mGrade[position]);
		if(grade.getText().equals("")){
			layout.setVisibility(View.GONE);
		}
		
		if (date.getText().toString().equals("")){
			LinearLayout datek = (LinearLayout) convertView.findViewById(R.id.datelayout);
			datek.setVisibility(View.GONE);
		}else{
			//grade.setPaintFlags(grade.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
			}
		
		return convertView;
	}
}