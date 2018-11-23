var _modulus = "b456360ff2e464fe57d49a7040289d4f00e2c423da7799704208270d24fba56578a6b63d691efe9a1b2b5a93e3cc22dbb3ffbac7771b5c33785cc220cf8d8a68753d1c5e2b6de623f86cd082247b3c1038c7812c898da60d33116aafe90971f1a02797d7a409ad2bf27dae8faf40de4f4044e21320a640835365e9f3e47f97bb";
var _public_exponent = "10001";
var _private_modulus = "b456360ff2e464fe57d49a7040289d4f00e2c423da7799704208270d24fba56578a6b63d691efe9a1b2b5a93e3cc22dbb3ffbac7771b5c33785cc220cf8d8a68753d1c5e2b6de623f86cd082247b3c1038c7812c898da60d33116aafe90971f1a02797d7a409ad2bf27dae8faf40de4f4044e21320a640835365e9f3e47f97bb";
var _pri_exponent = "15fb744ceeba0728372343f8198f59d83ed9658df69c21935cd9358ebff554d750fa5c0de6f9cf9090189c1feee2a6aa75548a3b5f8f7191174391fe57392a526b3cc448a7c64cb3b7ecd9df57c6adc00ecc10927f7bd229eb93bbdcaed0d1952b0e7453a4e15b372de3ccda13d73d953357627d09839a1b8c72fa966cca1b81";
function getAesKey(len) {　　
    len = len || 32;　　
    var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678'; /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/ 　　
    var maxPos = $chars.length;　　
    var keyStr = '';　　
    for(i = 0; i < len; i++) {　　　　
        keyStr += $chars.charAt(Math.floor(Math.random() * maxPos));　　
    }　　
    return keyStr;
}
function rsaEncode(data) {
//	var modulus = "";
//	var public_exponent = "";
	var publicKey = new RSAUtils.getKeyPair(_public_exponent, "", _modulus);
	//颠倒参数的顺序，要不然后解密后会发现顺序是反的
	return RSAUtils.encryptedString(publicKey, data.split("").reverse().join("") );
}
function rsaDecode(data) {
	var privateKey = new RSAUtils.getKeyPair("", _pri_exponent, _private_modulus);
	//颠倒参数的顺序，要不然后解密后会发现顺序是反的
	var data = RSAUtils.decryptedString(privateKey, data);
	return data.split("").reverse().join("");
}