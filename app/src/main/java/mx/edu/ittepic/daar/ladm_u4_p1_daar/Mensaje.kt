package mx.edu.ittepic.daar.ladm_u4_p1_daar

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Mensaje(val telefono : String ?= null, val mensaje: String ?= null, val fechaHora: String ?= null) {

}