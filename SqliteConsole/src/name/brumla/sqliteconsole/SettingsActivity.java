package name.brumla.sqliteconsole;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.preference.PreferenceActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders(List<Header> target) {
//		loadHeadersFromResource(R.xml.pref_general, target);
	}

}
