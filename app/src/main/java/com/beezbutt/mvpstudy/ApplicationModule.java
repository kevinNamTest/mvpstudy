package com.beezbutt.mvpstudy;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kevin on 26/09/2017.
 * This is a Dagger module. We use this to pass in the Context dependency to the
 * {@link com.beezbutt.mvpstudy.data.source.TasksRepositoryComponent}.
 */
@Module
public final class ApplicationModule {
    private final Context mContext;

    public ApplicationModule(Context context) {
        this.mContext = context;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}
