package com.mafaly.moviematch.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mafaly.moviematch.db.entities.GameEntity
import com.mafaly.moviematch.services.MovieService
import com.mafaly.moviematchduel.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameHistoryAdapter(private val gameList: List<GameEntity>?, private val scope: CoroutineScope) : RecyclerView.Adapter<GameHistoryAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameNameTv: TextView = view.findViewById(R.id.game_name_tv)
        val gameDateTv: TextView = view.findViewById(R.id.game_date_tv)
        val winnerMovieTitleTv: TextView = view.findViewById(R.id.winner_movie_title_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_party, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameList?.get(position)

        if (game != null) {
            holder.gameDateTv.text = game.gameDate
            holder.gameNameTv.text = game.gameName
            scope.launch {
                val movieEntity =
                    game.gameWinnerId?.let {
                        MovieService.getMovieById(holder.itemView.context,
                            it
                        )
                    }
                withContext(Dispatchers.Main) {
                    holder.winnerMovieTitleTv.text = movieEntity?.movieTitle ?: "Title Not Found"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return gameList?.size ?:0
    }
}


