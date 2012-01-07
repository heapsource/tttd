package com.toptal.todo.activities;

import com.toptal.todo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Dialog that shows options for sorting the tasks list.
 * @author thepumpkin
 *
 */
public class SortDialog extends Dialog {

	public SortDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		
		this.setContentView(R.layout.sort_dialog);

		this.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.sort);
		
		this.setTitle(R.string.sort_dialog_title);
		
		this.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
}
