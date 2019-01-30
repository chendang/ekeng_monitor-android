var colors = [
		['#18A80B', '#C6C6C6'],
		['#F7EA20', '#C6C6C6'],
		['#FF3600', '#C6C6C6'],
		['#C6C6C6', '#C6C6C6'],
		['#F4BCBF', '#D43A43']
	],
	circles = [];

function getColor_BPHigh(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 110 && vx <= 120) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx > 120 && vx <= 140) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	
	if(vx >= 90 && vx < 110) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}

	if(vx < 90 || vx > 140) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 90){
		valLevel="Low";
	}
	
	if(vx > 140){
		valLevel="High";
	}
	
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_BPLow(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 70 && vx <= 80) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx > 80 && vx <= 90) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	
	if(vx >= 60 && vx < 70) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}

	if(vx < 60 || vx > 90) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
    if(vx < 60){
		valLevel="Low";
	}
	
	if(vx > 90){
		valLevel="High";
	}
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_BPHL(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 30 && vx <= 50) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	
//	if(vx > 56 && vx <= 60) {
//		colorx = colors[1];
//		lightColor = "Yellow";
//		valLevel="HighSide";
//	}
//	
//	if(vx >= 20 && vx < 30) {
//		colorx = colors[1];
//		lightColor = "Yellow";
//		valLevel="LowSide";
//	}

	if(vx > 50 || vx < 30) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 30){
		valLevel="Low";
	}
	
	if(vx > 50){
		valLevel="High";
	}
	
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}



function getColor_BPPR(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 70 && vx <= 80) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx >= 60 && vx < 70) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}
	if(vx > 80 && vx <= 90) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	if(vx < 60 || vx > 90) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 60){
		valLevel="Low";
	}
	
	if(vx > 90){
		valLevel="High";
	}
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_WAIST(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 60 && vx <= 100) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}

	if(vx < 60 || vx > 100) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 60){
		valLevel="Low";
	}
	
	if(vx > 100){
		valLevel="High";
	}
	
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_SpO2(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 97 && vx <= 100) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}

	if(vx >= 93 && vx < 97) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}

	if(vx < 93) {
		colorx = colors[2];
		lightColor = "Red";
		valLevel="Low";
	}
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_GLU(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 4.8 && vx <= 5.2) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx > 5.2 && vx <= 6.1) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	
	if(vx >= 3.9 && vx < 4.8) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}

	if(vx < 3.9 || vx > 6.1) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 3.9){
		valLevel="Low";
	}
	
	if(vx > 6.1){
		valLevel="High";
	}
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_TEMP(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 36.7 && vx <= 37.1) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx > 37.1 && vx <= 37.5) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	if(vx >= 36 && vx < 36.7) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}
	if(vx < 36 || vx > 37.5) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 36){
		valLevel="Low";
	}
	
	if(vx > 37.5){
		valLevel="High";
	}
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_BMI(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 18.5 && vx <= 24) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx > 24 && vx <= 28) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	if(vx > 28 || vx < 18.5) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 18.5){
		valLevel="Low";
	}
	
	if(vx > 28){
		valLevel="High";
	}
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_BFPX(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 17 && vx <= 23) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx > 23 && vx <= 25) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}

	if(vx >= 14 && vx < 17) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}

	if(vx < 14 || vx > 25) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 14){
		valLevel="Low";
	}
	
	if(vx > 25){
		valLevel="High";
	}
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_BFPY(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 20 && vx <= 27) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}


	if(vx >= 17 && vx < 20) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}

	if(vx > 27 && vx <= 30) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	

	if(vx < 17 || vx > 30) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	
    if(vx < 17){
		valLevel="Low";
	}
	
	if(vx > 30){
		valLevel="High";
	}
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_CHOL(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 4 && vx <= 4.5) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx >= 3.1 && vx < 4) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}

	if(vx > 4.5 && vx <= 5.17) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}

	if(vx > 5.17 || vx < 3.1) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	
	if(vx < 3.1){
		valLevel="Low";
	}
	
	if(vx > 5.17){
		valLevel="High";
	}
	
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_TG(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 1 && vx <= 1.3) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx >= 0.56 && vx < 1) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}
	
	if(vx > 1.3 && vx <= 1.7) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}


	if(vx < 0.56 || vx > 1.7) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 0.56){
		valLevel="Low";
	}
	
	if(vx > 1.7){
		valLevel="High";
	}
	
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_UA(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 90 && vx <= 420) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}

	if(vx < 90 && vx > 0) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}
	if(vx > 420) {
		colorx = colors[2];
		lightColor = "Red";
		valLevel="High";
	}
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_UPH(vx) {
var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 6.5 && vx <= 7) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx >= 5.5 && vx < 6.5) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}
	if(vx > 7 && vx < 8) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	if(vx < 5.5 || vx >= 8) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 5.5){
		valLevel="Low";
	}
	
	if(vx >= 8){
		valLevel="High";
	}
	
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_SG(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 1.005 && vx <= 1.015) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	
	if(vx > 1.015 && vx <= 1.025) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}

	if(vx < 1.005 || vx > 1.025) {
		colorx = colors[2];
		lightColor = "Red";
	}
	
	if(vx < 1.005){
		valLevel="Low";
	}
	
	if(vx > 1.025){
		valLevel="High";
	}
	
	
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_SLEEP_TIME(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 7 && vx <= 9) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}
	if(vx >= 5 && vx <= 7) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}
	if(vx > 9) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="HighSide";
	}
	if(vx > 0 && vx < 5) {
		colorx = colors[2];
		lightColor = "Red";
		valLevel="Low";
	}
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getColor_STEP_NUMBER(vx) {
	var op = new Object();
	var colorx;
	var colorLight;
	var valLevel;
	if(vx >= 6000) {
		colorx = colors[0];
		lightColor = "Green";
		valLevel="Balance";
	}

	if(vx >= 4000 && vx < 6000) {
		colorx = colors[1];
		lightColor = "Yellow";
		valLevel="LowSide";
	}

	if(vx < 4000) {
		colorx = colors[2];
		lightColor = "Red";
		valLevel="Low";
	}
	if(vx <= 0) {
		colorx = colors[3];
		lightColor = "White";
		valLevel="No";
	}
	op.colorVal=colorx;
	op.lightColor=lightColor;
	op.valLevel=valLevel;
	return op;
}

function getCircleMaxValue(iType) {
	var maxVal = 0;
	switch(iType) {
		case 'BPHigh':
			maxVal = 300;

			break;
		case 'BPLow':
			maxVal = 200;

			break;
		case 'BPPR':
			maxVal = 300;
			break;
		case 'WAIST':
			maxVal = 1000;
			break;
		case 'SpO2':
			maxVal = 1000;
			break;
		case 'GLU':
			maxVal = 50;
			break;
		case 'TEMP':
			maxVal = 50;
			break;
		case 'BMI':
			maxVal = 50;
			break;
		case 'BFPX':
			maxVal = 80;
			break;
		case 'BFPY':
			maxVal = 80;
			break;
		case 'CHOL':
			maxVal = 30;
			break;
		case 'TG':
			maxVal = 30;
			break;
		case 'UA':
			maxVal = 600;
			break;
		case 'U_PH':
			maxVal = 30;
			break;
		case 'SG':
			maxVal = 10;
			break;
		case 'SLEEP_TIME':
			maxVal = 24;
			break;
		case 'STEP_NUMBER':
			maxVal = 10000;
			break;
	}

	return maxVal;

}

function getCircle(childx, valx, colorx, maxVal) {
	circles.push(Circles.create({
		id: childx.id,
		value: valx,
		radius: 30,
		width: 5,
		maxValue: maxVal,
		colors: colorx
	}));
}




function getChartData(iType) {
	var op = new Object();
    var ItemType="";
    var ItemUnit="";
    var minval=0;
    var maxval=100;
    var dot=2;
    var nametips="";
    var tips="";
	switch(iType) {
		case 'CHOL':
			ItemType="CHOL";
            ItemUnit="mmol/L";
            minval=0;
            maxval=10;
            tips="静脉血液中所有脂蛋白所含胆固醇的总和，正常参考范围是≥3.1且<5.7mmol/L，理想状况下是≥4.0且<5.2mmol/L，现代医学常用来指示动脉粥样硬化的动向，特别是反映冠心病风险。";
			break;
		case 'TG':
			ItemType="TG";
            ItemUnit="mmol/L";
            minval=0.5;
            maxval=5.65;
            tips="静脉血液中甘油和3个脂肪酸的结合物，正常参考范围是≥0.56且<1.7mmol/L，理想状况下是≥1且<1.3mmol/L，理想状况下是≥1且<1.3mmol/L，现代医学常用来指示能量储存与供给状况，特别是反映肥胖风险。";
			break;
		case 'BFP':
			 ItemType="BFP";
            ItemUnit="%";
            minval=5;
            maxval=45;
            dot=0;
            tips="人体内脂肪与体重的百分比，正常参考范围是 男士≥10且<20% ， 女士≥17且<30%，现代医学常用来指示人体内脂肪含量的胖与瘦，特别是反映肥胖风险。";
			break;
		case 'BMI':
			ItemType="BMI";
            ItemUnit="%";
            minval=12;
            maxval=40;
            dot=2;
            tips="用体重公斤数除以身高米数平方的比值，正常参考范围是≥18.5且<24%，理想状况下是22%左右，≥28%即为肥胖，现代医学常用来指示一定身高下不同体重的胖与瘦，特别是反映超重风险。";
			break;
		case 'TEMP':
			ItemType="TEMP";
            ItemUnit="℃";
            minval=35;
            maxval=41;
            dot=1;
            tips="人体内能量物质转化为热能的产物在体表的保持，耳温正常参考范围是 ≥36且<37.5℃ ，理想参考范围是 ≥36.7且<37.1℃，现代医学常用来指示体温调节中枢平衡产热和散热的新陈代谢状况，特别是反映内分泌紊乱风险。";
			break;
		case 'GLU':
			ItemType="GLU";
            ItemUnit="mmol/L";
            minval=0;
            maxval=30;
            dot=2;
            tips="静脉血液中的葡萄糖，是人体能量动力供应的主要来源，正常参考范围是≥3.9且<6.1mmol/L，理想状况下是≥4.8且<5.2mmol/L，现代医学常用来指示胰腺、肝脏等器官对能量平衡的新陈代谢，特别是反映糖尿病风险。";
			break;
		case 'SpO2':
			ItemType="SpO2";
            ItemUnit="%";
            minval=70;
            maxval=100;
            dot=0;
            tips="动脉血液中氧合血红蛋白相对于总血红蛋白的浓度比，正常参考范围是≥94且≤99%，理想状况下是≥97且≤99%，现代医学常用来指示肺脏氧合能力和血液携氧状况，特别是反映贫血和慢阻肺风险。";
			break;
		case 'WAIST':
			ItemType="WAIST";
            ItemUnit="CM";
            minval=0;
            maxval=200;
            dot=2;
            tips="经肚脐点的腰部水平围长，正常参考范围是 男士≥70且<90cm ， 女士≥70且<85cm，现代医学常用来指示人体脂肪总量和腹部脂肪分布状况，特别是反映肥胖风险。";
			break;
		case 'BPPR':
			ItemType="BPPR";
            ItemUnit="次/分";
            minval=40;
            maxval=180;
            dot=0;
            tips="动脉搏动的频率，一般与心率一致，正常参考范围是≥60且≤100bpm，理想状况下是≥70且≤80bpm，现代医学常用来指示体循环系统血管网对心跳泵血的传导功效以反馈心脏泵血功能状况，特别是心房颤动风险时格外重要。";
			break;
		case 'UA':
			ItemType="UA";
            ItemUnit="umol/L";
            minval=100;
            maxval=600;
            dot=2;
            tips="遗传物质中的嘌呤的代谢废物在血液中的含量，正常参考范围是 ≥90且<420mmol/L，现代医学常用来指示肾脏的滤过和重吸收功能，特别是反映痛风风险。";
			break;
		case 'U_PH':
			ItemType="U_PH";
            ItemUnit="PH";
            minval=5;
            maxval=8;
            dot=2;
            tips="尿液的酸碱度，正常参考范围是 >5且≤8，理想参考范围是 >6.5且≤7，现代医学常用来指示肾脏调节体液酸碱平衡的能力，特别是反映肿瘤免疫力风险。";
			break;
		case 'SG':
			ItemType="SG";
            ItemUnit="umol/L";
            minval=1;
            maxval=1.04;
            dot=2;
            tips="尿液与纯水的重量之比，正常参考范围是1.005≤尿比重≤1.025，在非水代谢紊乱情况下，高比重可见于脱水、蛋白尿、糖尿、急性肾炎、高热等。";
			break;
		case 'SLEEP_TIME':
			ItemType="SLEEP_TIME";
            ItemUnit="小时";
            minval=0;
            maxval=20;
            dot=1;
			break;
		case 'STEP_NUMBER':
			ItemType="STEP_NUMBER";
            ItemUnit="步";
            minval=0;
            maxval=15000;
            dot=0;
			break;
		case 'BPHigh':
			ItemType="BPHigh";
            ItemUnit="mmHg";
            minval=50;
            maxval=250;
            nametips="收缩压";
            tips="当左心室收缩，血液从心室流入动脉时对动脉壁的压力,正常参考范围是≥90且<140mmHg，理想状况下是≥110且<120mmHg,现代医学常用来指示血管对心脏泵血的顺应性，特别是反映高血压风险。";
            dot=0;
			break;
		case 'BPLow':
			ItemType="BPLow";
            ItemUnit="mmHg";
            minval=50;
            maxval=250;
            nametips="舒张压";
            tips="当左心室舒张，血液从血管回流到心脏时对动脉壁的压力，正常参考范围是≥60且<90mmHg，现代医学常用来指示血管回缩的弹性，特别是反映高血压风险。";
            dot=0;
			break;
		case 'BPHL':
			ItemType="BPHL";
            ItemUnit="mmHg";
            minval=0;
            maxval=200;
            dot=0;
            tips="脉压差正常参考范围是≥20且<=60mmHg，现代医学常用来指示血管回缩的弹性，特别是反映脑中风，冠心病风险。";
			break;
	}

	op.ItemType=ItemType;
	op.ItemUnit=ItemUnit;
	op.minval=minval;
	op.maxval=maxval;
	op.nametips=nametips;
	op.tips=tips;
	op.dot=dot;
	return op;

}






