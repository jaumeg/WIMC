package es.utopik.wimc

//import sun.font.LayoutPathImpl.getPath

//import android.R

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import es.utopik.jgsutils.*

import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {


    companion object {

        enum class RequestCodes (val code: Int){
            REQUEST_CODE__CAMBIAR_UBICACION (1),
            REQUEST_CODE__CAPTURE_IMAGE_ACTIVITY (1034),
            REQUEST_CODE__GETLOCATION_MAPS (9655)

        }

        const val REQUEST_CODE__CAMBIAR_UBICACION = 1
        const val REQUEST_CODE__CAPTURE_IMAGE_ACTIVITY = 1034
        const val REQUEST_CODE__GETLOCATION_MAPS = 9655

        const val APP_TAG = "WIMC"
        val photoFileName = "foto_original.jpg"
        var photoFile: File? = null

        var tempFotoFileName: String = "foto_original.jpg"
    }

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initUI()

        cargarDatos()

        //PER DEBUG!
//        runIntent_MapsActivity()

    }

    /**
     * 15.01.2020
     */
    fun initUI() {
        imgFoto.setImageResource(R.drawable.img_no_foto)


        imgFoto.setOnClickListener {
            this.launchCamera()
        }

        fab.setOnClickListener { view ->
            Intent(this, cambiarDatosActivity::class.java).apply {
                //                startActivity(this)
                startActivityForResult(this,   REQUEST_CODE__CAMBIAR_UBICACION)
            }
/*
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
*/
        }

        txtCoordenades.setOnClickListener{
            var latitud = SharedAPP.prefs.getFloatValue(SharedAPP.PREFS_LATITUD)
            var longitud  = SharedAPP.prefs.getFloatValue(SharedAPP.PREFS_LONGITUD)

            if (latitud!=null && longitud!=null){
                runIntent_MapsActivity(latitud, longitud)
            }
            else{
                snackbar("No hi ha coordenades guardades!",{
                    runIntent_MapsActivity()
                })
            }
        }

    }

    //https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#using-capture-intent
    /**
     * @returns OK
     */
    fun launchCamera(): Boolean {
//        var retOK=true

        // create Intent to take a picture and return control to the calling application
        val intentTakePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Create a File reference to access to future access
//        photoFile = getPhotoFileUri(photoFileName)
        photoFile = getPhotoFileUri(MainActivity.tempFotoFileName)


        if (photoFile == null) {
            alertMsg("error launchCamera: photoFile==null")
            return false
        }



        try {// wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.utopik.xxfileprovider", photoFile!!)

            intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        } catch (e: Exception) {
            TratarExcepcion(this, e)
//            alertMsg(e, "ERROR FileProvider.getUriForFile ")
            return false
        }

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intentTakePhoto.resolveActivity(packageManager) != null) { // Start the image capture intent to take photo
            startActivityForResult(intentTakePhoto, REQUEST_CODE__CAPTURE_IMAGE_ACTIVITY)
            return true // OK!!
        } else {
            return false
        }


    }


    /**
     * 17.01.2020
     * @returns file?
     */
    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File? {
//        // Get safe storage directory for photos
//        // Use `getExternalFilesDir` on Context to access package-specific directories.
//        // This way, we don't need to request external read/write runtime permissions.
//        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
//            alertMsg("failed to create directory: " + mediaStorageDir.absolutePath)
//            Log.d(APP_TAG, "failed to create directory")
//        }
//        // Return the file target for the photo based on filename
//        return File(mediaStorageDir.getPath() + File.separator.toString() + fileName)

        var sFullPath = this.getPathFotos(fileName)

        // Return the file target for the photo based on filename
        return File(sFullPath)

    }


    /**
     * 17.01.2020
     */
    fun getPathFotos(fileName: String = ""): String? {

        var sPath: String? = null

        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            alertMsg("Failed to create directory: " + mediaStorageDir.absolutePath)
            Log.d(APP_TAG, "failed to create directory")
        } else {
            sPath = mediaStorageDir.getPath() + File.separator.toString() + fileName
        }
        return sPath

    }

    /**
     * 15.01.2020
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

/*
//abans 17.01.2020
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE__CAMBIAR_UBICACION ->{
                    this.cargarRegistro()
                }
            }
        }
*/

        //si no OK --> acabam!
        if (resultCode != Activity.RESULT_OK) return;

        when (requestCode) {
            REQUEST_CODE__CAMBIAR_UBICACION -> {
                this.cargarDatos()
            }
            REQUEST_CODE__CAPTURE_IMAGE_ACTIVITY -> {
                takePhoto_processarResultat()
            }
            REQUEST_CODE__GETLOCATION_MAPS ->{
                data?.let{
                    var address = it.getStringExtra(MapsActivity.RESULT_ADDRESS)
                    /*
                        UEP!!
                        getFloatExtra --> retorna sempre 0!!
                        SOLUCIO >> FER SERVIR: bundle.getFloat
                    */
//                    var latitud = it.getFloatExtra(MapsActivity.RESULT_LATITUD,0f)
//                    var longitud = it.getFloatExtra(MapsActivity.RESULT_LONGITUD,0f)

                    var bundle = data.getExtras();
                    var latitud = bundle.getDouble(MapsActivity.RESULT_LATITUD).toFloat()
                    var longitud = bundle.getDouble(MapsActivity.RESULT_LONGITUD).toFloat()

                    //guardam valors
                    SharedAPP.prefs.setFloatValue(SharedAPP.PREFS_LATITUD, latitud)
                    SharedAPP.prefs.setFloatValue(SharedAPP.PREFS_LONGITUD, longitud)
                    SharedAPP.prefs.setStringValue(SharedAPP.PREFS_DIRECCIO, address)



                    txtCoordenades.setText( address)

//                    alertMsg("$latitud $longitud\n>>$address", "REQUEST_CODE__GETLOCATION_MAPS")

                }

            }
        }
    }

    /**
     * @fecha 17.01.2020
     */
    fun takePhoto_processarResultat() {
        // by this point we have the camera photo on disk


//        var sImgFullFile= photoFile!!.absolutePath
        var sTmpFile = getPathFotos(MainActivity.tempFotoFileName)

//        //guardam path foto
//        SharedAPP.prefs.setStringValue(SharedAPP.PREFS_FOTO_PATH, sTmpFile!!)

        //debug
        snackbar(sTmpFile!!, {})
        // RESIZE BITMAP, see section below
        //falta!

        var sFileResized: String? = getPathFotos("foto.jpg")

        resizeFoto(sTmpFile!!, sFileResized!!)

        //guardam path foto
        SharedAPP.prefs.setStringValue(SharedAPP.PREFS_FOTO_PATH, sFileResized)

        //mostram foto!

        val dummyBitmap = BitmapFactory.decodeFile(sFileResized)
        dummyBitmap.width
        alertMsg("$sFileResized ${dummyBitmap.width}x${dummyBitmap.height}")

        this.cargarFoto(sFileResized)
    }


    /**
     * @fecha 17.01.2020
     */
    fun resizeFoto(sFileIN: String, sFileOUT: String) {
        // See code above
//        val takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName))
        val takenPhotoUri = Uri.fromFile(File(sFileIN))

        // by this point we have the camera photo on disk
        val rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.path)


        //TODO: FALTA cridar a rotateBitmapOrientation

        // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
        val WIDTH_IMG = 600
        val resizedBitmap: Bitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, WIDTH_IMG)

        this.saveFoto(resizedBitmap, sFileOUT)
    }


    //TODO: bitmapGetOrientation
    fun bitmapGetOrientation(bitmap : Bitmap): Int{
        return 0
//        // Create and configure BitmapFactory
//        var bounds = BitmapFactory.Options()
//        bounds.inJustDecodeBounds = true
//
//        BitmapFactory.decodeFile(photoFilePath, bounds)
//
//        var opts = BitmapFactory.Options()
//        var bm = BitmapFactory.decodeFile (photoFilePath, opts);
//        // Read EXIF Data
//        var exif : ExifInterface? = null
//        try {
//            exif = ExifInterface (photoFilePath)
//        } catch (e: IOException) {
//            alertMsg("${e.message}\n${e.printStackTrace()}")
//            return null
////            e.printStackTrace();
//        }
//        var orientString = exif?.getAttribute (ExifInterface.TAG_ORIENTATION);
//        var orientation: Int
//
//        if (orientString != null) {
//            orientation = Integer.parseInt (orientString)
//        }else{
//            orientation = ExifInterface.ORIENTATION_NORMAL
//        }
    }

    /**
     * 17.01.2020
     */
    fun rotateBitmapOrientation(photoFilePath: String): Bitmap? {
        // Create and configure BitmapFactory
        var bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photoFilePath, bounds)

        var opts = BitmapFactory.Options()
        var bm = BitmapFactory.decodeFile (photoFilePath, opts);
        // Read EXIF Data
        var exif : ExifInterface? = null
        try {
            exif = ExifInterface (photoFilePath)
        } catch (e: IOException) {
            alertMsg("${e.message}\n${e.printStackTrace()}")
            return null
//            e.printStackTrace();
        }
        var orientString = exif?.getAttribute (ExifInterface.TAG_ORIENTATION);
        var orientation: Int

        if (orientString != null) {
            orientation = Integer.parseInt (orientString)
        }else{
            orientation = ExifInterface.ORIENTATION_NORMAL
        }

        var rotationAngle : Float = 0f;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90f;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180f;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270f;
        // Rotate Bitmap
        var  matrix = Matrix();
        matrix.setRotate(
            rotationAngle,
            (bm.getWidth () / 2) as Float,
            (bm.getHeight () / 2) as Float
        );
        var  rotatedBitmap = Bitmap.createBitmap (bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }


    /**
     * @fecha 17.01.2020
     */
//    fun saveFoto(bitmap: Bitmap, sFileOut: String ){
    fun saveFoto(bitmap: Bitmap, sFileOut: String) {

        val JPG_QUALITY = 40

// Configure byte output stream
        // Configure byte output stream
        val bytes = ByteArrayOutputStream()
        // Compress the image further
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPG_QUALITY, bytes)

        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
//        val resizedFile = getPhotoFileUri(photoFileName + "_resized")
//        val resizedFile = fileOut
//        resizedFile!!.createNewFile()
//        val fos = FileOutputStream(resizedFile)

        var fileOut = File(sFileOut)
        fileOut!!.createNewFile()
        val fos = FileOutputStream(fileOut)

        // Write the bytes of the bitmap to file
        fos.write(bytes.toByteArray())
        fos.close()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarDatos() {


        txtUbicacio.text = SharedAPP.prefs.getStringValue(SharedAPP.PREFS_UBICACIO)
        txtVinguentDe.setText(SharedAPP.prefs.getStringValue(SharedAPP.PREFS_VINGUENT_DE))
        txtData.setText(SharedAPP.prefs.getStringValue(SharedAPP.PREFS_DATA_TXT))
        txtCoordenades.setText(SharedAPP.prefs.getStringValue(SharedAPP.PREFS_DIRECCIO))
        var latitud = SharedAPP.prefs.getFloatValue(SharedAPP.PREFS_LATITUD)
        var longitud = SharedAPP.prefs.getFloatValue(SharedAPP.PREFS_LONGITUD)


        var dmy = SharedAPP.prefs.getStringValue(SharedAPP.PREFS_DATA_DMY)


        //---- carregam foto 17.01.2020
        var fotoPath = SharedAPP.prefs.getStringValue(SharedAPP.PREFS_FOTO_PATH)
        this.cargarFoto(fotoPath)
        //---- carregam foto


        var numDies = DateDiff_days(dmy, getCurrentDateTime_dmy())
        var s: String = ""

        when (numDies.toInt()) {
            0 -> s = "Avui"
            1 -> s = "Ahir"
            else -> s = numDies.toString()
        }
        txtNumDies.text = s

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

    fun cargarFoto(sFile: String) {
        if (fileExists(sFile)) {
            imgFoto.setImageBitmap(BitmapFactory.decodeFile(sFile))
        } else {
            imgFoto.setImageResource(R.drawable.img_no_foto)
        }
    }

    /**
     * 15.01.2020
     */
    fun dmy_to_Date(dmy: String): Date {
        val dateStr = "2/3/2017"
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date: Date = sdf.parse(dateStr)

        return date

    }

    /**
     * 15.01.2020
     */
    fun DateDiff_days(dmy1: String, dmy2: String): Long {
//        val dateStr = "2/3/2017"
//        val sdf = SimpleDateFormat("dd/MM/yyyy")
//        val date: Date = sdf.parse(dateStr)

        val date1 = dmy_to_Date(dmy1)
        val date2 = dmy_to_Date(dmy2)

        var dif: Long = date1.time - date2.time

        var ret = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)

        return ret


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menuItem_Historial -> {
//                window.decorView.findViewById<Content>() (android.R.id.content).snackbar2("TODO: ${item.title}")
                findViewById<TextView>(R.id.txtUbicacio).snackbar("TODO: ${item.title}  ")
//                toast("TODO: ${item.title}  ")
                true
            }
            R.id.menuItem_acercaDe -> {
                findViewById<TextView>(R.id.txtUbicacio).snackbar("TODO: ${item.title}  ")
//                toast("TODO: ${item.title}  ")
                true
            }
            R.id.menuItem_Ajustes -> {
                findViewById<TextView>(R.id.txtUbicacio).snackbar("TODO: ${item.title}  ")
//                toast("TODO: ${item.title}  ")
                true
            }
            R.id.menuItem_GoogleMaps -> {
                runIntent_MapsActivity()
                true
            }
            else -> {
                toast("TODO: ${item.title}  ")
                true
            }
            //else -> super.onOptionsItemSelected(item)
        }
    } // onOptionsItemSelected

    /**
     * 18.01.2020
     */
    fun runIntent_MapsActivity(latitud: Float? =null, longitud: Float?=null){
        var intent = Intent(this, MapsActivity::class.java)

        if (longitud!=null && latitud!=null) {
            intent.putExtra(MapsActivity.EXTRA_LATITUD, latitud)
            intent.putExtra(MapsActivity.EXTRA_LONGITUD, longitud)
        }

        startActivityForResult(intent, MainActivity.REQUEST_CODE__GETLOCATION_MAPS)
    }


}
