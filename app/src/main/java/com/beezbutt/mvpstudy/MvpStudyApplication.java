package com.beezbutt.mvpstudy;

import android.app.Application;

/**
 * Created by kevin on 25/09/2017.
 * Even though Dagger2 allows annotation a {@link dagger.Component} as a singleton, the code itself
 * must ensure only one instance of the class is created. Therefore, we create a custom
 * {@link Application} class to store a singleton reference to the {@link }
 * <p>
 * The Application is made of 2 Dagger components, as follows:<BR/>
 * {@link}
 * </p>
 */
public class MvpStudyApplication extends Application{


}
