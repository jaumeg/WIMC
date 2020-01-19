///**
// * 09.01.2020
// */
//
//package es.utopik.wimc
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Context
//import android.os.Build
//import android.view.View
//import android.view.inputmethod.InputMethodManager
//import androidx.annotation.RequiresApi
//import com.google.android.material.snackbar.Snackbar
//import java.io.File
//import java.nio.file.SimpleFileVisitor
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
//fun Activity.snackbar3(msg:String, action: ()->Unit, duration: Int = Snackbar.LENGTH_LONG){
//    val parentLayout: View = findViewById(android.R.id.content)
//    val listener = View.OnClickListener{
//        action()
//    }
//
//    Snackbar.make(parentLayout, msg, duration)
//        .setAction("OK", listener).show()
//
//
////    parentLayout.snackbar2(msg,duration)
//
//}
//
///**
// * 09.01.2020
// */
//fun View.snackbar2(msg:String){
//    Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
//        .setAction("OK", null).show()
//
//}
//
//
//fun Context.alertMsg(msg : String, title :String = "Aviso"){
//
//    val builder = AlertDialog.Builder(this)
//    builder.setTitle(title)
//    builder.setMessage(msg)
//    //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
//
//    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
//        /*
//                Toast.makeText(applicationContext,
//                    android.R.string.yes, Toast.LENGTH_SHORT).show()
//        */
//    }
//
///*
//    builder.setNegativeButton(android.R.string.no) { dialog, which ->
//        Toast.makeText(applicationContext,
//            android.R.string.no, Toast.LENGTH_SHORT).show()
//    }
//
//    builder.setNeutralButton("Maybe") { dialog, which ->
//
//        Toast.makeText(applicationContext,
//            "Maybe", Toast.LENGTH_SHORT).show()
//
//    }
//*/
//    builder.show()
//}
//
//fun Activity.hideKeyboard() {
//    hideKeyboard(currentFocus ?: View(this))
//}
//
//fun Context.hideKeyboard(view: View) {
//    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//}
//
//
///**
// * 15.01.2020
// */
//@RequiresApi(Build.VERSION_CODES.O)
//fun getCurrentDateTime_dmy():String{
//    val current = LocalDateTime.now()
////        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
//    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
//    val formatted = current.format(formatter)
//
//    return formatted
//}
//
///**
// * @fecha 17.01.2020
// * @return OK
// */
//fun fileExists(sFile : String?) : Boolean{
//    var retOK: Boolean=false
//
//    if (!sFile.isNullOrEmpty()) {
//        val file : File = File(sFile)
//        retOK = file.exists()
//    }
//
//    return retOK
//}