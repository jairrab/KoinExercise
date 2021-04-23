package com.github.jairrab.koinexercise

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.github.jairrab.koinexercise.BaseViewModel.Companion.BUNDLE_ARGS

abstract class BaseFragment(@LayoutRes resId: Int) : Fragment(resId) {

    override fun setArguments(args: Bundle?) {
        if (args != null) {
            val bundle = Bundle(args).apply { putBundle(BUNDLE_ARGS, args) }
            super.setArguments(bundle)
        } else {
            super.setArguments(null)
        }
    }

    fun navigate(@IdRes resId: Int, args: Bundle? = null) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_to_left)
            .setPopEnterAnim(R.anim.enter_from_left)
            .setPopExitAnim(R.anim.exit_to_right)
            .build()
        findNavController().navigate(resId, args, navOptions)
    }

    fun navigate(navDirections: NavDirections) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_to_left)
            .setPopEnterAnim(R.anim.enter_from_left)
            .setPopExitAnim(R.anim.exit_to_right)
            .build()
        findNavController().navigate(navDirections, navOptions)
    }
}

abstract class BaseViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val arguments get() = savedStateHandle.get<Bundle>(BUNDLE_ARGS)

    @MainThread
    inline fun <reified Args : NavArgs> navArgs() = NavArgsLazy(Args::class) {
        arguments ?: throw IllegalStateException("ViewModel $this has null arguments")
    }

    companion object{
        const val BUNDLE_ARGS = "BUNDLE_ARGS"
    }
}