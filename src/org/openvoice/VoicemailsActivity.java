package org.openvoice;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VoicemailsActivity extends ListActivity {
	
	private SharedPreferences mPrefs;
  private String [] mVoicemails;
  private Context mContext;
  
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
    mContext = getApplicationContext();
    mPrefs = mContext.getSharedPreferences(MessagingsActivity.PREFERENCES_NAME, MODE_WORLD_READABLE);
    handleUserVoicemail();    
	}

  private void handleUserVoicemail() {
    new VoicemailDownloadTask(getApplicationContext(), this).execute();	  
  }
  
  void showVoicemails(String[][] voicemails) {
    setListAdapter(new VoicemailListAdapter(this, voicemails));
  }

  /**
   * A sample ListAdapter that presents content from arrays of speeches and
   * text.
   * 
   */
  private class VoicemailListAdapter extends BaseAdapter {
      public VoicemailListAdapter(Context context, String[][] voicemails) {
          mContext = context;
          int length = voicemails.length;
          mFrom = new String[length];
          mText = new String[length];
          mPath = new String[length];
          for(int i=0; i<mFrom.length; i++) {
          	mFrom[i] = voicemails[i][0];          	
          	mText[i] = voicemails[i][1];
          	mPath[i] = voicemails[i][2];
          }
      }

      /**
       * The number of items in the list is determined by the number of speeches
       * in our array.
       * 
       * @see android.widget.ListAdapter#getCount()
       */
      public int getCount() {
          return mFrom.length;
      }

      /**
       * Since the data comes from an array, just returning the index is
       * sufficent to get at the data. If we were using a more complex data
       * structure, we would return whatever object represents one row in the
       * list.
       * 
       * @see android.widget.ListAdapter#getItem(int)
       */
      public Object getItem(int position) {
          return position;
      }

      /**
       * Use the array index as a unique id.
       * 
       * @see android.widget.ListAdapter#getItemId(int)
       */
      public long getItemId(int position) {
          return position;
      }

      /**
       * Make a SpeechView to hold each row.
       * 
       * @see android.widget.ListAdapter#getView(int, android.view.View,
       *      android.view.ViewGroup)
       */
      public View getView(int position, View convertView, ViewGroup parent) {
          VoicemailView sv;
          if (convertView == null) {
              sv = new VoicemailView(mContext, mFrom[position], mText[position], mPath[position]);
          } else {
              sv = (VoicemailView) convertView;
              sv.setTitle(mFrom[position] + ": " + mText[position]);
              sv.setPath(mPath[position]);
          }

          return sv;
      }

      private Context mContext;
      private String[] mFrom;
      private String[] mText;
      private String[] mPath;
  }
  
  /**
   * We will use a SpeechView to display each speech. It's just a LinearLayout
   * with two text fields.
   *
   */
  private class VoicemailView extends LinearLayout {
      public VoicemailView(Context context, String title, String text, String path) {
          super(context);
          this.setOrientation(VERTICAL);
          mContext = context;
          // Here we build the child views in code. They could also have
          // been specified in an XML file.
          mFrom = new TextView(context);
          mFrom.setText(title + ": " + text);
          addView(mFrom, new LinearLayout.LayoutParams(
                  LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

          mPath = path;
          
          mPlayButton = new Button(context);
          mPlayButton.setText("Play");
          mPlayButton.setOnClickListener(mPlayClickListener);
          addView(mPlayButton, new LinearLayout.LayoutParams(
                  LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      }
      
      private View.OnClickListener mPlayClickListener = new View.OnClickListener() {
        public void onClick(View view) {
          MediaPlayer mp = new MediaPlayer();
          
          Uri path = Uri.parse(mPath);
          try {
          	mp.reset();
      	    mp.setDataSource(getApplicationContext(), path);
      	    mp.prepare();
      	    mp.start();
          } catch (IllegalArgumentException e) {
      	    // TODO Auto-generated catch block
      	    e.printStackTrace();
          } catch (SecurityException e) {
      	    // TODO Auto-generated catch block
      	    e.printStackTrace();
          } catch (IllegalStateException e) {
      	    // TODO Auto-generated catch block
      	    e.printStackTrace();
          } catch (IOException e) {
      	    // TODO Auto-generated catch block
      	    e.printStackTrace();
          }
        }
      };

      public void setPath(String path) {
      	mPath = path;
      }
      
      /**
       * Convenience method to set the title of a SpeechView
       */
      public void setTitle(String title) {
          mFrom.setText(title);
      }

      private Context mContext;
      private TextView mFrom;
      //private String mText; //transcription for voicemail
      private String mPath;
      private Button mPlayButton;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.voicemail_menu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.refresh:
      	handleUserVoicemail();
    }
    return false;
  }  
}
