

//获取一个请求地址某一个参数的值
getQueryString = function (sURL, sParamName) {
    if (Trim(sURL) == "") {
        return undefined
    };
    var arrParam;
    var sParamValue;
    arrParam = sURL.substring(sURL.indexOf("?") + 1).split("&");
    for (var i = 0; i < arrParam.length; i++) {
        if (arrParam[i].indexOf(sParamName + "=") != -1) {
            var reg = new RegExp(sParamName + "=", "g");
            return arrParam[i].replace(reg, "")
        }
    };
    return undefined
};
//去除一个请求地址的某一个参数
removeQueryString = function (sURL, sParamName) {
    if (Trim(sURL) == "" || Trim(sParamName) == "") {
        return sURL
    };
    var arrParam;
    var sParamValue;
    if (sURL.indexOf("?") != -1) {
        arrParam = sURL.substring(sURL.indexOf("?") + 1).split("&");
        sURL = sURL.substring(0, sURL.indexOf("?"));
        var obj = new Object();
        for (var i = 0; i < arrParam.length; i++) {
            if (arrParam[i].indexOf(sParamName + "=") != 0) {
                obj[i] = arrParam[i]
            }
        };
        var newQuery = "";
        for (var key in obj) {
            newQuery = newQuery + obj[key] + "&"
        };
        if (newQuery.length != 0) {
            newQuery = newQuery.substring(0, newQuery.length - 1);
            sURL = sURL + "?" + newQuery
        }
    };
    return sURL
};



//去除首尾的空格
Trim = function (s) {
    return s.replace(/^[\s　]+|[\s　]+$/g, '')
};
removegetUrl = function (val) {
    var sURL = window.location.href;
    sURL = removeQueryString(sURL, "pages");
    if (sURL.indexOf("?") != -1) {
        if (sURL.indexOf(val) != -1) {
            window.location = removeQueryString(sURL, val)
        }
    };
    return false
};
removegetUrls = function (arr) {
    var sURL = window.location.href;
    if (sURL.indexOf("?") != -1) {
        for (var i = 0; i < arr.length; i++) {
            if (sURL.indexOf(arr[i]) != -1) {
                sURL = removeQueryString(sURL, arr[i])
            }
        };
        if (window.location.href != sURL) {
            window.location = sURL
        }
    };
    return false
};
Array.prototype.in_array = function (e) {
    for (i = 0; i < this.length; i++) {
        if (this[i] == e) {
            return true
        }
    };
    return false
}
SetCookie = function (sName, sValue, iExpireDays) {
    if (sName != "" && sName.indexOf(";") < 0 && iExpireDays >= 0) {
        date = new Date();
        dExpires = new Date(date.getTime() + (iExpireDays * 24 * 3600 * 1000));
        document.cookie = sName + "=" + escape(sValue) + "; expires=" + dExpires.toGMTString()
    }
    else {
        return false
    }
};
GetCookie = function (sName) {
    var aCookie = document.cookie.split("; ");
    for (var i = 0; i < aCookie.length; i++) {
        var aCrumb = aCookie[i].split("=");
        if (sName == aCrumb[0]) {
            return unescape(aCrumb[1]);
        }
    };
    return null
};
DelCookie = function (sName, sValue) {
    document.cookie = sName + "=" + escape(sValue) + "; expires=Fri, 31 Dec 1999 23:59:59 GMT;"
};
function StringBuilder() {
    this._buffers = [];
    this._length = 0;
    this._splitChar = arguments.length > 0 ? arguments[arguments.length - 1] : "";
    if (arguments.length > 0) {
        for (var i = 0, iLen = arguments.length - 1; i < iLen; i++) {
            this.Append(arguments[i])
        }
    }
};
StringBuilder.prototype.AppendRange = function () {
    for (var i = 0, n = arguments.length - 1; i < n; i++) {
        this._S.push(arguments[i])
    }
};
StringBuilder.prototype.append = function (str) {
    this._length += str.length;
    this._buffers[this._buffers.length] = str
};
StringBuilder.prototype.appendFormat = function () {
    if (arguments.length > 1) {
        var TString = arguments[0];
        if (arguments[1] instanceof Array) {
            for (var i = 0, iLen = arguments[1].length; i < iLen; i++) {
                var jIndex = i;
                var re = eval("/\\{" + jIndex + "\\}/g;");
                TString = TString.replace(re, arguments[1][i])
            }
        }
        else {
            for (var i = 1, iLen = arguments.length; i < iLen; i++) {
                var jIndex = i - 1;
                var re = eval("/\\{" + jIndex + "\\}/g;");
                TString = TString.replace(re, arguments[i])
            }
        };
        this.append(TString)
    }
    else if (arguments.length == 1) {
        this.append(arguments[0])
    }
};
StringBuilder.prototype.length = function () {
    if (this._splitChar.length > 0 && (!this.isEmpty())) {
        return this._length + (this._splitChar.length * (this._buffers.length - 1))
    }
    else {
        return this._length
    }
};
StringBuilder.prototype.isEmpty = function () {
    return this._buffers.length <= 0
};
StringBuilder.prototype.clear = function () {
    return this._buffers.length = 0
};
StringBuilder.prototype.toString = function () {
    if (arguments.length == 1) {
        return this._buffers.join(arguments[1])
    }
    else {
        return this._buffers.join(this._splitChar)
    }
};


(function ($) {
    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {return unescape(r[2]);}
        return null
    }
    $.getUrlParam2 = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {return unescape(r[2].replace(new RegExp(/(%25)/g),'%').replace(new RegExp(/(%25)/g),'%'));}
        return null
    }
})(jQuery);
(function () {
    var imgs = document.images;
    for (var i = 0; i < imgs.length; i++) {
        imgs[i].onerror = function () {
            this.src = "../upload-file/images/product/unknow.gif"
        }
    }
})();
function subLen(str, len) {
    var strlen = 0;
    var s = "";
    for (var i = 0; i < str.length; i++) {
        if (str.charCodeAt(i) > 128) {
            strlen += 2
        }
        else {
            strlen++
        };
        s += str.charAt(i);
        if (strlen >= len) {
            return s
        }
    };
    return s
}
function getRootPath() {
    var strFullPath = window.document.location.href;
    var strPath = window.document.location.pathname;
    var pos = strFullPath.indexOf(strPath);
    var prePath = strFullPath.substring(0, pos);
    var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
    return (prePath + postPath)
};
function _Shop_Car_Replace(uJsons, uJson) {
    for (var i = 0; i < uJsons.length; i++) {
        if (uJsons[i].productID == uJson.productID && typeof (eval(uJsons[i]))=="object") {
            uJsons.splice(i, 1);
            break
        }
    };
    return uJsons
}
function userBrowse(cookie, uJson, num) {
    var obj = $.cookies.get(cookie);
    var uArray = [];
    if (obj === null) {
        uArray.push(uJson)
    }
    else {
        uArray = _Shop_Car_Replace(obj, uJson);
        if (uArray.length < num) {
            uArray.push(uJson)
        }
        else {
            uArray.splice(0, 1);
            uArray.push(uJson)
        }
    };
    $.cookies.set(cookie, uArray, {
        hoursToLive: 3
    })
}
function updateCookie(cookie, uJson) {
    var uJsons = $.cookies.get(cookie);
    var tArray = [];
    if (uJsons === null) {
        tArray.push(uJson);
    } else {
        var that = 0;
        $.each(uJsons, function (i, _Json) {
            if (_Json.productID == uJson.productID) {
                that = _Json.addCount + uJson.addCount;
                _Json.addCount = that > 0 ? that : 0;
                return false;
            }
        });
        tArray = uJsons.concat();
    }
    $.cookies.set(cookie, tArray, {
        hoursToLive: 3
    });
}
function userShopCar(cookie, uJson, num) {
    if (uJson.productID == 0) {
        return true
    }
    else {
        var uJsons = $.cookies.get(cookie);
        var uArray = [];
        if (uJsons === null) {
            uArray.push(uJson)
        }
        else {
            var bool = true;
            for (var i = 0; i < uJsons.length; i++) {
                if (uJsons[i].productID == uJson.productID && typeof (eval(uJsons[i])) == "object") {
                    uJsons[i].addCount = uJsons[i].addCount + uJson.addCount;
                    bool = false;
                    break
                }
            };
            uArray = uJsons.concat();
            if (bool && uArray.length < num) {
                uArray.push(uJson)
            }
        };
        $.cookies.set(cookie, uArray, {
            hoursToLive: 3
        });
        return uArray.length == 0
    }
}
function removeCars(cookie, productID) {
    var rJson = {
        "bool": false, "rprice": 0, "rcount": 0
    };
    var uJsons = $.cookies.get(cookie);
    for (var i = 0; i < uJsons.length; i++) {
        if (uJsons[i].productID == productID && typeof (eval(uJsons[i])) == "object") {
            rJson = {
                "bool": true, "rprice": parseFloat(uJsons[i].productprice), "rcount": parseInt(uJsons[i].addCount)
            };
            uJsons.splice(i, 1);
            $.cookies.set(cookie, uJsons, {
                hoursToLive: 3
            });
            break
        }
    };
    return rJson
}
function removeiCars(cookie, productID) {
    var rJson = {
        "bool": false, "rprice": 0, "rcount": 0
    };
    var uJsons = $.cookies.get(cookie);
    for (var i = 0; i < uJsons.length; i++) {
        if (uJsons[i].productID == productID && typeof (eval(uJsons[i])) == "object") {
            rJson = {
                "bool": true, "rprice": parseFloat(uJsons[i].Changeintegral), "rcount": parseInt(uJsons[i].addCount)
            };
            uJsons.splice(i, 1);
            $.cookies.set(cookie, uJsons, {
                hoursToLive: 3
            });
            break
        }
    };
    return rJson
}
function removecardCars(cookie, productID) {
    var rJson = {
        "bool": false, "rprice": 0, "rcount": 0
    };
    var uJsons = $.cookies.get(cookie);
    for (var i = 0; i < uJsons.length; i++) {
        if (uJsons[i].productID == productID && typeof (eval(uJsons[i])) == "object") {
            rJson = {
                "bool": true, "rprice": parseFloat(uJsons[i].productprice), "rcount": parseInt(uJsons[i].addCount)
            };
            uJsons.splice(i, 1);
            $.cookies.set(cookie, uJsons, {
                hoursToLive: 3
            });
            break
        }
    };
    return rJson
}
function GetImage(productCode, _default, _callback) {
    $.get("../ajaxTodo/gettingImages.aspx?___" + Math.random(), {
        "productCode": productCode, "_default": _default
    }
	, _callback);
}

function addJsoncookies(cookie, uJson) {
    var uJsons = $.cookies.get(cookie);
    var uArray = [];
    if (uJsons === null || uJsons.length == 0) {
        uArray.push(uJson);
    } else {
        var bool = true;
        uArray = uJsons.concat();
        for (var i = 0; i < uArray.length; i++) {
            if (parseInt(uArray[i].productId) == parseInt(uJson.productId) && typeof (eval(uArray[i])) == "object") {
                bool = false;
                break;
            }
        }
        if (bool) {
            uArray.push(uJson);
        }
    }
    $.cookies.set(cookie, uArray, { hoursToLive: 72 });
    if (uJsons === null && uArray.length > 0) {
        return true;
    } else {
        return uJsons.length <= uArray.length;
    }
}


function isValidPhone(s) {
    var patrn = /^\d{8,15}$/;
    if (!patrn.exec(s)) return false;
    return true
};
function isValidMail(s) {
    var patrn = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
    if (!patrn.exec(s)) return false;
    return true
};
function isValidPwd(s) {
    var patrn = /^([_]|[a-zA-Z0-9]){6,18}$/;
    if (!patrn.exec(s)) return false;
    return true
};
function isValidUser(s) {
    var patrn = /^[\u4e00-\u9fa5A-Za-z0-9-_]{2,12}$/;
    if (!patrn.exec(s)) return false;
    return true
};
function isValidVcode(s) {
    var patrn = /^([A-Za-z0-9]){4}$/;
    if (!patrn.exec(s)) return false;
    return true
};

