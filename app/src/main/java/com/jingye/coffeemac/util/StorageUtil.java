package com.jingye.coffeemac.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.util.multicard.LimitSpaceUnwriteException;
import com.jingye.coffeemac.util.multicard.MultiCard;
import com.jingye.coffeemac.util.multicard.MultiCardFilePath;


public class StorageUtil {
	private static Boolean hadUnwriteTip = false;
 
	/**
	 * 获取文件写入路径，无视错误
	 * @param fileName 文件全名
	 * @return 返回路径，返回null则拒绝写入操作
	 */
	public static String getWritePathIgnoreError(Context context, String fileName) {
		return getWritePathIgnoreError(context, fileName, MultiCard.TYPE_UNKNOW);
	}
	
	public static String getWritePathIgnoreError(Context context, String fileName, int fileType) {
		return getWritePath(context, fileName, fileType, false, null, null);
	}
	
	public static boolean isInvalidVideoFile(String filePath) {
		String lowerCaseFilepath = filePath.toLowerCase();
		return (lowerCaseFilepath.endsWith(".3gp") 
				|| lowerCaseFilepath.endsWith(".mp4"));
	}
	
	public static boolean isInvalidPictureFile(String filePath) {
		String lowerCaseFilepath = filePath.toLowerCase();
		return (lowerCaseFilepath.endsWith(".jpg")
				|| lowerCaseFilepath.endsWith(".jpeg")
				|| lowerCaseFilepath.toLowerCase().endsWith(".png")
				|| lowerCaseFilepath.toLowerCase().endsWith(".bmp") || lowerCaseFilepath
				.toLowerCase().endsWith(".gif"));
	}
	
	/**
	 * 获取文件写入路径
	 * @param fileName 文件全名
	 * @return 返回路径，返回null则拒绝写入操作
	 */
	public static String getWritePath(Context context, String fileName) {
		return getWritePath(context, fileName, MultiCard.TYPE_UNKNOW);
	}
	
	public static String getWritePath(Context context, String fileName, int fileType) {
		return getWritePath(context, fileName, fileType, true, null, null);
	}
	
	/**
	 * 获取文件写入路径
	 * @param fileName 文件全名
	 * @param tip 是否带提示语
	 * @param warnningTip 警告提示语
	 * @param unwriteTip 拒绝写入提示语
	 * @return 返回路径，返回null则拒绝写入操作
	 */
	public static String getWritePath(Context context, String fileName, int fileType, boolean tip, String warnningTip, String unwriteTip) {		
		try {
			MultiCardFilePath path = MultiCard.getInstance().getWritePath(fileName, fileType);
			if(path.getCode() == MultiCardFilePath.RET_LIMIT_SPACE_WARNNING) {
				if(tip) {
					if(TextUtils.isEmpty(warnningTip)) {
                        ToastUtil.showToast(context, R.string.sdcard_not_enough_warning);
					} else {
                        ToastUtil.showToast(context, warnningTip);
					}
				}
			}
			return path.getFilePath();
		} catch (LimitSpaceUnwriteException e) {
			e.printStackTrace();
			if(tip) {
				if(TextUtils.isEmpty(unwriteTip)) {
                    ToastUtil.showToast(context, R.string.sdcard_not_enough);
				} else {
                    ToastUtil.showToast(context, unwriteTip);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * 获取文件读取路径
	 * @param orgFilePath 已经存在的文件路径(由于通过龙骞功能模块保存的图片没和翼聊程序的保持一致(why?)，所以必须这样处理，否则图片会找不到)
	 * @param fileName 文件全名
	 * @return 如果orgFilePath存在直接返回orgFilePath，否则根据一定规则返回路径，如果路径不存在返回""
	 */
	public static String getReadPath(String fileName, String orgFilePath) {
		if(!TextUtils.isEmpty(orgFilePath) && new File(orgFilePath).exists()) {
			return orgFilePath;
		} else {
			return MultiCard.getInstance().getReadPath(fileName);
		}
	}
	
	public static String getReadPath(String fileName, String orgFilePath, String mimetype) {
		if(!TextUtils.isEmpty(orgFilePath) && new File(orgFilePath).exists()) {
			return orgFilePath;
		} else {
			return MultiCard.getInstance().getReadPath(fileName, mimetype);
		}
	}
	
	public static String getReadPath(String fileName, String orgFilePath, int fileType) {
		if(!TextUtils.isEmpty(orgFilePath) && new File(orgFilePath).exists()) {
			return orgFilePath;
		} else {
			return MultiCard.getInstance().getReadPath(fileName, fileType);
		}
	}
	
	/**
	 * 判断存储卡空间(外置、内置存储卡是否有空间可写)
	 * @param context
	 * @param type	文件类型
	 * @param bNeedTip	是否要做出提示语
	 * @return true表示无存储卡或无空间可写,false表示ok
	 */
	public static boolean isSDCardSapceForWriteWithTip(Context context, int type, boolean bNeedTip) {
		return isSDCardSapceForWriteWithTip(context, MultiCard.TYPE_UNKNOW, type, bNeedTip);
	}
	
	public static boolean isSDCardSapceForWriteWithTip(Context context, int fileType, int type, boolean bNeedTip) {
		if(type == 0 && ! isSDcardExist()) {
			if(bNeedTip) {
                ToastUtil.showToast(context, R.string.sdcard_deny);
			}
			return true;			
		} else if(type == 1 && ! isExternalSDCardExist()) {
			if(bNeedTip) {
                ToastUtil.showToast(context, R.string.sdcard_deny);
			}
			return true;
		}

		if(!isLimitSDCardSpaceForWrite(fileType, type)) {
			if(bNeedTip) {
                ToastUtil.showToast(context, R.string.sdcard_not_enough);
			}
			return true;
		}

		if(!isLimitSDCardSpaceForWriteWarning(fileType, type)) {
			if(bNeedTip) {
                ToastUtil.showToast(context, R.string.sdcard_not_enough_warning);
			}
		}

		return false;
	}

	/**
	 * 根据文件类型检查外置及内置存储卡是否有空间可写
	 * @param fileType 文件类型
	 * @param type 检查范围,0表示所有,1表示只检查外置
	 * @return
	 */
	public static boolean isLimitSDCardSpaceForWrite(int fileType, int type) {
		return MultiCard.getInstance().checkSDCardSpace(fileType, type);
	}

	/**
	 * 根据文件类型检查外置及内置存储卡是否超过预警空间
	 * @param fileType 文件类型
	 * @param type 检查范围,0表示所有,1表示只检查外置
	 * @return
	 */
	public static boolean isLimitSDCardSpaceForWriteWarning(int fileType, int type) {
		return MultiCard.getInstance().islimitSpaceWarning(fileType, type);
	}

	/**
	 * 判断是否有SDCARD,外置存储卡及内置存储卡
	 * FIXME 由多存储卡类提供空间存在的判断
	 * @return
	 */
	public static boolean isSDcardExist() {
		return MultiCard.getInstance().isSDCardExist();
	}

	/**
	 * 判断是否有外置存储卡
	 * @return
	 */
	public static boolean isExternalSDCardExist() {
		return MultiCard.getInstance().isExternalSDCardExist();
	}

	public static String  saveSelfBitmapImage(Context context, Bitmap bitmap){
		String time = TimeUtil.getNowString();
		String fileName = U.getMyVendorNum() + "_" + time + ".jpg";

		bitmap = scaleBitmap(bitmap,120,120);
		
		// 转化Bitmap到String
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		byte[] dataContent = baos.toByteArray();
		
		// 把png文件存到本地
		String filePath = StorageUtil.getWritePath(context, fileName, MultiCard.TYPE_HEAD);
		AttachmentStore.save(dataContent, filePath);
		
		return filePath;
	}
	
	private static Bitmap scaleBitmap(Bitmap srcBitmap,int desWidth,int desHeight){
		int srcWidth = srcBitmap.getWidth();
		int srcHeight = srcBitmap.getHeight();

		// 计算缩放率，目标尺寸除以图片的原始尺寸
		float scaleWidth = ((float) desWidth) / srcWidth;
		float scaleHeight = ((float) desHeight) / srcHeight;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth,
				srcHeight, matrix, true);
	}
}
