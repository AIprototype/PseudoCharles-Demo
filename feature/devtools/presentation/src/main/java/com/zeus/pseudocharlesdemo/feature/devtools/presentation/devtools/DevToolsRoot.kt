package com.zeus.pseudocharlesdemo.feature.devtools.presentation.devtools

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zeus.pseudocharlesdemo.feature.devtools.presentation.ObserveEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun DevToolsRoot(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: DevToolsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // The SDK exposes isEnabled()/isActive()/networkProfile() as plain calls rather than flows, so
    // anything changed inside the SDK's own UI is only picked up when we come back to this screen.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onAction(DevToolsAction.OnRefresh)
    }

    // Registered at the root, not inside the LazyColumn: a launcher created in a lazy item is
    // disposed as soon as that item scrolls out of view, and the permission result is then dropped.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.onAction(DevToolsAction.OnNotificationToggle(true))
            viewModel.onAction(DevToolsAction.OnNotificationPermissionBlocked(false))
        } else {
            // Denied with the system dialog no longer offered => "don't ask again". The only route
            // left is the app's notification settings, so surface that instead of a dead switch.
            val blocked = !context.shouldShowNotificationRationale()
            viewModel.onAction(DevToolsAction.OnNotificationPermissionBlocked(blocked))
        }
    }

    ObserveEvents(events = viewModel.events) { event ->
        when (event) {
            is DevToolsEvent.ShowMessage ->
                snackbarHostState.showSnackbar(event.message.asString(context))

            DevToolsEvent.OpenNotificationSettings -> context.openNotificationSettings()
        }
    }

    DevToolsScreen(
        state = state,
        onAction = { action ->
            if (action is DevToolsAction.OnNotificationToggle && action.enabled &&
                !context.hasNotificationPermission()
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.onAction(action)
            }
        },
        modifier = modifier
    )
}

/** POST_NOTIFICATIONS only exists on API 33+; below that, posting is always allowed. */
private fun Context.hasNotificationPermission(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

private fun Context.shouldShowNotificationRationale(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
    val activity = findActivity() ?: return false
    return activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
}

/**
 * `LocalContext.current` is a ContextThemeWrapper around the Activity, not the Activity itself, so
 * a plain `as? Activity` always returns null here and every denial would look permanent.
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.openNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}
