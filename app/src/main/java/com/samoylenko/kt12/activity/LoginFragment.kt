package com.samoylenko.kt12.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.databinding.FragmentLoginBinding
import com.samoylenko.kt12.error.getErrorMessage
import com.samoylenko.kt12.util.AndroidUtils
import com.samoylenko.kt12.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private val viewModelMenu: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(false)
        (activity as AppCompatActivity).supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.title = "Авторизация"
            it.hideOffset
        }

        val binding = FragmentLoginBinding.inflate(inflater, container, false)


        binding.btnLogin.setOnClickListener {
            val login = binding.textLogin.text.toString()
            val pass = binding.textPassword.text.toString()

            if (login.isEmpty() or pass.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Логин или пароль пустые",
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                viewModelMenu.authentication(login, pass)
                AndroidUtils.hideSoftKeyBoard(requireView())
            }
        }

        viewModelMenu.statusAuth.observe(viewLifecycleOwner, { status ->
            when (status) {
                true -> findNavController().navigateUp()
            }
        })

        viewModelMenu.state.observe(viewLifecycleOwner, { uimodel ->
            binding.errorGroup.isVisible = uimodel.errorVisible
            binding.progress.isVisible = uimodel.loading
            binding.errorText.text = uimodel.error.getErrorMessage(resources)

        })

        return binding.root
    }
}