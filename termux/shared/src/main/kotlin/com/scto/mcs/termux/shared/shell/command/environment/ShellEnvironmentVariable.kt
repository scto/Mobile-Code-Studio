package com.scto.mcs.termux.shared.shell.command.environment

data class ShellEnvironmentVariable(
    val name: String,
    val value: String,
    val escaped: Boolean = false
) : Comparable<ShellEnvironmentVariable> {

    override fun compareTo(other: ShellEnvironmentVariable): Int {
        return this.name.compareTo(other.name)
    }
}
