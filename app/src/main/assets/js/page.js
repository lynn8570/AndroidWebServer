var nav = ['首页', 'WLAN设置', '流量管理', '固件升级', '设备信息', '系统设置'];
var swiper = new Swiper('.swiper-container', {
    initialSlide: 0,//首页
    pagination: '.swiper-pagination',
    paginationClickable: true,
    speed: 500,
    uniqueNavElements: false,
    spaceBetween: 0,
    observer: true,//修改swiper自己或子元素时，自动初始化swiper
    observeParents: true,//修改swiper的父元素时，自动初始化swiper
    momentumRatio:0,
    longSwipesRatio: 0.2,
    followFinger:false,
    paginationBulletRender: function(index, className) {
        return '<li class="' + className + '">' + nav[index] + '</li>';
    }
});
fll = window.location.search.substr(1).split("=")[1];

if(window.location.pathname.indexOf('submit')!=-1){
	swiper.slideTo(1, 0, false);
}else if(fll == 'true') {
    swiper.slideTo(3, 0, false);
}else if(fll == 'one'){
    swiper.slideTo(0, 0, false);
}else if(fll == 'two'){
    swiper.slideTo(1, 0, false);
}else if(fll == 'three'){
    swiper.slideTo(2, 0, false);
}else if(fll == 'four'){
    swiper.slideTo(3, 0, false);
}else if(fll == 'five'){
    swiper.slideTo(4, 0, false);
}else if(fll == 'six'){
    swiper.slideTo(5, 0, false);
}else{
    swiper.slideTo(0, 0, false);
}

function load() {
    var fixBoth = $("#fix-both");
    var x = 0,
        y = 0,
        isMove = false,
        n = 0,
        no = 1;
    var screenWidth = document.body.clientWidth;
    var fixBothWidth = parseInt($("#fix-both li").css('width')) * 6;
    fixBoth[0].addEventListener('touchstart', touchStart, false);
    fixBoth[0].addEventListener('touchmove', touchMove, false);
    fixBoth[0].addEventListener('touchend', touchEnd, false);
    window.addEventListener('touchend', touchChangeEnd, false);
    var oInp = document.getElementById("test");
    fixBoth.click(function(){
        touchChangeEnd();
        window.addEventListener('touchend', touchChangeEnd, false);
    });
    function touchStart(e) {
        var event = e || window.event;
        isMove = true;
        x = event.touches[0].pageX;
        y = event.touches[0].pageY;
    }

    function touchMove(e) {
        var event = e || window.event;
        if (Math.abs(e.touches[0].pageX - x) > (Math.abs(e.touches[0].pageY - y))) {
            isMove = true;
            n = e.touches[0].pageX - x;
            e.preventDefault();
        } else {
            isMove = false;
            n = 0;
        }

    }

    function touchEnd(e) {
        var event = e || window.event;
        window.removeEventListener('touchend', touchChangeEnd, false);

        if (n <= -10) {
            isMove = false;
            var leftFlag = parseInt($('#fix-both ul').css('left'));
            var maxLeft = fixBothWidth - screenWidth;
            if (no < 7) {
                $('#fix-both ul').animate({
                    left: '-' + maxLeft + "px"
                }, 400,'linear',function(){
                    window.addEventListener('touchend', touchChangeEnd, false);
                });
            }
            n = 0;
        }
        if (n >= 10) {
            isMove = false;
            if (no > 1) {
                $('#fix-both ul').animate({
                    left: "-" + ((no - 2) + 10) + "rem"
                }, 400,'linear',function(){
                    window.addEventListener('touchend', touchChangeEnd, false);
                });
                no -= 1;
            } else if (no == 1) {
                $('#fix-both ul').animate({
                    left: "-" + ((no - 1) * 10) + "rem"
                }, 400,'linear',function(){
                    window.addEventListener('touchend', touchChangeEnd, false);
                });
            }
            n = 0;
        }
    }

    function touchChangeEnd(e) {
        var title = document.getElementsByTagName('title');
        var titleData = ['4G LTE 移动数据终端', 'WLAN设置', '流量管理', '固件升级', '设备信息', '系统设置'];
        var event = e || window.event;
        var activeLi = $('#fix-both li.swiper-pagination-bullet-active');
        var liWidth = activeLi.width();
        var _index = activeLi.index();
        var documentWidth = document.body.clientWidth;
        var maxScrollWidth = parseInt($("#fix-both li").css('width')) * 6 - documentWidth; //正值
        var nowLeft = Math.abs(parseInt($('#fix-both ul').css('left')));
        var isMoveWidth = nowLeft + liWidth;

        var isMoveRightWidth = nowLeft - liWidth;

        var actionMoveWitdth = maxScrollWidth > isMoveWidth ? liWidth : (maxScrollWidth - nowLeft);

        var actionMoveRightWitdth = nowLeft > liWidth ? liWidth : nowLeft;

        var activeLiLeft = activeLi[0].offsetLeft - nowLeft;
        var activeLiRight = activeLi[0].offsetLeft + liWidth - nowLeft;
        if (activeLiLeft < documentWidth && activeLiRight > documentWidth) {
            //左侧在屏中，右侧不在，向左滑动actionMoveWitdth
            $('#fix-both ul').animate({
                left: "-" + (nowLeft + actionMoveWitdth) + "px"
            }, 300,'linear');
        } else if (activeLiLeft < 0 && activeLiRight >= 0) {
            $('#fix-both ul').animate({
                left: "-" + (nowLeft - actionMoveRightWitdth) + "px"
            }, 300,'linear');
        }else if(activeLiRight<0){
            $('#fix-both ul').animate({
                left: "-" + 0 + "px"
            }, 300,'linear');
        }else if(activeLiLeft>documentWidth){
            $('#fix-both ul').animate({
                left: "-" + maxScrollWidth + "px"
            }, 300,'linear');
        }
        //切换不同title值
        switch (_index) {
            case 0:
                title[0].innerHTML = titleData[0];
                break;
            case 1:
                title[0].innerHTML = titleData[1];
                break;
            case 2:
                title[0].innerHTML = titleData[2];
                break;
            case 3:
                title[0].innerHTML = titleData[3];
                break;
            case 4:
                title[0].innerHTML = titleData[4];
                break;
            case 5:
                title[0].innerHTML = titleData[5];
                break;
            default:
                title[0].innerHTML = titleData[0];

        }
    }

}

function wlan() {
		var resultMessage="未登录，或登录超时";
		var checkPermission=function(data){
			var varlabel = $(data).find("label").eq(0).text();
			return varlabel==resultMessage;
		};
		
		var alertMessage=function(alertMessage,returnlogin)
		{
				var traSuccess = $('#trafSuccess');
				$(traSuccess).text(alertMessage);
  			traSuccess.css('display', 'block');
				setTimeout(function() {
    		traSuccess.css('display', 'none');
    		if(returnlogin){
    			window.location.href="login.html";
    		}else{
    			window.location.href="index.html?flag=three";
    		}
				}, 2000);
		}
		var alertResult=function(isLogout,mesSuccess,messageFail){
  		if(isLogout){
  			alertMessage(messageFail,true);
  		}else{
  			alertMessage(mesSuccess,false);
  		}
		}
		
    var eyeCheck = $("#eyeCheck");
    var passWlanM = $('#passWlanM'),
        passWlan = $('#passWlan'),
        eye = $('.eye').eq(0),
        safeModule = $('.safeModule');
    eyeCheck.click(function() {
        if (eyeCheck.attr('checked')) {
	    eyeCheck.removeAttr('checked');
            passWlan.attr('type', 'text');
            eye.css('background-image', 'url(images/eyeOpen.png)');
        } else {
	    eyeCheck.attr('checked','checked')
            passWlan.attr('type', 'password');
            eye.css('background-image', 'url(images/eyeClose.png)');
        }
    });
    safeModule.click(function() {
    	var isPreChecked=$(this).parent('label').hasClass('active');
        $(this).parent('label').addClass('active');
        $(this).parent('label').siblings().removeClass('active');
        $(this).attr("checked",'checked');
       	if($(this).attr('name')=='safe'){//safe 设置安全模式
       		$('#security').val($(this).val());
	        if($(this).val()=='0'){
	        	$('.passWrap').css('display','none');
	        	$('.errortipInfo').eq(0).css('display','none');
    			$('.errortipInfo').eq(1).css('display','none');
    			$('.errortipInfo').eq(2).css('display','none');
    			$('.errortipInfo').eq(3).css('display','none');
	        }else{
	        	$('.passWrap').css('display','block');
	        }
        }else if($(this).attr('name')=='checkTime'){//checkTime 设置校准时间
        	//alert($(this).val());
        }else if($(this).attr('name')=='responeTraf'){//responeTraf 超限设置
        	
        }else if($(this).attr('name')=='typeB'){//typeB 流量单位转换
        	if(isPreChecked==true){
        		return false;
        	}
        	var converto=$(this).val();
        	var curValue=$('#trafNum').val()
        	var finalValue;
        	if(converto==1){// GB to MB
        			finalValue = curValue * 1024;
        			$('#trafNum').val(finalValue);
        	}else if(converto==2){// MB to GB
        			finalValue = curValue / 1024;
        			$('#trafNum').val(finalValue);
        	}
        }
       
    });
    $('#wlanSave').click(function() {
    	
    		$('.errortipInfo').eq(0).css('display','none');
    		$('.errortipInfo').eq(1).css('display','none');
    		$('.errortipInfo').eq(2).css('display','none');
    		$('.errortipInfo').eq(3).css('display','none');
    		//检查SSID
    		var valssid=$('#SSID').val();
    		var pressid=$('#SSID').attr('val');
    		//alert(valssid);
    		if(valssid==null||valssid==""){
    			$('.errortipInfo').eq(0).css('display','block');
    			return false;
    		}else{
    			$('.errortipInfo').eq(0).css('display','none');
    		}
    		if(strlen(valssid)>63){
    			$('.errortipInfo').eq(2).css('display','block');
    			return false;
    		}else{
    			$('.errortipInfo').eq(2).css('display','none');
    		}
    		
    		//检查密码
    		
    		var valsecurity=$('#security').val();
    		var pressecurity=$('#security').attr('val');
    		//alert(valsecurity);
    		var valpsw=$('#passWlan').eq(0).val();
    		var prepsw=$('#passWlan').eq(0).attr('val');
    		if(valsecurity=='1'&&(valpsw==null||valpsw==""||valpsw.length <8||strlen(valpsw)>63)){
    			$('.errortipInfo').eq(1).css('display','block');
    			return false;
    		}else{
    			$('.errortipInfo').eq(1).css('display','none');
    		}
    		//$(".wlanForm").action='/submit?flag=two';
    		//$(".wlanForm").submit();
    		//填充新的ssid值
		//原始值对比
	       	if(pressid==valssid&&pressecurity==valsecurity&&prepsw==valpsw){
	       		$('.errortipInfo').eq(3).css('display','block');
	       		return false;
	       	}else{
	       		$('.errortipInfo').eq(3).css('display','none');
	       	}
        $(".wlanForm").submit(function(){
	        	//$(".wlanForm").ajaxSubmit({
	        	$.ajax({
	        	type:'post',
	        	url:'/submit',
	        	data:{
	        		'SSID':valssid,
	        		'security':valsecurity,
	        		'password':valpsw
	        	},
	        	success:function(data){
	        		//alert(data);
	        		var isLogout = checkPermission(data);
	        		if(isLogout){
	        			alertMessage("未登录或登录超时",true);
	        		}else{
		        		var wlanSuccess=$('#wlanSuccess');
	        			wlanSuccess.find('span').eq(0).html(valssid);
	        			if(valsecurity=='0'){
	        				wlanSuccess.find('span').eq(1).html('空')
	        			}else{
	        				wlanSuccess.find('span').eq(1).html(valpsw)
	        			}
	        			
	        			wlanSuccess.css('display','block');
	        			$("#fix-both").css('display','none');
        			}
	        	}
	        	
	        });
	        return false;
        });
       	$(".wlanForm").submit();
       	//alert('提交等待中...');
        
        return false;
    });
    //wlan设置成功返回登陆
    $('#goLoginBtn').click(function(){
        window.location.href="login.html";
        $("#fix-both").css('display','block');
        $('#wlanSuccess').css('display','none');
    })
    //系统设置切换密码
    var sysChangeEye = function() {
        var syseyeType = false;
        var eyeBtn = $('.changeEye');
        var siblingsPas = eyeBtn.siblings('.pswInput');
        var changePlace = $("#changePlace");
        eyeBtn.click(function() {
            if (!syseyeType) { //当前是明文
                siblingsPas.attr('type', 'password');
                $(this).css('background-image', 'url(images/eyeClose.png)');
            } else {
                siblingsPas.attr('type', 'text');
                $(this).css('background-image', 'url(images/eyeOpen.png)');
            }
            syseyeType = !syseyeType;
        })
    };
    sysChangeEye();
    //流量设置保存
    $("#trafSave").click(function() {
		// 隐藏提示标签
		$('#checktrafNumMax').hide();
		$('#checktrafNumMin').hide();
		$('#checktrafNum').hide();
		
    	$("input[name='typeB']").eq(0).click();
        var trafNum = Number($("#trafNum").val());
        var checktrafNum = $('#checktrafNum');
        if ($("#trafNum").val() !== '' && typeof trafNum === 'number' && trafNum % 1 === 0 && trafNum !==0) {
    	 var vartotal,varlimit,varcal;
    	 vartotal = $("input[name='total']").val();
    	 varcal = $("input[name='checkTime']:checked").val();
    	 varlimit = $("input[name='responeTraf']:checked").val();
		 
		 // 不能输入小于 0 
		 if (Number(vartotal) < 0 ){
			 $('#checktrafNumMin').show();
			 return false;
		 }else if(Number(vartotal) > 2048000){  // 最大值为 2000GB or 2048000MB
			 $('#checktrafNumMax').show();
			 return false;
		 }
		 
    		$.ajax({
	        	type:'post',
	        	url:'/settotal',
	        	data:{
	        		'total':vartotal,
	        		'limit':varlimit,
	        		'calibration':varcal
	        	},
	        	success:function(data){
	        		var isLogout = checkPermission(data);		
	        		alertResult(isLogout,"流量设置成功","未登录或登录超时");
	        		//var traSuccess = $('#trafSuccess');
        			//traSuccess.css('display', 'block');
        			//setTimeout(function() {
            			//	traSuccess.css('display', 'none');
        			//	}, 2000);
	        		//}
	        		 
	        	}
	        	
	        });
        return false;
        } else {
            checktrafNum.css('display', 'block');
            return false;
        }

    	
        
    });
    //系统设置成功
    	var toVaildPwd = function(oldpwd,newpwd,newpwd2){
			
    		$('#cur_pwd_null').css('display','none');//当前密码不能为空
    		$('#new_pwd_null').css('display','none');//新密码不能为空
    		$('#new_2_pwd_null').css('display','none');//确认密码不能为空
    		$('#pwd_2_pwd_asymmetric').css('display','none');//新密码与确认新密码不匹配
    		$('#cur_32_max_pwd').css('display','none');//当前密码超过32个字符,请重新输入
    		$('#new_32_max_pwd').css('display','none');//新密码超过32个字符，请重新输入
			$('#pwd_err_settings').css('display','none');//当前密码有误或操作出错，请重新设置
			$('#cur_6_min_pwd').css('display','none');//当前密码小于6个字符，请重新输入
    		$('#new_6_min_pwd').css('display','none');//新密码小于6个字符，请重新输入    
			$('#new_old_same').css('display','none');//新旧密码相同，请重新设置

			
			if(oldpwd ==""){
				$('#cur_pwd_null').css('display','block');
				return false;
			}
			
			if(newpwd ==""){
				$('#new_pwd_null').css('display','block');
				return false;
			}
			
			if(newpwd2 ==""){
				$('#new_2_pwd_null').css('display','block');
				return false;
			}
			
			if(oldpwd.length < 6){
				$('#cur_6_min_pwd').css('display','block');
				return false;
			}
			
			if(newpwd.length < 6){
				$('#new_6_min_pwd').css('display','block');
				return false;
			}
			
			if(oldpwd.length > 32){
				$('#cur_32_max_pwd').css('display','block');
				return false;
			}
			
			if(newpwd.length > 32){
				$('#new_32_max_pwd').css('display','block');
				return false;
			}
			
			if(newpwd == oldpwd){
				$('#new_old_same').css('display','block');
				return false;
			}
			
			if(newpwd != newpwd2){
				$('#pwd_2_pwd_asymmetric').css('display','block');
				return false;
			}
			
			return true;
     }
    $('#systemSave').click(function(){	   
    	 var oldpwd,newpwd,username,newpwd2;
    	 oldpwd = $("#oldpwd").val().trim();
    	 newpwd = $("#newpwd").val().trim();
    	 username = $("#userName").val().trim();
    	 newpwd2 = $("#newpwd2").val().trim();
			 var result = toVaildPwd(oldpwd,newpwd,newpwd2);
			 
			 if(result==false){
			 	return false;
			 }else{
			 	//alert(result);
				$.ajax({
			        	type:'post',
			        	url:'/changepwd.json',
			        	data:{
			        		'username':username,
			        		'oldpwd':oldpwd,
			        		'newpwd':newpwd
			        	},
					success:function(data){
						//data = "{\"result\":\"false\",\"message\":\"管理员名称与密码不匹配\",\"data\":\"\",\"code\":103}";
						if(data!=null&&data!=""){
							var indexLabel = data.indexOf('label');
							if(indexLabel!=-1){
								var isLogout = checkPermission(data);
					        		if(isLogout){
					        			alertMessage("未登录或登录超时",true);
					        		}
							}else{
								//alert(data);
								var indextrue = data.indexOf('true');
								//alert(indextrue);
								if(indextrue!=-1){
									var wlanSuccess1=$('#wlanSuccess1');
	 								wlanSuccess1.css('display','block');
									$("#fix-both").css('display','none');
								}else{
									$('#pwd_err_settings').css('display','block');
								}	
							}
						}
			        	}
				});
			}
     return false;
    });
    //系统设置成功返回登陆
    $('#goLoginBtn1').click(function(){
        window.location.href="login.html";
        $("#fix-both").css('display','block');
        $('#wlanSuccess1').css('display','none');
    })
}
