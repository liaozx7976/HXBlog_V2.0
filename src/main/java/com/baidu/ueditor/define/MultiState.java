package com.baidu.ueditor.define;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.ueditor.Encoder;

/**
 * 多状态集合状态
 * 其包含了多个状态的集合, 其本身自己也是一个状态
 * @author hancong03@baidu.com
 *
 */
public class MultiState implements State {

	private boolean state = false;
	private String info = null;
	private Map<String, Long> intMap = new HashMap<String, Long>();
	private Map<String, String> infoMap = new HashMap<String, String>();
	private List<State> stateList = new ArrayList<State>();
	
	public MultiState ( boolean state ) {
		this.state = state;
	}
	
	public MultiState ( boolean state, String info ) {
		this.state = state;
		this.info = info;
	}
	
	public MultiState ( boolean state, int infoKey ) {
		this.state = state;
		this.info = AppInfo.getStateInfo( infoKey );
	}
	
	@Override
	public boolean isSuccess() {
		return this.state;
	}
	
	public void addState ( State state ) {
		stateList.add( state );
	}

	/**
	 * 该方法调用无效果
	 */
	@Override
	public void putInfo(String name, String val) {
		this.infoMap.put(name, val);
	}

	@Override
	public String toJSONString() {
		
		String stateVal = this.isSuccess() ? AppInfo.getStateInfo( AppInfo.SUCCESS ) : this.info;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append( "{\"state\": \"" + stateVal + "\"" );
		
		// 数字转换
		Iterator<String> iterator = this.intMap.keySet().iterator();
		
		while ( iterator.hasNext() ) {
			
			stateVal = iterator.next();
			
			builder.append( ",\""+ stateVal +"\": " + this.intMap.get( stateVal ) );
			
		}
		
		iterator = this.infoMap.keySet().iterator();
		
		while ( iterator.hasNext() ) {
			
			stateVal = iterator.next();
			
			builder.append( ",\""+ stateVal +"\": \"" + this.infoMap.get( stateVal ) + "\"" );
			
		}
		
		builder.append( ", \"imageList\": [" );
		
		
		Iterator<State> statIte = this.stateList.iterator();
		
		while ( statIte.hasNext() ) {
			
			builder.append( statIte.next() + "," );
			
		}
		
		if ( this.stateList.size() > 0 ) {
			builder.deleteCharAt( builder.length() - 1 );
		}
		
		builder.append( " ]}" );

		return Encoder.toUnicode( builder.toString() );

	}

	@Override
	public void putInfo(String name, long val) {
		this.intMap.put( name, val );
	}

	@Override
	public String getInfo(String name) {
		String val = infoMap.get(name);
		if(val != null)	 return val;
		Long _val = intMap.get(name);
		if(_val != null)	 return String.valueOf(_val);
		
		for(State state : stateList) {
			val = state.getInfo(name);
			if(val != null) {
				return val;
			}
		}
		return null;
	}

}
