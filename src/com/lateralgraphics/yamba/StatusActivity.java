package com.lateralgraphics.yamba;

// not only do you need to drag/drop (or cp) the twitter library jar into your project
// you need to add it to the project by selecting project properties > Java Build Path > Libraries
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher {
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button updateButton;
	Twitter twitter;
	TextView textCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view to named xml file in /res/layout/
        setContentView(R.layout.activity_status);
        // Find Views
        editText = (EditText) findViewById(R.id.editTextStatus);
        updateButton = (Button) findViewById(R.id.buttonStatus);
        
        // notify StatusActivity (which should have a handler for it) when button Event "click" is fired
        updateButton.setOnClickListener(this);
        
        // twitter account login
        // twitter = new Twitter("mark_fallon","mk37csjec]");
        twitter = new Twitter("student","password");
        twitter.setAPIRootUrl("http://yamba.marakana.com/api");
        
        // add text counter
        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
        
        // now tie in lister to editText
        editText.addTextChangedListener(this);
    }
    
    @Override
    public void afterTextChanged(Editable statusText) {
    	int count = 140 - statusText.length();
    	textCount.setText(Integer.toString(count));
    	if(count < 55) {
    		if(count < 15) textCount.setTextColor(Color.RED);
    		else textCount.setTextColor(Color.YELLOW);
    	}
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    	
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    	
    }
   
    
    // extend AsyncTask (template pattern) @Override required methods: doInBackground, onProgressUpdate, onPostExecute
    // note use of Java generics for each of the args (sequential?)
    class PostToTwitter extends AsyncTask<String,Integer,String> {
    	// note the use of String... arg. variable number of arguments in array 'statuses'
    	@Override
    	protected String doInBackground(String... statuses) {
    		try {
    			// run this on its own thread
    			Twitter.Status status = twitter.updateStatus(statuses[0]);
    			return status.text;
    		} catch(TwitterException e) {
    			// we got an error
    			Log.e(TAG,e.toString());
    			e.printStackTrace();
    			return "Error: Could not post";
    		}
    	}
    	
    	// called whenever there's progress reported by the doInBackground call
    	// if this were a download, progress indicator could be updated
    	@Override
    	protected void onProgressUpdate(Integer...integers) {
    		super.onProgressUpdate(integers);
    		// nothing to do yet
    	}
    	
    	// called after activity completes
    	@Override
    	protected void onPostExecute(String result) {
    		Toast.makeText(StatusActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
    	}
    }

    // as StatusActivity implements OnClickListener, we need onClick
    // this makes it capable of being a button listener
    public void onClick(View v) {
    	
    	// twitter.setStatus(this.editText.getText().toString());
    	// array
    	//poster.doInBackground(this.editText.getText().toString());
    	// we just need to call execute() on AsyncTask to kick it off
        // instantiate our PostToTwitter to handle status updates
    	// we don't need to store the reference for as many threads are created
    	
    	String status = this.editText.getText().toString();
        new PostToTwitter().execute(status);
    	Log.d(TAG,"onClicked");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this,PrefsActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
    
}
