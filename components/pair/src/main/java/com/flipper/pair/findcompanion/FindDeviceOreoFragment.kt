package com.flipper.pair.findcompanion

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import com.flipper.bridge.utils.Constants
import com.flipper.core.api.BottomNavigationActivityApi
import com.flipper.core.di.ComponentHolder
import com.flipper.core.utils.preference.FlipperSharedPreferences
import com.flipper.core.utils.preference.FlipperSharedPreferencesKey
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.R
import com.flipper.pair.di.PairComponent
import com.flipper.pair.find.service.PairDeviceViewModel
import com.flipper.pair.findcompanion.compose.ComposeFindDevice
import com.github.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class FindDeviceOreoFragment : ComposeFragment() {
    @Inject
    lateinit var router: Router

    @Inject
    lateinit var bottomNavigationActivityApi: BottomNavigationActivityApi

    @Inject
    lateinit var sharedPreferences: FlipperSharedPreferences

    private val pairDeviceViewModel by viewModels<PairDeviceViewModel>()

    // Result listener for device pair
    private val deviceConnectWithResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            pairDeviceViewModel.onFailedCompanionFinding(getString(R.string.pair_companion_error_return_not_ok))
            return@registerForActivityResult
        }
        val deviceToPair: BluetoothDevice? =
            result.data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
        if (deviceToPair == null) {
            pairDeviceViewModel.onFailedCompanionFinding(getString(R.string.pair_companion_error_return_device_null))
            return@registerForActivityResult
        }
        pairDeviceViewModel.startConnectToDevice(deviceToPair) {
            onDeviceReady(deviceToPair)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ComponentHolder.component<PairComponent>().inject(this)
    }

    @Composable
    override fun renderView() {
        val connectionState by pairDeviceViewModel.getConnectionState().collectAsState()
        val errorText by pairDeviceViewModel.getErrorState().collectAsState()
        ComposeFindDevice(connectionState, errorText, onClickBackButton = { router.exit() }) {
            openFindDeviceDialog()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openFindDeviceDialog()
    }

    private fun openFindDeviceDialog() {
        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            // Match only Bluetooth devices whose name matches the pattern.
            .setNamePattern(Constants.DEVICENAME_PREFIX_REGEXP)
            .build()
        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            // Find only devices that match this request filter.
            .addDeviceFilter(deviceFilter)
            .build()
        val deviceManager =
            requireContext().getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        pairDeviceViewModel.onStartCompanionFinding()

        deviceManager.associate(
            pairingRequest,
            object : CompanionDeviceManager.Callback() {
                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    val intentSenderRequest = IntentSenderRequest.Builder(chooserLauncher).build()
                    deviceConnectWithResult.launch(intentSenderRequest)
                }

                override fun onFailure(error: CharSequence) {
                    val errorText = "$error\n${getString(R.string.pair_companion_error_try_again)}"
                    pairDeviceViewModel.onFailedCompanionFinding(errorText)
                    Timber.e(error.toString())
                }
            },
            null
        )
    }

    private fun onDeviceReady(device: BluetoothDevice) {
        sharedPreferences.edit { putString(FlipperSharedPreferencesKey.DEVICE_ID, device.address) }
        bottomNavigationActivityApi.openBottomNavigationScreen()
    }
}
