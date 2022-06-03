package com.lyni.reader.biz.setting.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.lyni.permission.QuickPermission
import com.lyni.permission.core.Permissions
import com.lyni.reader.biz.setting.databinding.SettingFragmentMainBinding
import com.lyni.reader.biz.setting.vm.SettingMainViewModel
import com.lyni.reader.lib.common.base.ui.BaseFragment
import com.lyni.reader.lib.common.router.RouterTable
import com.lyni.treasure.lib.common.ktx.onClick
import com.lyni.treasure.lib.common.utils.showToast

/**
 * @date 2022/5/26
 * @author Liangyong Ni
 * description SettingFragment
 */
@Route(path = RouterTable.Fragment.SETTING_MAIN)
class SettingFragment : BaseFragment<SettingFragmentMainBinding, SettingMainViewModel>() {

    override fun initListener() {
        super.initListener()
        binding.test.onClick {
            QuickPermission.with(this)
                .addPermissions(Permissions.CAMERA)
                .info("相机权限", "权限仅用来支持应用正常运行，请放心授权")
                .onGranted {
                    showToast("授权成功")
                }.onDenied {
                    showToast("授权失败")
                }.request()
        }
    }
}