package net.techbrewery.jackie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import net.techbrewery.jackie.p2p.DiscoveryActivity

/**
 * Created by Jacek Kwiecień on 26.10.2017.
 */
class EngineActivity : Activity() {

    val handler = Handler()
    val service = PeripheralManagerService()
    var gpio: Gpio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gpio = service.openGpio("GPIO_37")
        gpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        gpio?.setActiveType(Gpio.ACTIVE_LOW)

        handler.post(blinkRunnable())

        startActivity(Intent(this, DiscoveryActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(blinkRunnable())
        gpio?.close()
    }

    private fun blinkRunnable(): Runnable = Runnable {
        Log.i("jacek", "STARTUJEMY")
        val safeGpio = this@EngineActivity.gpio
        safeGpio?.let {
            safeGpio.value = !safeGpio.value
            handler.postDelayed(blinkRunnable(), 1000)
        }
    }
}