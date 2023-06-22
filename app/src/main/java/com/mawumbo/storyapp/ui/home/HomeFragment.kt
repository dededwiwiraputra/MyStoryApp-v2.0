package com.mawumbo.storyapp.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mawumbo.storyapp.R
import com.mawumbo.storyapp.adapter.StoryListAdapter
import com.mawumbo.storyapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = StoryListAdapter(onClick = { storyId ->
            navigateToDetailStory(storyId)
        })

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.layoutManager = layoutManager

        binding.rvStories.adapter = adapter

        viewModel.allStory.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle = viewLifecycleOwner.lifecycle, pagingData = it)
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sign_out -> {
                    viewModel.logout()
                    val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                    findNavController().navigate(action)
                    Toast.makeText(
                        requireContext(),
                        "Berhasil Logout",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                R.id.story_with_location -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToStoryMapFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddStoryFragment()
            findNavController().navigate(action)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun navigateToDetailStory(storyId: String?) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(storyId)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}