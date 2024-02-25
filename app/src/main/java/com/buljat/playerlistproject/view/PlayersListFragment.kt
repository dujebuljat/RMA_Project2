package com.buljat.playerlistproject.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buljat.playerlistproject.R
import com.buljat.playerlistproject.viewmodel.PlayersListViewModel
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.buljat.playerlistproject.databinding.FragmentPlayersListBinding
import com.buljat.playerlistproject.model.PlayerDbEntity
import com.buljat.playerlistproject.viewmodel.PlayersListViewModelFactory

class PlayersListFragment : Fragment() {

    private val viewModel: PlayersListViewModel by viewModels {
        PlayersListViewModelFactory(requireActivity().application)
    }

    private var _binding: FragmentPlayersListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.players()
        viewModel.playerList.observe(viewLifecycleOwner) {
            populateListView(viewModel.playerList)
        }
        activateComponents()

    }

    private fun activateComponents() {
        binding.newPlayerFab.setOnClickListener {
            val action = PlayersListFragmentDirections.actionPlayersListFragmentToCrudFragment(-1)
            findNavController().navigate(action)
        }
    }

    private fun populateListView(playerList: LiveData<List<PlayerDbEntity>?>) {
        binding.linearLayout.removeAllViews()
        playerList.value?.let { playerList ->
            for (player in playerList) {
                val viewHolder = LayoutInflater.from(this.requireContext()).inflate(R.layout.player_list_item, null)

                viewHolder.findViewById<TextView>(R.id.tv_name).text = player.name
                viewHolder.findViewById<TextView>(R.id.tv_sport).text = player.sport

                viewHolder.setOnClickListener {
                    val action = PlayersListFragmentDirections.actionPlayersListFragmentToCrudFragment(player.id)
                    findNavController().navigate(action)
                }
                binding.linearLayout.addView(viewHolder)
            }
        }
    }

}