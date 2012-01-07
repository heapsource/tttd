package com.toptal.todo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Helper class around the Tasks Content Provider.
 * @author thepumpkin
 *
 */
public class Tasks {
	/**
	 * Content resolver used to do CRUD operations on the tasks using URI's.
	 */
	private ContentResolver content;
	
	/**
	 * Creates a Task Manager using the given content resolver.
	 * @param content an instance of a context resolver to manage the tasks data. It can not be null.
	 */
	public Tasks(ContentResolver content) {
		if(content == null)
			throw new IllegalArgumentException("content is required");
		this.content = content;
	}
	
	/**
	 * Creates a Task by Title with default due time and state.
	 * @param title
	 * @return Uri of the Created Task
	 */
	public Uri createByTitle(String title) {
		ContentValues values = new ContentValues();
		values.put(Content.Task.TITLE, title);
		values.put(Content.Task.DETAILS, "");
		values.put(Content.Task.COMPLETED, Content.COMPLETED_NO);
		values.put(Content.Task.PRIORITY, Content.DEFAULT_PRIORITY);
		return this.content.insert(Content.Task.CONTENT_URI, values);
	}
	
	/**
	 * Returns a task cursor for the given id.
	 * @param id If the the task
	 * @return An open cursor for the task.
	 */
	public Cursor getTaskById(long id) {
		return this.content.query(Content.Task.CONTENT_URI, Content.ALL_COLUMNS, "_id=" + Long.toString(id), null, null);
	}
	
	/**
	 * Creates a task using the given values.
	 * @param values Values of the Task.
	 * @return Returns the URI for the newly created task.
	 */
	public Uri create(ContentValues values) {
		return this.content.insert(Content.Task.CONTENT_URI, values);
	}
	
	/**
	 * Updates a task with the given values and id.
	 * @param id The id of the task to update.
	 * @param values The values of the task to update.
	 * @return Return a boolean value indicating whether the task was updated or not.
	 */
	public boolean update(long id, ContentValues values) {
		return this.content.update(Content.Task.CONTENT_URI, values, "_id=" +  Long.toString(id), null) > 0;
	}
	
	/**
	 * Changes the Completed state of the Task.
	 * @param id
	 * @param completed False to mark the task as non completed, otherwise true.
	 * @return Returns a boolean value indicating whether the task was updated or not.
	 */
	public boolean changeCompletedState(long id, boolean completed) {
		ContentValues values = new ContentValues();
		values.put(Content.Task.COMPLETED, completed ? Content.COMPLETED_YES : Content.COMPLETED_NO);
		boolean updated = this.update(id, values);
		return updated;
	}

	/**
	 * Remove a task from application.
	 * @param id Id of the task to remove.
	 * @return Returns a boolean value indicating whether the task was removed or not.
	 */
	public boolean remove(long id) {
		return this.content.delete(Content.Task.CONTENT_URI, "_id=" +  Long.toString(id), null) > 0;
	}
}
