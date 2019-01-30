/**
 * Created by Administrator on 2017/8/22.
 */
Date.prototype.format || (Date.prototype.format = function(format) {
        var o = {
            "M+": this.getMonth() + 1, //month
            "d+": this.getDate(), //day
            "h+": this.getHours(), //hour
            "m+": this.getMinutes(), //minute
            "s+": this.getSeconds(), //second
            "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
            "S": this.getMilliseconds() //millisecond
        };
        if (/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (var k in o) {
            if (new RegExp("(" + k + ")").test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
})

       var __sysTime = null;
        function setSysTime(systime) {
            if (systime) {
                __sysTime = systime;
            }
        }
        function getSysTime() {
            return __sysTime;
        }
        function formatDate(str) {
//          return new Date(str).format("yy/MM/dd");
            return new Date(str).format("MM/dd");
        }

        function string2Date(str) {
            str = str.replace(/-/g, "/");
            return new Date(str);
        }




        function confirm(strMsg, fnYes, fnNo) {
            var flag = window.confirm(strMsg);
            if (flag) {
                fnYes && fnYes();
            } else {
                fnNo && fnNo();
            }
        }
        
        function now() {
            return +new Date();
        }
        
        function getdate() {



        }
        
        function getDataZoom(len,date) {
            var zoomSize = 1/len *100;
            var startDate,endDate;
            if(date-4 <= 0){
                startDate = 0;
                endDate = 6 * zoomSize;
            }else if(date + 4 >= len){
                startDate =  (len - 6)*zoomSize;
                endDate = len*zoomSize;
            }else{
                startDate  = (date - 3) *zoomSize ;
                endDate = (date + 3)*zoomSize;
            }


            return {
                startZoom: startDate,
                endZoom:endDate
            }

        }


function showChartData(jsonstr,nametips,minval,maxval,showtype,dot){
        var myChart = echarts.init(document.getElementById('echarts'));
        myChart.showLoading();
        setSysTime(jsonstr.systime);
        var tody = new Date(getSysTime()).format("yyyy/MM/dd");
        var list = jsonstr.list;
        //var dataZoom = getDataZoom(list.length,new Date(getSysTime()).getDate());
        var dataZoom = getDataZoom(list.length,list.length);
        var xValue = [];
        var sData = [];
        var startZoom = dataZoom.startZoom;
        var endZoom = dataZoom.endZoom;
        list.forEach(function (item) {
            xValue.push(formatDate(item.date));
            sData.push(Number(item.value).toFixed(dot));

        });

        var option = {
            color: ['#3398DB'],
            backgroundColor: '#33A3D4',
            tooltip: {
                trigger: 'axis',
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'line'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            calculable: true,
            dataZoom: [
                {
                    type: 'inside',
                    start: startZoom,
                    end: endZoom
                }
            ],
            grid: {
                left: 0,
                right: 0,
                top: 10,
                bottom: 10,
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    data: xValue,
                    axisLabel: {
                        textStyle: {
                            color: '#fff',
                        }
                    },
                    axisLine:{
                        lineStyle:{
                            color:'rgba(0, 0, 0, 0.1)'
                        }
                    },
                    axisTick: {
                        lineStyle:{
                            color:'rgba(0, 0, 0, 0.1)'
                        }
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    axisLine: {
                        show: false

                    },
                    
                    markLine:{

                    },
                    axisLabel: {
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    min:minval,
                   	max:maxval,
//                 	interval:1,
                    axisLine: {
                        lineStyle: {
                            color:'#888',
                            opacity:0
                        }
                    }
                 
                }
            ],
            series: [
                {
                    name: nametips,
                    type: showtype,
                    lineStyle:{
                    normal:{
                        color:'#FFFF33'
                    }
                   },
                    markLine: {
                        itemStyle: {
                            normal: {lineStyle: {type: 'solid', color: 'rgba(0, 0, 0, 0.1)'},
                                label: {show: true, position: 'left'}}
                        }
                    },
                    barWidth: '60%',
                    data: sData
                }
            ]
        };
        myChart.setOption(option);
        myChart.hideLoading();
        
        

     }



        
function showChartTwoData(jsonstr,nametips1,nametips2,minval,maxval,showtype,dot){
	   var resarr=[];
       var myChart = echarts.init(document.getElementById('echarts'));
        myChart.showLoading();
        setSysTime(jsonstr.systime);
        var tody = new Date(getSysTime()).format("yyyy/MM/dd");
        var list1 = jsonstr.list1;
        var list2 = jsonstr.list2;
        //var dataZoom = getDataZoom(list.length,new Date(getSysTime()).getDate());
        var dataZoom = getDataZoom(list1.length,list1.length);
        
        var xValue = [];
        var sData1 = [];
        var sData2 = [];
        var startZoom = dataZoom.startZoom;
        var endZoom = dataZoom.endZoom;
        list1.forEach(function (item) {
            xValue.push(formatDate(item.date));
            sData1.push(Number(item.value).toFixed(dot));
        });
        
        list2.forEach(function (item) {
            sData2.push(Number(item.value).toFixed(dot));
        });
        
        resarr.push(xValue[xValue.length-1]);
        resarr.push(sData1[sData1.length-1]);
        resarr.push(sData2[sData2.length-1]);
        
        var option = {
            color: ['#3398DB'],
            backgroundColor: '#33A3D4',
            tooltip: {
                trigger: 'axis',
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'line'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            calculable: true,
            dataZoom: [
                {
                    type: 'inside',
                    start: startZoom,
                    end: endZoom
                }
            ],
            grid: {
                left: 0,
                right: 0,
                top: 10,
                bottom: 10,
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    data: xValue,
                    axisLabel: {
                        textStyle: {
                            color: '#fff',
                        }
                    },
                    axisLine:{
                        lineStyle:{
                            color:'rgba(0, 0, 0, 0.1)'
                        }
                    },
                    axisTick: {
                        lineStyle:{
                            color:'rgba(0, 0, 0, 0.1)'
                        }
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    axisLine: {
                        show: false

                    },
                    markLine:{

                    },
                    axisLabel: {
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    min:minval,
                   	max:maxval,
                    axisTick: {show: false}
                }
            ],
            series: [
                {
                    name: nametips1,
                    type: showtype,
                    lineStyle:{
                    normal:{
                        color:'#FFFF33'
                    }
                   },
                    markLine: {
                        itemStyle: {
                            normal: {lineStyle: {type: 'solid', color: 'rgba(0, 0, 0, 0.1)'},
                                label: {show: true, position: 'left'}}
                        }
                    },
                    barWidth: '60%',
                    data: sData1
                },
                {
                    name: nametips2,
                    type: showtype,
                    lineStyle:{
                    normal:{
                        color:'#00FF33'
                    }
                   },
                    markLine: {
                        itemStyle: {
                            normal: {lineStyle: {type: 'solid', color: 'rgba(0, 0, 0, 0.1)'},
                                label: {show: true, position: 'left'}}
                        }
                    },
                    barWidth: '60%',
                    data: sData2
                }
            ]
            
            
            
        };
        myChart.setOption(option);
        myChart.hideLoading();
        
        
        return resarr;
        

     }






