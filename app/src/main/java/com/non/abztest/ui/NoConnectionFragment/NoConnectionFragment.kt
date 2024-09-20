package com.non.abztest.ui.NoConnectionFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.non.abztest.R
import com.non.abztest.databinding.FragmentNoConnectionBinding
import com.non.abztest.utils.NetworkUtil
import com.non.abztest.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoConnectionFragment : Fragment() {
    private lateinit var binding: FragmentNoConnectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_no_connection, container, false)
        return binding.root
    }

    private fun setupEdgeToEdgeDisplay() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(left = insets.left, top = insets.top, right = insets.right)

            binding.tryAgainNoConnectionBtn.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val windowInsetsController = ViewCompat.getWindowInsetsController(requireActivity().window.decorView)
        windowInsetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEdgeToEdgeDisplay()

        binding.tryAgainNoConnectionBtn.setOnClickListener {
            if (NetworkUtil.isNetworkAvailable(requireContext())) {
                findNavController().navigate(R.id.nav_users)
            }else {
                Toast.makeText(requireContext(), "Still no connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val windowInsetsController = ViewCompat.getWindowInsetsController(requireActivity().window.decorView)
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
    }
}