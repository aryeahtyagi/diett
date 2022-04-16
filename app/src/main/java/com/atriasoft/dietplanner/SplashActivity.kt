package com.atriasoft.dietplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo

class SplashActivity : AppCompatActivity() , BillingProcessor.IBillingHandler{

    private lateinit var billingProcessor: BillingProcessor
    private lateinit var purchaseInfo: PurchaseInfo

    private val product_id = "plannerbilling"
    private val license_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiQiNDPUjXfg+YQhxCAU3vJ/mS0hB16bsOXbrf/rCHTkYZ0BW6/5++NBh1UZdHiHvccvfPONWlHLkAuPhekF4PNTcZ2/CDKlk70LEnAfFScvFAI6hc7z35A8XXRg08kLVtbfpee5QUGOkzrltebkpfvIiek1/CKXa8mj57j1v/GIltxjXORf8Vd5qa1VWVDSUWjjnNwbJJqho7pz01+USvM6/VFk3wFbUxV6nbPPV5m5wl0h2SiSr3L07yG7JQuBEsHCQqCSil3QoVS3l6MGhczetLQ1ddx6YK5JOT3JFBv4ANd/ncGFiYu9wyvOyMutAdmQAxGH6xK8Ehb8eVKdZ5QIDAQAB"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        billingProcessor = BillingProcessor(this,license_key,this)
        billingProcessor.initialize()
        Handler().postDelayed({
            billingProcessor.subscribe(this,product_id)
        },2000)

    }


    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        Toast.makeText(this, "Successfully Purchased", Toast.LENGTH_SHORT).show()
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {

    }

    override fun onBillingInitialized() {
        billingProcessor.loadOwnedPurchasesFromGoogleAsync(object : BillingProcessor.IPurchasesResponseListener {
            override fun onPurchasesSuccess() {
            }

            override fun onPurchasesError() {
            }

        })

        if(billingProcessor.getSubscriptionPurchaseInfo(product_id) != null) {
            purchaseInfo = billingProcessor.getSubscriptionPurchaseInfo(product_id)!!
            if(purchaseInfo.purchaseData.autoRenewing){
                val intent = Intent(this,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }else{
            }
        }else{
        }
    }

    override fun onDestroy() {
        billingProcessor.release()
        super.onDestroy()
    }

}