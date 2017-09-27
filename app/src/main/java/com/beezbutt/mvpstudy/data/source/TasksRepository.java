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

/**
 * Created by kevin on 26/09/2017.
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 * </p>
 * <p>
 * By marking the constructor with {@code @Inject} and the class with {@code Singleton}, Dagger
 * injects the dependencies required to create an instance of the TasksRepository (if it fails, it
 * emits a compiler error). It uses {@Link TaskRepositoryModule} to do so, and the constructed
 * instance is available in {@Link TaskRepositoryComponent}.
 * </p>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */

public class TasksRepository implements TasksDataSource {

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Task> mCachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     * This variable has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;


    /**
     * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
     * required to create an instance of the TasksRepository. Because {@link TasksDataSource} is an
     * interface, we must provide to Dagger a way to build those arguments, this is done in
     * {@link TasksRepositoryModule}.
     * <p>
     * when two arguments or more have the same type, we must provide to Dagger a way to
     * differentiate them. This is done using a {@code @Qualifier}
     * </p>
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code Nullable} values.
     * @param tasksRemoteDataSource
     * @param tasksLocalDataSource
     */
    @Inject
    public TasksRepository(@Remote TasksDataSource tasksRemoteDataSource,
           @Local TasksDataSource tasksLocalDataSource) {
        this.mTasksRemoteDataSource = tasksRemoteDataSource;
        this.mTasksLocalDataSource = tasksLocalDataSource;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if(mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        if(mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data frome the network.
            getTasksFromRemoteDataSource(callback);
        }
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                // after checking and refreshing the cache
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Task> tasks) {
        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for(Task task: tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        mTasksLocalDataSource.deleteAllTasks();
        for(Task task : tasks) {
            mTasksLocalDataSource.saveTask(task);
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.saveTask(task);
        mTasksLocalDataSource.saveTask(task);

        // Do in memory cache update to keep the app UI up-to-date
        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);

        // Do in memory cache update to keep the app UI up to date
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), completedTask);

    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        // Do in memory cache update to keep the app UI up to date
        Task activateTask = new Task(task.getTitle(), task.getDescription(), task.getId(), false);
        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), activateTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();
        if(mCachedTasks != null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     * </p>
     * @param taskId        Id of the task
     * @param callback      callback for response from dataSource
     */
    @Override
    public void getTask(@NonNull String taskId, @NonNull final GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        final Task cachedTask = getTaskWithId(taskId);

        // Respond immediately with cache if available
        if(cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Nullable
    private Task getTaskWithId(@NonNull String taskId) {
        checkNotNull(taskId);
        if(mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskId);
        }
    }

}
