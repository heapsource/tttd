package com.toptal.todo.activities;

import com.toptal.todo.Content;
import com.toptal.todo.R;
import com.toptal.todo.Tasks;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Main Activity for the App. Uses layout "res/layout/main.xml".
 * @author thepumpkin
 *
 */
public class MainActivity extends ListActivity {
	private static final int SORT_DIALOG = 0;
	private Tasks tasks = null;
	private EditText titleView = null;
	private InputMethodManager imm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the UI(res/layout/main.xml)
		this.setContentView(R.layout.main);

		/*
		 * Resolve UI Fields
		 */
		// Title View
		titleView = (EditText)this.findViewById(R.id.title);
		titleView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// If the user clicks in 'Done' in the soft-keyboard...
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		        	// ...Then we create a task.
		        	addTaskAction(v);
		            return true; // Yes, we handled the IME action.
		        }
		        return false; // Let Android handle the IME Action.
		    }
		});

		/*
		 * Initialize Manager Instances
		 */
		this.tasks = new Tasks(this.getContentResolver());
		this.imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE
				);

		/*
		 * Initialize ListView
		 */

		// Create a Cursor for all the Tasks.
		Cursor cursor = this.getContentResolver().query(Content.Task.CONTENT_URI, null, null, null, null);
		
		this.startManagingCursor(cursor);

		// Create a ListAdapter for the Cursor.
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, // Context.
				R.layout.task_row,  //row template
				cursor, // cursor
				new String[] {Content.Task.TITLE, Content.Task.COMPLETED, Content.Task.PRIORITY}, // columns
				new int[] {R.id.title, R.id.task_check, R.id.priority}) {
			
		};  // template id's
		
		// Implemented a custom View binder so we translate the Integer value from COMPLETED column into a boolean.
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
		    public boolean setViewValue(View view, final Cursor cursor, int columnIndex) {
				int completedColumn = cursor.getColumnIndex(Content.Task.COMPLETED);
				int priorityColumn = cursor.getColumnIndex(Content.Task.PRIORITY);
		        if(columnIndex == completedColumn) {
		                CheckBox cb = (CheckBox) view;
		                cb.setTag(new Long(cursor.getLong(cursor.getColumnIndex(Content.Task._ID))));
		                cb.setOnCheckedChangeListener(null);
		                cb.setChecked(cursor.getInt(completedColumn) == Content.COMPLETED_YES);
		                cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								// Update the record according to the checked state.
								tasks.changeCompletedState((Long)buttonView.getTag(), isChecked);
							}
						});
		                return true;
		        } else if(columnIndex == priorityColumn) {
		        	ImageView img = (ImageView) view;
	                if(cursor.getInt(priorityColumn) != Content.PRIORITY_HIGH) {
	                	img.setVisibility(View.GONE);
	                } else {
	                	img.setVisibility(View.VISIBLE);
	                }
	                return true;
		        }
		        return false;
		    }
		});
		// Bind adapter to the list.
		this.setListAdapter(adapter);
		
		// Add handler when the user clicks on the list.
		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View sender, int index,
					long id) {
				showTaskDetails(id);
			}

			
		});
		
		// Enable a contex menut for the List.
		registerForContextMenu(this.getListView());
	}
	public void showTaskDetails(long id) {
		Intent intent = new Intent(this, EditTaskActivity.class);
		
		// Set is as part of the intent.
		intent.putExtra(EditTaskActivity.TASK_ID_EXTRA, id);
		
		// Show the activity.
		this.startActivity(intent);
	}
	/**
	 * Handler for the 'Add' Button action and IME actions for the title field.
	 * @param sender
	 */
	public void addTaskAction(View sender) {
		// Get entered title
		String title = this.titleView.getText().toString();
		if(title.equalsIgnoreCase("")) {
			// Show the Task Creation Activity
			Intent intent = new Intent(this, EditTaskActivity.class);
			this.startActivity(intent);
		} else {
			// Create the Task using just the title
			createTaskByTitle(title);
		}
		
		// Hide soft keyboard.
		imm.hideSoftInputFromWindow(this.titleView.getWindowToken(), 0);
	}

	private void createTaskByTitle(String title) {
		// Creates the Task using the given title.
		this.tasks.createByTitle(title);

		// Clears the Text
		this.titleView.setText("");
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		this.getMenuInflater().inflate(R.menu.list, menu);
		menu.setHeaderTitle(R.string.tasks_list_menu_title);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    long selectedTaskId = getListAdapter().getItemId(info.position);
		if(item.getItemId() == R.id.edit) {
			// Edit the Task
			showTaskDetails(selectedTaskId);
			return true;
		} else if(item.getItemId() == R.id.remove) {
			// Remove the Task
			this.tasks.remove(selectedTaskId);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		this.getMenuInflater().inflate(R.menu.list_options, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.change_sort:
			this.showShortDialog();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showShortDialog() {
		this.showDialog(SORT_DIALOG);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case SORT_DIALOG:
			return new SortDialog(this);
		}
		return super.onCreateDialog(id);
	}
	
}
