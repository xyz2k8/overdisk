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

public class SelectOpration {
	
	public static final int OP_DELETE = 1;
	public static final int OP_HIDE   = 2;
	public static final int OP_OPEN   = 3;
	//public static final int OP_ATTRS  = 4;
	
	public interface OnOprationSelected {
		void onOprationSelected(int whichOprationSelected);
	}
	
	public static Dialog showOprationSheet(Context context, final OnOprationSelected oprationSheetSelected, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.OprationSheetAnimation);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.opration_sheet, null);
		
		TextView opDeleteNode = (TextView) layout.findViewById(R.id.op_delete);
		TextView opHideNode = (TextView) layout.findViewById(R.id.op_hide);
		TextView opOpenNode = (TextView) layout.findViewById(R.id.op_open);
		
		
		opDeleteNode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				oprationSheetSelected.onOprationSelected(SelectOpration.OP_DELETE);
				dlg.dismiss();
			}
		});

		opHideNode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				oprationSheetSelected.onOprationSelected(SelectOpration.OP_HIDE);
				dlg.dismiss();
			}
		});
		
		opOpenNode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				oprationSheetSelected.onOprationSelected(SelectOpration.OP_OPEN);
				dlg.dismiss();
			}
		});
		
		Window w = dlg.getWindow();
		w.setGravity(Gravity.CENTER);
		
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
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
	
	public static Dialog showDeleteSheet(Context context, final OnOprationSelected oprationSheetSelected, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.OprationSheetAnimation);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.opration_sheet, null);
		
		TextView opDeleteNode = (TextView) layout.findViewById(R.id.op_delete);
		TextView opHideNode = (TextView) layout.findViewById(R.id.op_hide);
		opHideNode.setVisibility(View.GONE);
		TextView opOpenNode = (TextView) layout.findViewById(R.id.op_open);
		opOpenNode.setVisibility(View.GONE);
		
//		TextView opAttrs = (TextView) layout.findViewById(R.id.op_attrs);
//		opAttrs.setVisibility(View.GONE);
		
		opDeleteNode.setBackgroundResource(R.drawable.actionsheet_middle_selector);
		opDeleteNode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				oprationSheetSelected.onOprationSelected(SelectOpration.OP_DELETE);
				dlg.dismiss();
			}
		});

		opHideNode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				oprationSheetSelected.onOprationSelected(SelectOpration.OP_HIDE);
				dlg.dismiss();
			}
		});
		
		opOpenNode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				oprationSheetSelected.onOprationSelected(SelectOpration.OP_OPEN);
				dlg.dismiss();
			}
		});
		
//		opAttrs.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				oprationSheetSelected.onOprationSelected(SelectOpration.OP_ATTRS);
//				dlg.dismiss();
//			}
//		});
		
		Window w = dlg.getWindow();
		w.setGravity(Gravity.CENTER);
		
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
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
