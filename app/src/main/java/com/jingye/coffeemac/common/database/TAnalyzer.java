package com.jingye.coffeemac.common.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.jingye.coffeemac.R;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.application.MyApplication;
import com.jingye.coffeemac.util.log.LogUtil;

public class TAnalyzer {

	private Map<String,SqlMaper> sqlMapers;

	private boolean result;
	private static TAnalyzer analyzer;
	private boolean namespaced;
	private Context mContext = MyApplication.Instance().getApplicationContext();
	private int resRawDatabase = R.raw.vendor;
	public  static int version = 1;
	public final static  String filename = "/data" + Environment.getDataDirectory().getAbsolutePath() +  
	"/com.jingye.coffeemac/vendor.db" ;
	
	private TAnalyzer(){
		result = false;
		createDatabase();
		sqlMapers = new HashMap<String,SqlMaper>();
		try{
			List<FileInclude> fileIncludes =  enterInclude();
			for(int i=0; i<fileIncludes.size(); i++){
				FileInclude fileInclude = fileIncludes.get(i);
				int resId = mContext.getResources().getIdentifier(fileInclude.getFile(),fileInclude.getSource(),
						mContext.getPackageName());

				InputStream source =  mContext.getResources().openRawResource(resId);
				execute(source) ; 
			}

			result = true;
			
		}catch(Exception e)
		{
			result = false;
		}
	}
	
	public boolean isNamespaced()
	{
		return this.namespaced;
	}
	
	public static TAnalyzer newInstance()
	{
		if(analyzer == null)
			analyzer = new TAnalyzer();
		return analyzer;
	}

	public void importDatebase(int version)
	{
		try{
			InputStream is = mContext.getResources().openRawResource(resRawDatabase); //欲导入的数据库
			FileOutputStream fos = new FileOutputStream(filename);
			byte[] buffer = new byte[1024*512];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveDatebase()
	{
		if(AppConfig.isDebugMode())
		{
			if(new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ "/ecp.db3").exists())
				return;
			
			try{
				InputStream is = new FileInputStream(new File(filename)); //欲导入的数据库
				FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() 
						+ "/ecp.db3");
				byte[] buffer = new byte[1024*512];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}	 
		}
	}

	public void createDatabase()
	{ 
		if (!(new File(filename).exists())) { 
			LogUtil.vendor("开始初始化数据库...");
			importDatebase(0);
		}
//		else 
//		{
//			saveDatebase();
//		}
	}

	public SqlMaper enter(String id)
	{
		if(result)
			return sqlMapers.get(id);
		else
			return null;
	}

	private List<FileInclude> enterInclude() throws Exception { 
		List<FileInclude> fileIncludes = new ArrayList<FileInclude>();
		int resId =  mContext.getResources().getIdentifier("database", "raw",
				mContext.getPackageName());
		InputStream source =  mContext.getResources().openRawResource(resId);

		Document documnet = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder = factory.newDocumentBuilder();
		documnet = builder.parse(source);
		Element root = documnet.getDocumentElement();
		String namespace = root.getAttribute("namespace");
		version = Integer.valueOf(root.getAttribute("version")) ;
		
		this.namespaced = "true".equals(namespace);

		NodeList nodes = root.getElementsByTagName("include");
		for(int i=0;i<nodes.getLength();i++){
			Element  node =  (Element)nodes.item(i); 

			FileInclude fileInclude = new FileInclude();
			fileInclude.setFile(node.getAttribute("file"));
			fileInclude.setSource(node.getAttribute("source"));
			fileIncludes.add(fileInclude);
		}
		return fileIncludes;
	}


	private void execute(InputStream source) throws Exception {
		Document documnet = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder = factory.newDocumentBuilder();
		documnet = builder.parse(source);
		Element root = documnet.getDocumentElement();
		String namespace = root.getAttribute("namespace");
		String[] mapTags = new String[]{"insert","delete","update","select","script"};
		for(int n=0;n<mapTags.length;n++)
		{
			String mapTag = mapTags[n];
			NodeList nodes =   root.getElementsByTagName(mapTag);
			if(nodes == null || nodes.getLength()==0)
				continue;

			for(int i=0;i<nodes.getLength();i++)
			{
				Element  node =  (Element)nodes.item(i); 

				if("insert".equals(mapTag))
				{
					InsertMaper maper = new InsertMaper();
					maper.setType(SqlMaper.ST_INSERT);
					if(this.namespaced)
						maper.setId(namespace + "."+ node.getAttribute("id"));
					else
						maper.setId(node.getAttribute("id"));
					maper.setScript(getNodeContent(node));
					maper.setParameterClass(node.getAttribute("parameterClass"));
					if(node.hasAttribute("cacheRefreshs"))
						maper.setCacheRefreshs(node.getAttribute("cacheRefreshs"));
					sqlMapers.put(maper.getId(), maper);
				}
				else if("select".equals(mapTag))
				{
					SelectMaper maper = new SelectMaper();
					maper.setType(SqlMaper.ST_SELECT);
					if(this.namespaced)
						maper.setId(namespace + "."+ node.getAttribute("id"));
					else
						maper.setId(node.getAttribute("id"));
					maper.setScript(getNodeContent(node));
					maper.setParameterClass(node.getAttribute("parameterClass"));
					maper.setReturnClass(node.getAttribute("returnClass"));
					if(node.hasAttribute("cached"))
						maper.setCached(node.getAttribute("cached").equals("true"));
					else
						maper.setCached(false);
					sqlMapers.put(maper.getId(), maper);
				}
				else if("delete".equals(mapTag))
				{
					DeleteMaper maper = new DeleteMaper();
					maper.setType(SqlMaper.ST_DELETE);
					if(this.namespaced)
						maper.setId(namespace + "."+ node.getAttribute("id"));
					else
						maper.setId(node.getAttribute("id"));
					maper.setScript(getNodeContent(node));
					maper.setParameterClass(node.getAttribute("parameterClass"));
					if(node.hasAttribute("cacheRefreshs"))
						maper.setCacheRefreshs(node.getAttribute("cacheRefreshs"));

					sqlMapers.put(maper.getId(), maper);
				}
				else if("update".equals(mapTag))
				{
					UpdateMaper maper = new UpdateMaper();
					maper.setType(SqlMaper.ST_UPDATE);
					if(this.namespaced)
						maper.setId(namespace + "."+ node.getAttribute("id"));
					else
						maper.setId(node.getAttribute("id"));
					maper.setScript(getNodeContent(node));
					maper.setParameterClass(node.getAttribute("parameterClass"));
					if(node.hasAttribute("cacheRefreshs"))
						maper.setCacheRefreshs(node.getAttribute("cacheRefreshs"));
					sqlMapers.put(maper.getId(), maper);
				}
				else if("script".equals(mapTag))
				{
					ScriptMaper maper = new ScriptMaper();
					maper.setType(SqlMaper.ST_SCRIPT);
					if(this.namespaced)
						maper.setId(namespace + "."+ node.getAttribute("id"));
					else
						maper.setId(node.getAttribute("id"));
					maper.setScript(getNodeContent(node));
					maper.setRegular(node.getAttribute("regular"));
					if(node.hasAttribute("cacheRefreshs"))
						maper.setCacheRefreshs(node.getAttribute("cacheRefreshs"));
					sqlMapers.put(maper.getId(), maper);
				}				 
			} 
		}
	}
	
	private String getNodeContent(Node paramNode){
		String str = "";
		if (Build.VERSION.SDK_INT >= 8){
			str = paramNode.getTextContent().trim();
		}else{
			for(Node node = paramNode.getFirstChild(); node != null; node = node.getNextSibling()){
				str = node.getNodeValue();
				if(str != null && (str.trim().length() > 10)){
					break;
				}
			}
		}
	  
		return str.trim();
  }
}
