package com.scto.mcs.provider

import android.database.Cursor
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.provider.DocumentsProvider
import android.graphics.Point

class MCSDocumentsProvider : DocumentsProvider() {
    override fun onCreate(): Boolean = true

    override fun queryRoots(projection: Array<String>?): Cursor {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun queryDocument(documentId: String?, projection: Array<String>?): Cursor {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun queryChildDocuments(parentDocumentId: String?, projection: Array<String>?, sortOrder: String?): Cursor {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun openDocument(documentId: String?, mode: String, signal: CancellationSignal?): ParcelFileDescriptor {
        throw UnsupportedOperationException("Not implemented")
    }
}
