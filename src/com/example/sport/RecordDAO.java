package com.example.sport;

public class RecordDAO {
	private static RecordDAO dbbus= null;
    private Db db = new Db(MyApplication.getContext());
    public static RecordDAO getInstance(){
          if(dbbus==null){
        	  dbbus = new RecordDAO();
          }
          return dbbus;
      }
      public Db getHelper(){
        return db;
      }
}
