package io.hpb.contract.common;

/**
 * @author ThinkPad
 *	这里全部声明常量
 */
public interface BcConstant {
	public static final String RETURN_CODE="RETURN_CODE";
	public static final String RETURN_MSG="RETURN_MSG";
	public static final String SUCCESS_CODE="000000";
	public static final String ERROR_CODE="999999";
	public static final String SUCCESS_MSG = "操作成功";
	public static final int WEB3J_TIMEOUT = 5;
	public static final String DEFAULT_BLOCKNUMBER = "-1";
	public static final String IS_LAZY = "isLazy";
	public static final String PROCCESS_ID = "proccessId";
	public static final String REDIRECT_URL = "redirectUrl";
	public static final String PROCCESS_FROM_ID = "processFromId";
	public static final String SESSION_KEY = "HPBBC_SESSION_KEY";
	public static final String SESSION_ID = "sessionId";
	public static final String RSAKEY_DEFAULT="EEEEEEEEEEEEEEEEE";
	public static final String DECODEKEY_DEFAULT="FFFFFFFFFFFFFFFF";
	public static final String TIMEOUT_MSG = "调用超时";
	public static final String EXECUTION_MSG = "执行出错";
	public static final String INTERRUPTED_MSG = "执行中断";
	public static final String _PROTOCOL = "http://";
	public static final String ICON_NODE_DEFAULT = "icon-large-chart";
	public static final String BUSERID="buserid";
	public static final String TRANSTYPE_DEFAULT="0";
	public static final String GAS_LIMIT="gasLimit";
	public static final String GAS_PRICE="gasPrice";
	public static final String TRADE_VALUE="value";
	public static final String USER_INFO = "userInfo";
	public static final String TRADE_NAME = "tradeName";
	public static final String TRADE_HASH = "tradeHash";
	public static final String PAGENUM = "pageNum";
	public static final String PAGESIZE = "pageSize";
	public static final Integer PAGESIZE_DEFAULT=10;
	public static final String NODE_INFOS = "nodeInfos";
	public static final String NODE_ID = "nodeId";
	public static final String NODE_INFO = "nodeInfo";
	public static final String ORIG_PARAM = "origParams";
	public static final String PARAMID_DEFAULT = "1";
	public static final String TRADE_STATUS_PEDDING = "0";
	public static final String TRADE_STATUS_SUCCESS = "1";
	public static final String PARAM_INFO = "paramInfo";
	public static final String RETURN_URL = "returnUrl";
	public static final String TOTAL_SIZE = "totalSize";
	public static final String DETAIL = "detail";
	public static final String REQUEST = "request";
	public static final String RESPONSE = "response";
	public static final String NONCE_ERROR_MSG = "replacement transaction underpriced";
	public static final String[] PARAM_IGNORE = new String[]{
		PROCCESS_FROM_ID,PROCCESS_ID,BUSERID,SESSION_ID,RETURN_URL
	};
	public static final String SOLC_CMD_DEFAULT="docker rm -rf solc;docker run -it --privileged=true --net=host -v /home/hpbroot/ethereum_go/contract:/contract --name solc ethereum/solc:stable";
	public static final String DEFAULT_NONCE = "0";
	public static final String XLSX = ".xlsx";
	public static final String XLS = ".xls";
	public static final String SERVICE_TYPE = "serviceType";
	public static final String SERVICE_CODE = "serviceCode";
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String DEFAULT_KEEP_TIME = "1800000";
	public static final String PROCESS_ID = "processId";
	public static final Integer PAGENUM_DEFAULT = 1;
	public static final String CACHE_NAME_CMS = "cms";//缓存名称cms  add by huangshuidan 2018-08-10
	public static final String CACHE_NAME_ARTICLE = "article";//缓存名称article
	public static final String CACHE_NAME_VERSION = "version";//缓存名称version
}
