/**
 * 移动端富文本编辑器
 * @author ganzw@gmail.com
 * @url    https://github.com/baixuexiyang/artEditor
 */
$.fn.extend({
	_opt: {
		placeholader: '<p>请输入文章正文内容</p>',
		validHtml: [],
		limitSize: 3,
		showServer: false
	},
	artEditor: function(options) {
		var _this = this,
			styles = {
				"-webkit-user-select": "text",
				"user-select": "text",
				"overflow-y": "auto",
				"text-break": "brak-all",
				"outline": "none",
				"cursor": "text"
			};
		$(this).css(styles).attr("contenteditable", true);
		_this._opt = $.extend(_this._opt, options);
		$(_this._opt.imgTar).on('tap', function() {
			if (mui.os.plus) { 
                var a = [{ 
                    title: "拍照" 
                }, { 
                    title: "从手机相册选择" 
                }]; 
                plus.nativeUI.actionSheet({ 
                    title: "上次图片", 
                    cancel: "取消", 
                    buttons: a 
                }, function(b) { /*actionSheet 按钮点击事件*/ 
                    switch (b.index) { 
                        case 0: 
                            break; 
                        case 1: 
                            var cmr = plus.camera.getCamera();  
            				cmr.captureImage(function(p) {  
                    			plus.io.resolveLocalFileSystemURL(p, function(entry) {  
	                            	var path = entry.toLocalURL();  
											if(_this._opt.showServer) {
						                      //图片压缩
						                      
						                	 var image=new Image();  
						                     image.src=path;
						                     image.onload=function(){  
						                       var that = this;  
						                      //生成比例   
						                       var w = that.width,  
						                       h = that.height,  
						                       scale = w / h;  
						                       w = 480 || w; //480  你想压缩到多大，改这里  
						                       h = w / scale;
						                     
						                      //生成canvas  
						                      var canvas = document.createElement('canvas');  
						                      var ctx = canvas.getContext('2d');  
						                      $(canvas).attr({  
							                      width: w,  
							                      height: h  
						                      });  
						                      ctx.drawImage(that, 0, 0, w, h);  
						                      var base64 = canvas.toDataURL('image/jpeg', 1 || 1); //1最清晰，越低越模糊。
						                       //图片压缩完成 开始上传
						                      _this.upload(base64);
			                    			}  
			                				return ;
			                			}	                            
		                            //picIndex += 1;  
//		                            appendFile(path); //处理图片的地方  
//		                            setTimeout("upload(1)", 1000);  
                        		},  
		                        function(e) {  
		                            alert("读取拍照文件错误：" + e.message);  
		                        });  
			                },  
			                function(e) {  
			                    //                  alert("失败：" + e.message);  
			                }, {  
			                    filename: "_doc/camera/",  
			                    index: 1  
			                });  
	                        break; 
                        case 2: 
                            //galleryImg();/*打开相册*/ 
							    plus.gallery.pick(function(path) {
							    	 if(_this._opt.showServer) {
						                      //图片压缩
						                      
						                	 var image=new Image();  
						                     image.src=path;
						                     image.onload=function(){  
						                       var that = this;  
						                      //生成比例   
						                       var w = that.width,  
						                       h = that.height,  
						                       scale = w / h;  
						                       w = 480 || w; //480  你想压缩到多大，改这里  
						                       h = w / scale;
						                     
						                      //生成canvas  
						                      var canvas = document.createElement('canvas');  
						                      var ctx = canvas.getContext('2d');  
						                      $(canvas).attr({  
							                      width: w,  
							                      height: h  
						                      });  
						                      ctx.drawImage(that, 0, 0, w, h);  
						                      var base64 = canvas.toDataURL('image/jpeg', 1 || 1); //1最清晰，越低越模糊。
						                       //图片压缩完成 开始上传
						                      _this.upload(base64);
			                    			}  
			                				return ;
			                			}
					            		var img = '<img src="'+ f.target.result +'" style="width:90%;" />';
					            	    _this.insertImage(img);
							    });
					}
			
				});
			}
		});
		
		_this.placeholderHandler();
		_this.pasteHandler();
		if(_this._opt.formInputId && $('#'+_this._opt.formInputId)[0]) {
				$(_this).on('input', function() {
					$('#'+_this._opt.formInputId).val(_this.getValue());
				});
		}			
	},
	upload: function(data) {
		var _this = this, filed = _this._opt.uploadField;
		$.ajax({
			url: _this._opt.uploadUrl,
			type: 'post',
			//data: $.extend(_this._opt.data, {filed: data}),
			data: {base64File:data},
			cache: false
		})
		.then(function(res) {
			var src = _this._opt.uploadSuccess(res);
			if(src) {
				var img = '<img src="'+ src +'" style="width:90%;" />';
			    _this.insertImage(img);
			} else {
				_this._opt.uploadError(res);
			}
		});
	},
	insertImage: function(src) {
	    $(this).focus();
		var selection = window.getSelection ? window.getSelection() : document.selection;
		var range = selection.createRange ? selection.createRange() : selection.getRangeAt(0);
		if (!window.getSelection) {
		    range.pasteHTML(src);
		    range.collapse(false);
		    range.select();
		} else {
		    range.collapse(false);
		    var hasR = range.createContextualFragment(src);
		    var hasLastChild = hasR.lastChild;
		    while (hasLastChild && hasLastChild.nodeName.toLowerCase() == "br" && hasLastChild.previousSibling && hasLastChild.previousSibling.nodeName.toLowerCase() == "br") {
		        var e = hasLastChild;
		        hasLastChild = hasLastChild.previousSibling;
		        hasR.removeChild(e);
		    }
		    range.insertNode(range.createContextualFragment("<br/>"));
		    range.insertNode(hasR);
		    if (hasLastChild) {
		        range.setEndAfter(hasLastChild);
		        range.setStartAfter(hasLastChild);
		    }
		    selection.removeAllRanges();
		    selection.addRange(range);
		}
		if(this._opt.formInputId && $('#'+this._opt.formInputId)[0]) {
			$('#'+this._opt.formInputId).val(this.getValue());
		}
	},
	pasteHandler: function() {
		var _this = this;
		$(this).on("paste", function(e) {
			console.log(e.clipboardData.items);
			var content = $(this).html();
			console.log(content);
			valiHTML = _this._opt.validHtml;
			content = content.replace(/_moz_dirty=""/gi, "").replace(/\[/g, "[[-").replace(/\]/g, "-]]").replace(/<\/ ?tr[^>]*>/gi, "[br]").replace(/<\/ ?td[^>]*>/gi, "&nbsp;&nbsp;").replace(/<(ul|dl|ol)[^>]*>/gi, "[br]").replace(/<(li|dd)[^>]*>/gi, "[br]").replace(/<p [^>]*>/gi, "[br]").replace(new RegExp("<(/?(?:" + valiHTML.join("|") + ")[^>]*)>", "gi"), "[$1]").replace(new RegExp('<span([^>]*class="?at"?[^>]*)>', "gi"), "[span$1]").replace(/<[^>]*>/g, "").replace(/\[\[\-/g, "[").replace(/\-\]\]/g, "]").replace(new RegExp("\\[(/?(?:" + valiHTML.join("|") + "|img|span)[^\\]]*)\\]", "gi"), "<$1>");
			if (!/firefox/.test(navigator.userAgent.toLowerCase())) {
			    content = content.replace(/\r?\n/gi, "<br>");
			}
			$(this).html(content);
		});
	},
	placeholderHandler: function() {
		var _this = this;
		$(this).on('focus', function() {
			if($.trim($(this).html()) === _this._opt.placeholader) {
				$(this).html('');
			}
		})
		.on('blur', function() {
			if(!$(this).html()) {
				$(this).html(_this._opt.placeholader);
			}
		});

		if(!$.trim($(this).html())) {
			$(this).html(_this._opt.placeholader);
		}
	},
	getValue: function() {
		return $(this).html();
	},
	setValue: function(str) {
		$(this).html(str);
	}
});





