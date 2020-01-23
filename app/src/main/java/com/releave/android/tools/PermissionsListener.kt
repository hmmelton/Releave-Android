package com.releave.android.tools

/**
 * Callback used in PermissionsManager
 */

interface PermissionsListener {

    fun onExplanationNeeded(permissionsToExplain: List<String>)

    fun onPermissionResult(granted: Boolean)
}
