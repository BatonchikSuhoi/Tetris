package com.batonchiksuhoi.tetris

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.batonchiksuhoi.tetris.constants.ControlStates
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

        val controlState : Byte = intent.getByteExtra("controlState", 0)
        val layoutButtons = findViewById<LinearLayout>(R.id.layout_buttons)
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

        if (controlState == ControlStates.SCREEN.value){
            layoutButtons.isVisible = false
            tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        }else{
            layoutButtons.isVisible = true
            btnLeft.setOnTouchListener(this::onLeftBtnTouch)
            btnRight.setOnTouchListener(this::onRightBtnTouch)
            btnDown.setOnTouchListener(this::onDownBtnTouch)
            btnRotate.setOnTouchListener(this::onRotateBtnTouch)
        }

        btnRestart.setOnClickListener(this::btnRestartClick)
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

    private fun onLeftBtnTouch(view: View, event: MotionEvent): Boolean{
        moveTetromino(AppModel.Motions.LEFT)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> view.setPressed(true)
            MotionEvent.ACTION_UP -> view.setPressed(false)
        }
        return true
    }

    private fun onRightBtnTouch(view: View, event: MotionEvent): Boolean{
        moveTetromino(AppModel.Motions.RIGHT)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> view.setPressed(true)
            MotionEvent.ACTION_UP -> view.setPressed(false)
        }
        return true
    }


    private fun onRotateBtnTouch(view: View, event: MotionEvent): Boolean{
        moveTetromino(AppModel.Motions.ROTATE)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> view.setPressed(true)
            MotionEvent.ACTION_UP -> view.setPressed(false)
        }
        return true
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