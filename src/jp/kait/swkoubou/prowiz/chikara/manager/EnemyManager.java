package jp.kait.swkoubou.prowiz.chikara.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import jp.crudefox.chikara.util.CFUtil;
import jp.kait.swkoubou.prowiz.R;
import jp.kait.swkoubou.prowiz.chikara.manager.MemberManager.MItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;


/*		Auth: Chikara Funabashi
 * 		Date: 2013/08/09
 *
 */

public class EnemyManager {

	private static String FRENDS_URL = CFConst.SERVER + CFConst.FREDNS;
	private static String SEARCH_FRENDS_URL = CFConst.SERVER + CFConst.SEARCH_FRENDS;

	private Context mContext;

	//private ArrayList<ConItem>mList = new ArrayList<ConItem>();

	private ArrayList<EItem> mList = new ArrayList<EItem>();


	public static class EItem implements Serializable, Cloneable{
		public String id;
		public String name;
		public Bitmap icon;
		public int atk;
		public int def;
		public int hp;
		//public String body;

		public EItem(String name,Bitmap icon,int atk, int def, int hp) {
			this.id = String.format("e_%s", name);
			this.name = name;
			this.icon = icon;
			this.atk = atk;
			this.def = def;
			this.hp = hp;
		}

		@Override
		public EItem clone(){
			try{
				return (EItem) super.clone();
			}catch(CloneNotSupportedException e){
				e.printStackTrace();
			}
			return null;
		}


	}




	public EnemyManager(Context context){
		mContext = context;
	}


	//http://hellin.dip.jp/m/MemberList.php

	public synchronized boolean update_kari(LoginInfo l){

		if(!l.isLoggedIn()) return false;

		try {

			mList.clear();

			Bitmap icon = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
			Bitmap monster1 = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.monster1)).getBitmap();
			Bitmap monster2 = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.monster2)).getBitmap();
			Bitmap monster3 = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.monster3)).getBitmap();

			mList.add(new EItem("すらいむ", monster1, 1, 1 , 5));
			mList.add(new EItem("かいじゅう", monster2, 1, 1 , 5));
			mList.add(new EItem("でんきねずみ", monster3, 1, 1 , 5));


			return true;
		} catch (Exception e) {
			e.printStackTrace();
			CFUtil.Log("member ", e);
			return false;
		}


	}



	public synchronized boolean update(LoginInfo l){

		if(CFConst.IS_MIRAI){
			return update_kari(l);
		}

		if(!l.isLoggedIn()) return false;

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("sid", l.sid.sid));
//		params.add(new BasicNameValuePair("range_start", ""+start_time));
//		params.add(new BasicNameValuePair("range_end", ""+end_time));

		log("member 001");

		String data = null;

		//JSONObject json;
//		if(CFUtil.LOCAL_DEBUG){
//			//json = CUtil.getRawTextJson(mContext, R.raw.board_resp_sample);
//		}else{
//
//			//json = CUtil.postDataReturnJson(BOARD_URL, params);
//		}

		data = CFUtil.postData( FRENDS_URL, params);

		if(data==null) return false;

		log(data);

		mList.clear();

		log("frends 002");

		try {

			log("frends 003");

			JSONObject json = new JSONObject(data);

			String result = json.getString("result");

			log("frends 004");

			if(!result.equalsIgnoreCase("OK") ){
				return false;
			}

//			if(result.equalsIgnoreCase(LoginManager.BOARD_ERR_OVER_TIME_TOKEN )){
//				return false;
//			}

//			Resources res = mContext.getResources();
//			Bitmap[] bmps = new Bitmap[2];
//			bmps[0] = ((BitmapDrawable) res.getDrawable(R.drawable.ic_launcher)).getBitmap();
//			bmps[1] = ((BitmapDrawable) res.getDrawable(R.drawable.ic_launcher)).getBitmap();

			JSONArray jdata = json.getJSONArray("data_list");

			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			int  num = jdata.length();
			for(int i=0;i<num;i++){
				JSONObject jc = jdata.getJSONObject(i);

//				MItem item = new MItem();
//				item.id = jc.getString("user_id");
//
//				if(!jc.isNull("user_name"))
//					item.name = jc.getString("user_name");
//				else item.name = null;
//
//				item.icon = CFUtil.getImage(CFConst.SERVER+CFConst.PROFILE_IMEGES_FROM_USER_ID_DIR+item.id, 500, 500);
//
//				mList.add(item);
			}

			CFUtil.Log("num="+mList.size());

			return true;
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			CFUtil.Log("member ", e);
			return false;
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			CFUtil.Log("member ", e);
			return false;
		}


	}
//	public synchronized boolean deleteContoribute(LoginManager lf,LoginInfo li, int id){
//
//		if( lf.deleteContribute(li, id) ){
//			removeItemById(id);
//			return true;
//		}
//
//		return false;
//	}



	public synchronized EItem removeItemById(String id){
		EItem mitem = getItemById(id);
		if(mitem==null) return null;
		if( mList.remove(mitem) ) return mitem;
		return null;
	}


	public synchronized void clear(){

		mList.clear();

	}




	public synchronized EItem getItemById(String id){
		for(int i=0;i<mList.size();i++){
			EItem item = mList.get(i);
			if( item.id.equals(id) ) return item;
		}
		return null;
	}

	public synchronized EItem getItemByIndex(int index){
		if(index>=mList.size() || index<0) return null;
		return mList.get(index);
	}
	public synchronized int getItemLength(){
		return mList.size();
	}


	private synchronized void sortByDate(final boolean asc){

		Comparator<MItem> comp;

//		if(asc){
//			comp = new Comparator<SItem>() {
//				@Override
//				public int compare(MItem lhs, MItem rhs) {
////					if(lhs.name<rhs.time) return -1;
////					if(lhs.time>rhs.time) return +1;
////					return 0;
//					return lhs.name.compareTo(rhs.name);
//				}
//			};
//		}else{
//			comp = new Comparator<SItem>() {
//				@Override
//				public int compare(MItem lhs, MItem rhs) {
////					if(lhs.time<rhs.time) return +1;
////					if(lhs.time>rhs.time) return -1;
////					return 0;
//					return lhs.name.compareTo(rhs.name);
//				}
//			};
//		}
//
//
//		Collections.sort(mList, comp);


	}




	public static void log(String str){
		android.util.Log.d("test", str);
	}




}
