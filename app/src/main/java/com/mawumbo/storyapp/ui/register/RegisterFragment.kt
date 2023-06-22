package com.mawumbo.storyapp.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mawumbo.storyapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            viewModel.register(name, email, password)
            setFragmentResult("email_request", bundleOf("email" to email))
            setFragmentResult("password_request", bundleOf("password" to password))
//            findNavController().navigateUp()
        }

        binding.edtPassword.isError.observe(viewLifecycleOwner) {
            binding.btnRegister.isEnabled = it
        }

        val uiState = viewModel.uiState
        lifecycleScope.launch {
            uiState.collect {
                if (it.navigateToLogin == true) {
                    findNavController().navigateUp()
                    viewModel.navigatedToLogin()
                }
                it.error?.let { error ->
                    Snackbar.make(
                        requireView(),
                        error,
                        Snackbar.LENGTH_SHORT
                    ).addCallback(
                        object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                viewModel.errorShown()
                            }
                        }
                    ).show()
                }
            }

        }

    }
}