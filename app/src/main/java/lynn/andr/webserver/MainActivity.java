package lynn.andr.webserver;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
public class MainActivity extends Activity {

	private TextView hostipText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button b =  (Button) findViewById(R.id.button);
		hostipText= (TextView) findViewById(R.id.hostip);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startintent = new Intent(MainActivity.this,SocketService.class);
				startService(startintent);
				hostipText.setText("starting webserver......");
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						hostipText.setText("try visit http://"+SocketService.hostip+":"+SocketService.PORT);
					}
				}, 5000);
			}
		});
		
		
		
	}

	private Handler mHandler = new Handler();
	
}
