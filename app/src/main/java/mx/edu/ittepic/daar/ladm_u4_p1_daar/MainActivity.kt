package mx.edu.ittepic.daar.ladm_u4_p1_daar

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import mx.edu.ittepic.daar.ladm_u4_p1_daar.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var listaIDs = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(this,
                PERMISO_RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(PERMISO_RECEIVE_SMS), siPermisoReciever)
        }

        binding.enviarSMS.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,
                    PERMISO_SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(PERMISO_SEND_SMS), siPermiso)
            } else {
                if (binding.txttelefono.text.toString() == "" || binding.txtMensaje.text.toString() == "") {
                    alerta("CAMPOS VACIOS")
                    return@setOnClickListener
                }
                envioSMS()
            }
        }

        binding.recibidos.setOnClickListener {
            var otraVentana = Intent(this,MainActivity2::class.java)
            startActivity(otraVentana)
        }
    }

    private fun limpiarCampos() {
        binding.txttelefono.setText("")
        binding.txtMensaje.setText("")
    }

    private fun envioSMS() {
        var fechaHora = ""
        val cal = GregorianCalendar.getInstance()
        var basedatos = Firebase.database.reference

        fechaHora = "${cal.get(Calendar.DAY_OF_MONTH)}/" +
                "${cal.get(Calendar.MONTH)}/" +
                "${cal.get(Calendar.YEAR)} " +
                "${cal.get(Calendar.HOUR)}:" +
                "${cal.get(Calendar.MINUTE)}"

        val sms = Mensaje(binding.txttelefono.text.toString(),
            binding.txtMensaje.text.toString(), fechaHora
        )

        SmsManager.getDefault().sendTextMessage(binding.txttelefono.text.toString(),null,
            binding.txtMensaje.text.toString(), null, null)
        mensaje("SE ENVIO EL SMS")

        basedatos.child(rama)
            .push().setValue(sms)
            .addOnSuccessListener {
                mensaje("SE INSERTO")
                limpiarCampos()
            }
            .addOnFailureListener {
                alerta(it.message!!)
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == siPermiso) {
            envioSMS()
        }
        if (requestCode == siPermisoReciever) {
            mensajeRecibir()
        }
    } // Si ya esta el permiso se ejecuta actomaticamente


    private fun mensajeRecibir() {
        alerta("PERMISOS OBTENIDOS DE RECIBIR Y ENVIAR SMS")
    }

    private fun mensaje(texto : String) {
        Toast.makeText(this,texto, Toast.LENGTH_LONG).show()
    }

    private fun alerta(texto :String) {
        AlertDialog.Builder(this)
            .setMessage(texto)
            .setPositiveButton("OK") {d,i ->}
            .show()
    }

    // Variables globales
    val siPermiso = 1
    val siPermisoReciever = 2
    val rama = "telefono"
    val PERMISO_SEND_SMS = android.Manifest.permission.SEND_SMS
    val PERMISO_RECEIVE_SMS = android.Manifest.permission.RECEIVE_SMS
}