(function(){
	// 提交表单
	var eyeType = true;
	$('#loginSubmit').click(function() {	
		//linlian@2016.09.27 validate password and submit@{
		//var name = $('#username').val();
		//var password = $('#password').val();
		//if(name=='admin' && password=='123') {
			// 登录成功
		//	window.location.href = 'index.html';
		//}else {
			// 登录错误提示
		//	$('.errLogin').css('display','block');
		//}
		//var oForm=document.getElementsByTagName('form')[0];
		//oForm.onsubmit();
		//linlian@2016.09.27 validate password and submit@}
		
		var varResult = toVaild();
		if(varResult==false){
			return false;
		}
		 var varname = $('#username').val();
		 var varpassword = $('#password').val();
		 //alert(name);
		 //alert(password);
		
			 $.ajax({
			    type: "post",
			    data: {
			    	'username':varname,
			    	'password':varpassword
			    	},
			    contentType: "application/json",
			    url: "/login.json",
			    beforeSend: function () {
			       
			       $("#loginSubmit").attr({ disabled: "disabled" });
			    },
			    success: function (data) {
			    	//alert('aa');
			    	//alert(data);
			    	var parsedJson = jQuery.parseJSON(data); 
			    	//alert(parsedJson.result);
			    	if(parsedJson.result=='false'){
			    		$('.errLogin').eq(0).css('display','block');
			    		//alert('用户名与密码不匹配，请重试');
			    	}else{
			    		//alert('true');
			    		window.location.href = 'index.html';
			    	}	        
			    },
			    complete: function () {
			    	//alert('complete');
			        $("#loginSubmit").removeAttr("disabled");
			       
			    },
			    error: function (data) {
			    	//alert('error');
			    	 $("#loginSubmit").removeAttr("disabled");
			      console.info("error: " + data.responseText);
			    }
				});
				return false;
	});
	// 修改用户名 取消错误提示
	$('#username').on('input', function() {
		$("#loginSubmit").removeAttr("disabled");
	   $('.errLogin').css('display','none');
	});
	// 修改密码 取消错误提示
	$('#password').on('input', function() {
		 $("#loginSubmit").removeAttr("disabled");
	   $('.errLogin').css('display','none');
	});
	$(".closeOrOpen .eye").click(function() {
		if(eyeType) {
			$("#password").attr('type','text');
			$(".closeOrOpen .eye").addClass('bgCss');
		}else {
			$("#password").attr('type','password');
			$(".closeOrOpen .eye").removeClass('bgCss')
		}
		eyeType = !eyeType;
	});
})()