package com.sense.internal;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface SlmControl extends Library {
    SlmControl slm_control = Native.load(ForJavaUtils.getJNAPrefixPath() + "slm_control", SlmControl.class);

    /*!
     *   @brief      客户端打开 IPC句柄，与 Virbox许可服务 进行通信
     *   @param[out] ipc      返回IPC句柄
     *   @return     成功返回 SS_OK，失败返回相应的错误码
     *   @remarks    #slm_ctrl_client_open 是 @link ControlAPI @endlink 最先调用的一个接口，由于打开与 Virbox许可服务 的 IPC 通信管道
     *               所有与 Virbox许可服务 交互的操作，都需要率先打开 IPC。
     */
    long slm_ctrl_client_open(PointerByReference ipc);

    /*!
     *   @brief      关闭客户端IPC句柄
     *   @param[in]  ipc     IPC句柄，通过 #slm_ctrl_client_open 获得
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    IPC 打开使用完毕后，必须要关闭，否则会存在资源浪费、内存泄露等问题。
     *   @see        slm_ctrl_client_open
     */
    long slm_ctrl_client_close(Pointer ipc);

    /*!
     *   @brief      获取本地设备描述
     *   @param[in]  ipc             IPC句柄，通过 #slm_ctrl_client_open 获得
     *   @param[in]  format_type     参数类型( #JSON )
     *   @param[out] desc            设备描述，json数组，需要调用 #slm_ctrl_free 释放
     *   @return     成功返回SS_OK，失败返回相应错误码
     *   @remarks    硬件锁有本地锁和网络锁两种，获取本地设备描述为硬件锁设备描述获取方式的一种，更为精确的只获取本地锁的硬件锁描述。
     *               调用方式参考 #slm_ctrl_get_all_description ，其 desc 参数也一致
     *   @see        slm_ctrl_client_open slm_ctrl_get_all_description
     */
    long slm_ctrl_get_local_description(Pointer ipc, long format_type, PointerByReference desc);

}
