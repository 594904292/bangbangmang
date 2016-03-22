package com.bbxiaoqu.ui;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgreementActivity extends BaseActivity {
	private TextView textView;
	TextView title;
	TextView about_save;
	TextView right_text;
	ImageView top_more;
	ImageView qr_android;
	//private IWXAPI wxApi;
	// Bitmap bitmap=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agreement);
		initView();
		initData();
		//wxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
	    //wxApi.registerApp(Constants.APP_ID);




		textView = (TextView) findViewById(R.id.agreement_textView);
		textView.setText("用户使用协议\n" +
				"\n" +
				"欢迎使用帮帮忙，请您仔细阅读以下条款，如果您对本协议的任何条款表示异议，您可以选择不使用帮帮忙；进入帮帮忙则意味着您将同意并遵守本协议下的全部规定，并完全服从于帮帮忙的统一管理。\n" +
				"\n" +
				"第一章 总则\n" +
				"\n" +
				"第1条“帮帮忙”为北京思博易科技有限公司（下称“思博易公司”）注册商标，任何人未经思博易公司许可不得以任何形式擅自使用。\n" +
				"\n" +
				"第2条帮帮忙所有权、经营权、管理权均属思博易公司。\n" +
				"\n" +
				"第3条本协议最终解释权归属思博易公司。\n" +
				"\n" +
				"第二章 帮帮忙用户\n" +
				"\n" +
				"第4条凡是注册用户和浏览用户均为帮帮忙用户（以下统称“用户”）。\n" +
				"\n" +
				"第5条用户的头像、昵称、等级数、排名、好友关系等信息，将可能会在个人中心及排行榜中被展示，但不会接受任何应用外个人或单位的查询。国家机关依法查询除外，用户选择公开以上信息除外。\n" +
				"\n" +
				"第6条用户享有言论自由的权利。\n" +
				"\n" +
				"第7条用户的言行不得违反《计算机信息网络国际联网安全保护管理办法》、《互联网信息服务管理办法》、《互联网电子公告服务管理规定》、《维护互联网安全的决 定》、《互联网新闻信息服务管理规定》等相关法律规定，不得在帮帮忙发布、传播或以其它方式传送含有下列内容之一的信息：" +
				"1）反对宪法所确定的基本原则的；" +
				"2）危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；" +
				"3）损害国家荣誉和利益的；" +
				"4）煽动民族仇恨、民族歧视、破坏民族团结的；" +
				"5）破坏国家宗教政策，宣扬邪教和封建迷信的；" +
				"6）散布谣言，扰乱社会秩序，破坏社会稳定的；" +
				"7）散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；" +
				"8）侮辱或者诽谤他人，侵害他人合法权利的；" +
				"9）煽动非法集会、结社、游行、示威、聚众扰乱社会秩序的；" +
				"10）以非法民间组织名义活动的；" +
				"11) 含有虚假、有害、胁迫、侵害他人隐私、骚扰、侵害、中伤、粗俗、猥亵、或其它道德上令人反感的内容；" +
				"12） 含有中国法律、法规、规章、条例以及任何具有法律效力之规范所限制或禁止的其它内容的。\n" +
				"\n" +
				"第8条用户不得在帮帮忙内发布任何形式的广告。\n" +
				"\n" +
				"第9条用户应承担一切因其个人的行为而直接或间接导致的民事或刑事法律责任，因用户行为给思博易公司造成损失的，用户应负责赔偿。\n" +
				"\n" +
				"第10条帮帮忙拥有对违反应用规则的用户进行处理的权力，直至禁止其在帮帮忙内发布信息。\n" +
				"\n" +
				"第11条任何用户发现帮帮忙内容涉嫌侮辱或者诽谤他人、侵害他人合法权益的或违反帮帮忙协议的，有权进行投诉。\n" +
				"\n" +
				"第12条为了能够给广大用户提供一个优质的交流平台，同时使帮帮忙能够良性、健康地发展，将对涉及反动、色情的头像、昵称和发布不良内容的用户，进行严厉处理。一经发现此类行为，将给予永久封禁ID并清空所有发言的处罚。\n" +
				"\n" +
				"第三章 权利声明\n" +
				"\n" +
				"第13条帮帮忙是让用户自由参与公益支持和交流的平台。帮帮忙内容仅代表用户的观点，与思博易公司无关。因用户留言、评论等存在权利瑕疵或侵犯了第三方的合法权益（包括但不限于专利权、商标权、著作权及著作邻接权、肖像权、隐私权、名誉权等），由提交发布言论的用户承担所有责任，思博易公司不承担任何责任。同时，帮帮忙有权无需事先通知即可自行决定对项目或言论采取拒绝上线、下线、删除等处理措施。\n" +
				"\n" +
				"第14条用户之间因使用帮帮忙而产生或可能产生的任何纠纷和/或损失，由用户自行解决并承担相应的责任，与思博易公司无关。\n" +
				"\n" +
				"第15条用户在帮帮忙发表的言论，思博易公司有权转载或引用。\n" +
				"\n" +
				"第16条用户在任何时间段在帮帮忙发表的任何内容的著作财产权，用户许可思博易公司在全世界范围内免费地、永久性地、不可撤销地、可分许可地和非独家地使用的权利，包括但不限于：复制权、发行权、出租权、展览权、表演权、放映权、广播权、信息网络传播权、摄制权、改编权、翻译权、汇编权以及《著作权法》规定的由著作权人享有的其他著作财产权利。并且，用户许可思博易公司有权利就任何主体侵权而单独提起诉讼，并获得全部的赔偿。\n" +
				"\n" +
				"第四章 处罚规则\n" +
				"\n" +
				"第17条思博易公司郑重提醒用户，若出现下列情况任意一种或几种，将承担包括被关闭全部或者部分权限、被暂停或被删除其帐号的后果，情节严重者，还将承担相应的法律责任。\n" +
				"\n" +
				"1、使用不雅或不恰当ID和昵称；\n" +
				"\n" +
				"2、发表含有猥亵、色情、人身攻击和反政府言论等非法或侵权言论的；\n" +
				"\n" +
				"3、从事非法商业活动；\n" +
				"\n" +
				"4、模仿帮帮忙管理人员ID或者他人ID，用以假冒管理人员或破坏管理人员形象；\n" +
				"\n" +
				"5、使用发贴机等非法软件进行违规发布的行为；\n" +
				"\n" +
				"6、其他思博易公司认为不恰当的情况。\n" +
				"\n" +
				"第18条 凡文章出现含有本协议第７条禁止发布、传播内容之一的，帮帮忙管理人员有权不提前通知作者直接删除，并依照有关规定作相应处罚。情节严重者，帮帮忙管理人员有权对其做出关闭部分权限、暂停直至删除其帐号。\n" +
				"\n" +
				"第五章 附则\n" +
				"\n" +
				"第19条所有用户发表的文章而引起的法律纠纷，与思博易公司无关。\n" +
				"\n" +
				"第20条帮帮忙如因系统维护或升级等而需暂停服务时，将事先公告。若因硬件故障或其它不可抗力而导致暂停服务，于暂停服务期间造成的一切不便与损失，帮帮忙不负任何责任。由于帮帮忙的调整导致信息丢失和/或其他结果的，思博易公司不承担任何责任。\n" +
				"\n" +
				"第21条本协议未涉及的问题参见国家有关法律法规，当本协议与国家法律法规冲突时，以国家法律法规为准。\n" +
				"\n" +
				"第22条思博易公司保留随时地、不事先通知地、不需要任何理由地、单方面地修改本协议的权利。本协议一经修订，思博易公司将会用修订后的协议版本完全替代修订前的协议版本，并通过www.bbxiaoqu.com向所有用户公布。您应当及时关注和了解本协议的修订情况，如果您不同意修订后的协议版本，请您立刻停止对帮帮忙实施任何使用行为，否则即视同您同意并完全接受修订后的协议版本。\n" +
				"\n" +
				"思博易公司拥有对以上各项条款内容的解释权及修改权。");
	}

	public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                              .equals(Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if  (sdCardExist)    
        {                                   
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录  
          }    
        return sdDir.toString();  
          
    }  
	

	
	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);		
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.GONE);

	}

	private void initData() {
		title.setText("用户协议");
		right_text.setText("");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}
	

}
