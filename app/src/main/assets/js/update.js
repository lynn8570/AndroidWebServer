$(function(){
	
	
$('#checkForNew').on('click', function () {
	// 检测当前版本 curVersion
	// 检测当前版本 curVersion
	var curVersion = $('.firmwareUpdate .checkInfo .versionInfo').html();
	$('.updateW .check .checkInfo .versionInfo').html(curVersion);
		
	var checkagain = function(isForce) {
				var dataForce='true';
				if("true"!=isForce){
					dataForce='false';
				}
        $.ajax({
		    type: "get",
		    data: {'force':dataForce},
		    contentType: "application/json",
		    url: "/checkversion.json",
		    beforeSend: function () {
		       	// 检测版本
					if("true"==isForce){
							$('.mask p').html('正在检查中');
							$('.mask').css('display','block');
					}
		    },
		    success: function (data) {
		    	var parsedJson = jQuery.parseJSON(data); 
		        var varVersion = parsedJson.version;
		        var varResult = parsedJson.result;
			
						if(varVersion!=null&&varVersion!=""){
							// 如果检测新版本
							$('.mask').css('display','none');
							//$('.updateW .check').css('display','none');
							$('.check').css('display','none');
							$('.updateW .checkNew').css('display','block');
							$('.checkNewVersion').css('display','block');
							$('.firmwareUpdate .checkInfo .versionInfo').html(varVersion);	
							//$('.updateW .checkNew .checkInfo .versionInfo').html(varVersion);	
						} else if(varResult=='true'){
							//再查一次
							setTimeout(function(){
								checkagain("false");
							},3000);
						}else{
							//最新版本
							if("1004"==parsedJson.code){
				    	 	$('.mask').css('display','none');
					    	$('.checkOldVersion').css('display','block');
								// 2秒后提示隐藏
								setTimeout(function(){
									$('.checkOldVersion').css('display','none');
								}, 2000);
				    	}else{
				    			//更新出错
				    		$('.mask').css('display','none');
		    				var traSuccess = $('#trafSuccess');
								$(traSuccess).text(parsedJson.message);
  							traSuccess.css('display', 'block');
								setTimeout(function() {
    							traSuccess.css('display', 'none');
		    				}, 2000);
								//alert(parsedJson.message);
								//window.location.href="index.html?flag=true";
				    	}
						
						}
		        
		    },
		    complete: function () {
		        //$("#submit").removeAttr("disabled");
		       
		    },
		    error: function (data) {
		    	
		        console.info("error: " + data.responseText);
		    }
			});
	};
	checkagain("true");
////模拟检测过程
//	var a;
//	setTimeout(function(){
//		$('.mask').css('display','none');
//		a = 2;
//		// 如果检测没有新版本
//		if(a==1) {
//			// 提示当前是最新版本
//			$('.checkOldVersion').css('display','block');
//			// 2秒后提示隐藏
//			setTimeout(function(){
//				$('.checkOldVersion').css('display','none');
//			}, 2000);
//		}
//		// 如果检测新版本
//		else if (a==2) {
//			var newVersion = 'v2.2333333';
//			$('.check').css('display','none');
//			$('.updateW .checkNew').css('display','block');
//			$('.checkNewVersion').css('display','block');
//			$('.firmwareUpdate .checkInfo .versionInfo').html(newVersion);	
//		}
//	}, 2000);

	// 立即下载
	$('#download').on('click' , function(){
		var checkDownload = function(isForce) {
			  var dataForce='true';
				if("true"!=isForce){
					dataForce='false';
				}
        $.ajax({
		    type: "get",
		    data: {'force':dataForce},
		    contentType: "application/json",
		    url: "/download.json",
		    beforeSend: function () {
			if("true"==isForce){
			$('.mask p').html('正在下载');
 				$('.mask').css('display','block');
			$('.checkNewVersion').css('display','none');
			}
		    },
		    success: function (data) {
		    		
		    		var parsedJson = jQuery.parseJSON(data); 
		    		
		       if(parsedJson.version!=null&&parsedJson.version!=""){
			       	// 下载结束
						$('.updateW .checkNew').css('display','none');
						$('.check').css('display','block');
					  	$('.updateW .sureUpdate').css('display','block');
					  	$('.mask .spinner-box').css('display','none');
						}else if(parsedJson.result=='true'){
							//再查一次
							setTimeout(function(){
								checkDownload("false");
							},3000);
						} else{
							$('.mask').css('display','none');
		    				var traSuccess = $('#trafSuccess');
								$(traSuccess).text(parsedJson.message);
  							traSuccess.css('display', 'block');
								setTimeout(function() {
    							traSuccess.css('display', 'none');
		    				}, 2000);
							//alert(parsedJson.message);
							//window.location.href="index.html?flag=true";
						}
		        
		    },
		    complete: function () {
		        //$("#submit").removeAttr("disabled");
		    },
		    error: function (data) {
		        console.info("error: " + data.responseText);
		    }
			});
		};
		
		checkDownload("true");
		// 模拟下载过程
		//setTimeout(function(){
		//	$('.updateW .checkNew').css('display','none');
		//	$('.check').css('display','block');
		//	$('.updateW .sureUpdate').css('display','block');
		//	$('.mask .spinner-box').css('display','none');
		//	$('.mask p').css('display','none');
		//}, 2000);
	});
	// 立即升级
	$('#upNow').on('click', function() {
		// 更新操作
		$.ajax({
    type: "get",
    data: {'force':'true'},
    contentType: "application/json",
    url: "/upgrade.json",
    beforeSend: function () {
    	// 更新操作
			$('.updateW .sureUpdate').css('display','none');
			$('.updateW .check').css('display','block');
			$('.mask p').css('display','block');
			$('.mask p').html("<span>MiFi系统即将重启，</span><span>请在MiFi屏幕上确认“固件升级成功”</span>");
			$('.mask').css('display','block');
			$('.mask .spinner-box').css('display','block');
    },
    success: function (data) {
    		
        
    },
    complete: function () {
        //$("#submit").removeAttr("disabled");
    },
    error: function (data) {
        console.info("error: " + data.responseText);
    }
	});
		setTimeout(function(){ 
			$('.mask').css('display','none');
			window.location.href='index.html?flag=true';
		}, 60000);
	});
	// 下次再说
	$('#upNext').on('click', function() {
		// 操作
		$('.firmwareUpdate .checkInfo .versionInfo').html(curVersion);	
		$('.updateW .sureUpdate').css('display','none');
		$('.mask p').css('display','block');
		$('.mask .spinner-box').css('display','block');
		$('.mask').css('display','none');
	});
	})

})
	
