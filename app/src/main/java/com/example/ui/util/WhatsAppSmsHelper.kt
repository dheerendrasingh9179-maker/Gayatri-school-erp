package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppSmsHelper {

    fun sendWhatsAppReminder(context: Context, phoneNumber: String, message: String) {
        try {
            val formattedPhone = if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"
            val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp not installed. Sending SMS...", Toast.LENGTH_SHORT).show()
            sendSms(context, phoneNumber, message)
        }
    }

    fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:$phoneNumber")
                putExtra("sms_body", message)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open messaging app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
