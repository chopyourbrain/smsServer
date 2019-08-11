package com.example.checkmacserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage


class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val data = intent.extras

        val pdus = data?.get("pdus") as Array<*>

        for (i in pdus.indices) {
            val smsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
            val sender = smsMessage.displayOriginatingAddress
                val messageBody = smsMessage.messageBody
                smsListener?.messageReceived(messageBody, sender)
        }

    }

    companion object {
        private var smsListener: SmsListener? = null
        fun bindListener(listener: SmsListener) {
            smsListener = listener
        }
    }
}
