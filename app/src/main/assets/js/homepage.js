(function(){
	// 已允许数量信息列表
	//var homeCheckbos = $(".homePage .selbtn .checkbox");
	//var liContent;
	// 已允许数量信息
	//var liContent = "<li><p class='fl'>01</p><div class='fl info'><p><span class='c999 fping'>主机名称</span><span class='c666'>5AB1256437S</span></p><p><span class='c999'>MAC地址</span><span class='c666'>30:2f:3g:7s:2e:9d</span></p><p><span class='c999 fnet'>IP地址</span><span class='c666'>192.168.88.123</span></p></div><p class='fl line'></p><div class='fl selbtn'><p>允许</p><label class='label-switch'><input type='checkbox'><div class='checkbox'></div></label></div></li>";

	// 选择的连接总数   允许连接数   不允许连接数
	//var sumNum = 1, allowedN=1, NallowedN=0;

	//for (var i = 0; i < homeCheckbos.length; i++) {
	//	homeCheckbos[i].flag = true;
	//}
	// 设置允许连接数单选按钮事件
	$(".homePage .selectedNum li").click(function(){
		$(".homePage .selectedNum li").removeClass('radioA');
		$(this).addClass('radioA');
		sumNum = $(this).children('span').html();
		//allowedN =  sumNum;
		// 设置已允许数量
		//$('.homePage .allowedN .allowedNm').html(allowedN);
		//$('.selectDet ul').html('');
		//for(var i = 0; i < sumNum; i++) {
		//	$('.selectDet ul').append(liContent);
		//}
		//homeCheckbos = $(".homePage .selbtn .checkbox");
		//for (var i = 0; i < homeCheckbos.length; i++) {
		//	homeCheckbos[i].flag = true;
		//}
		if(sumNum!=null){
			window.location="/chgmax?max="+sumNum;//linlian@2016.09.27
		}

	});
	// 已允许数量信息里面开关事件
	$("body").on('click','.homePage .selbtn .checkbox',function(){
		// 阻止
		
		var cid=$(this)[0].id;
		var checkflag = $(this).prev().attr("checked");
		if(checkflag=="true"||checkflag=="checked"){
			$(this).parent().siblings('p').html('阻止');
			$(this).prev().attr("class", "unActive");
			$(this).prev().attr("checked", "false");
			window.location="/block?macaddress="+cid;
			
		}else{
			$(this).parent().siblings('p').html('允许');
			$(this).prev().attr("class", "Active");
			$(this).prev().attr("checked", "true");
			window.location="/unblock?macaddress="+cid;
		}
		
		//if(this.flag) {
		//	$(this).parent().siblings('p').html('阻止');
		//	allowedN--;
			
			//window.location="/block?macaddress="+cid;
	//	}else {
		// 允许
			//$(this).parent().siblings('p').html('允许');
		//	allowedN++;
			
			//window.location="/unblock?macaddress="+cid;
		//}
		// 设置已允许数量
		//$('.homePage .allowedN .allowedNm').html(allowedN);
		// 设置已阻止数量
		//$('.homePage .allowedN .NallowedN').html(sumNum-allowedN);
		//this.flag = !this.flag;
		
	});
})();