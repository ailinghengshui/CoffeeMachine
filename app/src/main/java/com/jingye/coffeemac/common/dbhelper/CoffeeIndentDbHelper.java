package com.jingye.coffeemac.common.dbhelper;

import com.jingye.coffeemac.common.database.IDataset;
import com.jingye.coffeemac.common.database.TDataset;
import com.jingye.coffeemac.common.database.TException;
import com.jingye.coffeemac.domain.CoffeeIndent;
import com.jingye.coffeemac.util.log.LogUtil;

public class CoffeeIndentDbHelper {

	/// ------------------ insert --------------------
	public static void insertCoffeeIndent(CoffeeIndent indent) {
		IDataset service = TDataset.newInstance();
		try {
			service.insert("insertCoffeeIndent", indent);
		} catch (TException e) {
			LogUtil.e("CoffeeIndentDbHelper", "insert coffee indent error.");
			e.printStackTrace();
		}
	}
	
	/// ------------------ delete --------------------
	
	/// ------------------ query ----------------------
	public static CoffeeIndent getCoffeeIndent(CoffeeIndent indent){
		IDataset service = TDataset.newInstance();
		try {
			CoffeeIndent indentRes = (CoffeeIndent)
					service.queryForObject("getCoffeeIndent", indent);
			return indentRes;
		} catch (TException e) {
			LogUtil.e("CoffeeIndentDbHelper", "get coffee indent error.");
			e.printStackTrace();
		}

		return null;
	} 
	
	/// ------------------ update --------------------
	public static void updateCoffeeIndentStatus(CoffeeIndent indent){
		try {
			IDataset service = TDataset.newInstance();
			try {
				service.update("updateCoffeeIndentStatus", indent);
			} catch (TException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			LogUtil.e("CoffeeIndentDbHelper", "update coffee indent error.");
			e.printStackTrace();
		}
	}
}
