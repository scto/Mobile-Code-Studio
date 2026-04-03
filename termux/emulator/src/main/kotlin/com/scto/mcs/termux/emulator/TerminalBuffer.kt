package com.scto.mcs.termux.emulator

import java.util.Arrays

class TerminalBuffer(
    var mColumns: Int,
    var mTotalRows: Int,
    var mScreenRows: Int
) {
    var mLines: Array<TerminalRow?> = arrayOfNulls(mTotalRows)
    private var mActiveTranscriptRows = 0
    private var mScreenFirstRow = 0

    init {
        blockSet(0, 0, mColumns, mScreenRows, ' '.toInt(), TextStyle.NORMAL)
    }

    fun externalToInternalRow(externalRow: Int): Int {
        if (externalRow < -mActiveTranscriptRows || externalRow > mScreenRows)
            throw IllegalArgumentException("extRow=$externalRow, mScreenRows=$mScreenRows, mActiveTranscriptRows=$mActiveTranscriptRows")
        val internalRow = mScreenFirstRow + externalRow
        return if (internalRow < 0) (mTotalRows + internalRow) else (internalRow % mTotalRows)
    }

    fun blockSet(sx: Int, sy: Int, w: Int, h: Int, valChar: Int, style: Long) {
        if (sx < 0 || sx + w > mColumns || sy < 0 || sy + h > mScreenRows) {
            throw IllegalArgumentException("Illegal arguments!")
        }
        for (y in 0 until h)
            for (x in 0 until w)
                setChar(sx + x, sy + y, valChar, style)
    }

    fun setChar(column: Int, row: Int, codePoint: Int, style: Long) {
        if (row < 0 || row >= mScreenRows || column < 0 || column >= mColumns)
            throw IllegalArgumentException("TerminalBuffer.setChar(): row=$row, column=$column")
        val internalRow = externalToInternalRow(row)
        allocateFullLineIfNecessary(internalRow).setChar(column, codePoint, style)
    }

    fun allocateFullLineIfNecessary(row: Int): TerminalRow {
        if (mLines[row] == null) mLines[row] = TerminalRow(mColumns, 0)
        return mLines[row]!!
    }
    
    // Weitere Methoden wie scrollDownOneLine, blockCopy etc. müssten hier ebenfalls migriert werden.
}
