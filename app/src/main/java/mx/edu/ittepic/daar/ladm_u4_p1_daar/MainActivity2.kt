package mx.edu.ittepic.daar.ladm_u4_p1_daar

import android.R
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import mx.edu.ittepic.daar.ladm_u4_p1_daar.databinding.ActivityMain2Binding
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.*
import java.lang.StringBuilder

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    var listaIDs = ArrayList<String>()
    var csv = ArrayList<Dato>()
    var listacsv = ArrayList<Dato>()
    var datos = ArrayList<String>()
    var listaSMSSeleccionados = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle("Daniel Ayala Ramirez - U4P1SMS")

        //------------------------
        val consulta = FirebaseDatabase.getInstance().getReference().child(rama)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaIDs.clear()

                for (data in snapshot.children!!) {
                    val id = data.key
                    listaIDs.add(id!!)
                    val telefono = data.getValue<Mensaje>()!!.telefono
                    val mensaje = data.getValue<Mensaje>()!!.mensaje
                    val fecha = data.getValue<Mensaje>()!!.fechaHora
                    datos.add("Telefono: ${telefono}\nMensaje: ${mensaje}\nFecha y hora: ${fecha}")
                    csv.add(Dato(telefono.toString(),mensaje.toString(),fecha.toString()))
                }
                mostratLista(datos)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        consulta.addValueEventListener(postListener)
        //-------------------

        binding.listaCVS.setOnItemClickListener { adapterView, view, i, l ->
            var pos = i
            android.app.AlertDialog.Builder(this)
                .setTitle("ELIMINAR")
                .setMessage("¿Desea eliminar el indice ${pos}?")
                .setPositiveButton("ELIMINAR", {d,i->
                    listaSMSSeleccionados.removeAt(pos)
                    eliminar()
                    d.dismiss()
                })
                .setNeutralButton("CANCELAR", {d,i->
                    d.cancel()
                })
                .show()
        }

        binding.botonCSV.setOnClickListener {
            if (listaSMSSeleccionados.isEmpty()) {
                Toast.makeText(this, "LISTA VACIA",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            crearCsvDocumento(csv)
        }

        binding.abrirArchivo.setOnClickListener {
            openCsvDocument()
        }
    }

    fun mostratLista(datos: ArrayList<String>) {
        binding.listaSMS.adapter = ArrayAdapter<String>(this, R.layout.simple_expandable_list_item_1,datos)

        binding.listaSMS.setOnItemClickListener { adapterView, view, i, l ->
            var posicion = i
            AlertDialog.Builder(this)
                .setTitle("GUARDAR")
                .setMessage("¿Desea agregar el indice ${posicion}?")
                .setPositiveButton("AGREGAR") { d, i ->
                    listaSMSSeleccionados.add("${csv[posicion].telefono},${csv[posicion].mensaje},${csv[posicion].fecha}")
                    listacsv.add(
                        Dato(
                            csv[posicion].telefono,
                            csv[posicion].mensaje,
                            csv[posicion].fecha
                        )
                    )

                    binding.listaCVS.adapter = ArrayAdapter<String>(
                        this,
                        R.layout.simple_expandable_list_item_1,
                        listaSMSSeleccionados
                    )
                    d.dismiss()
                }
                .setNeutralButton("CANCELAR") { d, i ->
                    d.cancel()
                }
                .show()
        }
    }

    private fun crearCsvDocumento(smsMessageList : ArrayList<Dato>) {
        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        )

        val fileName = "archivo.csv"
        val file = File(path, "/$fileName")
        Toast.makeText(this,"${path}",Toast.LENGTH_SHORT).show()

        writeToCsvFile(file, smsMessageList)
    }

    private fun writeToCsvFile(file : File, smsMessageList : ArrayList<Dato>) {
        val writer = BufferedWriter(file.bufferedWriter());

        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader("telefono","Mensaje", "Fecha y hora"));

        for (sms in smsMessageList) {
            val archivo = listOf(
                sms.telefono,
                sms.mensaje,
                sms.fecha
            )
            csvPrinter.printRecord(archivo)
        }
        csvPrinter.flush()
        csvPrinter.close()

        Toast.makeText(this,"SE GUARDO",Toast.LENGTH_SHORT).show()
        csv.clear()
        listaSMSSeleccionados.clear()
        binding.listaCVS.adapter = ArrayAdapter<String>(this, R.layout.simple_expandable_list_item_1,listaSMSSeleccionados)
    }

    private fun openCsvDocument() {
        try {

            // Obtenemos el file del path
            val path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            )

            val fileName = "archivo.csv"
            val file = File(path, "/$fileName")

            // Abrimos el archivo
            val csvIntent = Intent(Intent.ACTION_VIEW)
            csvIntent.setDataAndType(
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file),
                "text/csv"
            )

            csvIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            csvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            this.startActivity(csvIntent)
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Aplicación no encontrada. Asegurese de tener excel instalado", Toast.LENGTH_LONG).show()
            e.printStackTrace()

        }
    }

    private fun eliminar() {
        binding.listaCVS.adapter = ArrayAdapter<String>(this, R.layout.simple_expandable_list_item_1,listaSMSSeleccionados)
    }

    // Variables globales
    val rama = "telefono"
}