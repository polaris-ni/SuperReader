package com.lyni.reader.lib.common.base.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.lyni.reader.lib.common.base.viewmodel.BaseViewModel
import java.io.File

/**
 * @author Liangyong Ni
 * description BasePicSelectActivity
 * @date 2022/3/22
 */
@Deprecated("not used")
abstract class BasePicSelectActivity<VB : ViewBinding, VM : BaseViewModel> : BaseActivity<VB, VM>() {
    //相机拍照保存的位置
    private lateinit var photoUri: Uri

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1000 //权限
        private const val REQUEST_CODE_ALBUM = 1001 //相册
        private const val REQUEST_CODE_CAMERA = 1002 //相机
    }

    protected fun openAlbum() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = "android.intent.action.GET_CONTENT"
        intent.addCategory("android.intent.category.OPENABLE")
        startActivityForResult(intent, REQUEST_CODE_ALBUM)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ALBUM -> {
                    onPicSelected(data?.data!!)
                }
                REQUEST_CODE_CAMERA -> {
                    //从保存的位置取值
                    onPicSelected(photoUri)
                }
//                UCrop.REQUEST_CROP -> {
//                    val resultUri: Uri = UCrop.getOutput(data!!)!!
//                    val bitmap =
//                        BitmapFactory.decodeStream(contentResolver.openInputStream(resultUri))
//                    binding.ivResult.setImageBitmap(bitmap)
//                }
//                UCrop.RESULT_ERROR -> {
//                    val error: Throwable = UCrop.getError(data!!)!!
//                    showToast("图片剪裁失败" + error.message)
//                }
            }
        }
    }

//    private fun doCrop(sourceUri: Uri) {
//        Intrinsics.checkParameterIsNotNull(sourceUri, "资源为空")
//        UCrop.of(sourceUri, getDestinationUri())//当前资源，保存目标位置
//            .withAspectRatio(1f, 1f)//宽高比
//            .withMaxResultSize(500, 500)//宽高
//            .start(this)
//    }

    private fun getDestinationUri(): Uri {
        val fileName = String.format("fr_crop_%s.jpg", System.currentTimeMillis())
        val cropFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        return Uri.fromFile(cropFile)
    }

    protected fun checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                showWarning("拒绝会导致无法使用相机")
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoUri = getDestinationUri()
        photoUri = FileProvider.getUriForFile(this, "$packageName.fileProvider", File(photoUri.path!!))
        // android11以后强制分区存储，外部资源无法访问，所以添加一个输出保存位置，然后取值操作
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    abstract fun onPicSelected(sourceUri: Uri?)

    fun loadBitmapByUri(uri: Uri): Bitmap? {
        try {
            return BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}