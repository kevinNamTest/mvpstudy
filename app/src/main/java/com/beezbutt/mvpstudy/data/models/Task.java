package com.beezbutt.mvpstudy.data.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Created by kevin on 26/09/2017.
 * Immutable model class for a Task
 */
public final class Task {
    @NonNull
    private final String mId;

    @Nullable
    private final String mTitle;

    @Nullable
    private final String mDescription;

    private final boolean mComplete;

    /**
     * Use this constructor to create a new active Task.
     * @param title        title of the task
     * @param description  description of the task
     */
    public Task(@Nullable String title, @Nullable String description) {
        this(title, description, UUID.randomUUID().toString(), false);
    }

    /**
     * Use this constructor to create an active task if the task already has an id (copy of another
     * task).
     *
     * @param title        title of the task
     * @param description  description of the task
     * @param id           id of the task
     */
    public Task(@Nullable String title, @Nullable String description, @NonNull String id) {
        this(title, description, id, false);
    }

    /**
     * Use this constructor to create an active task if the task already has an id (copy of another
     * task).
     *
     * @param title        title of the task
     * @param description  description of the task
     * @param id           id of the task
     * @param complete      true if the task is completed, false if it's active
     */
    public Task(@Nullable String title, @Nullable String description,
                @NonNull String id, boolean complete) {
        this.mId = id;
        this.mTitle = title;
        this.mDescription = description;
        this.mComplete = complete;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    public boolean isCompleted() {
        return mComplete;
    }

    public boolean isActive() {
        return !mComplete;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) &&
                Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        Task task = (Task)obj;
        return Objects.equal(mId, task.mId) &&
                Objects.equal(mTitle, task.mTitle) &&
                Objects.equal(mDescription, task.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription);
    }

    @Override
    public String toString() {
        return "Task with title" + mTitle;
    }
}
