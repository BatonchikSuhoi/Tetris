package com.batonchiksuhoi.tetris.models

import android.graphics.Point
import com.batonchiksuhoi.tetris.constants.CellConstants
import com.batonchiksuhoi.tetris.constants.FieldConstants
import com.batonchiksuhoi.tetris.helpers.arrayOfByte
import com.batonchiksuhoi.tetris.storage.AppPreferences

class AppModel {

    var score: Int = 0
    private var preferences: AppPreferences? = null
    var curBlock: Block? = null
    var curState: String = Statuses.AWATING_START.name

    private var field: Array<ByteArray> = arrayOfByte(
        FieldConstants.ROW_COUNT.value,
        FieldConstants.COLUMN_COUNT.value
    )

    private fun validTranslation(position: Point, shape: Array<ByteArray>,) : Boolean{
        return if (position.y < 0 || position.x < 0){
            false
        } else if (position.y + shape.size > FieldConstants.ROW_COUNT.value) {
            false
        } else if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value) {
            false
        } else {
            for (i in 0 until shape.size){
                for (j in 0 until shape[i].size){
                    val y = position.y + i
                    val x = position.x + j
                    if (CellConstants.EMPTY.value != shape[i][j] &&
                        CellConstants.EMPTY.value != field[y][x]){
                        return false
                    }
                }
            }
            true
        }
    }

    fun generateField(action: String){
        if (isGameActive()) {
            resetField()
            var frameNumber: Int? = curBlock?.frameNumber
            val coordinate: Point? = Point()
            coordinate?.x = curBlock?.position?.x
            coordinate?.y = curBlock?.position?.y

            when (action) {
                Motions.LEFT.name -> {
                    coordinate?.x = curBlock?.position?.x?.minus(1)
                }
                Motions.RIGHT.name -> {
                    coordinate?.x = curBlock?.position?.x?.plus(1)
                }
                Motions.DOWN.name -> {
                    coordinate?.y = curBlock?.position?.y?.plus(1)
                }
                Motions.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)
                    if(frameNumber != null){
                        if (frameNumber >= curBlock?.getFrameCount() as Int){
                            frameNumber = 0
                            coordinate?.y = curBlock?.position?.y?.plus(1)
                        }
                    }
                }
            }

            if (!moveValid(coordinate as Point, frameNumber)){
                translateBlock(curBlock?.position as Point, curBlock?.frameNumber as Int)
                if (Motions.DOWN.name == action) {
                    boostScore()
                    persistCellData()
                    assessField()
                    generateBlock()
                    if(!blockAdditionPossible()){
                        curState = Statuses.OVER.name;
                        curBlock = null;
                        resetField(false);
                    }
                }
            } else {
                if (frameNumber != null) {
                    translateBlock(coordinate, frameNumber)
                    curBlock?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun resetField(ephemeralCellsOnly: Boolean = true) {
        for(i in 0 until FieldConstants.ROW_COUNT.value) {
            (0 until FieldConstants.COLUMN_COUNT.value)
                .filter {
                    !ephemeralCellsOnly || field[i][it] == CellConstants.EPHEMERAL.value
                }
                .forEach {
                    field[i][it] = CellConstants.EMPTY.value
                }
        }
    }

    private fun blockAdditionPossible(): Boolean{
        if (!moveValid(curBlock?.position as Point,
            curBlock?.frameNumber)) {
            return false
        }
        return true
    }

    private fun assessField() {
        for (i in 0 until field.size){
            var emptyCells = 0
            for (j in 0 until field[i].size) {
                val status = getCellStatus(i, j)
                val isEmpty = CellConstants.EMPTY.value == status
                if (isEmpty)
                    emptyCells++
            }
            if (emptyCells == 0)
                shiftRows(i)
        }
    }

    private fun shiftRows(nToRow: Int) {
        if (nToRow > 0) {
            for (j in nToRow - 1 downTo 0){
                for (m in 0 until field[j].size){
                    setCellStatus(j + 1, m, getCellStatus(j,m))
                }
            }
        }
        for (j in 0 until field[0].size){
            setCellStatus(0, j, CellConstants.EMPTY.value)
        }
    }

    private fun persistCellData() {
        for(i in 0 until field.size){
            for(j in 0 until field[i].size) {
                var status = getCellStatus(i, j)
                if (status == CellConstants.EPHEMERAL.value) {
                    status = curBlock?.staticValue
                    setCellStatus(i,j, status)
                }
            }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field){
            val shape: Array<ByteArray>? = curBlock?.getShape(frameNumber)
            if (shape != null){
                for (i in shape.indices){
                    for (j in 0 until shape[i].size){
                        val y = position.y + i
                        val x = position.x + j
                        if (CellConstants.EMPTY.value != shape[i][j]){
                            field[y][x] = shape[i][j]
                        }
                    }
                }
            }
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?,): Boolean{
        val shape: Array<ByteArray>? = curBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)
    }

    fun setPreferences(preferences: AppPreferences?){
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, column: Int): Byte?{
        return field[row][column]
    }

    private fun setCellStatus(row: Int, column: Int,status: Byte?){
        if(status != null){
            field[row][column] = status
        }
    }

    fun startGame(){
        if (!isGameActive()) {
            curState = Statuses.ACTIVE.name
            generateBlock()
        }
    }

    fun restartGame(){
        resetModel()
        startGame()
    }

    private fun resetModel() {
        resetField(false)
        curState = Statuses.AWATING_START.name
        score = 0
    }

    fun endGame(){
        score = 0
        curState = Statuses.OVER.name
    }

    fun isGameOver(): Boolean{
        return curState == Statuses.OVER.name
    }

    fun isGameAwaitingStart(): Boolean{
        return curState == Statuses.AWATING_START.name
    }

    fun isGameActive(): Boolean{
        return curState == Statuses.ACTIVE.name
    }

    private fun boostScore(){
        score += 1
        if (score > preferences?.getHighScore() as Int)
            preferences?.saveHighScore(score)
    }

    private fun generateBlock(){
        curBlock = Block.createBlock()
    }

    enum class Statuses{
        AWATING_START, ACTIVE, INACTIVE, OVER
    }
    enum class Motions{
        LEFT, RIGHT, DOWN, ROTATE
    }
}