package com.sense.internal;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;


public interface D2C extends Library {
    D2C d2c = Native.load( ForJavaUtils.getJNAPrefixPath()+ "libd2c", D2C.class);

    /**
     * 签发给账户许可的账户名最大长度
     */
    public static final long MAX_ACCOUNT_NAME_LENGTH = 64;
    /**
     * 账户GUID的长度
     */
    public static final long D2C_GUID_LENGTH = 37;
    /**
     * 修复时钟的随机数长度
     */
    public static final long FIXTIME_RAND_LENGTH = 8;
    /**
     * 碎片代码种子大小
     */
    public static final long SNIPPET_SEED_LENGTH = 32;

    /**
     * 锁内文件权限定义：文件开发者可读
     */
    public static final long ACCESS_READ = 0x01;
    /**
     * 锁内文件权限定义：文件开发者可修改删除
     */
    public static final long ACCESS_WRITE_DELETE = 0x02;
    /**
     * 锁内文件权限定义：文件开发者可使用（针对密钥文件）
     */
    public static final long ACCESS_USE = 0x04;
    /**
     * 锁内文件权限定义：文件开发者可远程升级
     */
    public static final long ACCESS_WRITE_DELETE_RU = 0x08;

    /**
     * 文件Entry可读
     */
    public static final long ACCESS_ENTRY_READ = 0x10;
    /**
     * 文件Entry可修改删除
     */
    public static final long ACCESS_ENTRY_WRITE_DELETE = 0x20;
    /**
     * 文件Entry可使用（针对密钥文件）
     */
    public static final long ACCESS_ENTRY_USE = 0x40;
    /**
     * 文件可远程升级
     */
    public static final long ACCESS_ENTRY_WRITE_DELETE_RU = 0x80;

    /**
     * 系统级别CA
     */
    public static final long PKI_CA_TYPE_SYSTEM = 0;
    /**
     * 开发者CA
     */
    public static final long PKI_CA_TYPE_DEVELOPER = 1;
    /**
     * 硬件设备CA
     */
    public static final long PKI_CA_TYPE_DEVICE = 2;
    /**
     * 账户证书
     */
    public static final long PKI_CA_TYPE_ACCOUNT = 3;
    /**
     * 根证书
     */
    public static final long PKI_CA_TYPE_ROOT = 0x80;

    /**
     * 开发锁PIN码最小长度
     */
    public static final long CTRL_PIN_MIN_SIZE = 8;
    /**
     * 开发锁PIN码最大长度
     */
    public static final long CTRL_PIN_MAX_SIZE = 255;


    /** D2C Developer2Customer的句柄 deprecated*/
    //typedef struct _D2C_HANDLE_INTERNAL* D2C_HANDLE;  //PointerByReference

    /** 控制锁的句柄  deprecated*/
    //typedef struct _MASTER_HANDLE_INTERNAL* MASTER_HANDLE; //PointerByReference

    /**
     * D2C包的签发类型(SIGN_TYPE)
     */
    public interface SIGN_TYPE {
        /**
         * D2C由证书签发，签发给指定用户锁
         */
        public static final long SIGN_TYPE_CERT = 1;
        /**
         * D2C由种子码签发(暂不支持签发许可)，签发给所有用户锁
         */
        public static final long SIGN_TYPE_SEED = 2;
    }

    ;

    /**
     * D2C包的签名哈希类型
     */
    public interface D2C_HASH_ALGO {
        /**
         * 哈希算法为：SHA-1
         */
        public static final long D2C_HASH_ALGO_SHA1 = 1;
        /**
         * 哈希算法为：SHA-256
         */
        public static final long D2C_HASH_ALGO_SHA256 = 2;

    }

    ;

    /**
     * 账户类型，指定给特定的签发对象
     */
    public interface ACCOUNT_TYPE {
        /**
         * 签给非账户（硬件锁锁设备）
         */
        public static final int ACCOUNT_TYPE_NONE = 0;
        /**
         * 签给在线账户（硬件锁锁号作为匿名账户）
         */
        public static final long ACCOUNT_TYPE_USB = 1;
        /**
         * 签给邮箱账户（暂不支持）
         */
        public static final long ACCOUNT_TYPE_EMAIL = 2;
        /**
         * 签给手机账户（暂不支持）
         */
        public static final long ACCOUNT_TYPE_PHONE = 3;

    }

    ;

    /**
     * 碎片代码种子结构（开发者不必关心）
     */
//    typedef struct _SNIPPET_CODE_CONTEXT
//    {
//        /**  版本号 */
//        long       version;
//        /**  代码标识 */
//        long       code_id;
//        /** 许可ID */
//        long       license_id;
//        /** 密钥（密文）*/
//        SS_BYTE         key[32];
//        /** 种子（密文）*/
//        SS_BYTE         snippet_seed[SNIPPET_SEED_LENGTH];
//    } SNIPPET_CODE_CONTEXT;


    /*!
     *   @brief      打开签发设备（开发锁）
     *   @param[out] device_handle       开发锁设备句柄
     *   @return     成功返回 SS_OK，失败返回相应的错误码
     *   @remarks    一切的签发功能都离不开开发锁，若不打开开发锁，所有许可签发的操作将不能进行
     *   @see        master_close d2c_lic_new
     */
    long master_open(PointerByReference device_handle);

    /*!
     *   @brief      关闭签发设备（开发锁）
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @return     成功返回SS_OK，失败返回相应的错误码
     *   @see        master_open
     */
    long master_close(Pointer device_handle);

    /*!
     *   @brief      获取开发锁开发商ID
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] developer_id    开发商ID
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    开发商ID 是每个开发者唯一的一个标识号，每个开发者独立，通过获取开发锁的开发商ID，可判断此开发锁是否为本人所有。
     *               最常使用的区别方式是区别 Demo 开发锁和正式开发锁。
     *   @code
     *       {
     *           MASTER_HANDLE master_handle ;
     *           SS_BYTE developer_id[8] = {0};
     *           long status = SS_OK;
     *
     *           status = master_open(&master_handle);
     *           if(status != SS_OK)
     *               return status;
     *
     *           status = master_get_developer_id(master_handle, developer_id);
     *           if(status == SS_OK)
     *           {
     *               // todo
     *               // HEXTOSTRING(developer_id);    // 将十六进制转换为字符串
     *           }
     *           master_close(master_handle);
     *       }
     *   @endcode
     */
    long master_get_developer_id(Pointer device_handle, ByteByReference developer_id);

    /*!
     *   @brief      获取开发商锁内所有的根证书数量
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] count           根证书数量
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    自 2018年1月1日 起，VirboxLM 对开发锁和用户锁进行安全升级，开发锁将支持多根证书，新用户锁将使用新的根证书。
     *               为兼容之前版本的用户锁，开发锁中将拥有多个根证书
     */
    long master_get_root_count(Pointer device_handle, LongByReference count);

    /*!
     *   @brief      通过证书类型，获取开发锁 CA 证书
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  ca_type             证书类型，详见 #PKI_CA_TYPE_DEVELOPER 等
     *   @param[out] ca_cert             存放锁CA证书的缓冲区
     *   @param[in]  cert_bufsize        缓冲区大小
     *   @param[out] cert_size           CA 证书大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    签发许可需要带上开发商证书链，因此获取开发锁的证书、CA 将是不可或缺的接口。
     *   @code
     *       {
     *           long status = SS_OK;
     *           MASTER_HANDLE  master_handle;
     *               SS_BYTE    ca[2048] = { 0 };   // 证书或者CA的大小一般不会超过1.5k，这里缓冲区使用2k足够大了
     *               long  ca_size = 0;
     *
     *           master_open(&master_handle);
     *
     *           status = master_get_ca_cert(master_handle, PKI_CA_TYPE_DEVELOPER, ca, sizeof(ca), &ca_size);
     *           if(status == SS_OK)
     *           {
     *               // save ca to file
     *           }
     *           master_close(master_handle);
     *       }
     *   @endcode
     */
    long master_get_ca_cert(Pointer device_handle, byte ca_type,
                            ByteByReference ca_cert, long cert_bufsize, LongByReference cert_size);


    /*!
     *   @brief      通过证书类型，获取开发锁 CA 证书
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  ca_type             证书类型，详见 #PKI_CA_TYPE_DEVELOPER 等
     *   @param[in]  root_index          根证书索引序号
     *   @param[out] ca_cert             存放锁CA证书的缓冲区
     *   @param[in]  cert_bufsize        缓冲区大小
     *   @param[out] cert_size           CA 证书大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    签发许可需要带上开发商证书链，因此获取开发锁的证书、CA 将是不可或缺的接口。
     *   @see        master_get_ca_cert
     */
    long master_get_ca_cert_ex(Pointer device_handle, byte ca_type, long root_index,
                               ByteByReference ca_cert, long cert_bufsize, LongByReference cert_size);

    /*!
     *   @brief      获取开发锁 Virbox Root CA 证书
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] root_ca             存放开发锁 Virbox Root CA 证书的缓冲区
     *   @param[in]  root_bufsize        缓冲区大小
     *   @param[out] root_size           开发锁 Virbox Root CA 证书大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @see        master_open master_get_ca_cert
     */
    long master_get_root_ca(Pointer device_handle,
                            ByteByReference root_ca, long root_bufsize, LongByReference root_size);

    /*!
     *   @brief      获取开发锁设备 CA 证书
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] device_ca           存放设备 CA 证书的缓冲区
     *   @param[in]  device_bufsize      缓冲区大小
     *   @param[out] device_size         设备 CA 证书大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @see        master_open master_get_ca_cert
     */
    long master_get_device_ca(Pointer device_handle,
                              ByteByReference device_ca, long device_bufsize, LongByReference device_size);

    /*!
     *   @brief      获取开发锁开发者 CA 证书
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] developer_ca        存放开发者 CA 证书的缓冲区
     *   @param[in]  ca_bufsize          缓冲区大小
     *   @param[out] ca_size             开发者 CA 证书大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @see        master_open master_get_ca_cert
     */
    long master_get_developer_ca(Pointer device_handle,
                                 ByteByReference developer_ca, long ca_bufsize, LongByReference ca_size);

    /*!
     *   @brief      获取开发锁开发者证书
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] developer_cert      存放开发者证书的缓冲区
     *   @param[in]  cert_bufsize        缓冲区大小
     *   @param[out] cert_size           开发者证书大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @see        master_open master_get_ca_cert
     */
    long master_get_developer_cert(Pointer device_handle,
                                   ByteByReference developer_cert, long cert_bufsize, LongByReference cert_size);

    /*!
     *   @brief      根据根证书索引，获取开发锁开发者证书（扩展接口）
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  root_index          根证书索引序号
     *   @param[out] developer_cert      存放开发者证书的缓冲区
     *   @param[in]  cert_bufsize        缓冲区大小
     *   @param[out] cert_size           开发者证书大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @see        master_open master_get_ca_cert
     */
    long master_get_developer_cert_ex(Pointer device_handle, long root_index,
                                      ByteByReference developer_cert, long cert_bufsize, LongByReference cert_size);

    /*!
     *   @brief      验证开发锁 CA 证书
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  store               验证时，是否将证书存储在锁内。TRUE，存储；FALSE，不存储。
     *   @param[in]  ca_type             证书类型，详见 #PKI_CA_TYPE_DEVELOPER 等
     *   @param[in]  ca_cert             证书缓冲区
     *   @param[in]  ca_len              证书长度
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks
     *   @code
     *       {
     *           long status = SS_OK;
     *           MASTER_HANDLE  master_handle;
     *           SS_BYTE    ca[2048] = { 0 };   // 证书或者CA的大小一般不会超过1.5k，这里缓冲区使用2k足够大了
     *           long  ca_size = 0;
     *
     *           master_open(&master_handle);
     *
     *           // 通过master_get_ca_cert、master_get_developer_ca获取到开发者ca，或者加载外部存储的开发者ca
     *           // ... ...
     *
     *           status = master_verify_ca(master_handle, FALSE, PKI_CA_TYPE_DEVELOPER, ca, ca_size);
     *           if(status == SS_OK)
     *           {
     *               // save ca to file
     *           }
     *           master_close(master_handle);
     *       }
     *   @endcode
     */
    long master_verify_ca(Pointer device_handle, int store,
                          byte ca_type, ByteByReference ca_cert, long ca_len);

    /*!
     *   @brief      验证开发锁CA证书（扩展接口）
     *   @param[in]  device_handle       开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  store               验证时，是否将证书存储在锁内。TRUE，存储；FALSE，不存储。
     *   @param[in]  ca_type             证书类型，详见 #PKI_CA_TYPE_DEVELOPER 等
     *   @param[in]  root_index          根证书序号
     *   @param[in]  ca_cert             证书缓冲区
     *   @param[in]  ca_len              证书长度
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    此接口仅对 #PKI_CA_TYPE_DEVELOPER CA 验证有效
     */
    long master_verify_ca_ex(Pointer device_handle, int store,
                             byte ca_type, long root_index, ByteByReference ca_cert, long ca_len);

    /*!
     *   @brief      获取签发设备的种子码密钥（种子码公钥）
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] key             缓冲区
     *   @param[in]  max_key_len     缓冲区长度
     *   @param[out] key_len         证书的长度
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     */
    long master_get_seedkey(
            Pointer device_handle,
            ByteByReference key,
            long max_key_len,
            LongByReference key_len
    );

    /*!
     *   @brief      获取开发锁设备信息
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  type            获取信息类型，当前只能 = 1
     *   @param[out] info            获取到的信息，json结构
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    info 需要调用 #master_heap_free 释放
     *   @code
     *     - info 的 json 结构
     *       {
     *           "support_pin":true,
     *           "enable_pin":true,
     *           "developer_id":"0300000000000009",
     *           "lock_firmware_version":"3.1.16",
     *           "lm_firmware_version":"1.2.1.0",    (PIN 码验证通过后获取信息是才有此字段)
     *           "sn":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
     *       }
     *   @endcode
     */
    long master_get_info(Pointer device_handle, byte type, PointerByReference info);

    /*!
     *   @brief      释放内存
     *   @param[in]  buf             内部分配的内存
     *   @return     永远返回 SS_OK
     *   @remarks    例如 #master_get_info 分配的内存
     */
    long master_heap_free(Pointer buf);

    /*!
     *   @brief      修改开发商开发锁 PIN 码
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  pin_index       PIN 码所在的索引位置，目前只支持 1 个 PIN 码，当前为 0
     *   @param[in]  old_pin         当前使用中的 PIN 码
     *   @param[in]  old_pin_len     当前使用的 PIN 码长度
     *   @param[in]  new_pin         新 PIN 码
     *   @param[in]  new_pin_len     新 PIN 码长度
     *   @param[in]  hash            新 PIN 码的 sha256 hash 结果，用于内部校验
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    PIN 码遵循以下格式：
     *               - 长度不得低于 8 个字符，不得大于 255 个字符
     *               - 不得使用汉字，全角字符等
     *               - 不得出现二级制 0 等会影响真实长度的字符
     *               - 初始 PIN 码必须修改，否则初始 PIN 码无法通过验证，初始 PIN 码从 VirboxLM 开发者中心获取（https://developer.lm.virbox.com）
     */
    long master_pin_change(
            Pointer device_handle,
            byte pin_index,
            ByteByReference old_pin,
            long old_pin_len,
            ByteByReference new_pin,
            long new_pin_len,
            ByteByReference hash
    );

    /*!
     *   @brief      验证开发锁 PIN 码
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  pin_index       PIN 码所在的索引位置，目前只支持 1 个 PIN 码，当前为 0
     *   @param[in]  pin             当前使用中的 PIN 码
     *   @param[in]  pin_len         当前使用的 PIN 码长度
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    PIN 验证遵循以下规则：
     *               - 初始 PIN 码不能通过验证
     *               - 必须通过 PIN 码验证才能签发升级包
     */
    long master_pin_verify(
            Pointer device_handle,
            int pin_index,
            Pointer pin,
            long pin_len
    );

    /*!
     *   @brief      确认 PIN 码是否验证通过
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    如果返回 SS_ERROR_MASTER_OUTDATED_VERSION ，表示控制锁版本太低，必须更换控制锁；
     *               如果返回 0x0100000D ，标明没有进行 PIN 吗验证。
     */
    long master_pin_is_verified(Pointer device_handle);

    /*!
     *   @brief      启用开发锁 PIN 码
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    启用开发锁 PIN 功能
     */
    long master_pin_enable(Pointer device_handle);

    /*!
     *   @brief      禁用开发锁 PIN 码
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @return     成功返回 SS_OK，失败返回相应的错误码
     *   @remarks    禁用当前开发锁的 PIN 功能，需先调用 #master_pin_verify
     */
    long master_pin_disable(Pointer device_handle);

    /*!
     *   @brief      清除 PIN 权限
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    清除当前 PIN 权限后，下次签发许可时，需要重新调用 #master_pin_verify
     */
    long master_pin_deauth(Pointer device_handle);


    /*!
     *   @brief      创建 D2C 句柄，用于签发许可
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] d2c_license     返回 D2C 的句柄
     *   @param[in]  account_type    账号类型，参考 #ACCOUNT_TYPE
     *   @param[in]  account_id      账户名（字符串）或锁号（16字节锁号）
     *   @param[in]  account_size    账户名长度
     *   @param[in]  cert            证书，如果是 account_type = #ACCOUNT_TYPE_NONE ，填入硬件锁设备证书链，账户许可填入云开发者证书链
     *   @param[in]  cert_size       证书长度
     *   @return     成功返回 SS_OK ，失败返回相应的错误码，SS_ERROR_PARSE_CERT 表示解析设备证书失败。
     *   @remarks    创建 D2C 句柄，生成 D2C 包的初始化工作。
     *   @see        d2c_delete d2c_add_developer_cert d2c_add_pkg d2c_get
     */
    long d2c_lic_new(
            Pointer device_handle,
            PointerByReference d2c_license,
            int account_type,
            Pointer account_id,
            long account_size,
            Pointer cert,
            long cert_size
    );

    /*!
     *   @brief      创建D2C句柄，用于签发非许可类型升级包，例如签发文件
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[out] d2c_file        返回 D2C 的句柄
     *   @param[in]  sign_type       签包类型，参考 #SIGN_TYPE
     *   @param[in]  param           如果是设备证书，则传入设备证书链，种子码签发填入 Virbox Root CA
     *   @param[in]  param_size      证书链的大小或种签子码签发填 Virbox根证书大小
     *   @return     成功返回S S_OK，失败返回相应的错误码
     *   @remarks    自2018年1月1日起，VirboxLM 对开发锁和用户锁进行安全升级，开发锁将支持多根证书，新用户锁将使用新的根证书，
     *               原来的 d2c_famile_seed_new 将废弃，变更为当前的 #d2c_file_new 。
     *               主要变更有
     *                   - 在使用种子码签发文件时，param 参数由原来的传空，变成传入 Virbox Root CA。
     *
     *               Virbox Root CA 可以通过 #master_get_ca_cert_ex 、 #master_get_root_count 获取。
     *               最为正确的证书获取方法是做法是：
     *                   - 开发商从固件版本号 3.1.20 以下版本的用户锁中，获取到 Virbox Root CA 旧版本根证书 传值；
     *                   - 开发商从固件版本号 3.1.20 以上（含 3.1.20 ）版本的用户锁中，获取 Virbox Root CA 新版本根证书 传值；
     *                   - 开发商从深思数盾提供的 设备证书链中获取对应根证书。
     *
     *               由于根证书都是一样的，只是新用户锁使用新版本根证书，因此两种证书只需要取一次保存即可。
     */
    long d2c_file_new(
            Pointer device_handle,
            PointerByReference d2c_file,
            SIGN_TYPE sign_type,
            ByteByReference param,
            long param_size
    );

    /*!
     *   @brief      增加开发者证书链（不需要再调用了，保留仅为兼容性）
     *   @param[in]  d2c_handle      D2C 句柄，由 #d2c_lic_new 或 #d2c_file_new 得到
     *   @param[in]  cert            开发者证书链
     *   @param[in]  cert_len        证书大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    D2C 在生成时将自动添加开发商证书链到 D2C 包中，不必再调用此接口传入证书链。
     */
    long d2c_add_developer_cert(Pointer d2c_handle, ByteByReference cert, long cert_len);

    /*!
     *   @brief      签发动态代码
     *   @param[in]  device_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  sign_type       签包类型，参考 #SIGN_TYPE
     *   @param[in]  param           设备证书链 (种子码签发方式填对应的 Virbox Root CA)
     *   @param[in]  param_size      证书链长度 (种子码签发方式填对应的 Virbox Root CA 长度）
     *   @param[in]  exec_code       动态代码
     *   @param[in]  code_size       代码大小（最大支持 2048 字节大小的动态代码）
     *   @param[in]  bound_info      绑定信息（许可ID 数组，许可ID 为 4 个字节）
     *   @param[in]  bound_size      绑定信息的长度（单位：字节）
     *   @param[out] pkg             存放升级包的用户缓冲区
     *   @param[in]  max_pkg_len     缓冲区的长度
     *   @param[out] pkg_len         签得的代码长
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    自 2018年1月1日 起，VirboxLM 对开发锁和用户锁进行安全升级，开发锁将支持多根证书，新用户锁将使用新的根证书
     *               主要变更有
     *                   - 在使用种子码签发动态代码时，param 参数由原来的传空，变成传入 Virbox Root CA。
     *
     *               Virbox Root CA 可以通过 #master_get_ca_cert_ex 、 #master_get_root_count 获取。
     *               最为正确的证书获取方法是做法是：
     *                   - 开发商从固件版本号 3.1.20 以下版本的用户锁中，获取到 Virbox Root CA 旧版本根证书 传值；
     *                   - 开发商从固件版本号 3.1.20 以上（含 3.1.20 ）版本的用户锁中，获取 Virbox Root CA 新版本根证书 传值；
     *                   - 开发商从深思数盾提供的 设备证书链中获取对应根证书。
     *
     *               由于根证书都是一样的，只是新用户锁使用新版本根证书，因此两种证书只需要取一次保存即可。
     */
    long gen_dynamic_code(
            Pointer device_handle,
            SIGN_TYPE sign_type,
            ByteByReference param,
            long param_size,
            ByteByReference exec_code,
            long code_size,
            LongByReference bound_info,
            long bound_size,
            ByteByReference pkg,
            long max_pkg_len,
            LongByReference pkg_len
    );

    /*!
     *   @brief      根据描述，生成升级包，并添加升级包到 D2C，针对非许可项内容的升级
     *   @param[in]  d2c_file        文件 D2C 句柄，由 #d2c_file_new 得到
     *   @param[in]  param           升级包json串
     *   @param[in]  opr_desc        包描述字符串
     *   @exception  没有异常
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks
     *   - 升级规则：
     *       -#  更新文件时，执行文件的 offset 必须为 0
     *       -#  种子码方式，不能签发许可
     *   - 重置锁注意事项：
     *       -#  加密锁升级时必须保持与签包服务器时间相同，否则会出现重置锁失败的情况。
     *       -#  锁时间戳小于重置锁限定开始时间，调整计算机本地时间与服务器相同后升级数据包。
     *       -#  锁时间戳大于重置锁限定结束时间，重新签发重置锁升级包。
     *   @code
     *       json 参数说明：
     *       文件操作：
     *       {
     *           "op":           "addfile" | "updatefile" | "delfile"    文件操作类型，依次为：添加 | 更新 |删除
     *           "filename":     "file_name.xxx",                        文件名，长度需小于 16 字节
     *           "filetype":     "evx" | "evd" | "key"                   文件类型，依次为：可执行文件 | 数据文件 | 密钥文件。 （删除文件时不需要填写类型）
     *           "access":       number                                  文件的访问权限（添加，更新，删除文件时都可以设置，默认为 0x0F （开发者所有权限， Entry 不可访问），参考 #ACCESS_READ 等宏
     *           "timestamp":    number(0 ~ 0xFFFFFFFF)                  文件的生成时间（用于抗重放），如果JSON中没有此项，则使用当前时间生成一个时间戳
     *           "filebuffer":   "0123456789ABCDEF"                      文件内容 HEX16 字符串（删除文件时不需要填写此项）
     *           "fileoffset":   number  (可选)                          文件偏移，默认为 0（删除文件时不需要）
     *           "bind_lic":     [1,2,3,4]                               可执行文件绑定的许可，仅文件类型为 evx (可执行程序)时有效
     *       }
     *
     *       重置锁：
     *       {
     *           "op":           "reset"             操作类型：重置锁
     *           "not_before":   UTC 时间（有效开始时间）
     *           "not_after":    UTC 时间（有效终止时间），升级包在起止时间范围内（包含起止的那一秒）可重复使用。
     *       }
     *
     *       修订时钟：
     *       {
     *           "op" :          "fixtime",                  操作类型：修复时钟
     *           "lock_time":      number,					用户锁的时间
     *           "rand":         "0102030405060708"          8 个字节 HEX16 字符串
     *           "diff":          number,                    时间差 (pc_time - lock_time)
     *       }
     *
     *   @endcode
     *   @see d2c_lic_new d2c_add_developer_cert
     *
     */
    long d2c_add_pkg(
            Pointer d2c_file,
            ByteByReference param,
            ByteByReference opr_desc
    );

    /*!
     *   @brief      根据描述，生成升级包，并添加升级包到D2C，针对许可项内容的升级
     *   @param[in]  d2c_license     许可 D2C句柄，由 #d2c_lic_new 函数得到
     *   @param[in]  param           升级包 json 串
     *   @param[in]  opr_desc        包操作描述字符串，开发者自定义
     *   @param[out] guid            许可 GUID @see D2C_GUID_LENGTH
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks
     *   - 升级规则：
     *       -#  种子码方式，不能签发许可
     *   - json 参数说明：
     *   @code
     *       参数名称            参数类型    必选字段      参数值                  参数描述
     *       "op"                string     是           "installlic"            安装许可
     *                                                   "addlic"                添加许可
     *                                                   "activelic"             激活许可
     *                                                   "updatelic"             更新许可
     *                                                   "dellic"                删除许可
     *                                                   "lockalllic"            锁定所有许可（0号许可专用）
     *                                                   "unlockalllic"          解锁所有许可（0号许可专用）
     *                                                   "delalllic"             删除所有许可（0号许可专用）
     *       "version"           string     否            "op number"            许可版本号（数字，范围从 1 ~ 0xFFFFFFFF）   op: '+' | '-' | '='    eg. "=123"
     *       "license_id"        number     是(视op)      --                     许可ID（数字，范围从 1 ~ 0xFFFFFFFF）（lockalllic, delalllic, unlockalllic不需要）
     *       "force"             bool       否            true/false             是否强制升级，若为 true ，则升级的策略为：
     *                                                                           [addlic]无则添加，有则覆盖
     *                                                                           [dellic]有则删除，无则成功
     *       "start_time"        string     否            "op number"            起始时间(UTC 秒)                           op : '+' | '-' | '=' | 'disable'    eg. "=123456", "disable"(禁用设置，常用于更新时禁用原限制条件)
     *       "end_time"          string     否            "op number"            终止时间，不设置标明是永久许可(UTC秒)     op : '+' | '-' | '=' | 'disable'    eg. "+456789"
     *       "counter"           string     否            "op number"            使用次数                                  op : '+' | '-' | '=' | 'disable'   eg. "-123"
     *       "span"   	        string     否            "op number"            时间跨度(UTC 秒)                           op : '+' | '-' | '=' | 'disable'   eg. "=987654"
     *       "concurrent"        string     否            "op number"            最大并发数(0~65535)                       op : '+' | '-' | '=' | 'disable'   eg. "=654"
     *       "concurrent_type"   string     否            "process"              以进程限制并发
     *                                                    "win_user_session",    以 Windows 会话限制并发数
     *       "module":           json_array 否            [1,2,3,4,5,...,64]     模块区，json 整数数组结构，数组里可以表示模块，范围从 1 到 64
     *       "timestamp":        number     否            --                     许可的签发时间戳，用于防重放，如果JSON中没有此项，则使用当前时间生成一个时间戳
     *       "serial":           number     否            --                     许可的签发流水号，用于防重放，如果JSON中没有此项，则与时间戳配合，生成一个流水号
     *       "rom":              json       否            --                     只读数据区
     *                                                    {
     *                                                        "data":"HEX字符串",     数据区内容
     *                                                        "offset":number,        写入数据区的偏移，默认为 0
     *                                                        "resize":number         如果存在此字段，表示重置数据区大小，值即为新的大小
     *                                                    }
     *       "raw":              json       否            {"data":"HEX字符串", "offset":number,"resize":number}           读写数据区，内容参考只读取描述
     *       "pub":              json       否            {"data":"HEX字符串", "offset":number,"resize":number}           公开数据区，内容参考只读取描述
     *
     *       注： 除"lockalllic", "unlockalllic", "delalllic"外，其它操作均需要设置"license_id"字段
     *   @endcode
     *   @see d2c_lic_new d2c_add_developer_cert
     *
     */
    long d2c_add_lic(
            Pointer d2c_license,
            //ByteByReference param,
            Pointer param,
            //ByteByReference opr_desc,
            Pointer opr_desc,
            byte guid[]
    );

    /*!
     *   @brief      从 D2C 句柄中获取 D2C 流，可保存为 .d2c 文件用于升级
     *   @param[in]  d2c_handle  D2C 句柄，由 #d2c_lic_new 或 #d2c_file_new 得到
     *   @param[out] d2c_buf     得到的 D2C 数据流，该数据流为字符串格式
     *                           如果为 0，则 out_len 返回需要的长度
     *   @param[in]  max_buf_len d2c_buf 的缓冲区大小
     *   @param[out] out_len     得到的 D2C 数据流长度
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    通过此接口获取到最终的 D2C 包内容，可直接将数据写入锁内，也可保存为 .d2c 文件，异步写入锁内。
     *   @code
     *       {
     *           ["pkg_type":"type", "pkg_data":"base64", "pkg_desc":"desc"]
     *           ["pkg_type":"type", "pkg_data":"base64", "pkg_desc":"desc"]
     *       }
     *   @endcode
     *   @see d2c_lic_new d2c_add_developer_cert
     */
    long d2c_get(
            Pointer d2c_handle,
            Pointer d2c_buf,
            //ByteByReference d2c_buf,
            long max_buf_len,
            LongByReference out_len
    );

    /*!
     *  @brief       获得开发者自定义的 D2C 操作描述
     *  @param[in]   d2c_handle      D2C句柄，由 #d2c_lic_new 或 #d2c_file_new 得到
     *  @param[in]   index           pkg的索引，从 1 开始
     *  @return      字符串，返回值不需要释放，否则会出错
     */
    ByteByReference d2c_get_pkg_desc(
            Pointer d2c_handle,
            long index
    );

    /*!
     *  @brief       删除 D2C 句柄
     *  @param[in]   d2c_handle      D2C句柄，由 #d2c_lic_new 或 #d2c_file_new 得到
     *  @return      总是返回SS_OK
     *  @see         d2c_lic_new
     */
    long d2c_delete(
            Pointer d2c_handle
    );

    /*!
     *   @brief      D2C 签名 （开发者不必关心）
     *   @param[in]  master_handle   开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  hash_algo       哈希算法类型，参考 #D2C_HASH_ALGO
     *   @param[in]  root_index      Virbox Root CA 根证书序号
     *   @param[in]  data_in         要签名的数据
     *   @param[in]  data_len        要签名数据的长度
     *   @param[out] sign            签名后的数据
     *   @param[in]  max_sign_len    签名缓冲区长度，至少为 256 + 64 字节
     *   @param[out] sign_len        签名数据长度
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     *   @remarks    sign 包含 256字节的开发锁签名结果，和 64 字节的开发商信息结果。前 256 字节为签名，后 64 字节为开发商信息。
     */
    long d2c_sign(Pointer master_handle, D2C_HASH_ALGO hash_algo, long root_index, ByteByReference data_in, long data_len,
                  ByteByReference sign, long max_sign_len, LongByReference sign_len);

    /*!
     *   @brief      D2C加密 （开发者不必关心）
     *   @param[in]  certs           证书链
     *   @param[in]  certs_len       链书链长度
     *   @param[in]  plain           明文
     *   @param[in]  plain_len       明文长度
     *   @param[out] cipher          密文
     *   @param[in]  max_cipher_len  缓冲区大小
     *   @param[out] cipher_len      密文长度
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     */
    long d2c_encrypt(ByteByReference certs, long certs_len, ByteByReference plain, long plain_len,
                     ByteByReference cipher, long max_cipher_len, LongByReference cipher_len);

    /*!
     *   @brief      获取碎片代码种子（开发者不必关心）
     *   @param[in]  master          开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  license_id      许可ID
     *   @param[out] snippet_seed    碎片代码种子（32 字节）  @see SNIPPET_SEED_LENGTH
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     */
    long get_snippet_seed(Pointer master, long license_id, byte snippet_seed[]);

    /*!
     *   @brief      生成碎片代码（开发者不必关心）
     *   @param[in]  master              开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  license_id          绑定的许可ID
     *   @param[in]  evx_code            执行的原始文件
     *   @param[in]  evx_size            执行文件的大小
     *   @param[out] snippet_code        存放生成的碎片代码的缓冲区
     *   @param[in]  snippet_buf_size    缓冲区 snippet_code 的大小
     *   @param[out] snippet_size        生成的碎片代码的大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     */
    long gen_snippet_code(Pointer master, long license_id, ByteByReference evx_code, long evx_size,
                          ByteByReference snippet_code, long snippet_buf_size, LongByReference snippet_size);

    /*!
     *   @brief      获取生成碎片代码请求（开发者不必关心）
     *   @param[in]  sc_ctx          碎片代码上下文
     *   @param[in]  evx             执行代码原文
     *   @param[in]  evx_size        执行代码原文大小
     *   @param[out] snippet_code    碎片代码缓冲区
     *   @param[in]  snippet_bufsize 碎片代码缓冲区大小
     *   @param[out] snippet_size   碎片代码大小
     *   @return     成功返回 SS_OK ，失败返回相应的错误码
     */
    long gen_snippet_code_with_key(Pointer sc_ctx, ByteByReference evx, long evx_size,
                                   ByteByReference snippet_code, long snippet_bufsize, LongByReference snippet_size);

    /*!
     *   @brief      使用开发锁进行许可加密（开发者不必关心）
     *   @param[in]  master      开发锁设备句柄，通过调用 #master_open 得到
     *   @param[in]  license_id  许可ID
     *   @param[in]  plain       明文
     *   @param[in]  len         加密长度（必须是16的倍数）
     *   @param[out] cipher      密文
     *   @return     成功返回 SS_OK ，失败则返回相应的错误码
     */
    long license_encrypt(Pointer master, long license_id, ByteByReference plain, long len, ByteByReference cipher);
}
