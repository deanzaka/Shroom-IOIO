package rpl.shroomIOIO;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class MenuAct extends IOIOActivity {

	private SeekBar seekBar_;
	private ToggleButton buttonGlobal_;
	private ImageButton buttonAC_;
	private ImageButton buttonTV_;
	private ImageButton buttonCurt_;
	private ImageButton buttonLamp_;
	public Integer ACStatus_;
	public Integer TVStatus_;
	public Integer CurtStatus_;
	public Integer lampStatus_;
	public Integer globalStatus_;
	SharedPreferences prefAC_;
	SharedPreferences prefTV_;
	SharedPreferences prefCurt_;
	SharedPreferences prefLamp_;
	SharedPreferences prefGlobal_;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//read status AC
		prefAC_ = getSharedPreferences("Sai",Context.MODE_PRIVATE);
		ACStatus_  = prefAC_.getInt("lang_us", 0);
		//read status TV
		prefTV_ = getSharedPreferences("Sai",Context.MODE_PRIVATE);
		TVStatus_  = prefTV_.getInt("lang_us", 0);
		//read status Curtain
		prefCurt_ = getSharedPreferences("Sai",Context.MODE_PRIVATE);
		CurtStatus_  = prefCurt_.getInt("lang_us", 0);
		//read status Lamp
		prefLamp_ = getSharedPreferences("Sai",Context.MODE_PRIVATE);
		lampStatus_  = prefLamp_.getInt("lang_us", 0);
		//read status Curtain
		prefGlobal_ = getSharedPreferences("Sai",Context.MODE_PRIVATE);
		globalStatus_  = prefGlobal_.getInt("lang_us", 0);		
		
		seekBar_ = (SeekBar) findViewById(R.id.SeekBar);
		buttonAC_ = (ImageButton) findViewById(R.id.ac);
		buttonTV_ = (ImageButton) findViewById(R.id.tv);
		buttonCurt_ = (ImageButton) findViewById(R.id.curtain);
		buttonLamp_ = (ImageButton) findViewById(R.id.lamp);
		buttonGlobal_ = (ToggleButton) findViewById(R.id.global);
	
		//enableUi(false);
	}
	
	class Looper extends BaseIOIOLooper {
		private DigitalOutput Global_;
		private DigitalOutput AC_;
		private DigitalOutput Lamp_;
		private DigitalOutput TV_;
		private DigitalOutput CurtOpen_;
		private DigitalOutput CurtClose_;
		private PwmOutput pwmOutput_;

		@Override
		public void setup() throws ConnectionLostException {
			Global_ = ioio_.openDigitalOutput(11, true);
			Lamp_ = ioio_.openDigitalOutput(10, true);
			AC_ = ioio_.openDigitalOutput(9, true);
			TV_ = ioio_.openDigitalOutput(8,true);
			CurtOpen_ = ioio_.openDigitalOutput(7, true);
			CurtClose_ = ioio_.openDigitalOutput(6, true);
			pwmOutput_ = ioio_.openPwmOutput(12, 100);
			//enableUi(true);
		}

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			pwmOutput_.setPulseWidth(300 + seekBar_.getProgress() * 2);
			if(buttonAC_.isPressed()) {
				if(ACStatus_ == 1)	{
					AC_.write(true);
					Thread.sleep(1000);
					AC_.write(false);
					ACStatus_ = 0;
					saveAC();
				}
				else	{
					AC_.write(true);
					Thread.sleep(1000);
					AC_.write(false);
					ACStatus_ = 1;
					saveAC();
				}
			}
			
			if(buttonLamp_.isPressed())	{
				if(lampStatus_ == 1)	{
					Lamp_.write(false);
					lampStatus_ = 0;
					saveLamp();
					}
					else {
					Lamp_.write(true);
					lampStatus_ = 1;
					saveLamp();
					}
				Thread.sleep(500);
			}
			
			if(buttonTV_.isPressed()) {
				if(TVStatus_ == 1)	{
					TV_.write(true);
					Thread.sleep(1000);
					TV_.write(false);
					TVStatus_ = 0;
					saveTV();
				}
				else	{
					TV_.write(true);
					Thread.sleep(1000);
					TV_.write(false);
					TVStatus_ = 1;
					saveTV();
				}
			}
			
			if(buttonCurt_.isPressed()) {
				if(CurtStatus_ == 1)	{
					CurtOpen_.write(true);
					Thread.sleep(3000);
					CurtOpen_.write(false);
					CurtStatus_ = 0;
					saveCurt();
				}
				else	{
					CurtClose_.write(true);
					Thread.sleep(3000);
					CurtClose_.write(false);
					CurtStatus_ = 1;
					saveCurt();
				}
			}
			
			if(buttonGlobal_.isChecked())	{
				Global_.write(false);
				globalStatus_ = 0;
				saveGlobal();
				TVStatus_ = 1;
				ACStatus_ = 0;
			}
			else {
				Global_.write(true);
				globalStatus_ = 1;
				saveGlobal();
			}
			Thread.sleep(10);
		}

		@Override
		public void disconnected() {
			//enableUi(false);
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/*
	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				toggleGlobal_.setEnabled(enable);
				buttonAC_.setEnabled(enable);
				buttonTV_.setEnabled(enable);
				buttonCurt_.setEnabled(enable);
				buttonLamp_.setEnabled(enable);
			}
		});
	}*/
	
	public void saveAC() {
		prefAC_ = getSharedPreferences("Sai", Context.MODE_PRIVATE);
		Editor editor = prefAC_.edit();
		editor.putInt("lang_us", ACStatus_);
		editor.commit();		
	}
	
	public void saveTV() {
		prefTV_ = getSharedPreferences("Sai", Context.MODE_PRIVATE);
		Editor editor = prefTV_.edit();
		editor.putInt("lang_us", TVStatus_);
		editor.commit();		
	}
	
	public void saveCurt() {
		prefCurt_ = getSharedPreferences("Sai", Context.MODE_PRIVATE);
		Editor editor = prefCurt_.edit();
		editor.putInt("lang_us", CurtStatus_);
		editor.commit();		
	}
	
	public void saveLamp() {
		prefLamp_ = getSharedPreferences("Sai", Context.MODE_PRIVATE);
		Editor editor = prefLamp_.edit();
		editor.putInt("lang_us", lampStatus_);
		editor.commit();			
	}
	
	public void saveGlobal() {
		prefGlobal_ = getSharedPreferences("Sai", Context.MODE_PRIVATE);
		Editor editor = prefGlobal_.edit();
		editor.putInt("lang_us", globalStatus_);
		editor.commit();			
	}

}
