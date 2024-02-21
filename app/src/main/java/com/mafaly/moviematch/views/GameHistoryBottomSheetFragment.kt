package com.mafaly.moviematch.views


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mafaly.moviematch.services.GameService
import com.mafaly.moviematch.views.adapters.GameHistoryAdapter
import com.mafaly.moviematchduel.R

class GameHistoryBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_historique, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return

        val rvPartiesList: RecyclerView = view.findViewById(R.id.parties_list_rv)
        rvPartiesList.layoutManager = LinearLayoutManager(context)
        rvPartiesList.setHasFixedSize(true)

        GameService.getAllGames(context) { gameList ->
            if (!gameList.isNullOrEmpty()) {
                rvPartiesList.adapter =
                    GameHistoryAdapter(gameList, viewLifecycleOwner.lifecycleScope)
            } else {
                Toast.makeText(context, "No games found", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val TAG = "GameHistoryBottomSheet"
    }
}