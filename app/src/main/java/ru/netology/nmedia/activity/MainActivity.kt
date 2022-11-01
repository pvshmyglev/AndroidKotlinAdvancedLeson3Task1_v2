package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: AppAuth
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_auth, menu)
        menu?.setGroupVisible(R.id.group_authorized, viewModel.authorized)
        menu?.setGroupVisible(R.id.group_unauthorized, !viewModel.authorized)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =

        when (item.itemId) {
            R.id.item_auth -> {
                findNavController(R.id.container_for_fragments).navigate(R.id.nav_auth_fragment)
                true
            }
            R.id.item_registration -> {
                findNavController(R.id.container_for_fragments).navigate(R.id.nav_auth_fragment)
                true
            }
            R.id.item_logout -> {
                auth.clearAuth()
                true
            }
            else -> {
                false
            }
        }

}