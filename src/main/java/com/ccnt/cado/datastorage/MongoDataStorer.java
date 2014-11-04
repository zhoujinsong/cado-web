package com.ccnt.cado.datastorage;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.stereotype.Repository;

import com.ccnt.cado.datafetch.MetricData;
import com.ccnt.cado.datafetch.MonitorObject;
import com.ccnt.cado.util.BeanCaster;
import com.ccnt.cado.web.bean.PlatformInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


@Repository("mongoDataStorer")
public class MongoDataStorer implements DataStorer{
	private static final String MONGO_ADDR = "mongo.address";
	private static final String MONGO_PORT = "mongo.port";
	private static final String MONGO_DB = "mongo.db";
	private static final String COLLNAME_MONITOROBJECT = "monitorObject";
	private static final String COLLNAME_METRICDATA = "metricData";
	private static final String COLLNAME_ID = "id";
	private static final String COLLNAME_PLATFORMINFO = "platformInfo";
	
	private MongoClient client;
	private DB db;

	public MongoDataStorer(){
		super();
		try {
			Properties props = new Properties();
			props.load(getClass().getClassLoader().getResourceAsStream("mongodb.properties"));
			String address, port, dbName;
			address = props.getProperty(MONGO_ADDR);
			port = props.getProperty(MONGO_PORT);
			dbName = props.getProperty(MONGO_DB);
			client = new MongoClient(address, Integer.parseInt(port));
			db = client.getDB(dbName);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private synchronized int  getCollId(String collName){
		DBCollection idColl = db.getCollection(COLLNAME_ID);
		DBObject object = idColl.findOne(new BasicDBObject("_id",collName));
		if(object == null){
			BasicDBObject doc = new BasicDBObject().
					append("_id", collName).
					append("currentId", 0);
			idColl.insert(doc);
			return 0;
		}
		int id = (Integer)object.get("currentId") + 1;
		idColl.update(new BasicDBObject("_id",collName),new BasicDBObject("$set",new BasicDBObject("currentId",id)));
		return id;
	}
	
	public void put(MonitorObject object) {
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_MONITOROBJECT);
		BasicDBObject doc = new BasicDBObject();
		int id = getCollId(COLLNAME_MONITOROBJECT);
		doc.put("_id", id);
		object.getAttributes().put("_id", id);
		for(Entry<String,Object> entry : object.getAttributes().entrySet()){
				doc.put(entry.getKey(), entry.getValue());
		}
		for(Entry<String,MonitorObject> entry : object.getFathers().entrySet()){
			doc.put(entry.getKey()+"_Id", entry.getValue().getAttributes().get("_id"));
		}
		monitorObjectColl.insert(doc);
	}
	
	public void put(MetricData metricData) {
		DBCollection metricDataColl = db.getCollection(COLLNAME_METRICDATA);
		BasicDBObject doc = new BasicDBObject();
		int id = getCollId(COLLNAME_METRICDATA);
		doc.put("_id", id);
		doc.put("time", metricData.getTime());
		for(Entry<String,Object> entry : metricData.getDatas().entrySet()){
			doc.put(entry.getKey(), entry.getValue());
		}
		doc.put("monitorObjectId", metricData.getMonitorObject().getAttributes().get("_id"));
		metricDataColl.insert(doc);
	}

	
	public void remove(MonitorObject object) {
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_MONITOROBJECT);
		monitorObjectColl.remove(new BasicDBObject("_id",object.getAttributes().get("_id")));
		for(Entry<String,List<MonitorObject>> entry : object.getSons().entrySet()){
			for(MonitorObject monitorObject : entry.getValue()){
				remove(monitorObject);
			}
		}
	}


	
	public List<Map<String,Object>> getMonitorObjects(Map<String, Object> queryConditions) {
		List<Map<String,Object>> attributesArray = new ArrayList<Map<String,Object>>();
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_MONITOROBJECT);
		DBCursor cursor = monitorObjectColl.find(new BasicDBObject(queryConditions));
		while(cursor.hasNext()){
			Map<String,Object> attributes = new HashMap<String,Object>();
			DBObject dbObject = cursor.next();
			for(String key : dbObject.keySet()){
				attributes.put(key, dbObject.get(key));
			}
			attributesArray.add(attributes);
		}
		cursor.close();
		return attributesArray;
	}

	
	public void dropAll() {
		db.getCollection(COLLNAME_MONITOROBJECT).drop();
		db.getCollection(COLLNAME_METRICDATA).drop();
	}

	public void put(PlatformInfo platformInfo) {
		DBCollection platformColl = db.getCollection(COLLNAME_PLATFORMINFO);
		DBObject doc = BeanCaster.castBean2DBObject(platformInfo);
		int id = getCollId(COLLNAME_PLATFORMINFO);
		doc.put("_id", id);
		platformInfo.set_id(id);
		platformColl.insert(doc);
	}

	public void removePlatform(int platformId) {
		DBCollection platformColl = db.getCollection(COLLNAME_PLATFORMINFO);
		platformColl.remove(new BasicDBObject("_id",platformId));
	}

	public void updatePlatform(PlatformInfo platformInfo) {
		DBCollection platformColl = db.getCollection(COLLNAME_PLATFORMINFO);
		platformColl.save(BeanCaster.castBean2DBObject(platformInfo));
	}

	public PlatformInfo getPlatform(int platformId) {
		DBCollection platformColl = db.getCollection(COLLNAME_PLATFORMINFO);
		DBObject dbObject = platformColl.findOne(new BasicDBObject("_id",platformId));
		return (PlatformInfo)BeanCaster.castDBObject2Bean(dbObject, PlatformInfo.class);
	}

	public List<PlatformInfo> getAllPlatforms() {
		List<PlatformInfo> list = new ArrayList<PlatformInfo>();
		DBCollection platformColl = db.getCollection(COLLNAME_PLATFORMINFO);
		DBCursor cursor = platformColl.find();
		while(cursor.hasNext()){
			list.add((PlatformInfo)BeanCaster.castDBObject2Bean(cursor.next(), PlatformInfo.class));
		}
		return list;
	}

	public List<Map<String, Object>> getMetricData(
			Map<String, Object> queryConditions) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_METRICDATA);
		DBCursor cursor = monitorObjectColl.find(new BasicDBObject(queryConditions));
		while(cursor.hasNext()){
			Map<String,Object> attributes = new HashMap<String,Object>();
			DBObject dbObject = cursor.next();
			for(String key : dbObject.keySet()){
				attributes.put(key, dbObject.get(key));
			}
			list.add(attributes);
		}
		cursor.close();
		return list;
	}

	public List<Map<String, Object>> getNewestMetricData(
			Map<String, Object> queryConditions, int count) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_METRICDATA);
		DBCursor cursor = monitorObjectColl.find(new BasicDBObject(queryConditions)).limit(count).sort(new BasicDBObject("time",-1));
		while(cursor.hasNext()){
			Map<String,Object> attributes = new HashMap<String,Object>();
			DBObject dbObject = cursor.next();
			for(String key : dbObject.keySet()){
				attributes.put(key, dbObject.get(key));
			}
			list.add(attributes);
		}
		cursor.close();
		return list;
	}
	
}
