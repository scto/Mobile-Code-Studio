package com.scto.mcs.termux.shared.termux.extrakeys

class SpecialButton(val key: String) {

    companion object {
        private val map = HashMap<String, SpecialButton>()

        val CTRL = SpecialButton("CTRL")
        val ALT = SpecialButton("ALT")
        val SHIFT = SpecialButton("SHIFT")
        val FN = SpecialButton("FN")

        fun valueOf(key: String): SpecialButton? = map[key]
    }

    init {
        map[key] = this
    }

    override fun toString(): String = key
}
