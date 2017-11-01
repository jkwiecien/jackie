package net.techbrewery.jackie.controller

import android.app.Fragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.techbrewery.jackie.R
import net.techbrewery.jackie.controller.connection.ConnectionFragment
import net.techbrewery.jackie.controller.remote.RemoteFragment
import timber.log.Timber

class ControllerActivity : AppCompatActivity() {

    lateinit var viewModel: ControllerViewModel

    private val currentFragment: Fragment? get() = fragmentManager.findFragmentById(R.id.containerAtControllerActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        viewModel = ViewModelProviders.of(this).get(ControllerViewModel::class.java)
        viewModel.eventLiveData.observe(this, Observer<ControllerViewEvent> { event ->
            when (event) {
                is ControllerViewEvent.RobotResponded -> Timber.i("Robot responded")
                is ControllerViewEvent.ControllerIpFound -> (currentFragment as? ConnectionFragment)?.updateControllerIpLabel(event.labelText)
                is ControllerViewEvent.RobotIpFound -> (currentFragment as? ConnectionFragment)?.updateRobotIpLabel(event.labelText)
            }
        })

        if (savedInstanceState == null) addConnectionFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }

    private fun addConnectionFragment() {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.containerAtControllerActivity, ConnectionFragment())
        transaction.commit()
    }

    fun connect(robotLastIpPart: String) {
        viewModel.connect(robotLastIpPart)
    }

    fun toggleLed() {
        viewModel.toggleLed()
    }

    fun showRemote() {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.containerAtControllerActivity, RemoteFragment())
        transaction.commit()
    }

}
