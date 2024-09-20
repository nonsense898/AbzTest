package com.non.abztest.ui.UsersFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.non.abztest.R
import com.non.abztest.adapter.UserAdapter
import com.non.abztest.databinding.FragmentUsersBinding
import com.non.abztest.databinding.ItemProgressBinding
import com.non.abztest.paging.UsersLoaderStateAdapter
import com.non.abztest.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var progressBinding: ItemProgressBinding
    private lateinit var userAdapter: UserAdapter

    private val mainViewModel: MainViewModel by viewModels({ requireActivity() })

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
        progressBinding = DataBindingUtil.inflate(inflater, R.layout.item_progress, container, false)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        observeUsers()
        handleReload()


        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            userAdapter = UserAdapter()
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter.withLoadStateFooter(UsersLoaderStateAdapter())
        }

        userAdapter.addLoadStateListener { loadState ->
            progressBinding.progress.isVisible = loadState.source.refresh is LoadState.Loading

            val isEmpty =
                loadState.source.refresh is LoadState.Loading && userAdapter.itemCount == 0

            binding.emptyState.isVisible = isEmpty
            binding.recyclerView.isVisible = !isEmpty

            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    it.error.localizedMessage ?: "Unknown error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.AddUserBtn.setOnClickListener {
            findNavController().navigate(R.id.nav_signup)
        }
    }


    private fun observeUsers() {
        mainViewModel.users.observe(viewLifecycleOwner) { pagingData ->
            lifecycleScope.launch {
                userAdapter.submitData(pagingData)
            }
        }
    }

    private fun handleReload() {
        mainViewModel.reloadUsers.observe(viewLifecycleOwner) { shouldReload ->
            if (shouldReload) {
                lifecycleScope.launch {
                    userAdapter.submitData(lifecycle, PagingData.empty())
                    delay(100)
                    mainViewModel.users.observe(viewLifecycleOwner) { newPagingData ->
                        userAdapter.submitData(lifecycle, newPagingData)
                    }
                    mainViewModel.finishReloadingUsers()
                }
            }
        }
    }
}


