package com.msmikeescom.minesweeper.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.ui.adapter.PersonalRecordsAdapter
import com.msmikeescom.minesweeper.viewmodel.GameBoardViewModel

/** The player's records, best first. Opened from the account menu on the game screen. */
@AndroidEntryPoint
class PersonalRecordsFragment : Fragment() {

    private val gameBoardViewModel: GameBoardViewModel by activityViewModels()

    private val adapter = PersonalRecordsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.records_recyclerview)
        val emptyView = view.findViewById<TextView>(R.id.records_empty)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        gameBoardViewModel.rankedRecords.observe(viewLifecycleOwner) { records ->
            adapter.submit(records)
            emptyView.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
