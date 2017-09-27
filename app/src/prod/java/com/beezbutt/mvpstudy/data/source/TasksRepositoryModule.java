package com.beezbutt.mvpstudy.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beezbutt.mvpstudy.data.models.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;


@Module
public class TasksRepositoryModule {

    @Singleton
    @Provides
    @Local
    TasksDataSource provideTasksLocalDataSource(Context context) {
        return new TasksLocalDataSource(context)
    }

    @Singleton
    @Provides
    @Remote
    TasksDataSource provideTasksRemoteDataSource() {
        return new TasksRemoteDataSource();
    }
}
