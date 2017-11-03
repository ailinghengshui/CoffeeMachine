package com.jingye.coffeemac.common.action;

import java.util.HashMap;
import java.util.Map;

public class TActionFactory {

	private static TActionFactory factory = new TActionFactory();
	
	public static TActionFactory newInstance()
	{
		return factory;
	}
	private TActionFactory()
	{
		
	}
	protected  Map<Integer, IAction> actions = new HashMap<Integer, IAction>();
	public IAction getAction(int key)
	{
		return actions.get(key);
	}
	public boolean unRegisterAction(IAction action)
	{
		if(actions.containsKey(action.getKey()))
		{
			return false;
		}
		else 
		{
			actions.remove( action.getKey() );
			return true;
		}
	}
	public boolean registerAction(IAction action)
	{
		
		if(actions.containsKey(action.getKey()))
		{
			return false;
		}
		else 
		{
			actions.put(action.getKey(), action);
			return true;
		}
	}


	 
}
