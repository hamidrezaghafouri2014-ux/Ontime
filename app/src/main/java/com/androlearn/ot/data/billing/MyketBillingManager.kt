
package com.androlearn.ot.data.billing

import android.app.Activity
import android.util.Log
import ir.myket.billingclient.IabHelper
import com.androlearn.ot.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyketBillingManager(
    private val activity: Activity,
    private val proPreferences: ProPreferences,
    private val scope: CoroutineScope,
    private val onProStatusChanged: (Boolean) -> Unit,
    private val onError: (String) -> Unit
) {

    companion object {
        const val PRODUCT_ID = "ontime_pro_lifetime"
        private const val TAG = "OnTimeBilling"
    }

    private var iabHelper: IabHelper? = null

    fun connect() {
        if (iabHelper != null) return

        val helper = IabHelper(
            activity,
            BuildConfig.IAB_PUBLIC_KEY
        )

        iabHelper = helper
        helper.enableDebugLogging(BuildConfig.DEBUG, TAG)

        helper.startSetup { result ->
            if (!result.isSuccess) {
                onError("اتصال به مایکت ناموفق بود: ${result.message}")
                return@startSetup
            }

            checkPurchasedProduct()
        }
    }

    private fun checkPurchasedProduct() {
        val helper = iabHelper ?: return

        helper.queryInventoryAsync(true, listOf(PRODUCT_ID)) {
                result, inventory ->

            if (result.isFailure || inventory == null) {
                onError("بررسی خریدهای قبلی ناموفق بود")
                return@queryInventoryAsync
            }

            val isPurchased = inventory.getPurchase(PRODUCT_ID) != null

            scope.launch(Dispatchers.IO) {
                proPreferences.setPro(isPurchased)
            }

            onProStatusChanged(isPurchased)
            Log.d(TAG, "Previous purchase checked")
        }
    }

    fun purchasePro() {
        val helper = iabHelper

        if (helper == null) {
            onError("ابتدا اتصال به مایکت برقرار شود")
            return
        }

        helper.launchPurchaseFlow(
            activity,
            PRODUCT_ID,
            { result, purchase ->

                if (result.isFailure || purchase == null) {
                    onError("خرید ناموفق بود: ${result.message}")
                    return@launchPurchaseFlow
                }

                if (purchase.sku != PRODUCT_ID) {
                    onError("شناسه محصول خریداری‌شده معتبر نیست")
                    return@launchPurchaseFlow
                }

                checkPurchasedProduct()
            },
            "ontime-pro-lifetime"
        )
    }

    fun dispose() {
        iabHelper?.dispose()
        iabHelper = null
    }
}
