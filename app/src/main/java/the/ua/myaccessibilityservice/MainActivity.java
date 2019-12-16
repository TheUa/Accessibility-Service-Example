package the.ua.myaccessibilityservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button youTubeClick = findViewById(R.id.click);

        // create dialog
        if (checkAccess()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }

        youTubeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startApp();
            }
        });

    }

    private void startApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.messaging.ui.conversationlist");
        // Запуск из нужного места без предыстории приложения
        assert launchIntent != null;
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchIntent);
    }

    protected boolean checkAccess() {
        String string = getString(R.string.accessibilityservice_id);
        for (AccessibilityServiceInfo id : ((AccessibilityManager) Objects.requireNonNull(getSystemService(Context.ACCESSIBILITY_SERVICE))).getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)) {
            if (string.equals(id.getId())) {
                return true;
            }
        }
        return false;
    }
}
