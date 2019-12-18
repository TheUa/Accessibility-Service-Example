package the.ua.myaccessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {
    public MyAccessibilityService() {
    }

    static final String TAG = "RecorderService";

    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
        }
        return "default";
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, String.format(
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
                getEventType(event), event.getClassName(), event.getPackageName(),
                event.getEventTime(), getEventText(event)));

        AccessibilityNodeInfo nodeInfoCompat = event.getSource();

        if (nodeInfoCompat != null) {
            nodeInfoCompat.refresh();
            String id = nodeInfoCompat.getViewIdResourceName();
            if (id == null) {
                Log.v("ViewID", "id = null");
            } else
                Log.v("ViewID", id);
        }
        setYouTubeSearch(event);

        setInputText(event);

        setClickedAndBackHome(event);
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT |
                AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS |
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;

        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);


    }

    private void setYouTubeSearch(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getPackageName().equals("com.google.android.youtube")) {
                AccessibilityNodeInfo currentNode = event.getSource();
//              Работает только с русской локалью
//              Конечно, нужно вместо поиска по тексту использовать поиск по ссылке (findAccessibilityNodeInfosByViewId),
//              но не получилось сразу найти необходимую ссылку, а времени к сожалению не много свободного,
//              поэтому просто для демонстрации работоспособности оставляю так
                if (currentNode != null) {
                    List<AccessibilityNodeInfo> nodeInfoList =
                            currentNode.findAccessibilityNodeInfosByText("Введите запрос");
                    if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
                        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
    }


    private void setInputText(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            if (event.getPackageName().equals("com.google.android.youtube")) {
                AccessibilityNodeInfo currentNodeEditText = event.getSource();
                if (currentNodeEditText != null && event.getClassName().equals("android.widget.EditText")) {
                    Bundle arguments = new Bundle();
                    arguments.putString(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "Рандомный текст");
                    currentNodeEditText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    currentNodeEditText.refresh();

                }
            }
        }
    }

    private void setClickedAndBackHome(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getPackageName().equals("com.google.android.youtube")) {
                AccessibilityNodeInfo nodeInfoForGetList = getRootInActiveWindow();
                if (nodeInfoForGetList != null) {
                    List<AccessibilityNodeInfo> nodeEditTextList =
                            nodeInfoForGetList.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/results");
                    if (nodeEditTextList != null && !nodeEditTextList.isEmpty()) {
                        for (AccessibilityNodeInfo nodeChild : nodeEditTextList) {
                            if (nodeChild.getChild(0) != null)
                                nodeChild.getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // Actions to do after 5 seconds
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 5000);
                        }
                    }
                }
            }
        }
    }
}
