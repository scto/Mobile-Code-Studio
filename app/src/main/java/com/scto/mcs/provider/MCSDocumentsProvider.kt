package com.scto.mcs.provider

import android.provider.DocumentsProvider

class MCSDocumentsProvider : DocumentsProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun queryRoots(projection: Array<String>?): android.database.Cursor {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun queryChildDocuments(
        parentDocumentId: String?,
        projection: Array<String>?,
        sortOrder: String?
    ): android.database.Cursor {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun queryDocument(documentId: String?, projection: Array<String>?): android.database.Cursor {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun openDocument(
        documentId: String?,
        mode: String,
        signal: android.os.CancellationSignal?
    ): android.os.ParcelFileDescriptor {
        throw UnsupportedOperationException("Not implemented")
    }
}
