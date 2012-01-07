package com.toptal.todo.activities;

import com.toptal.todo.Content;
import com.toptal.todo.R;
import com.toptal.todo.Tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Creates and Edit Tasks. Uses TASK_ID_EXTRA extra to edit task.
 * @author thepumpkin
 *
 */
public class EditTaskActivity extends Activity {
	public static final String TASK_ID_EXTRA = "TASK_ID";
	
	private Tasks tasks = null;
	private EditText titleView, detailsView;
	private Spinner priorityView;
	private CheckBox completedView;
	
	/**
	 * Id of the task being edited.
	 */
	private long taskId;
	
	/**
	 * True if the activity is editing a task.
	 */
	private boolean isEdit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.edit_task);
		
		/*
		 * Initialize a Tasks manager.
		 */
		this.tasks = new Tasks(this.getContentResolver());
		this.taskId = this.getIntent().getLongExtra(TASK_ID_EXTRA, 0);
		
		this.titleView = (EditText)this.findViewById(R.id.title);
		this.detailsView = (EditText)this.findViewById(R.id.details);
		this.priorityView = (Spinner)this.findViewById(R.id.priority);
		this.completedView = (CheckBox)this.findViewById(R.id.completed);
		
		this.isEdit = this.taskId != 0;
		if(this.isEdit) {
			// On edit mode, show the current task information.
			this.loadInfo();
		} else {
			// On creation mode, show the default information.
			this.reset();
		}
	}
	
	/**
	 * Reset's the UI.
	 */
	private void reset() {
		this.priorityView.setSelection(Content.DEFAULT_PRIORITY);
	}

	/**
	 * Refreshes the UI with the latest info from the content provider.
	 */
	private void loadInfo() {
		Cursor cursor = this.tasks.getTaskById(this.taskId);
		this.startManagingCursor(cursor);
		if(cursor.moveToFirst()) {
			this.titleView.setText(cursor.getString(cursor.getColumnIndex(Content.Task.TITLE)));
			this.detailsView.setText(cursor.getString(cursor.getColumnIndex(Content.Task.DETAILS)));
			this.priorityView.setSelection(cursor.getInt(cursor.getColumnIndex(Content.Task.PRIORITY)));
			this.completedView.setChecked(cursor.getInt(cursor.getColumnIndex(Content.Task.COMPLETED)) == Content.COMPLETED_YES);
		}
		cursor.close();
	}
	
	/**
	 * Handler for the 'Save' button.
	 * @param sender
	 */
	public void saveAction(View sender) {
		ContentValues values = new ContentValues();
		// Fill the record attributes.
		values.put(Content.Task.TITLE, this.titleView.getText().toString());
		values.put(Content.Task.DETAILS, this.detailsView.getText().toString());
		values.put(Content.Task.COMPLETED, this.completedView.isChecked() ? Content.COMPLETED_YES : Content.COMPLETED_NO);
		values.put(Content.Task.PRIORITY, this.priorityView.getSelectedItemPosition());
		if(this.isEdit) {
			// Update the id of the task with the given values.
			this.tasks.update(this.taskId, values);
		} else {
			// Insert a new Record
			this.tasks.create(values);
		}
		// Close the Activity.
		this.finish();
	}
}
