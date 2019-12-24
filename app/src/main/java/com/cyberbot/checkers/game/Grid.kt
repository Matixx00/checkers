package com.cyberbot.checkers.game

import java.lang.RuntimeException
import kotlin.math.pow

class Grid(val size: Int = 8, private val playerRows: Int = 3) : Iterable<GridEntry> {
    private val gridEntries: Array<GridEntry>

    var allowFirstPlayerMove = true
    var allowSecondPlayerMove = false

    init {
        gridEntries = Array(size.toDouble().pow(2).toInt()) {
            val y = it / size
            val entry = GridEntry(it % size, y)

            if (y < playerRows && entry.legal()) {
                entry.player = PlayerNum.FIRST
            } else if (y >= size - playerRows && entry.legal()) {
                entry.player = PlayerNum.SECOND
            }

            return@Array entry
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun getEntryByCoords(x: Int, y: Int): GridEntry {
        if (x >= size || y >= size) {
            throw IndexOutOfBoundsException("Coordinates ($x, $y) out of bounds for grid with size $size")
        }


        forEach {
            if (it.x == x && it.y == y) {
                return it
            }
        }

        throw RuntimeException("Entry ($x, $y) not found in Grid")
    }

    fun moveAllowed(srcEntry: GridEntry, dstEntry: GridEntry): Boolean {
        // TODO: Do proper checks for allowing a move
        return srcEntry == dstEntry || (dstEntry.player == PlayerNum.NOPLAYER &&
                dstEntry.legal() && playerMoveAllowed(srcEntry.player))
    }

    fun playerMoveAllowed(player: PlayerNum): Boolean {
        return when (player) {
            PlayerNum.NOPLAYER -> false
            PlayerNum.FIRST -> allowFirstPlayerMove
            PlayerNum.SECOND -> allowSecondPlayerMove
        }
    }

    fun attemptMove(srcEntry: GridEntry, dstEntry: GridEntry): Boolean {
        if (dstEntry == srcEntry || !moveAllowed(srcEntry, dstEntry)) {
            return false
        }

        val dstIndex = gridEntries.indexOf(dstEntry)
        val srcIndex = gridEntries.indexOf(srcEntry)
        gridEntries[dstIndex].player = srcEntry.player
        gridEntries[srcIndex].player = PlayerNum.NOPLAYER

        return true
    }

    override fun iterator(): Iterator<GridEntry> {
        return gridEntries.iterator()
    }
}