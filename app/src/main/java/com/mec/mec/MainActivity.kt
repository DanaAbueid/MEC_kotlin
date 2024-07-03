package com.mec.mec
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isEmpty
import androidx.navigation.findNavController
import com.mec.mec.databinding.ActivityMainBinding
import com.mec.mec.viewModel.AuthViewModel


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var selectedItemId = R.id.customer_list
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()
        UserSession.defaultMenu = R.menu.bottom_nav_menu
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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


    private fun matchIdToFragment(id: Int): Int? {
        if (id == R.id.navigation_customer)
            return R.id.customer_list
        else if (id == R.id.navigation_maintenance)
            return R.id.maintenanceFragment
        else if (id == R.id.navigation_employee)
            return R.id.employeeFragment
     //   else if (id == R.id.navigation_settings)
      //      return R.id.settingsFragment
        return null
    }

    private fun navigate(@IdRes fragId: Int) {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        if (isFragmentInBackStack(fragId))
            navController.popBackStack(fragId, false)
        else
            navController.navigate(fragId)
    }

    private fun isFragmentInBackStack(destinationId: Int) =
        try {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }

}