package com.mafaly.moviematch.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mafaly.moviematch.db.entities.GameEntity
import com.mafaly.moviematchduel.R

class GameHistoryAdapter(private val gameList: List<GameEntity>?) : RecyclerView.Adapter<GameHistoryAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGameName: TextView = view.findViewById(R.id.tvGameName)
        val tvGameDate: TextView = view.findViewById(R.id.tvGameDate)
        val tvWinnerMovieTitle: TextView = view.findViewById(R.id.tvWinnerMovieTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_party, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameList?.get(position)

        if (game != null) {
            holder.tvGameDate.text = game.gameDate
            holder.tvGameName.text = game.gameName
            holder.tvWinnerMovieTitle.text= game.gameWinnerId.toString()
        }


    }

    override fun getItemCount(): Int {
        return gameList?.size ?:0
    }
}


