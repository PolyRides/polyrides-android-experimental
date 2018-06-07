package com.polyrides.polyridesv2.notifications;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.polyrides.polyridesv2.AppMain;

import java.util.Map;

public class PolyridesMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        Map<String,String> data = remoteMessage.getData();
    Map<String, String> dnn = data;

    Intent intent = new Intent(this, AppMain.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    String uid = "";
    if (data.containsKey("offerUid")) {
        uid = data.get("offerUid");
        intent.putExtra("offerUid", uid);
    }
    else {
        uid = data.get("requestUid");
        intent.putExtra("requestUid", uid);
    }

    startActivity(intent);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


}
