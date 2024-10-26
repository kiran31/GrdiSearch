package com.example.gridsearch

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var gridLayout: GridLayout
    lateinit var grid: Array<Array<String>>
    lateinit var rowsInput: EditText
    lateinit var colsInput: EditText
    lateinit var generateGridBtn: Button
    lateinit var searchBtn: Button
    lateinit var resetBtn: Button
    lateinit var searchInput: EditText
    var mRows: Int = 0
    var nColumns: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Adjust layout for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        rowsInput = findViewById(R.id.rowsInput)
        colsInput = findViewById(R.id.colsInput)
        generateGridBtn = findViewById(R.id.generateGridBtn)
        searchInput = findViewById(R.id.searchInput)
        searchBtn = findViewById(R.id.searchBtn)
        resetBtn = findViewById(R.id.resetBtn)
        gridLayout = findViewById(R.id.gridLayout)

        // Handle grid generation button click with validation
        generateGridBtn.setOnClickListener {
            if (validateGridInput()) {
                mRows = rowsInput.text.toString().toInt()
                nColumns = colsInput.text.toString().toInt()
                createGrid(mRows, nColumns)
            }
        }

        // Handle search button click with validation
        searchBtn.setOnClickListener {
            val searchText = searchInput.text.toString().trim()
            if (searchText.isEmpty()) {
                Toast.makeText(this, "Enter a word to search", Toast.LENGTH_SHORT).show()
            } else if (::grid.isInitialized) {
                searchInGrid(searchText)
            } else {
                Toast.makeText(this, "Please generate a grid first", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle reset button click
        resetBtn.setOnClickListener {
            gridLayout.removeAllViews()
            rowsInput.text.clear()
            colsInput.text.clear()
            searchInput.text.clear()
        }
    }

    // Validate input for grid generation
    private fun validateGridInput(): Boolean {
        val rowsText = rowsInput.text.toString().trim()
        val colsText = colsInput.text.toString().trim()

        if (rowsText.isEmpty() || colsText.isEmpty()) {
            Toast.makeText(this, "Please enter both rows and columns", Toast.LENGTH_SHORT).show()
            return false
        }

        val rows = rowsText.toIntOrNull()
        val columns = colsText.toIntOrNull()

        if (rows == null || columns == null || rows <= 0 || columns <= 0) {
            Toast.makeText(this, "Enter valid positive numbers", Toast.LENGTH_SHORT).show()
            return false
        }

        if (rows > 10 || columns > 10) {
            Toast.makeText(this, "Rows and columns should not exceed 10", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Create the grid dynamically
    private fun createGrid(rows: Int, columns: Int) {
        gridLayout.removeAllViews()
        gridLayout.rowCount = rows
        gridLayout.columnCount = columns
        grid = Array(rows) { Array(columns) { randomAlphabet() } }

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val textView = TextView(this).apply {
                    text = grid[i][j]
                    textSize = 24f
                    setPadding(16, 16, 16, 16)
                    setBackgroundResource(R.drawable.grid_item_border)
                }
                val params = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    setMargins(10, 10, 10, 10)
                }
                gridLayout.addView(textView, params)
            }
        }
    }

    // Generate a random alphabet
    private fun randomAlphabet(): String {
        val alphabet = ('A'..'Z').random()
        return alphabet.toString()
    }

    // Search for a word in the grid
    private fun searchInGrid(word: String) {
        var found = false

        // Search East (left to right)
        for (i in 0 until mRows) {
            for (j in 0 until nColumns - word.length + 1) {
                if (searchEast(i, j, word)) {
                    highlightWord(i, j, word.length, "EAST")
                    found = true
                }
            }
        }

        // Search South (top to bottom)
        for (i in 0 until mRows - word.length + 1) {
            for (j in 0 until nColumns) {
                if (searchSouth(i, j, word)) {
                    highlightWord(i, j, word.length, "SOUTH")
                    found = true
                }
            }
        }

        // Search Diagonals (Southeast and Southwest)
        for (i in 0 until mRows - word.length + 1) {
            for (j in 0 until nColumns - word.length + 1) {
                if (searchDiagonalSE(i, j, word)) {
                    highlightWord(i, j, word.length, "SE")
                    found = true
                }
            }
            for (j in word.length - 1 until nColumns) {
                if (searchDiagonalSW(i, j, word)) {
                    highlightWord(i, j, word.length, "SW")
                    found = true
                }
            }
        }

        if (!found) {
            Toast.makeText(this, "Word not found", Toast.LENGTH_SHORT).show()
        }
    }

    // Search methods for different directions
    private fun searchEast(row: Int, col: Int, word: String): Boolean {
        for (k in word.indices) {
            if (grid[row][col + k] != word[k].toString()) return false
        }
        return true
    }

    private fun searchSouth(row: Int, col: Int, word: String): Boolean {
        for (k in word.indices) {
            if (grid[row + k][col] != word[k].toString()) return false
        }
        return true
    }

    private fun searchDiagonalSE(row: Int, col: Int, word: String): Boolean {
        for (k in word.indices) {
            if (grid[row + k][col + k] != word[k].toString()) return false
        }
        return true
    }

    private fun searchDiagonalSW(row: Int, col: Int, word: String): Boolean {
        for (k in word.indices) {
            if (grid[row + k][col - k] != word[k].toString()) return false
        }
        return true
    }

    // Highlight the found word
    private fun highlightWord(row: Int, col: Int, length: Int, direction: String) {
        for (k in 0 until length) {
            when (direction) {
                "EAST" -> gridLayout.getChildAt(row * nColumns + (col + k))
                    .setBackgroundColor(Color.YELLOW)
                "SOUTH" -> gridLayout.getChildAt((row + k) * nColumns + col)
                    .setBackgroundColor(Color.YELLOW)
                "SE" -> gridLayout.getChildAt((row + k) * nColumns + (col + k))
                    .setBackgroundColor(Color.YELLOW)
                "SW" -> gridLayout.getChildAt((row + k) * nColumns + (col - k))
                    .setBackgroundColor(Color.YELLOW)
            }
        }
    }
}
