package com.beezbutt.mvpstudy.data.source;

import com.beezbutt.mvpstudy.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by kevin on 26/09/2017.
 */

@Singleton
@Component(modules = {TasksRepositoryModule.class, ApplicationModule.class})
public interface TasksRepositoryComponent {
    TasksRepository getTasksRepository();
}
