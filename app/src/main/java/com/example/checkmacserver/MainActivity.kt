package com.example.checkmacserver

import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS), 10
        )
        SmsReceiver.bindListener(object : SmsListener {
            override fun messageReceived(messageText: String, sender: String) {
                val answer = MacAddressUtil.checkMACAddress(messageText)
                val smsManager = SmsManager.getDefault()
                if (answer == true)
                    smsManager.sendTextMessage(sender, null, "Device connected", null, null)
                else
                    smsManager.sendTextMessage(sender, null, "Device disconnected", null, null)
            }
        })
    }
}
