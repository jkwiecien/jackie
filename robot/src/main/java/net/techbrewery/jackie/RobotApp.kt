package net.techbrewery.jackie

import android.app.Application
import timber.log.Timber

/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */
class RobotApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}