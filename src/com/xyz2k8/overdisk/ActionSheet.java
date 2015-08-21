package com.xyz2k8.overdisk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyz2k8.overdisk.R;

public class ActionSheet {
	public static final int ACTION_YES = 1;
	public static final int ACTION_NO  = 2;
	
	public interface OnActionSheetSelected {
		void onActionSelected(int whichAction, String title);
	}
	
	private static void updateQuestion(String[] questions, LinearLayout info_view, Context context)
	{
		if(null == questions || null == info_view || null == context)
		{
			return;
		}
		
		//int color = context.getResources().getColor(R.color.light_blue);
		int color = 0xff0597d2;		
		
		if(questions.length == 8)
		{
			TextView txView = (TextView)info_view.findViewById(R.id.info_filename_lab);
			txView.setTextColor(color);
			txView.setText(questions[0]);			
			
			txView = (TextView)info_view.findViewById(R.id.info_filename);
			txView.setTextColor(color);
			txView.setText(questions[1]);
			
			txView = (TextView)info_view.findViewById(R.id.info_filesize_lab);
			txView.setTextColor(color);
			txView.setText(questions[2]);
			
			txView = (TextView)info_view.findViewById(R.id.info_filesize);
			txView.setTextColor(color);
			txView.setText(questions[3]);
			
			txView = (TextView)info_view.findViewById(R.id.info_filenum_lab);
			txView.setTextColor(color);
			txView.setText(questions[4]);
			
			txView = (TextView)info_view.findViewById(R.id.info_filenum);
			txView.setTextColor(color);
			txView.setText(questions[5]);
			
			txView = (TextView)info_view.findViewById(R.id.info_filefullpath_lab);
			txView.setTextColor(color);
			txView.setText(questions[6]);
			
			txView = (TextView)info_view.findViewById(R.id.info_filefullpath);
			txView.setTextColor(color);
			txView.setText(questions[7]);
		}
		
		if(questions.length == 2)
		{
			TextView txView = (TextView)info_view.findViewById(R.id.info_filename_lab);
			txView.setTextColor(color);
			txView.setText(questions[0]);
			txView.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
			txView.setPadding(txView.getPaddingLeft(), txView.getPaddingTop() + 24, txView.getPaddingRight(), txView.getPaddingBottom());
			
			txView = (TextView)info_view.findViewById(R.id.info_filename);
			txView.setTextColor(color);
			txView.setText(questions[1]);
			
			txView = (TextView)info_view.findViewById(R.id.info_filesize_lab);
			txView.setVisibility(View.GONE);
			
			txView = (TextView)info_view.findViewById(R.id.info_filesize);
			txView.setVisibility(View.GONE);
			
			txView = (TextView)info_view.findViewById(R.id.info_filenum_lab);
			txView.setVisibility(View.GONE);
			
			txView = (TextView)info_view.findViewById(R.id.info_filenum);
			txView.setVisibility(View.GONE);
			
			txView = (TextView)info_view.findViewById(R.id.info_filefullpath_lab);
			txView.setVisibility(View.GONE);
			
			txView = (TextView)info_view.findViewById(R.id.info_filefullpath);
			txView.setVisibility(View.GONE);
		}
	}
	
	public static Dialog showActionSheet(final Context context, final OnActionSheetSelected actionSheetSelected,
			OnCancelListener cancelListener, final String title, String[] questions) 
	{
		final Dialog dlg = new Dialog(context, R.style.OprationSheetAnimation);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.get_option, null);
		
		TextView mTitle = (TextView) layout.findViewById(R.id.get_opt_input_title);
		TextView mYes = (TextView) layout.findViewById(R.id.get_opt_input_yes);
		TextView mNo = (TextView) layout.findViewById(R.id.get_opt_input_no);
		
		LinearLayout info_view = (LinearLayout)(layout.findViewById(R.id.infoView));
		info_view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.actionsheet_middle_selector));
		mTitle.setText(title);
		mTitle.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.actionsheet_middle_selector));
		mYes.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.actionsheet_middle_selector));
		mNo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.actionsheet_middle_selector));
		
		updateQuestion(questions, info_view, context);
		
		mYes.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				{
					actionSheetSelected.onActionSelected(ActionSheet.ACTION_YES, title);
				    dlg.dismiss();
				}
			}
		});
		
        mNo.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				{
					actionSheetSelected.onActionSelected(ActionSheet.ACTION_NO, title);
				    dlg.dismiss();
				}
			}
		});

        Window w = dlg.getWindow();
		w.setGravity(Gravity.CENTER);
		
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		//lp.width = 1000;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(false);
		if (cancelListener != null)
		{
			dlg.setOnCancelListener(cancelListener);
		}

		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}	
}
