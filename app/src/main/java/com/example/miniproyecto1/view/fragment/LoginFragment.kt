package com.example.miniproyecto1.view.fragment

import android.appwidget.AppWidgetManager
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
import com.example.miniproyecto1.view.MainActivity
import com.example.miniproyecto1.viewmodel.LoginViewModel
import com.example.miniproyecto1.widget.InventoryWidgetProvider
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
        binding.btnLogin.setOnClickListener {
            loginViewModel.loginUser(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }
        binding.tvRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            val userRequest = UserRequest(email, pass)
            loginViewModel.registerUser(userRequest)
        }
    }

    private fun observeViewModel() {
        loginViewModel.areFieldsComplete.observe(viewLifecycleOwner) { ready ->
            binding.btnLogin.isEnabled = ready
            binding.tvRegister.isEnabled = ready
        }
        loginViewModel.isPasswordValid.observe(viewLifecycleOwner) { isValid ->
            binding.tilPassword.error = if (!isValid) "Mínimo 6 dígitos" else null
        }
        loginViewModel.progressState.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        loginViewModel.authResult.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                sessionManager.saveLoginState(true)
                handlePostLoginNavigation()
            } else {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handlePostLoginNavigation() {
        val mainActivity = requireActivity() as? MainActivity
        val fromWidget = mainActivity?.isFromWidget() ?: false
        val widgetAction = mainActivity?.getWidgetAction()
        val widgetId = mainActivity?.getWidgetId() ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (fromWidget && widgetAction != null) {
            when (widgetAction) {
                "toggleBalance" -> {
                    if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        val prefs = requireContext().getSharedPreferences(
                            "inventory_widget_prefs",
                            android.content.Context.MODE_PRIVATE
                        )
                        prefs.edit().putBoolean("visible_$widgetId", true).apply()
                    }
                    InventoryWidgetProvider.updateAllWidgets(requireContext())
                    requireActivity().finish()
                }
                "manageInventory" -> {
                    findNavController().navigate(R.id.action_loginFragment_to_homeInventoryFragment)
                }
                else -> {
                    findNavController().navigate(R.id.action_loginFragment_to_homeInventoryFragment)
                }
            }
        } else {
            findNavController().navigate(R.id.action_loginFragment_to_homeInventoryFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.isLoggedIn()) {
            handlePostLoginNavigation()
        }
    }
}