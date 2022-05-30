package mx.edu.ittepic.daar.ladm_u4_p1_daar

import android.R
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import mx.edu.ittepic.daar.ladm_u4_p1_daar.databinding.ActivityMain2Binding
import java.io.IOException
import java.io.OutputStreamWriter

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    var listaIDs = ArrayList<String>()
    var csv = ArrayList<Dato>()
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
            if (listaSMSSeleccionados.size == null) {
                Toast.makeText(this, "LISTA VACIA",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var lista = "Telefono,Mensaje,Fecha y hora\n"

            (0..listaSMSSeleccionados.size-1).forEach { 
                lista+=listaSMSSeleccionados.get(it)+"\n"
            }
            try {
                val archivo = OutputStreamWriter(openFileOutput("archivo.csv", Activity.MODE_PRIVATE))
                archivo.write(lista)
                archivo.flush()
                archivo.close()
            } catch (e: IOException) { }
            Toast.makeText(this, "Los datos fueron grabados",Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun mostratLista(datos: ArrayList<String>) {
        binding.listaSMS.adapter = ArrayAdapter<String>(this, R.layout.simple_expandable_list_item_1,datos)

        binding.listaSMS.setOnItemClickListener { adapterView, view, i, l ->
            var posicion = i
            AlertDialog.Builder(this)
                .setTitle("GUARDAR")
                .setMessage("¿Desea agregar el indice ${posicion}?")
                .setPositiveButton("AGREGAR", {d,i->
                    listaSMSSeleccionados.add("${csv[posicion].telefono},${csv[posicion].mensaje},${csv[posicion].fecha}")

                    binding.listaCVS.adapter = ArrayAdapter<String>(this, R.layout.simple_expandable_list_item_1,listaSMSSeleccionados)
                    d.dismiss()
                })
                .setNeutralButton("CANCELAR", {d,i->
                    d.cancel()
                })
                .show()
        }
    }

    private fun eliminar() {
        binding.listaCVS.adapter = ArrayAdapter<String>(this, R.layout.simple_expandable_list_item_1,listaSMSSeleccionados)
    }

    // Variables globales
    val rama = "telefono"
}