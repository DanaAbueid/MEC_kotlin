package com.mec.mec


import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.isEmpty
import androidx.navigation.findNavController
import com.mec.mec.databinding.ActivityMainBinding
import com.mec.mec.databinding.ViewMenuItemBinding


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var selectedItemId = R.id.customer_list
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        UserSession.defaultMenu = R.menu.bottom_nav_menu


    }

    fun isLoggedIn(state: Boolean) {
        binding?.navView?.visibility = View.VISIBLE.takeIf { state } ?: View.GONE
        if (state) {
            setBottomNav()
        }
    }

    private fun setBottomNav() {
        UserSession.defaultMenu?.let {
            if (binding?.navView?.menu?.isEmpty() == true) {
                binding?.navView?.menu?.clear()
                binding?.navView?.inflateMenu(it)
            }
            binding?.navView?.setSelectedItemId(selectedItemId)
            binding?.navView?.setOnItemSelectedListener {
                matchIdToFragment(it.itemId)?.let { idNotNull ->
                    selectedItemId = it.itemId
                    navigate(idNotNull)
                }
                true
            }
        }
    }

//    private fun setMenu() {
//        binding?.navView?.menu?.let { menu ->
//            binding?.clMenu?.llItems?.removeAllViews()
//            menu.forEach {
//                val itemBinding = ViewMenuItemBinding.inflate(layoutInflater)
//                itemBinding.ivLogo.setImageDrawable(it.icon)
//                itemBinding.tvTitle.text = it.title
//
//                itemBinding.root.setOnClickListener { view ->
//                    toggleSideMenu(false)
//                    matchIdToFragment(it.itemId)?.let { idNotNull ->
//                        selectedItemId = it.itemId
//                        navigate(idNotNull)
//                    }
//                }
//                if (it.itemId == selectedItemId) {
//                    itemBinding.tvTitle.setTextColor(
//                        ContextCompat.getColor(
//                            this,
//                            R.color.light_green
//                        )
//                    )
//                    itemBinding.ivLogo.setColorFilter(
//                        ContextCompat.getColor(
//                            this,
//                            R.color.light_green
//                        )
//                    )
//                    itemBinding.root.setBackgroundColor(
//                        ContextCompat.getColor(
//                            this,
//                            R.color.semi_transparent_light_green
//                        )
//                    )
//                }
//            }
//
//
//        }
//    }

    private fun matchIdToFragment(id: Int): Int? {
        if (id == R.id.navigation_customer)
            return R.id.customer_list
        else if (id == R.id.navigation_maintenance)
            return R.id.maintenanceFragment
        else if (id == R.id.navigation_employee)
            return R.id.employeeFragment
        else if (id == R.id.navigation_settings)
            return R.id.settingsFragment
        return null
    }

    private fun navigate(@IdRes fragId: Int) {

        if (isFragmentInBackStack(fragId))
            findNavController(R.id.mobile_navigation).popBackStack(fragId, false)
        else
            findNavController(R.id.mobile_navigation).navigate(fragId)
    }

    private fun isFragmentInBackStack(destinationId: Int) =
        try {
            findNavController(R.id.mobile_navigation).getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }

}