package com.batonchiksuhoi.tetris

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.batonchiksuhoi.tetris.models.AppModel
import com.batonchiksuhoi.tetris.storage.AppPreferences
import com.batonchiksuhoi.tetris.view.TetrisView

class GameActivity : AppCompatActivity() {

    var tvHighScore: TextView? = null
    var tvCurrentScore: TextView? = null
    var appPreferences: AppPreferences? = null


    private lateinit var tetrisView: TetrisView
    private val appModel: AppModel = AppModel()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        appPreferences = AppPreferences(this)
        appModel.setPreferences(appPreferences)

        val btnRestart = findViewById<Button>(R.id.btn_restart)
        val btnLeft = findViewById<ImageButton>(R.id.btn_left)
        val btnRight = findViewById<ImageButton>(R.id.btn_right)
        val btnDown = findViewById<ImageButton>(R.id.btn_down)
        val btnRotate = findViewById<ImageButton>(R.id.btn_spin)
        val btnBack = findViewById<Button>(R.id.btn_back)

        tvHighScore = findViewById<TextView>(R.id.tv_high_score)
        tvCurrentScore = findViewById<TextView>(R.id.tv_current_score)
        tetrisView = findViewById<TetrisView>(R.id.view_tetris)
        tetrisView.setActivity(this)
        tetrisView.setModel(appModel)
        tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        btnRestart.setOnClickListener(this::btnRestartClick)
        btnLeft.setOnClickListener(this::onClickLeftBtn)
        btnRight.setOnClickListener(this::onClickRightBtn)
        btnDown.setOnTouchListener(this::onDownBtnTouch)
        btnRotate.setOnClickListener(this::onClickRotateBtn)
        btnBack.setOnClickListener(this::onClickBackBtn)

        updateHighScore()
        updateCurrentScore()

        appModel.startGame()
        tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
    }

    private fun onClickBackBtn(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun btnRestartClick(view: View){
        appModel.restartGame()
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent):Boolean{
        if (appModel.isGameOver() || appModel.isGameAwaitingStart()){
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        } else if(appModel.isGameActive()){
            when (resolveTouchDirection(view, event)){
                0 -> moveTetromino(AppModel.Motions.LEFT)
                1 -> moveTetromino(AppModel.Motions.ROTATE)
                2 -> moveTetromino(AppModel.Motions.DOWN)
                3 -> moveTetromino(AppModel.Motions.RIGHT)
            }
        }
        return true
    }

    private fun onClickLeftBtn(view: View){
        moveTetromino(AppModel.Motions.LEFT)
    }

    private fun onClickRightBtn(view: View){
        moveTetromino(AppModel.Motions.RIGHT)
    }

    private fun onClickRotateBtn(view: View){
        moveTetromino(AppModel.Motions.ROTATE)
    }

    private fun onDownBtnTouch(view: View, event: MotionEvent): Boolean{
        moveTetromino(AppModel.Motions.DOWN)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> view.setPressed(true)
            MotionEvent.ACTION_UP -> view.setPressed(false)
        }
        return true
    }

    private fun moveTetromino(motion: AppModel.Motions) {
        if (appModel.isGameActive()){
            tetrisView.setGameCommand(motion)
        }
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height
        val direction: Int
        direction = if (y > x){
            if (x > 1 - y) 2
            else 0
        } else {
            if (x > 1 - y) 3 else 1
        }
        return direction
    }

    private fun updateCurrentScore() {
        tvCurrentScore?.text = "0"
    }

    private fun updateHighScore() {
        tvHighScore?.text = "${appPreferences?.getHighScore()}"
    }

}