package com.example.checkmacserver

interface SmsListener {
    fun messageReceived(messageText: String, sender: String)
}