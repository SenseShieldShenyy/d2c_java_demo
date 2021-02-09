package com.sense.d2c;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sense.internal.D2C;
import com.sense.internal.SSDefine;
import com.sense.internal.SSError;
import com.sense.internal.SlmControl;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;

public class Main {
    

    /***
     * 使用PointerByReference 时声明变量的前边不要有Pointer 变量，不然传参会出错
     */
    public static void main(String[] args) throws IOException {
        //System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.library.path", System.getProperty("java.library.path"));
        System.out.println(System.getProperty("jna.library.path"));
        CLibrary.C_LIBRARY.printf("Hello, World\n");


        //打开控制锁
        Pointer master_handle;
        long status = SSError.SS_OK;
        PointerByReference pbr = new PointerByReference(Pointer.NULL);
        status = D2C.d2c.master_open(pbr);
        if(status != SSError.SS_OK) {
            System.out.println("master_open failed: " + status);
            return;
        }
        else
            System.out.println("master_open success");

        //验证控制锁pin码
        master_handle = pbr.getValue();
        String pwd = "12345678";
        Pointer p = new Memory(pwd.length() + 1);
        p.setString(0, pwd);
        status = D2C.d2c.master_pin_verify(master_handle, 0, p, 8);
        if(status != SSError.SS_OK) {
            System.out.println("master_pin_verify failed: " + status);
            return;
        }
        else
            System.out.println("master_pin_verify success");


        //#获取加密锁序列号
        List<String> dongle_sns = getDevSn("080000000000090d");
        String str_sns = "";
        for(String str  : dongle_sns){
                 str_sns += str;
        }
        System.out.println(str_sns);
        String first_dongle_sn = dongle_sns.get(0);
        byte[] byte_sn = convertHexStringToBytes(first_dongle_sn);
        Pointer devicesn = new Memory(byte_sn.length);
        devicesn.write(0, byte_sn, 0, byte_sn.length);
        long sn_size = byte_sn.length;
        System.out.println(dongle_sns.get(0));

        //#获取加密锁证书链
        byte[] byte_p7b_cert = getP7bCert(str_sns);
        //String str_p7b_cert = new String(byte_p7b_cert,"utf-8");
        Pointer p7b_cert = new Memory(byte_p7b_cert.length);
        p7b_cert.write(0, byte_p7b_cert, 0, byte_p7b_cert.length);
        long p7b_size = byte_p7b_cert.length;

        //创建许可  许可json参数说明参考 @d2c_add_lic
        pbr = new PointerByReference(Pointer.NULL);
        status = D2C.d2c.d2c_lic_new(master_handle, pbr, D2C.ACCOUNT_TYPE.ACCOUNT_TYPE_NONE, devicesn, sn_size, p7b_cert, p7b_size);

        if(status != SSError.SS_OK) {
            System.out.println("d2c_lic_new failed: " + status);
            return;
        }
        else {
            System.out.println("d2c_lic_new success");
        }
        JSONObject lic_json_object = new JSONObject();
        lic_json_object.put("op","addlic");
        lic_json_object.put("license_id",100);
        lic_json_object.put("force",true);
        long long_start_time = System.currentTimeMillis()/1000;
        lic_json_object.put("start_time","="+Long.toString(long_start_time));
        lic_json_object.put("end_time","="+Long.toString(long_start_time+100000));
        lic_json_object.put("concurrent_type","process");
        lic_json_object.put("concurrent","=0");
        //数据区
        JSONObject data_json_object = new JSONObject();
        String data = converStringToHexstring("sense123");
        data_json_object.put("data",data);
        data_json_object.put("offset",0);
        data_json_object.put("resize", "sense123".length());
        lic_json_object.put("pub",data_json_object);
        String str_lic_object = lic_json_object.toJSONString();
        System.out.println(str_lic_object);
        byte[] byte_lic_object = str_lic_object.getBytes();
        Pointer d2c_handle = pbr.getValue();
        byte[] guid = new byte[(int) D2C.D2C_GUID_LENGTH];
        Pointer opr_param_str =new Memory(byte_lic_object.length ) ;
        opr_param_str.write(0,byte_lic_object,0,byte_lic_object.length);
        byte[] byte_desc = "test_add_lic_test".getBytes();
        Pointer opr_desc_str = new Memory(byte_desc.length);
        opr_desc_str.write(0,byte_desc,0,byte_desc.length);
        status = D2C.d2c.d2c_add_lic(d2c_handle, opr_param_str, opr_desc_str, guid);
        if (status == 0) {
            System.out.println("addlic success");
        }
        else {
            System.out.println("d2c add lic error :"+status);
        }
        LongByReference buffer_size =new LongByReference(0);
        status = D2C.d2c.d2c_get(d2c_handle,null,0,buffer_size);
        if (status == 4) {
            System.out.println("d2c get size success");
        }
        else {
            System.out.println("error:"+status);
        }
        Pointer d2c_buffer = new Memory(buffer_size.getValue());
        status = D2C.d2c.d2c_get(d2c_handle,d2c_buffer,buffer_size.getValue(),buffer_size);
        if (status == 0) {
            System.out.println("get d2c buffer success");
        }
        else {
            System.out.println("error :"+ status);
        }
        try {
            FileWriter my_file = new FileWriter("C:\\Users\\shenyy\\Desktop\\d2c\\test.d2c");
            System.out.println(d2c_buffer.getString(0));
            my_file.write(d2c_buffer.getString(0));
            my_file.close();
            System.out.println("Successfully wrote to the file.");
        }
        catch(IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    //获取已插入加密锁的sn号
    public static List<String> getDevSn(String developer_id){
        long status = SSError.SS_OK;
        List<String> sns = null;

        PointerByReference pbr = new PointerByReference(Pointer.NULL);
        status = SlmControl.slm_control.slm_ctrl_client_open(pbr);
        if(status != SSError.SS_OK) {
            System.out.println("slm_ctrl_client_open failed: " + status);
            return sns;
        }

        Pointer ipc = pbr.getValue();
        PointerByReference outjsonstr = new PointerByReference(Pointer.NULL);
        status = SlmControl.slm_control.slm_ctrl_get_local_description(ipc, SSDefine.INFO_FORMAT_TYPE.JSON, outjsonstr);
        if(status != SSError.SS_OK) {
            System.out.println("slm_ctrl_get_local_description failed: " + status);
            SlmControl.slm_control.slm_ctrl_client_close(ipc);
            return sns;
        }
        else
            System.out.println("slm_ctrl_get_local_description success ");

        Pointer jsondesc = outjsonstr.getValue();
        String jsonstr = jsondesc.getString(0);
        if(jsonstr != null && jsonstr.length() != 0)
            System.out.println("received dongle infos: \n" + jsonstr);
        else
            System.out.println("slm_ctrl_get_local_description receive json success, but no content");
        SlmControl.slm_control.slm_ctrl_client_close(ipc);

        sns = new ArrayList<String>();
        JSONArray jsonArray = (JSONArray) JSONObject.parse(jsonstr);
        for(int i = 0; i<jsonArray.size(); i++) {
            JSONObject json = (JSONObject)jsonArray.get(i);
            if(json.getString("developer_id").equalsIgnoreCase(developer_id))
                sns.add(json.getString("sn"));
        }

        System.out.println(sns.toString());
        return sns;
    }
    //获取加密锁证书
    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract(pure = true)
    public static byte[] getP7bCert(String developer_id){
        String p7b_from_db = "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";
        byte[] base64_decode_bytes = Base64.getDecoder().decode(p7b_from_db);
        return base64_decode_bytes;
    }
    //将16 进制字符串转化为字节形式
    public static byte[] convertHexStringToBytes(String hexString){
        //判空
        if(hexString == null || hexString.length() == 0) {
            return null;
        }

        //合法性校验
        if(!hexString.matches("[a-fA-F0-9]*") || hexString.length() % 2 != 0) {
            return null;
        }

        //计算
        int mid = hexString.length() / 2;
        byte[]bytes = new byte[mid];
        for (int i = 0; i < mid; i++) {
            bytes[i] = Integer.valueOf(hexString.substring(i * 2, i * 2 + 2), 16).byteValue();
        }

        return bytes;
    }
    @NotNull
    public static String converStringToHexstring(@NotNull String string) {
        StringBuffer sb = new StringBuffer();
        char ch[] = string.toCharArray();
        for(int i = 0; i < ch.length; i++){
            String hex_str = Integer.toHexString(ch[i]);
            sb.append(hex_str);
        }
        return sb.toString();
    }
}
