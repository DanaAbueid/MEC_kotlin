package com.mec.mec
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isEmpty
import androidx.navigation.findNavController
import com.mec.mec.databinding.ActivityMainBinding
import com.mec.mec.viewModel.AuthViewModel
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var selectedItemId = R.id.customer_list
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load saved language preference
      //  loadLanguagePreference(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()
        UserSession.defaultMenu = R.menu.bottom_nav_menu
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set language selection button listener
//        binding?.btnSelectLanguage?.setOnClickListener {
//            showLanguageSelectionDialog()
//        }
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
        return when (id) {
            R.id.navigation_customer -> R.id.customer_list
            R.id.navigation_maintenance -> R.id.maintenanceFragment
            R.id.navigation_employee -> R.id.employeeFragment
            else -> null
        }
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

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()
    }

    private fun loadLanguagePreference(context: Context) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "en")
        setLocale(context, language ?: "en")
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Arabic")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Language")
        builder.setSingleChoiceItems(languages, -1) { dialog, which ->
            when (which) {
                0 -> {
                    setLocale(this, "en")
                    saveLanguagePreference(this, "en")
                }
                1 -> {
                    setLocale(this, "ar")
                    saveLanguagePreference(this, "ar")
                }
            }
            dialog.dismiss()
            val intent = intent
            finish()
            startActivity(intent)
        }
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_language -> {
                showLanguageSelectionDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
