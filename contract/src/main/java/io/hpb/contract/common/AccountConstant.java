package io.hpb.contract.common;

import java.math.BigInteger;

/**
 * @author ThinkPad
 *	这里全部声明常量
 */
public interface AccountConstant {
	public static final String PASSWORD_DEFAULT="12345678";
	public static final String ERROFORMAT_MSG = "输入账户格式不对";
	public static final String ICON_ACCOUNT_DEFAULT = "icon-large-chart";
	public static final String TO_ACCOUNTID="toAccountId";
	public static final String AUTHORIZE_TO="authorizedTo";
	public static final String DATAORIGIN="dataOrigin";
	public static final String TAKEEFFECTTIME="takEeffectTime";
	public static final String LOSEEFFICACYTIME="loseEfficacyTime";
	public static final String ACCOUNT_ID = "accountId";
	public static final String PASS_WORD = "password";
	public static final String FROM_ACCOUNT_ID = "fromAccountId";
	public static final String ACCOUNT_NAME = "accountName";
	public static final String ACCOUNT_DESC = "accountDesc";
	public static final String USER_NAME = "userName";
	public static final String ACCOUNT_DEFAULT_ID = "-1";
	public static final String OWNER_DEFAULT_STATUS = "1";
	public static final String THIRD_DEFAULT_ID = "-1";
	public static final String ACCOUNT_BASE_INFOS = "accountBaseInfos";
	public static final String DOWNLOAD_FILE_INFO = "downloadFileInfo";
	public static final String UPLOAD_FILE_INFO = "uploadFileInfo";
	public static final String IS_DOWNLOAD_FILE = "isDownloadFile";
	public static final String CONTENT_TYPE = "content-type";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String ACOUNT_MAIN = "1";
	public static final String ACCOUNT_PASSWORD_NOT_ACCORD = "ACCOUNT_PASSWORD_NOT_ACCORD";
	public static final BigInteger ACCOUNT_UNLOCK_DURATION = BigInteger.valueOf(60);
	public static final String ACCOUNT_BASE_INFO = "accountBaseInfo";
	public static final String BALANCE_VALUE = "balanceValue";
}
