package net.techbrewery.jackie.controller.remote

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.techbrewery.jackie.R

/**
 * Created by Jacek Kwiecień on 01.11.2017.
 */
class RemoteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_remote, container, false)
    }
}