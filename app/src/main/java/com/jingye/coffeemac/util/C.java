package com.jingye.coffeemac.util;

/**系统常量类
 * @author yangwc
 *
 */
public class C {
    //关于文件名的常量
    public static final class FileName {
        public static final String IMAGE = "image";
        public static final String THUMBNAIL = "thumbnail";
        public static final String AUDIO = "audio";
        public static final String AVATAR = "avatar";
        public static final String LOG = "log";
        public static final String NOMEDIA = "nomedia";
        public static final String ECPIMAGESAVE = "ecpimgsave";
    }
    
    //关于文件后缀的常量
    public static final class FileSuffix {
        public static final String JPG = ".jpg";
        public static final String PNG = ".png";
        public static final String M4A = ".m4a";
        public static final String THREE_3GPP = ".3gp";
        public static final String BMP = ".bmp";
        public static final String MP4 = ".mp4";
        public static final String AMR_NB = ".amr";
        public static final String APK = ".apk";
        public static final String AAC = ".aac";
    }
    
    public static String queryMimeTypeByFileSuffix(String fileSuffix){
    	String mimeType = null;
    	if(fileSuffix.endsWith(FileSuffix.JPG)){
    		mimeType = MimeType.MIME_JPEG;
    	}else if(fileSuffix.endsWith(FileSuffix.PNG)){
    		mimeType = MimeType.MIME_PNG;
    	}else if(fileSuffix.endsWith(FileSuffix.BMP)){
    		mimeType = MimeType.MIME_BMP;
    	}else if(fileSuffix.endsWith(FileSuffix.THREE_3GPP)){
    		mimeType = MimeType.MIME_VIDEO_3GPP;
    	}else if(fileSuffix.endsWith(FileSuffix.MP4)){
    		mimeType = MimeType.MIME_VIDEO_MP4;
    	}
    	
    	return mimeType;
    }
    
    public static String queryFileSuffixByMimeType(String mimeType){
    	String fileSuffix = null;
    	if(mimeType.equals(MimeType.MIME_JPEG)){
    		fileSuffix = FileSuffix.JPG;
    	}else if(mimeType.equals(MimeType.MIME_PNG)){
    		fileSuffix = FileSuffix.PNG;
    	}else if(mimeType.equals(MimeType.MIME_BMP)){
    		fileSuffix = FileSuffix.BMP;
    	}else if(mimeType.equals(MimeType.MIME_AUDIO_3GPP)){
    		fileSuffix = FileSuffix.THREE_3GPP;
    	}else if(mimeType.equals(MimeType.MIME_AUDIO_MP4)){
    		fileSuffix = FileSuffix.MP4;
    	}else if(mimeType.equals(MimeType.MIME_AUDIO_M4A)){
    		fileSuffix = FileSuffix.M4A;
    	}else if(mimeType.equals(MimeType.MIME_AUDIO_AMR_NB)){
    		fileSuffix = FileSuffix.AMR_NB;
    	}else if(mimeType.equals(MimeType.MIME_AUDIO_AAC)){
    		fileSuffix = FileSuffix.AAC;
    	}
    	
    	return fileSuffix;
    }
    
    //关于mimetype的常量
    public static final class MimeType {
        public static final String MIME_JPEG = "image/jpeg";
        public static final String MIME_PNG = "image/png";        
        public static final String MIME_BMP = "image/x-MS-bmp";
        public static final String MIME_GIF = "image/gif";
        public static final String MIME_AUDIO_3GPP = "audio/3gpp";
        public static final String MIME_AUDIO_MP4 = "audio/mp4";
        public static final String MIME_AUDIO_M4A = "audio/m4a";
		public static final String MIME_AUDIO_AMR_NB = "audio/amr";
		public static final String MIME_AUDIO_AAC = "audio/aac";
        public static final String MIME_TXT = "txt/txt";// 用 于PC长消息
        public static final String MIME_WAPPUSH_SMS = "message/sms";
        public static final String MIME_WAPPUSH_TEXT = "txt/wappush";	//文字wappush
        public static final String MIME_MUSIC_LOVE = "music/love";	// 爱音乐
        public static final String MIME_MUSIC_XIA = "music/xia";	// 虾米音乐
        public static final String MIME_VIDEO_3GPP = "video/3gpp";
        public static final String MIME_VIDEO_MP4 = "video/mp4";
        public static final String MIME_LOCATION_BAIDU = "locate/baidu";//位置 百度

        //是否图片类型
        public static boolean isImageType(String type) {
        	if(null == type) {
        		return false;
        	} else if(type.startsWith("image")) {
        		return true;
        	}
        	return false;
        }
        // 是否是音乐类型
        public static boolean isMusicType(String type){
        	if(null == type){
        		return false;
        	} else if(type.startsWith("music")){
        		return true;
        	}
        	return false;
        }
        
        //是否位置类型
        public static boolean isLocateType(String type) {
        	if(null == type) {
        		return false;
        	} else if(type.startsWith("locate")) {
        		return true;
        	}
        	return false;
        }
        //是否视频类型
        public static boolean isVideoType(String type) {
        	if(null == type) {
        		return false;
        	} else if(type.startsWith("video")) {
        		return true;
        	}
        	return false;
        }
        //是否音频类型
        public static boolean isAudioType(String type) {
        	if(null == type) {
        		return false;
        	} else if(type.startsWith("audio")) {
        		return true;
        	}
        	return false;
        }
        
        //是否PC端长消息
        public static boolean isPCLongMsg(String type) {
        	if(null == type) {
        		return false;
        	} else if(type.equals(MIME_TXT)) {
        		return true;
        	}
        	return false;
        }
        
        //是否wap push
        public static boolean isWapPushMsg(String type) {
        	if(null == type) {
        		return false;
        	} else if(type.equals(MIME_WAPPUSH_SMS)) {
        		return true;
        	}
        	return false;
        }
    }
    
    // IM 自定义消息前缀
    public static final String IM_CUSTOM_PRE = "<*#ECPPROPMSG#*>";
    public static final String IM_CUSTOM_PRE2 = "<*#ECPPROPMSG#*><*#"; // FIXME 为了屏蔽PC临时群消息
    
    // IM 自定义消息多媒体前缀
    public static final String IM_CUSTOM_MEDIA_FILE = "<*#ECPPROPMSG#*><*#Media#*><4>type=file;id=";	// 文件方式
    public static final String IM_CUSTOM_MEDIA_PIC = "<*#ECPPROPMSG#*><*#Media#*><4>type=pic;id="; 		// 图片方式
    public static final String IM_CUSTOM_SAYHELLO = "<*#ECPPROPMSG#*><*#hello#*><5>";	// 有缘人
    public static final String IM_CUSTOM_SAYHELLOSHAKE = "<*#ECPPROPMSG#*><*#hello_shake#*><5>"; // 偶遇
    public static final String IM_CUSTOM_MUSIC = "<*#ECPPROPMSG#*><*#Music#*>";	// 爱音乐 和虾米音乐消息
    // IM自定义多媒体不支持的返回消息
    public static final String IM_CUSTOM_TYPEOUT = "<*#ECPPROPMSG#*><*#TypeOut#*>Media";
    // IM长消息中图片消息体head
    public static final String IM_CUSTOM_PIC_HEAD = "{ecp/image_";

    // 建立好友版本
    public static final String BUILD_RELATION_VERSION = "http://www.ecplive.com/ecp/add_friend1.0";
    
    // 网络重发超时定义
    public static final int RESEND_REQUEST_RETRY_COUNT_NO = 0;
    public static final int RESEND_REQUEST_RETRY_COUNT_FEW = 1;
    public static final int RESEND_REQUEST_RETRY_COUNT_NORMAL = 2;
    public static final int RESEND_REQUEST_RETRY_COUNT_MANY = 10;
    public static final int RESEND_REQUEST_RETRY_PERIOD_SHORT = 5;
    public static final int RESEND_REQUEST_RETRY_PERIOD_NORMAL = 10;
    public static final int RESEND_REQUEST_RETRY_PERIOD_LONG = 20;
}
