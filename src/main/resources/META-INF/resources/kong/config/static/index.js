$(document).ready(function() {
   query();
});

var leaf = 0;
function query(){
	$.ajax({
  	  type: "POST",
  	  url: "query",
  	  data:JSON.stringify({"path":"/"}),
  	  dataType: "json",
  	  contentType:'application/json'
  	}
  ).done(function( msg ) {
  	if(msg["success"]){
  	 var data =  msg["data"];
  	 var childrens = data["childrens"] ;
  	 buildNodeTree(data,"rootNav");
  	}  
  });
}

function queryNode(node){
	var tmpleaf =  $(node).attr("leaf");
	if(tmpleaf != undefined && tmpleaf != null ){
		leaf = tmpleaf;
	} else {
		leaf = 0;
	}
	var id = $(node).attr("id");
	var value = $(node).attr("value");
	$("#rootNav a").removeClass("active node-active");
	$(node).addClass("active node-active");
	$("#currentPath").text(id);
	$("#currentPath").attr("value",id);
    $("#currentValue").val(value);
}

function addNode(){
	var parentPath = $("#currentPath").attr("value");
	var path =parentPath + "/" + $("#nextPath").val();
    var value = $("#nextValue").val();
	 $.ajax({
	  	  type: "POST",
	  	  url: "add",
	  	  data:JSON.stringify({"path":path,"value":value}),
	  	  dataType: "json",
	  	  contentType:'application/json'
	    }).done(function( msg ) {
	    	if(msg["success"]){
	    		successNotice("添加节点成功");
	    	    setTimeout(function () {
	    	    	location.reload();
	    	    }, 2000);
	    	} else{
	    		errorNotice(msg["errorMessage"]);
	    	}
	  });
}

function updateNode(){
	var path = $("#currentPath").attr("value");
    var value = $("#currentValue").val();
    $.ajax({
  	  type: "POST",
  	  url: "update",
  	  data:JSON.stringify({"path":path,"value":value}),
  	  dataType: "json",
  	  contentType:'application/json'
    }).done(function( msg ) {
    	if(msg["success"]){
    		 successNotice("更新节点成功");
     	     setTimeout(function () {
    	    	location.reload();
    	     }, 2000);
    	} else{
    		errorNotice(msg["errorMessage"]);
    	}
  });

}

function deleteNode(){
	if(leaf != 1){
		errorNotice("非叶子节点不能删除");
		return;
	}
	var path = $("#currentPath").attr("value");
	$.ajax({
	  	  type: "POST",
	  	  url: "delete",
	  	  data:JSON.stringify({"path":path}),
	  	  dataType: "json",
	  	  contentType:'application/json'
	    }).done(function( msg ) {
	    	if(msg["success"]){
	    		successNotice("删除节点成功");
	    	    setTimeout(function () {
	    	    	location.reload();
	    	    }, 2000);
	    	}  else{
	    		errorNotice(msg["errorMessage"]);
	    	}
	  });
}

function buildNodeTree(data,id){
	 var childrens = data["childrens"] ;
	 var re = new RegExp("/","g");
	 if(childrens != undefined && childrens != null && childrens.length > 0 ){
		 var rootPath = data["rootPath"].replace(re,'');
		 var value = data["value"];
		 if(value == undefined || value == null){
			 data["value"] = "";
		 }
		 if(id == "rootNav"){
			 $("#rootNav").append('<a href="#" class="bd-links-link d-inline-block rounded node active node-active" style="font-size:18px" onclick="queryNode(this)" id="'+data["rootPath"]+ '" value="'+data["value"]+'">/</a>');
			 $("#"+id).append(' <ul class="list-unstyled fw-normal pd-2" id="'+rootPath+'Nav">');

		 } else {
			 $("#"+id).append('<li><a  href="#" class="bd-links-link d-inline-block rounded node " style="font-size:18px" onclick="queryNode(this)" id="'+data["rootPath"]+ '" value="'+data["value"]+'"><i class=" icon-angle-right icon-large"></i>&nbsp;'+data["path"]+'</a>');
			 $("#"+id).append(' <ul class="list-unstyled fw-normal ms-3" id="'+rootPath+'Nav">');
		 }
		 $.each(childrens, function(key, value) { 
				buildNodeTree(value,rootPath+"Nav");
		 });
		 $("#"+id).append('</ul>');
	 } else {
		 $("#"+id).append('<li><a  href="#" class="bd-links-link d-inline-block rounded node " style="font-size:18px" onclick="queryNode(this)" leaf="1" id="'+data["rootPath"]+ '" value="'+data["value"]+'">'+data["path"]+'</a></li>');
	 }
}

function successNotice(content){
	if (content == undefined || content == ""){
		content = "操作成功";
	}
	var ops = {color:"green",content:content,autoClose:3000,closeOnClick:'box',overlay:false};
	notice(ops);
}
function errorNotice(content){
	if (content == undefined || content == ""){
		content = "操作失败";
	}
	var ops = {color:"red",content:content,autoClose:5000,closeOnClick:'box',overlay:false};
	notice(ops);
}
function notice(ops){
	var jbox =  new jBox('Notice', {
	      attributes: {
	    	x: 'right',
	        y: 'top'
	      },
	      offset : {
	    	  y : window.innerHeight/4,
	    	  x : window.innerWidth/2-ops.content.length*5
	      },
	      stack: false,
	      animation: {
	        open: 'tada',
	        close: 'zoomIn'
	      },
	      autoClose: ops.autoClose,
	      closeOnClick: ops.closeOnClick,
	      overlay: ops.overlay,
	      color: ops.color,
	      content: ops.content,
	      delayOnHover: true,
	      showCountdown: true,
	      closeButton: false
	    });
	
}