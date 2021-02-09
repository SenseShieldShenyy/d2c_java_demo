package com.sense.internal;

public interface SSDefine {
    /** 参数格式枚举  */
    public interface INFO_FORMAT_TYPE {
        /** JSON格式  */
        public static final long JSON = 2;
        /** 结构体格式  */
        public static final long STRUCT = 3;
        /**  字符串模式,遵行 Key=value 规则（暂不支持）  */
        public static final long STRING_KV = 4;
        /** 加密二进制格式（暂不支持） */
        public static final long CIPHER = 5;
    };

/** 设备证书类型 */
    public interface CERT_TYPE{
        /** 证书类型：根证书  */
        public static final long CERT_TYPE_ROOT_CA = 0;

        /** 证书类型：设备子CA  */
        public static final long CERT_TYPE_DEVICE_CA = 1;

        /** 证书类型：设备证书  */
        public static final long CERT_TYPE_DEVICE_CERT = 2;

        /** 证书类型：深思设备证书  */
        public static final long CERT_TYPE_SENSE_DEVICE_CERT = 3;
    };
}
