package com.scto.mcs.termux.emulator

import java.util.Arrays

/**
 * A row in a terminal, composed of a fixed number of cells.
 */
class TerminalRow(private val mColumns: Int, style: Long) {

    private companion object {
        private const val SPARE_CAPACITY_FACTOR = 1.5f
    }

    var mText: CharArray
    private var mSpaceUsed: Short = 0
    var mLineWrap: Boolean = false
    val mStyle: LongArray
    var mHasNonOneWidthOrSurrogateChars: Boolean = false

    init {
        mText = CharArray((SPARE_CAPACITY_FACTOR * mColumns).toInt())
        mStyle = LongArray(mColumns)
        clear(style)
    }

    fun copyInterval(line: TerminalRow, sourceX1: Int, sourceX2: Int, destinationX: Int) {
        var destX = destinationX
        var srcX1 = sourceX1
        mHasNonOneWidthOrSurrogateChars = mHasNonOneWidthOrSurrogateChars or line.mHasNonOneWidthOrSurrogateChars
        val x1 = line.findStartOfColumn(srcX1)
        val x2 = line.findStartOfColumn(sourceX2)
        var startingFromSecondHalfOfWideChar = (srcX1 > 0 && line.wideDisplayCharacterStartingAt(srcX1 - 1))
        val sourceChars = if (this === line) line.mText.copyOf() else line.mText
        var latestNonCombiningWidth = 0
        
        var i = x1
        while (i < x2) {
            val sourceChar = sourceChars[i]
            var codePoint = if (Character.isHighSurrogate(sourceChar)) {
                Character.toCodePoint(sourceChar, sourceChars[++i])
            } else {
                sourceChar.toInt()
            }
            
            if (startingFromSecondHalfOfWideChar) {
                codePoint = ' '.toInt()
                startingFromSecondHalfOfWideChar = false
            }
            val w = WcWidth.width(codePoint)
            if (w > 0) {
                destX += latestNonCombiningWidth
                srcX1 += latestNonCombiningWidth
                latestNonCombiningWidth = w
            }
            setChar(destX, codePoint, line.getStyle(srcX1))
            i++
        }
    }

    fun getSpaceUsed(): Int = mSpaceUsed.toInt()

    fun findStartOfColumn(column: Int): Int {
        if (column == mColumns) return getSpaceUsed()

        var currentColumn = 0
        var currentCharIndex = 0
        while (true) {
            val newCharIndex = currentCharIndex
            val c = mText[currentCharIndex++]
            val isHigh = Character.isHighSurrogate(c)
            val codePoint = if (isHigh) Character.toCodePoint(c, mText[currentCharIndex++]) else c.toInt()
            val wcwidth = WcWidth.width(codePoint)
            if (wcwidth > 0) {
                currentColumn += wcwidth
                if (currentColumn == column) {
                    var nextIndex = currentCharIndex
                    while (nextIndex < mSpaceUsed) {
                        if (Character.isHighSurrogate(mText[nextIndex])) {
                            if (WcWidth.width(Character.toCodePoint(mText[nextIndex], mText[nextIndex + 1])) <= 0) {
                                nextIndex += 2
                            } else {
                                break
                            }
                        } else if (WcWidth.width(mText[nextIndex].toInt()) <= 0) {
                            nextIndex++
                        } else {
                            break
                        }
                    }
                    return nextIndex
                } else if (currentColumn > column) {
                    return currentCharIndex
                }
            }
        }
    }

    private fun wideDisplayCharacterStartingAt(column: Int): Boolean {
        var currentCharIndex = 0
        var currentColumn = 0
        while (currentCharIndex < mSpaceUsed) {
            val c = mText[currentCharIndex++]
            val codePoint = if (Character.isHighSurrogate(c)) Character.toCodePoint(c, mText[currentCharIndex++]) else c.toInt()
            val wcwidth = WcWidth.width(codePoint)
            if (wcwidth > 0) {
                if (currentColumn == column && wcwidth == 2) return true
                currentColumn += wcwidth
                if (currentColumn > column) return false
            }
        }
        return false
    }

    fun clear(style: Long) {
        Arrays.fill(mText, ' ')
        Arrays.fill(mStyle, style)
        mSpaceUsed = mColumns.toShort()
        mHasNonOneWidthOrSurrogateChars = false
    }

    fun setChar(columnToSet: Int, codePoint: Int, style: Long) {
        var col = columnToSet
        if (col < 0 || col >= mStyle.size)
            throw IllegalArgumentException("TerminalRow.setChar(): columnToSet=$col, codePoint=$codePoint, style=$style")

        mStyle[col] = style
        val newCodePointDisplayWidth = WcWidth.width(codePoint)

        if (!mHasNonOneWidthOrSurrogateChars) {
            if (codePoint >= Character.MIN_SUPPLEMENTARY_CODE_POINT || newCodePointDisplayWidth != 1) {
                mHasNonOneWidthOrSurrogateChars = true
            } else {
                mText[col] = codePoint.toChar()
                return
            }
        }

        val newIsCombining = newCodePointDisplayWidth <= 0
        val wasExtraColForWideChar = (col > 0) && wideDisplayCharacterStartingAt(col - 1)

        if (newIsCombining) {
            if (wasExtraColForWideChar) col--
        } else {
            if (wasExtraColForWideChar) setChar(col - 1, ' '.toInt(), style)
            val overwritingWideCharInNextColumn = newCodePointDisplayWidth == 2 && wideDisplayCharacterStartingAt(col + 1)
            if (overwritingWideCharInNextColumn) setChar(col + 1, ' '.toInt(), style)
        }

        val oldStartOfColumnIndex = findStartOfColumn(col)
        val oldCodePointDisplayWidth = WcWidth.width(mText, oldStartOfColumnIndex)

        val oldCharactersUsedForColumn = if (col + oldCodePointDisplayWidth < mColumns) {
            findStartOfColumn(col + oldCodePointDisplayWidth) - oldStartOfColumnIndex
        } else {
            mSpaceUsed - oldStartOfColumnIndex
        }

        val newCharactersUsedForColumn = Character.charCount(codePoint)
        val javaCharDifference = (newCharactersUsedForColumn + (if (newIsCombining) oldCharactersUsedForColumn else 0)) - oldCharactersUsedForColumn
        
        if (javaCharDifference > 0) {
            if (mSpaceUsed + javaCharDifference > mText.size) {
                val newText = CharArray(mText.size + mColumns)
                System.arraycopy(mText, 0, newText, 0, oldStartOfColumnIndex + oldCharactersUsedForColumn)
                System.arraycopy(mText, oldStartOfColumnIndex + oldCharactersUsedForColumn, newText, oldStartOfColumnIndex + oldCharactersUsedForColumn + javaCharDifference, mSpaceUsed - (oldStartOfColumnIndex + oldCharactersUsedForColumn))
                mText = newText
            } else {
                System.arraycopy(mText, oldStartOfColumnIndex + oldCharactersUsedForColumn, mText, oldStartOfColumnIndex + oldCharactersUsedForColumn + javaCharDifference, mSpaceUsed - (oldStartOfColumnIndex + oldCharactersUsedForColumn))
            }
        } else if (javaCharDifference < 0) {
            System.arraycopy(mText, oldStartOfColumnIndex + oldCharactersUsedForColumn, mText, oldStartOfColumnIndex + oldCharactersUsedForColumn + javaCharDifference, mSpaceUsed - (oldStartOfColumnIndex + oldCharactersUsedForColumn))
        }
        mSpaceUsed = (mSpaceUsed + javaCharDifference).toShort()
        Character.toChars(codePoint, mText, oldStartOfColumnIndex + (if (newIsCombining) oldCharactersUsedForColumn else 0))
    }

    fun isBlank(): Boolean {
        for (i in 0 until mSpaceUsed) if (mText[i] != ' ') return false
        return true
    }

    fun getStyle(column: Int): Long = mStyle[column]
}
