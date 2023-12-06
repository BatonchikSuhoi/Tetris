package com.batonchiksuhoi.tetris

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.batonchiksuhoi.tetris.storage.AppPreferences
import com.batonchiksuhoi.tetris.ui.theme.TetrisTheme
import com.google.android.material.snackbar.Snackbar
import androidx.constraintlayout.widget.ConstraintLayout;


var tvHighScore: TextView? = null

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnNewGame: Button = findViewById<Button>(R.id.btn_new_game)
        val btnResetScore: Button = findViewById<Button>(R.id.btn_reset_score)
        val btnExit: Button = findViewById<Button>(R.id.btn_exit)
        val btnMaxoni: Button = findViewById(R.id.btn_maxoni)
        val preferences = AppPreferences(this)

        tvHighScore = findViewById<TextView>(R.id.tv_high_score)
        tvHighScore?.text = "High score: ${preferences.getHighScore()}"

        btnNewGame.setOnClickListener(this::onBtnNewGameClick)
        btnResetScore.setOnClickListener(this::onBtnResetScoreClick)
        btnExit.setOnClickListener(this::handleExitEvent)
        btnMaxoni.setOnClickListener(this::onBtnMaxoniClick)
    }

    private fun onBtnResetScoreClick(view: View) {
        val preferences = AppPreferences(this)
        preferences.clearHighScore()
        Snackbar.make(view, "Score succesfully reset", Snackbar.LENGTH_SHORT).show()
        tvHighScore?.text = "High score: ${preferences.getHighScore()}"
    }

    private fun onBtnNewGameClick(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun onBtnMaxoniClick(view: View){
        Toast.makeText(applicationContext, "Привет Максончик", Toast.LENGTH_SHORT).show()
    }

    fun handleExitEvent(view: View){
        System.exit(0)
    }
}
