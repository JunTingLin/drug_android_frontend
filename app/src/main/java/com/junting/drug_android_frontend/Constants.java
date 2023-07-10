package com.junting.drug_android_frontend;

class Constants {

    // values have to be globally unique
    static final String INTENT_ACTION_DISCONNECT = "com.junting.drug_android_frontend" + ".Disconnect";
    static final String NOTIFICATION_CHANNEL =  "com.junting.drug_android_frontend" + ".Channel";
    static final String INTENT_CLASS_MAIN_ACTIVITY = "com.junting.drug_android_frontend" + ".MainActivity";

    // values have to be unique within each app
    static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    private Constants() {}
}
