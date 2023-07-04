package com.nextgenapp.nextgentech.ui.main.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nextgenapp.nextgentech.R
import com.nextgenapp.nextgentech.data.api.Response
import com.nextgenapp.nextgentech.data.model.DepartmentResponse
import com.nextgenapp.nextgentech.data.model.EmployeeCreateResponse
import com.nextgenapp.nextgentech.databinding.ActivityAddEmployeeBinding
import com.nextgenapp.nextgentech.ui.base.BaseActivity
import com.nextgenapp.nextgentech.ui.main.adapter.SpinnerAdapter
import com.nextgenapp.nextgentech.ui.viewmodel.EmployeeViewModel
import com.nextgenapp.nextgentech.utils.Session
import com.nextgenapp.nextgentech.utils.hideSoftKeyboard
import com.nextgenapp.nextgentech.utils.hideSpinnerDropDown
import com.nextgenapp.nextgentech.utils.showToast
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddEmployeeActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val READ_PERMISSION_CODE = 1001
    }

    private lateinit var activityAddEmployeeBinding: ActivityAddEmployeeBinding
    private val today = Calendar.getInstance()
    private lateinit var datePicker: DatePickerDialog
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private var selectedImageURI: Uri? = null
    private val employeeViewModel: EmployeeViewModel by viewModels()
    private val departmentArray by lazy { ArrayList<DepartmentResponse.DataItem?>() }
    private var departmentToken: String? = null

    /**
     * photo picker this runs on Android 11 (API level 30) or higher
     */
    private val pickSingleMediaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "Failed picking media.", Toast.LENGTH_SHORT).show()
            } else {
                imagePickerURI(uri = it.data?.data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddEmployeeBinding = ActivityAddEmployeeBinding.inflate(layoutInflater)
        setContentView(activityAddEmployeeBinding.root)
        setStatusBar(R.color.white)

        employeeViewModel.departmentResponse.observe(this, departmentObserver)
        employeeViewModel.createEmployeeResponse.observe(this, employeeObserver)
        employeeViewModel.errorHandlerLiveData.observe(this, errorObserver)

        activityAddEmployeeBinding.backButton.setOnClickListener(this)
        activityAddEmployeeBinding.addEmployee.setOnClickListener(this)
        activityAddEmployeeBinding.chooseDepartment.setOnClickListener(this)
        activityAddEmployeeBinding.dateOfBirth.setOnClickListener(this)
        activityAddEmployeeBinding.employeeImage.setOnClickListener(this)

        lifecycleScope.launch { employeeViewModel.getDepartment() }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            activityAddEmployeeBinding.backButton.id -> finish()
            activityAddEmployeeBinding.addEmployee.id -> {
                if (selectedImageURI == null) {
                    showToast("Please add employee photo")
                } else {

                    var bitmap: Bitmap? = null

                    selectedImageURI.let {
                        bitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedImageURI
                            )
                        } else {
                            val source = ImageDecoder.createSource(
                                this.contentResolver,
                                selectedImageURI!!
                            )
                            ImageDecoder.decodeBitmap(source)
                        }
                    }

                    val fileURL = bitmapToFile(bitmap!!).toString()

                    val file = File(fileURL)

                    val requestFile = RequestBody.create(MediaType.parse("image/png"), file)

                    if (departmentToken == null) {
                        showToast("Please select the department")
                    } else {
                        lifecycleScope.launch {
                            employeeViewModel.createEmployee(
                                department_token = departmentToken!!,
                                name = activityAddEmployeeBinding.employeeName.text.toString(),
                                email = activityAddEmployeeBinding.emailAddress.text.toString(),
                                mobile_number = activityAddEmployeeBinding.contactNumber.text.toString(),
                                date_of_birth = activityAddEmployeeBinding.dateOfBirth.text.toString(),
                                blood_group = activityAddEmployeeBinding.bloodGroup.text.toString(),
                                address = activityAddEmployeeBinding.address.text.toString(),
                                photo = requestFile,
                                fileName = file.name
                            )
                        }
                    }
                }
            }
            activityAddEmployeeBinding.chooseDepartment.id -> {
                hideSoftKeyboard(this)
                activityAddEmployeeBinding.departmentSpinner.performClick()
            }
            activityAddEmployeeBinding.dateOfBirth.id -> {
                hideSoftKeyboard(this)
                showDatePicker()
            }
            activityAddEmployeeBinding.employeeImage.id -> {
                hideSoftKeyboard(this)
                photoPicker()
            }
        }
    }


    private fun compressBitmap(image: Bitmap): Bitmap {

        val bitmapRatio = image.width.toFloat() / image.height.toFloat()

        return if (bitmapRatio > 1) {
            val width = 300
            val height = (width / bitmapRatio).toInt()
            Bitmap.createScaledBitmap(image, width, height, true)
        } else {
            val height = 300
            val width = (height / bitmapRatio).toInt()
            Bitmap.createScaledBitmap(image, width, height, true)
        }
    }

    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        val sdf = SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault())
        val fileImage = "${Session.userDetail?.token}_${sdf.format(Date())}"
        Log.d("filename", fileImage)
        file = File(file, "$fileImage.png")

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }


    /**
     * First check for the READ_EXTERNAL_STORAGE permission if the permission is
     * not GRANTED, need to request the permission, Once the permission is GRANTED
     * pick the image from library. From android M it is mandatory to request
     * permission for certain actions.
     *
     * If the android version is 11 and above we will use photo picker library to pick the image.
     * If the version is below 11 we use the Intent to pick the image.
     */
    private fun photoPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pickSingleMediaLauncher.launch(Intent(MediaStore.ACTION_PICK_IMAGES))
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    val permissions = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permissions, READ_PERMISSION_CODE)
                } else {
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerResultLauncher.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                }
            }
        }
    }

    /**
     * We can get the response from the selected image here which will be returned as URI.
     */
    private val imagePickerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null) {
                if (result.resultCode == RESULT_OK) {
                    performCrop(picUri = result.data!!.data!!)
//                    imagePickerURI(uri = result.data!!.data!!)
                }
            }
        }

    private fun imagePickerURI(uri: Uri?) {
        selectedImageURI = uri
        Glide.with(this).load(uri).circleCrop().into(activityAddEmployeeBinding.employeeImage)
    }

    private fun showDatePicker() {
        datePicker = DatePickerDialog(
            this,
            dateSetListener,
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = today.timeInMillis
        datePicker.show()
    }

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate[Calendar.MONTH] = month
            selectedDate[Calendar.DAY_OF_MONTH] = dayOfMonth
            selectedDate[Calendar.YEAR] = year

            activityAddEmployeeBinding.dateOfBirth.text = simpleDateFormat.format(selectedDate.time)
        }

    private fun spinnerItemSelected(data: String) {
        activityAddEmployeeBinding.chooseDepartment.text = data
        val selectedPosition = getPosition(departmentName = data)
        departmentToken = departmentArray[selectedPosition]!!.token
        hideSpinnerDropDown(activityAddEmployeeBinding.departmentSpinner)
    }

    private fun getPosition(departmentName: String): Int =
        departmentArray.indexOfFirst { it!!.name == departmentName }

    private fun performCrop(picUri: Uri) {

        /**
         * picUri - indicate image type and Uri
         * crop - set crop properties here
         * aspectX / aspectY - indicate aspect of desired crop
         * outputX / outputY - indicate output X and Y
         * return-data - retrieve data on return
         */
        try {
            val cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(picUri, "image/*")
            cropIntent.putExtra("crop", true)
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            cropIntent.putExtra("outputX", 348)
            cropIntent.putExtra("outputY", 348)
            cropIntent.putExtra("return-data", true)
            imageCropResult.launch(cropIntent)
        } catch (exception: ActivityNotFoundException) {
            val errorMessage = "Whoops - your device doesn't support the crop action!"
            val toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
            toast.show()
            imagePickerURI(uri = picUri)
        }
    }


    private val imageCropResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null) {
                if (result.resultCode == RESULT_OK) {
                    val extras: Bundle = result.data!!.extras!!
                    val selectedBitmap =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            extras.getParcelable("data", Bitmap::class.java)
                        } else {
                            extras.getParcelable<Bitmap>("data")
                        }

                    val croppedImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        saveImageInQ(selectedBitmap!!)
                    } else {
                        saveImageInLegacy(selectedBitmap!!)
                    }
                    imagePickerURI(uri = croppedImageUri)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveImageInQ(bitmap: Bitmap): Uri {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var imageUri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = application.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!, contentValues, null, null)

        return imageUri as Uri
    }

    private fun saveImageInLegacy(bitmap: Bitmap): Uri {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, "crop_${Calendar.getInstance().timeInMillis}")
        val fos = FileOutputStream(image)
        fos.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return image.toUri()
    }

    private val departmentObserver = androidx.lifecycle.Observer<Response<DepartmentResponse>> {
        when (it) {
            is Response.Success -> {
                departmentArray.clear()
                val dataItem = it.data
                if (dataItem != null) {
                    val departmentArrayData = dataItem.data
                    if (departmentArrayData != null) {
                        if (departmentArrayData.isNotEmpty()) {
                            departmentArray.addAll(departmentArrayData)
                            populateSpinnerData(departmentArray)
                        }
                    }
                }
            }
            is Response.Error -> showToast(it.errorMessage!!)
            is Response.Loading -> {
                if (it.showLoader!!) {
                    activityAddEmployeeBinding.progressBar.visibility = View.VISIBLE
                } else {
                    activityAddEmployeeBinding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun populateSpinnerData(departmentArray: java.util.ArrayList<DepartmentResponse.DataItem?>) {
        val spinnerData: ArrayList<String> = ArrayList()

        for (data in departmentArray) {
            spinnerData.add(data?.name!!)
        }

        activityAddEmployeeBinding.departmentSpinner.adapter = SpinnerAdapter(
            context = this, spinnerData = spinnerData, itemClicker = ::spinnerItemSelected
        )
    }

    private val employeeObserver = androidx.lifecycle.Observer<Response<EmployeeCreateResponse>> {
        when (it) {
            is Response.Loading -> {
                if (it.showLoader!!) {
                    activityAddEmployeeBinding.progressBar.visibility = View.VISIBLE
                } else {
                    activityAddEmployeeBinding.progressBar.visibility = View.GONE
                }
            }
            is Response.Error -> showToast(it.errorMessage!!)
            is Response.Success -> {
                it.data?.message?.let { it1 -> showToast(it1) }
                finish()
            }
        }
    }

}