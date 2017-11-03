package com.jingye.coffeemac.service.action;

import java.util.HashMap;
import java.util.Map;

 
 
public  class TActionFactory {

	private static TActionFactory factory = new TActionFactory();
	
	public static TActionFactory newInstance()
	{
		return factory;
	}
	private TActionFactory()
	{
	   
	}
	protected  Map<Integer, IAction> actions = new HashMap<Integer, IAction>();
 
	public boolean registerAction(IAction action)
	{
		
		if(actions.containsKey(action.getWhat()))
		{
			return false;
		}
		else 
		{
			actions.put(action.getWhat(), action);
			return true;
		}
	}
	public boolean removeAction(IAction action)
	{
		if(actions.containsKey(action.getWhat()))
		{
			actions.remove(action.getWhat());
			return true;
		}
		 
			return false;
 
	}
 
	public IAction getAction(int key)
	{
		return actions.get(key);
	}
}
