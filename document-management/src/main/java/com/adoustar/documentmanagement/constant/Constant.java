package com.adoustar.documentmanagement.constant;

public class Constant {

    public static final int STRENGTH = 12;
    public static final int NINETY_DAYS = 90;
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITIES = "authorities";
    public static final String EMPTY_VALUE = "empty";
    public static final String TYPE = "typ";
    public static final String JWT_TYPE = "JWT";
    public static final String ROLE = "role";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String MANAGER_AUTHORITIES = "user:read,document:create,document:read,document:update,document:delete";
    public static final String SUPER_ADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete";
    public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,document:create,document:read,document:update,document:delete";
    public static final String USER_AUTHORITIES = "document:create,document:read,document:update,document:delete";

    public static final String[] PUBLIC_ROUTES = {"/user/resetpassword/reset", "/user/verify/resetpassword", "/user/resetpassword",
    "/user/stream", "/user/verify/qrcode", "/user/new/password", "/user/verify", "/user/refresh/token", "/user/image", "/user/verify/account",
    "/user/verify/password", "/user/verify/code","/user/id", "/user/login", "/user/register"};
    public static final String[] PUBLIC_URLS = {"/user/resetpassword/reset/**", "/user/verify/resetpassword/**", "/user/resetpassword/**", "/user/verify/qrcode/**", "/user/login/**", "/user/verify/account/**", "/user/register/**", "/user/new/password/**", "/user/verify/**", "/user/resetpassword/**", "/user/image/**", "/user/verify/password/**"};

    public static final String BASE_PATH = "/**";
    public static final String FILE_NAME = "File-Name";

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String PASSWORD_RESET_REQUEST = "Password Reset Request";
}
