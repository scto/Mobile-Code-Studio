package com.scto.mcs.domain.repository

import java.io.File

interface GitRepository {
    fun clone(url: String, destination: File)
}
