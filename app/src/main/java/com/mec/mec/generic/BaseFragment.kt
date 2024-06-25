package com.mec.mec.generic

import androidx.fragment.app.Fragment
import com.mec.mec.MainActivity

abstract class BaseFragment : Fragment() {
    abstract fun isLoggedin(): Boolean


    override fun onResume() {
        super.onResume()
        activity?.let {
            if (it is MainActivity) {
                it.isLoggedIn(isLoggedin())
            }
        }
    }


}