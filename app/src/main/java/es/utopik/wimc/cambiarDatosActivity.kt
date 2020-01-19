package es.utopik.wimc

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import es.utopik.jgsutils.*

import kotlinx.android.synthetic.main.activity_cambiar_datos.*
import org.jetbrains.anko.design.snackbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class cambiarDatosActivity : AppCompatActivity() {

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_datos)

//        txtData.text = this.getCurrentDateTime()

        initUI()

//        var calendar = Calendar.getInstance()
//        calendar.toString()
        txtData.setText (getCurrentDateTime())

    }




    fun initUI(){

//        txtData.focusable = NOT_FOCUSABLE
//        txtData.apply{
//        }
//        with(txtData){
//            focusable (false)
//        }
        txtData.isEnabled = false

        this.cargarRegistro()


        btnOK.setOnClickListener{
            //validam controls
            if (validarControls()) {

                //---------------- guardarRegistre
                guardarRegistro()

                //retornam RESULT_OK!
                val data = Intent()
//                data.data = Uri.parse(cad)
                setResult(Activity.RESULT_OK, data)
                finish()

            }

        }

        btnCancel.setOnClickListener{

            val builder = AlertDialog.Builder(this)
            builder.setTitle("confirmar cancelar")
            builder.setMessage("Segur que vols cancel·lar?")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton("SI") { dialog, which ->
                finish()
            }
            builder.setNegativeButton("NO") { dialog, which ->
            }

            builder.show()

            /*           AlertDialog.Builder(this).apply {
                                      setTitle("Confirmar")
                                      setMessage("Segur que vols cancel·lar?")

                                      setPositiveButton("SI") { dialog, which ->{
                                          finish()
                                      }

                                      setPositiveButton("SI"){_,_ ->

                                      }
                                      setNegativeButton("NO",{
                                          //RES
                                      })
                                      show()

                                  }*/

//            alertMsg("FALTARIA CONFIRMAR CANCELAR")
//            finish()
            //it.snackbar("TODO: btnOK")
        }

        imgFoto.setOnClickListener{
            it.snackbar("TODO: elegir foto")
        }
    }



    fun guardarRegistro(){
        SharedAPP.prefs.setStringValue(SharedAPP.PREFS_UBICACIO, txtUbicacio.text.toString())
        SharedAPP.prefs.setStringValue(SharedAPP.PREFS_VINGUENT_DE, txtVinguentDe.text.toString())
        SharedAPP.prefs.setStringValue(SharedAPP.PREFS_DATA_TXT, txtData.text.toString())
        SharedAPP.prefs.setStringValue(SharedAPP.PREFS_DATA_HORA, txtHORA.text.toString())

        var dmy = getCurrentDateTime_dmy()

        SharedAPP.prefs.setStringValue(SharedAPP.PREFS_DATA_DMY, dmy)

        SharedAPP.prefs.setStringValue(SharedAPP.PREFS_COMENTARIS, txtComentaris.text.toString())
    }


    fun cargarRegistro(){


        txtUbicacio.setText(SharedAPP.prefs.getStringValue(SharedAPP.PREFS_UBICACIO))
        txtVinguentDe.setText (SharedAPP.prefs.getStringValue(SharedAPP.PREFS_VINGUENT_DE))
        txtData.setText (SharedAPP.prefs.getStringValue(SharedAPP.PREFS_DATA_TXT))

        var dmy = SharedAPP.prefs.getStringValue(SharedAPP.PREFS_DATA_DMY)


//        txtComentaris.setText (SharedAPP.prefs.getStringValue(SharedAPP.PREFS_COMENTARIS))

/*
        SharedAPP.apply {
            txtUbicacio.setText (prefs.getStringValue(PREFS_UBICACIO))
            txtVinguentDe.setText (prefs.getStringValue(PREFS_VINGUENT_DE))
            txtData.setText (prefs.getStringValue(PREFS_DATA_TXT))
            txtComentaris.setText (prefs.getStringValue(PREFS_COMENTARIS))

        }
*/

    }




    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime() : String{

/*
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        System.out.println(" C DATE is  "+currentDate)
*/

        val current = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val formatted = current.format(formatter)
        var diaSemana = current.format(DateTimeFormatter.ofPattern("e")).toInt()
        diaSemana = diaSemana -1

        val aDias = arrayOf<String>("dummy!","Dilluns", "Dimarts", "Dimecres", "Dijous", "Divendres", "Dissabte", "Diumenge")


        return formatted + " " + aDias[diaSemana]
    }


    /**
     * 15.01.2020
     */
    fun validarControls(): Boolean{
        var retOK : Boolean = true

        if (txtUbicacio.text.isNullOrEmpty()) {
            txtUbicacio.requestFocus()
//            toast("REQUERIT: Ubicació")
            snackbar("REQUERIT: Ubicació", {})
            retOK=false
        }

        return retOK
    }
}
