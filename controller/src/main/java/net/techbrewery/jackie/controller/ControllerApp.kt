package net.techbrewery.jackie.controller

import android.app.Application
import net.techbrewery.jackie.BuildConfig
import timber.log.Timber

/**
 * Created by Jacek Kwiecień on 01.11.2017.
 */
class ControllerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}