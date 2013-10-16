package com.JHapps.eKool;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TextAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;
	
	private String[] mStrings;
	private String[] mType;
	
	private int mViewResourceId;
	
	public TextAdapter(Context ctx, int viewResourceId,
			String[] strings, String[] type) {
		super(ctx, viewResourceId, strings);
		mInflater = (LayoutInflater)ctx.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		
		mStrings = strings;
		mType = type;
		
		mViewResourceId = viewResourceId;
	}

	@Override
	public int getCount() {
		return mStrings.length;
	}
	@Override
	public String getItem(int position) {
		return mType[position];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(mViewResourceId, null);
		
		TextView tv = (TextView)convertView.findViewById(R.id.option_text);
		
		TextView grade_type = (TextView)convertView.findViewById(R.id.grade_type);
		grade_type.setText(mType[position]);
		
		/*if(mStrings[position].toString().contains(".course_grade")){
			String newergrade = mStrings[position].toString().replace(".course_grade", "");
			tv.setText(newergrade);
			tv.setPaintFlags(grade_type.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
		}else{*/
			tv.setText(mStrings[position]);
		//}
		
		return convertView;
	}
}