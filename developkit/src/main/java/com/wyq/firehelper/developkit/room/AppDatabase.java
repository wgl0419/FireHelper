package com.wyq.firehelper.developkit.room;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.wyq.firehelper.developkit.room.entity.UserEntity;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {UserEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    @VisibleForTesting
    private static final String DATABASE_NAME = "user-db";
    private static AppDatabase sInstance;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(Context context, AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }


    private static AppDatabase buildDatabase(final Context context, AppExecutors executors) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Logger.i("onCreate user-db");
                        executors.diskIO().execute(() -> {
                            AppDatabase database = AppDatabase.getInstance(context, executors);
                            UserEntity user = new UserEntity();
                            user.setUid(0);
                            user.setFirstName("Wu");
                            user.setLastName("Yuanqing");
                            database.runInTransaction(() -> {
                                database.userDao().insertUser(user);
                            });
                            //notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                .addMigrations(migration)
                .build();
    }

    static Migration migration = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user ADD COLUMN age INTEGER");
        }
    };

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(Context context){
        if(context.getDatabasePath(DATABASE_NAME).exists()){
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}
