package com.scto.mcs.feature.onboarding

import androidx.lifecycle.ViewModel
import com.scto.mcs.core.PermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val permissionChecker: PermissionChecker
) : ViewModel() {

    fun hasPermission(): Boolean {
        return permissionChecker.hasManageExternalStoragePermission()
    }
}
