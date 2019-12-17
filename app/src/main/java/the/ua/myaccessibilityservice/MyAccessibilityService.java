package the.ua.myaccessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

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
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s" ,
                getEventType(event), event.getClassName(), event.getPackageName(),
                event.getEventTime(), getEventText(event)));
        getYouTubeSearch(event);


    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.flags = AccessibilityServiceInfo.DEFAULT |
                AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS |
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;

        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        info.notificationTimeout = 100;
        setServiceInfo(info);

    }

    private void getYouTubeSearch(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName().equals("com.google.android.youtube")) {
                AccessibilityNodeInfo currentNode = getRootInActiveWindow();
                if (currentNode != null) {

//                    List<AccessibilityNodeInfo> nodeInfoList = currentNode.findAccessibilityNodeInfosByText("Введите запрос");
                    List<AccessibilityNodeInfo> nodeInfoList =
                            currentNode.findAccessibilityNodeInfosByText("Введите запрос");

                    if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
                        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {

                            if (nodeInfo.getClassName().equals("android.widget.EditText")) {
                                Log.e("Ed", (String) currentNode.getContentDescription());
                            }

                            Log.e("1111", String.valueOf(nodeInfo.getContentDescription()));
                            Bundle arguments = new Bundle();
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            arguments.putString(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "Lol777");
                            Log.e("2222", String.valueOf(nodeInfo));
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//                            nodeInfo.setText("fdfsdf");

                            Log.e("1111", String.valueOf(nodeInfo.getClassName()));

                        }

                    }
                }
            }
        }
    }

}
