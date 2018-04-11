package lynn.andr.webserver;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SmallTest;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zowee-laisc on 2018/4/11.
 */

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Button mStartButton;
    private TextView mAddrText;

    public MainActivityTest(){//否则会报错
        super(MainActivity.class);
    }

    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mStartButton = getActivity().findViewById(R.id.btn_start);
        mAddrText = getActivity().findViewById(R.id.text_ip);
    }


    @LargeTest
    public void testStartService(){

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mStartButton.performClick();//需要放在主线程，否则报错
            }
        });
        SystemClock.sleep(5000);
        //^((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}$
        assertTrue(mAddrText.getText().toString().contains("http://"));

        Pattern pattern = Pattern.compile("((25[0-5]|2[0-4]\\\\d|[1]{1}\\\\d{1}\\\\d{1}|[1-9]{1}\\\\d{1}|\\\\d{1})($|(?!\\\\.$)\\\\.)){4}$");
        Matcher matcher = pattern.matcher(mAddrText.getText().toString());
        assertTrue(matcher.find());
    }
}
