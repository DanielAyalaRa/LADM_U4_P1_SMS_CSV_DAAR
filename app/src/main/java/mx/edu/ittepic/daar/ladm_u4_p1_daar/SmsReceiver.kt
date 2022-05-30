package mx.edu.ittepic.daar.ladm_u4_p1_daar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(contexto: Context, intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            var sms = extras.get("pdus") as Array<Any>

            for (indice in sms.indices) {
                val formato = extras.getString("format")

                var smsMensaje = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                } else {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                Toast.makeText(contexto, "ENTRO CONTENIDO", Toast.LENGTH_LONG).show()
            }
        }
    }
}