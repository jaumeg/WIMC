package es.utopik.wimc

import android.app.Application
import android.content.Context
import androidx.room.Room

data class entityRegistro (
    val ubicacio : String="",
    val data_txt: String="",
    val data_dmy: String="",
    val vinguent_de: String="",
    val comentaris: String="",
    var location_latitud: Float? = null,
    var location_longitud: Float? = null
)
{

}


class SharedAPP : Application() {
    val DATABASE_NAME ="todo-list.db"
    var EMAIL_DEVELOPER = "info@jaumegelabert.com"

    companion object {
        lateinit var db: AppDatabase
        lateinit var prefs : Prefs



        //--------------
        const val PREFS_UBICACIO = "UBICACIO"
        const val PREFS_VINGUENT_DE = "VINGUENT_DE"

        const val PREFS_DATA_HORA = "DATA_HORA"
        const val PREFS_DATA_DIA = "DATA_DIA"
        const val PREFS_DATA_TXT = "DATA_TXT"
        const val PREFS_DATA_DMY = "DATA_DMY"
        const val PREFS_COMENTARIS = "COMENTARIS"
        const val PREFS_FOTO_PATH = "FOTO_PATH"
        const val PREFS_LATITUD = "LATITUD"
        const val PREFS_LONGITUD = "LONGITUD"
        const val PREFS_DIRECCIO = "DIRECCIO"


        /**
         * @date 08.01.2020
         * @param v si posam valor, defineix el mode debug
         * @return true | false
         */
        fun modoDebug(v : Boolean? = null) : Boolean{
            // posam valor!
            if (v!=null){
                prefs.modoDebug = v
            }

            //retornam valor
            return SharedAPP.prefs.modoDebug
        }
    } // companion object

    class Prefs(context: Context){
        val PREFS_NAME = "prefs_name"
//        val SHARED_NAME = "shared_name"
        val prefs = context.getSharedPreferences(PREFS_NAME,0)

/*
        fun get(name: String): String{
            return prefs.getString(name,"")
        }
*/


//        var name: String
//            get() = prefs.getString(SHARED_NAME,"")
//            set(value) = prefs.edit().putString(SHARED_NAME, value).apply()

        var modoDebug: Boolean
            get() = prefs.getBoolean("modoDebug",false)
            set(value) = prefs.edit().putBoolean("modoDebug", value).apply()

        fun setStringValue (key :String, value : String){
            prefs.edit().putString(key, value).apply()
        }
        fun getStringValue (key :String, defValue : String ="") : String{
            return prefs.getString(key,defValue)
        }

        fun setFloatValue (key :String, value : Float){
            prefs.edit().putFloat(key, value).apply()
        }
        fun getFloatValue (key :String, defValue : Float? =null) : Float?{
//            var ret : Float?
//            if (!prefs.contains(key)) ret = null
//            else ret = prefs.getFloat(key,0f)


            return if (prefs.contains(key))  prefs.getFloat(key,0f) else null
        }



    } // class Prefs

/*
    fun initDB(){
        SharedAPP.db =  Room.databaseBuilder(this, AppDatabase::class.java, DATABASE_NAME).build()
    }
*/





    override fun onCreate() {
        super.onCreate()

//        SharedAPP.db = Room.databaseBuilder(this, AppDatabase::class.java, DATABASE_NAME).build()
        SharedAPP.db = Room.databaseBuilder(this, AppDatabase::class.java, DATABASE_NAME).allowMainThreadQueries().build()
        SharedAPP.prefs = Prefs(this)
    }
}