package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewmodel.AuthViewModel

@AndroidEntryPoint
class FragmentAuth : Fragment() {

    private val viewModel: AuthViewModel  by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAuthBinding.inflate(inflater, container, false)

        binding.buttonLogin.setOnClickListener{
            val login = binding.fieldLogin.text.toString()
            val password = binding.fieldPassword.text.toString()
            viewModel.loginAsUser(login, password)
        }

        viewModel.authCompleted.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root

    }

}