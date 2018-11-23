/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For complete reference see:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config
	
	config.width = 1080;
	
	config.height = 400; 
	
	config.baseFloatZIndex = 100001;

	// The toolbar groups arrangement, optimized for two toolbar rows.
	config.toolbarGroups = [
		{ name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
		{ name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
		{ name: 'links' },
		{ name: 'insert' },
		{ name: 'forms' },
		{ name: 'tools' },
		{ name: 'document',	   groups: [ 'mode', 'document', 'doctools' ] },
		{ name: 'others' },
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		{ name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ] },
		{ name: 'styles'},
		{ name: 'colors'}
	];
	
	// Remove some buttons provided by the standard plugins, which are
	// not needed in the Standard(s) toolbar.
	config.removeButtons = 'Underline,Subscript,Superscript';

	// Set the most common block elements.
	config.format_tags = 'p;h1;h2;h3;pre';

	// Simplify the dialog windows.
	
	config.removeDialogTabs = 'image:advanced;link:advanced';
//	config.removeDialogTabs = 'image:advanced;image:Link'; // 移除图片上传页面的'高级','链接'页签
	
	//预览区域内容
	config.image_previewText=' ';
	//上传图片路径
	config.filebrowserImageUploadUrl = '/contract/article/imageUpload';
	//拖拽图片上传
//	uploadUrl: '/admin/article/dragImageUpload?command=QuickUpload&type=Files&responseType=json'
	// 移除编辑器底部状态栏显示的元素路径和调整编辑器大小的按钮
	config.removePlugins = 'elementspath,resize'; 
	
	config.allowedContent = false; // 是否允许使用源码模式进行编辑
	config.forcePasteAsPlainText = true; // 是否强制复制过来的文字去除格式
	config.enterMode = CKEDITOR.ENTER_BR; // 编辑器中回车产生的标签CKEDITOR.ENTER_BR(<br>),CKEDITOR.ENTER_P(<p>),CKEDITOR_ENTER(回车)
    // 设置快捷键
	// 用于实现Ctrl + V进行粘贴
	// 无此配置，无法进行快捷键粘贴
	config.keystrokes = [
	     [CKEDITOR.CTRL + 86 /* V */, 'paste']
	];
	 
	// 设置快捷键，可能与浏览器冲突plugins/keystrokes/plugin.js
	// 用于实现Ctrl + V进行粘贴
	// 此配置将会启动粘贴之前进行过滤，若无此配置，将会出现粘贴之后才弹出过滤框
	config.blockedKeystrokes = [
	    CKEDITOR.CTRL + 86
	];
	
	//工具栏是否可以被收缩（即：右上角的三角符号是否显示）   
	config.toolbarCanCollapse =true;
	
	//添加插件,多个使用','隔开
//	config.extraPlugins = 'colorbutton,panelbutton,button,panel,dialog,colordialog,dialogui';
	
	
};
