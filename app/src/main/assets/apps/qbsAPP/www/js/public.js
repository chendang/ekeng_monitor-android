 var GetLength = function (str) {
        ///<summary>获得字符串实际长度，中文2，英文1</summary>
        ///<param name="str">要获得长度的字符串</param>
        var realLength = 0, len = str.length, charCode = -1;
        for (var i = 0; i < len; i++) {
            charCode = str.charCodeAt(i);
            if (charCode >= 0 && charCode <= 128) realLength += 1;
            else realLength += 2;
        }
        return realLength;
    };

    //js截取字符串，中英文都能用  
    //如果给定的字符串大于指定长度，截取指定长度返回，否者返回源字符串。  
    //字符串，长度  

    /** 
     * js截取字符串，中英文都能用 
     * @param str：需要截取的字符串 
     * @param len: 需要截取的长度 
     */
    function cutstr(str, len) {
        var str_length = 0;
        var str_len = 0;
        str_cut = new String();
        str_len = str.length;
        for (var i = 0; i < str_len; i++) {
            a = str.charAt(i);
            str_length++;
            if (escape(a).length > 4) {
                //中文字符的长度经编码之后大于4  
                str_length++;
            }
            str_cut = str_cut.concat(a);
            if (str_length >= len) {
                str_cut = str_cut.concat("...");
                return str_cut;
            }
        }
        //如果给定字符串小于指定长度，则返回源字符串；  
        if (str_length < len) {
            return str;
        }
    }



var each =  function(object, callback){
  var type = (function(){
          switch (object.constructor){
            case Object:
                return 'Object';
                break;
            case Array:
                return 'Array';
                break;
            case NodeList:
                return 'NodeList';
                break;
            default:
                return 'null';
                break;
        }
    })();
    // 为数组或类数组时, 返回: index, value
    if(type === 'Array' || type === 'NodeList'){
        // 由于存在类数组NodeList, 所以不能直接调用every方法
        [].every.call(object, function(v, i){
            return callback.call(v, i, v) === false ? false : true;
        });
    }
    // 为对象格式时,返回:key, value
    else if(type === 'Object'){
        for(var i in object){
            if(callback.call(object[i], i, object[i]) === false){
                break;
            }
        }
    }
}

function jsGetAge(strBirthday) {
    var returnAge;
    var strBirthdayArr = strBirthday.split("-");
    var birthYear = strBirthdayArr[0];
    var birthMonth = strBirthdayArr[1];
    var birthDay = strBirthdayArr[2];

    d = new Date();
    var nowYear = d.getFullYear();
    var nowMonth = d.getMonth() + 1;
    var nowDay = d.getDate();

    if (nowYear == birthYear) {
        returnAge = 0; //同年 则为0岁
    }
    else {
        var ageDiff = nowYear - birthYear; //年之差
        if (ageDiff > 0) {
            if (nowMonth == birthMonth) {
                var dayDiff = nowDay - birthDay; //日之差
                if (dayDiff < 0) {
                    returnAge = ageDiff - 1;
                }
                else {
                    returnAge = ageDiff;
                }
            }
            else {
                var monthDiff = nowMonth - birthMonth; //月之差
                if (monthDiff < 0) {
                    returnAge = ageDiff - 1;
                }
                else {
                    returnAge = ageDiff;
                }
            }
        }
        else {
            returnAge = -1; //返回-1 表示出生日期输入错误 晚于今天
        }
    }

    return returnAge; //返回周岁年龄

}

function add0(num) {
return num <= 9 ? '0' + num : num;
}
function getDateTimeStamp(dateStr){
 return Date.parse(dateStr.replace(/-/gi,"/"));
}

  function getDateDiff(dateTimeStamp){
	var minute = 1000 * 60;
	var hour = minute * 60;
	var day = hour * 24;
	var halfamonth = day * 15;
	var month = day * 30;
	var now = new Date().getTime();
	var diffValue = now - dateTimeStamp;
	if(diffValue < 0){return;}
	var monthC =diffValue/month;
	var weekC =diffValue/(7*day);
	var dayC =diffValue/day;
	var hourC =diffValue/hour;
	var minC =diffValue/minute;
	if(monthC>=1){
		result="" + parseInt(monthC) + "月前";
	}
	else if(weekC>=1){
		result="" + parseInt(weekC) + "周前";
	}
	else if(dayC>=1){
		result=""+ parseInt(dayC) +"天前";
	}
	else if(hourC>=1){
		result=""+ parseInt(hourC) +"小时前";
	}
	else if(minC>=1){
		result=""+ parseInt(minC) +"分钟前";
	}else
	result="刚刚";
	return result;
}
  
  
  function dateConvert(dateParms){ 
    // 对传入的时间参数进行判断
    if(dateParms instanceof Date){
        var datetime=dateParms;
    }
    //判断是否为字符串
    if((typeof dateParms=="string")&&dateParms.constructor==String){
         
        //将字符串日期转换为日期格式
        var datetime= new Date(Date.parse(dateParms.replace(/-/g,   "/")));
     
    }
     
    //获取年月日时分秒
     var year = datetime.getFullYear();
     var month = datetime.getMonth()+1; 
     var date = datetime.getDate(); 
     var hour = datetime.getHours(); 
     var minutes = datetime.getMinutes(); 
     var second = datetime.getSeconds();
     
     //月，日，时，分，秒 小于10时，补0
     if(month<10){
      month = "0" + month;
     }
     if(date<10){
      date = "0" + date;
     }
     if(hour <10){
      hour = "0" + hour;
     }
     if(minutes <10){
      minutes = "0" + minutes;
     }
     if(second <10){
      second = "0" + second ;
     }
      
     //拼接日期格式【例如：yyyymmdd】
     var time = year+month+date; 
      
     //或者：其他格式等
     //var time = year+"年"+month+"月"+date+"日"+hour+":"+minutes+":"+second; 
      
     //返回处理结果
     return time;
    }
  
  
  function GetDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    return y+"-"+m+"-"+d;
}
  
  
  function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes()
            + seperator2 + date.getSeconds();
    return currentdate;
}

function layer(t, num) {
    //var t=000;
    pt = $('<div class="body_back"></div>');
    pt2 = $('<div class="pop-up"><p>' + t + '</p> </div>');
    $('body').append(pt);
    $('body').append(pt2);

    setTimeout(function () {
        $('.body_back').remove();
        $('.pop-up').remove();
    }, num);
}

//获取url中的参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值

}


function GetApiDomain() {
    var data = "http://HealthBack.ekeng365.com/";
    return data;
}

function GetMvcApiDomain() {
    var data = "http://healthapi.ekeng365.com/";
    return data;
}


function GetHttpApi() {
    var data = "http://webapi.ekeng365.com/";
    return data;
}

function GetUploadApi() {
    var data = "http://upload.kzjk360.com/";
    return data;
}

function GetServerUrl() {
    var data = "http://180.97.81.235:9000/";
    return data;
}









