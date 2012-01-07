package com.toptal.todo;

import android.net.Uri;
import android.provider.BaseColumns;

public class Content {
	/**
	 * Authority for the Published Tasks.
	 */
	public static final String AUTHORITY = "com.toptal.todo";
	public static final int COMPLETED_YES = 1;
	public static final int COMPLETED_NO = 0;

	public static final int PRIORITY_LOW = 0;
	
	/**
	 * Normal priority for the task. This is the default priority.
	 */
	public static final int PRIORITY_NORMAL = 1;
	
	/**
	 * High priority for the task. This it will cause the task to be visually shown with an special icon.
	 */
	public static final int PRIORITY_HIGH = 2;
	
	/**
	 * Default priority for the tasks.
	 */
	public static final int DEFAULT_PRIORITY = PRIORITY_NORMAL;
	
	/**
	 * All columns available to be used as projections when querying the content provider.
	 */
	public static final String[] ALL_COLUMNS = new String[]{Task._ID, Task.TITLE, Task.COMPLETED, Task.DETAILS, Task.PRIORITY};
	
	/**
	 * Data model for the Task table.
	 * @author thepumpkin
	 *
	 */
	public static final class Task implements BaseColumns {
		
		/**
		 * Content URI for the Tasks.
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tasks");

		/**
		 * {{TEXT}} Title of the Task
		 */
		public static final String TITLE = "TITLE";
		
		/**
		 * {{INTEGER}} See COMPLETED_YES or COMPLETED_NO.
		 */
		public static final String COMPLETED = "COMPLETED";
		
		/**
		 * {{STRING}} Details of the Task.
		 */
		public static final String DETAILS = "DETAILS";

		/**
		 * {{INTEGER}} 0 to 2 Priority of the Task. 2 means High priority. The default is 1(Normal).
		 */
		public static final String PRIORITY = "PRIORITY";
	}
}
