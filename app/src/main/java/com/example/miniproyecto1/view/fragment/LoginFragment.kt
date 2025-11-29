package com.example.miniproyecto1.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.miniproyecto1.R
import com.example.miniproyecto1.databinding.FragmentLoginBinding
import com.example.miniproyecto1.model.auth.UserRequest
import com.example.miniproyecto1.utils.SessionManager
import com.example.miniproyecto1.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        // 🔹 Validación tiempo real
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            loginViewModel.checkFieldsCompletion(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            val password = text.toString()
            loginViewModel.validatePassword(password)
            loginViewModel.checkFieldsCompletion(
                binding.etEmail.text.toString(),
                password
            )
        }

        // 🔹 Botón Login
        binding.btnLogin.setOnClickListener {
            loginViewModel.loginUser(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        // 🔹 Registrarse
        binding.tvRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            val userRequest = UserRequest(email, pass)
            loginViewModel.registerUser(userRequest)
        }
    }

    private fun observeViewModel() {
        // 🔹 Habilitar botones cuando campos completos y password válida
        loginViewModel.areFieldsComplete.observe(viewLifecycleOwner) { ready ->
            binding.btnLogin.isEnabled = ready
            binding.tvRegister.isEnabled = ready
        }

        // 🔹 Validación contraseña tiempo real
        loginViewModel.isPasswordValid.observe(viewLifecycleOwner) { isValid ->
            if (!isValid) {
                binding.tilPassword.error = "Mínimo 6 dígitos"
            } else {
                binding.tilPassword.error = null
            }
        }

        // 🔹 Progreso
        loginViewModel.progressState.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // 🔹 Resultado login / registro
        loginViewModel.authResult.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                // Guardamos sesión y navegamos al home
                sessionManager.saveLoginState(true)
                findNavController().navigate(R.id.action_loginFragment_to_homeInventoryFragment)
            } else {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_loginFragment_to_homeInventoryFragment)
        }
    }
}
