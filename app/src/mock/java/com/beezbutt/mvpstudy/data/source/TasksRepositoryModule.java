package com.beezbutt.mvpstudy.data.source;

import android.content.Context;

import com.beezbutt.mvpstudy.data.source.local.TasksLocalDataSource;
import com.beezbutt.mvpstudy.data.source.remote.TasksRemoteDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class TasksRepositoryModule {

    @Singleton
    @Provides
    @Local
    TasksDataSource provideTasksLocalDataSource(Context context) {
        return new TasksLocalDataSource(context);
    }

    @Singleton
    @Provides
    @Remote
    TasksDataSource provideTasksRemoteDataSource() {
        return new TasksRemoteDataSource();
    }
}
