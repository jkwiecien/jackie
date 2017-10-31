package net.techbrewery.jackie.controller

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_main.*
import net.techbrewery.jackie.R
import timber.log.Timber

class ControllerActivity : AppCompatActivity() {

    lateinit var viewModel: ControllerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(ControllerViewModel::class.java)
        viewModel.eventLiveData.observe(this, Observer<ControllerViewEvent> { event ->
            when (event) {
                is ControllerViewEvent.RobotResponded -> Timber.i("Robot responded")
                is ControllerViewEvent.ControllerIpFound -> controllerIpAddressLabel.text = event.labelText
                is ControllerViewEvent.RobotIpFound -> robotIpAddressLabel.text = event.labelText
            }
        })

        robotIpEndInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                viewModel.onRobotIpAddressChanged(text.toString())
            }

        })

        sendButton.setOnClickListener {
            viewModel.toggleLed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }
}
