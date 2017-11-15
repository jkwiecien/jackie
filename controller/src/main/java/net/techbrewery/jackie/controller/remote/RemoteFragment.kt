package net.techbrewery.jackie.controller.remote

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_remote.*
import net.techbrewery.jackie.R
import net.techbrewery.jackie.controller.ControllerActivity

/**
 * Created by Jacek Kwiecień on 01.11.2017.
 */
class RemoteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_remote, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupJoystick()
    }

    private fun setupJoystick() {
        joystickAtRemoteFragment.setOnMoveListener { angle, strength ->
            //            Timber.i("strength: $strength, angle: $angle")

//            var fixedAngle = angle
//            if (fixedAngle in 180..270) fixedAngle = 180
//            else if (fixedAngle in 270..360) fixedAngle = 0
//
//            Timber.i("fixedAngle: $fixedAngle")
//            var leftWheelMultiplier = 1f
//            var rightWheelMultiplier = 1f
//            when (fixedAngle) {
//                in 91..180 -> {
//                    //turning left
//                    var leftTurnAngle = Math.abs(90 - fixedAngle)
//                    if (leftTurnAngle > 90) leftTurnAngle = 90
//                    Timber.i("leftTurnAngle: $leftTurnAngle")
//                    leftWheelMultiplier = (90f - leftTurnAngle.toFloat()) / 90f
//                }
//                in 0..89 -> {
//                    //turning right
//                    var rightTurnAngle = Math.abs(0 - fixedAngle)
//                    if (rightTurnAngle > 90) rightTurnAngle = 90
//                    Timber.i("rightTurnAngle: $rightTurnAngle")
//                    rightWheelMultiplier = (90f - rightTurnAngle.toFloat()) / 90f
//                }
//            }
//
//            Timber.i("leftWheelMultiplier: $leftWheelMultiplier, rightWheelMultiplier: $rightWheelMultiplier")

            (activity as? ControllerActivity)?.sendMovementParams(angle, strength)
        }
    }
}