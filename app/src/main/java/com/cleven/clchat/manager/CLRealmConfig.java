package com.cleven.clchat.manager;

import android.content.Context;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class CLRealmConfig {

    /**
     * Realm版本号
     */
    private static final int realmConfigVersionNumber = 0;

    public static void setupRealm(Context context){
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("clchat.realm") //文件名
                .schemaVersion(realmConfigVersionNumber)
                .migration(new CustomMigration())//升级数据库
                .build();
        Realm.getInstance(config);
    }


    /**
     * 升级数据库
     */
    static class CustomMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();
            if (oldVersion == 0 && newVersion == 1) {


            }
        }
    }
}
