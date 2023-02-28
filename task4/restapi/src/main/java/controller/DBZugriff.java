package controller;

import model.Favourite;
import model.Poi;
import model.Shop;

import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DBZugriff
{
	private static Queue<QueueElement> waitingQueue = new LinkedList<>();
	private static Lock queueLock = new ReentrantLock();
	
	/*
	public static List<Object> simpleGet() {
		List<Object> ret = new ArrayList<Object>();
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM MARS.SHOPS;";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Object o = new Object();
				//Auslesen mit rs.getInt(par) wobei par entweder spaltennummer oder spaltenname in der abfragetabelle bzw select
				//Analog für rs.getString usw
				ret.add(o);
			}

		}catch(Exception e){
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
			System.err.println(e.getClass() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}*/

    /**
     *
     * @param sCategory
     * @param sName String with escaped special characters
     *     .replace("!", "!!")
     *     .replace("%", "!%")
     *     .replace("_", "!_")
     *     .replace("[", "![");
     * @param sPoi
     * @param sDistance the search radius in coordinates ex 10m = 0.000000003
     * @return
     */
	public static List<Shop> search(String sCategory, String sName, Poi sPoi, double sDistance, long distance) {
		List<Shop> ret = new ArrayList<Shop>();
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM MARS.SHOPS" +
						" WHERE TRUE " +
						((sCategory != null) ? "AND CATEGORY = ?" : "") +
                        ((sName != null) ? " AND SHOPNAME LIKE ? ESCAPE '!'" : "") +
                        ((sPoi != null) ? " AND ST_DISTANCE(POINT(LONGITUDE, LATITUDE), POINT(?, ?)) <= ? " : "") + ";";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			int counter = 1;
			if(sCategory != null) {
                pstmt.setString(counter, sCategory);
			    counter++;
            }
            if(sName != null) {
                pstmt.setString(counter, "%" + sName + "%");
                counter++;
            }
            if(sPoi != null) {
                pstmt.setDouble(counter, sPoi.getLongitude());
                counter++;
                pstmt.setDouble(counter, sPoi.getLatitude());
                counter++;
                pstmt.setDouble(counter, sDistance);
                counter++;
            }
			rs = pstmt.executeQuery();
			while(rs.next()){
				Shop s = new Shop();
				s.setId(rs.getLong("ID"));
				s.setLongitude(rs.getDouble("LONGITUDE"));
				s.setLatitude(rs.getDouble("LATITUDE"));
				s.setCategory(rs.getString("CATEGORY"));
				s.setShopname(rs.getString("SHOPNAME"));
				s.setOpeningHours(rs.getString("OPENINGHOURS"));
				s.setWebsite(rs.getString("WEBSITE"));
				if(sPoi != null) {
					if(distFrom(sPoi.getLatitude(), sPoi.getLongitude(), s.getLatitude(), s.getLongitude()) <= distance)
						ret.add(s);
				} else {
					ret.add(s);
				}
			}

		}catch(Exception e){
			System.err.println("search :- " + e.getClass() + ": " + e.getMessage());
			ret = null;
		} finally {
			try{rs.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{con.close();}catch(Exception e1){;}
		}
		return ret;
	}
	
	/**
	 *  This code was taken  from stackoverflow
	 *  https://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
	 */
	private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371000; //meters
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
						Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return (earthRadius * c);
	}

	/**
	 * returns a list of all points of intrest
	 */
	public static List<Poi> getAllPois() {
		List<Poi> ret = new ArrayList<Poi>();
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM MARS.POIS;";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Poi p = new Poi();
				p.setId(rs.getString("ID"));
				p.setLongitude(rs.getDouble("LONGITUDE"));
				p.setLatitude(rs.getDouble("LATITUDE"));
				p.setPoiName(rs.getString("PNAME"));
				ret.add(p);
			}

		}catch(Exception e){
			System.err.println("getAllPois :- " + e.getClass() + ": " + e.getMessage());
			ret = null;
		} finally {
			try{rs.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{con.close();}catch(Exception e1){;}
		}
		return ret;
	}


	/**
	 * returns a list of all favourites
	 */
	public static List<Favourite> getAllFavourites() {
		List<Favourite> ret = new ArrayList<Favourite>();
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM MARS.FAVOURITES LEFT OUTER JOIN MARS.POIS " +
						" ON MARS.FAVOURITES.SPOI = MARS.POIS.ID;";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Favourite f = new Favourite();
				f.setName(rs.getString("FNAME"));
				f.setsCategory(rs.getString("SCATEGORY"));
				f.setsName(rs.getString("SNAME"));
				f.setsDistance(rs.getInt("SDISTANCE") == 0 ? null : rs.getInt("SDISTANCE"));
				Poi p = null;
				if(rs.getString("SPOI") != null) {
					p = new Poi();
					p.setId(rs.getString("ID"));
					p.setLongitude(rs.getDouble("LONGITUDE"));
					p.setLatitude(rs.getDouble("LATITUDE"));
					p.setPoiName(rs.getString("PNAME"));
				}
				f.setsPoi(p);
				ret.add(f);
			}

		}catch(Exception e){
			System.err.println("getAllFavourites :- " + e.getClass() + ": " + e.getMessage());
			ret = null;
		} finally {
			try{rs.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{con.close();}catch(Exception e1){;}
		}
		return ret;
	}


	/**
	 * adds a favourite to the database
	 */
	public static Boolean addFavourite(Favourite favourite){
		Boolean ret;
		String sql = "INSERT INTO MARS.FAVOURITES (FNAME, SCATEGORY, SNAME, SPOI, SDISTANCE) " +
						" VALUES (?, ?, ?, ?, ?);";
		try{
			HashMap<Integer, Pair<String, Object>> params = new HashMap<>();
			params.put(1, new Pair<>("String", favourite.getName()));
			params.put(2, new Pair<>("String", favourite.getsCategory()));
			params.put(3, new Pair<>("String", favourite.getsName()));
			params.put(4, new Pair<>("String", favourite.getsPoi() != null ? favourite.getsPoi().getId() : null));
			params.put(5, new Pair<>("Int", favourite.getsDistance() != null ? favourite.getsDistance() : 0));
			QueueElement element = new QueueElement(sql, params);
			enqueueQuerry(element);
			ret = true;
		}catch(Exception e){
			System.out.println("addFavourite :- " + e.getClass().getName() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}

	/**
	 * adds a favourite to the database
	 */
	public static Boolean isFavouriteNameInUse(String name){
		Boolean ret;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT * " +
				" FROM MARS.FAVOURITES " +
				"       WHERE FNAME = ?;";
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, name);
			ret = pstmt.executeQuery().next();
		}catch(Exception e){
			ret = null;
		} finally {
			try{pstmt.close();}catch(Exception e1){;}
			try{con.close();}catch(Exception e1){;}
		}
		return ret;
	}

	public static Boolean deleteFavourite(String name) {
		Boolean ret;
		String sql = "DELETE FROM MARS.FAVOURITES " +
				" WHERE FNAME = ?;";
		try{
			HashMap<Integer, Pair<String, Object>> params = new HashMap<>();
			params.put(1, new Pair<>("String", name));
			QueueElement element = new QueueElement(sql, params);
			enqueueQuerry(element);
			ret = true;
		}catch(Exception e){
			System.out.println("deleteFavourite :- " + e.getClass().getName() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}

	public static Boolean updateFavourite(Favourite favourite) {
		Boolean ret;
		String sql = "UPDATE MARS.FAVOURITES " +
						" SET SCATEGORY = ?," +
						" 	  SNAME = ?, " +
						" 	  SPOI = ?, " +
						" 	  SDISTANCE = ? " +
						" WHERE FNAME = ?;";
		try{
			HashMap<Integer, Pair<String, Object>> params = new HashMap<>();
			params.put(1, new Pair<>("String", favourite.getsCategory()));
			params.put(2, new Pair<>("String", favourite.getsName()));
			params.put(3, new Pair<>("String", favourite.getsPoi() != null ? favourite.getsPoi().getId() : null));
			params.put(4, new Pair<>("Int", favourite.getsDistance() != null ? favourite.getsDistance() : 0));
			params.put(5, new Pair<>("String", favourite.getName()));
			QueueElement element = new QueueElement(sql, params);
			enqueueQuerry(element);
			ret = true;
		}catch(Exception e){
			System.out.println("updateFavourite :- " + e.getClass().getName() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}

	public static Boolean addShop(Shop shop){
		Boolean ret;
		String sql = "INSERT INTO MARS.SHOPS (LONGITUDE, LATITUDE, CATEGORY, SHOPNAME, OPENINGHOURS, WEBSITE) " +
				" VALUES (?, ?, ?, ?, ?, ?);";
		try{
			HashMap<Integer, Pair<String, Object>> params = new HashMap<>();
			params.put(1, new Pair<>("Double", shop.getLongitude()));
			params.put(2, new Pair<>("Double", shop.getLatitude()));
			params.put(3, new Pair<>("String", shop.getCategory()));
			params.put(4, new Pair<>("String", shop.getShopname()));
			params.put(5, new Pair<>("String", shop.getOpeningHours()));
			params.put(6, new Pair<>("String", shop.getWebsite()));
			QueueElement element = new QueueElement(sql, params);
			enqueueQuerry(element);
			ret = true;
		}catch(Exception e){
			System.out.println("addShop :- " + e.getClass().getName() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}
	
	public static Boolean deleteShop(long shopId){
		Boolean ret;
		String sql = "DELETE FROM MARS.SHOPS " +
				" WHERE ID = ?;";
		try{
			HashMap<Integer, Pair<String, Object>> params = new HashMap<>();
			params.put(1, new Pair<>("Long", shopId));
			QueueElement element = new QueueElement(sql, params);
			enqueueQuerry(element);
			ret = true;
		}catch(Exception e){
			System.out.println("deleteShop :- " + e.getClass().getName() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}
	
	public static Boolean updateShop(Shop shop){
		Boolean ret;
		String sql = "UPDATE MARS.SHOPS " +
				" SET LONGITUDE = ?," +
				" 	  LATITUDE = ?, " +
				" 	  CATEGORY = ?, " +
				" 	  SHOPNAME = ?, " +
				" 	  OPENINGHOURS = ?, " +
				" 	  WEBSITE = ? " +
				" WHERE ID = ?;";
		try{
			HashMap<Integer, Pair<String, Object>> params = new HashMap<>();
			params.put(1, new Pair<>("Double", shop.getLongitude()));
			params.put(2, new Pair<>("Double", shop.getLatitude()));
			params.put(3, new Pair<>("String", shop.getCategory()));
			params.put(4, new Pair<>("String", shop.getShopname()));
			params.put(5, new Pair<>("String", shop.getOpeningHours()));
			params.put(6, new Pair<>("String", shop.getWebsite()));
			params.put(7, new Pair<>("Long", shop.getId()));
			QueueElement element = new QueueElement(sql, params);
			enqueueQuerry(element);
			ret = true;
		}catch(Exception e){
			System.out.println("updateShop :- " + e.getClass().getName() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}
	
	public static List<String> getAllCategories(){
		List<String> ret = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT CATEGORY FROM MARS.SHOPS GROUP BY CATEGORY;";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				ret.add(rs.getString("CATEGORY"));
			}
		}catch(Exception e){
			System.out.println("getAllCategories :- " + e.getClass() + ": " + e.getMessage());
			ret = null;
		} finally {
			try{rs.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{con.close();}catch(Exception e1){;}
		}
		return ret;
	}
	
	/**
	 * returns a list of all points of intrest
	 */
	public static Poi getPoiById(String poiid) {
		Poi ret = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM MARS.POIS" +
													" WHERE POIS.ID = ?;";
		ResultSet rs = null;
		if(poiid == null)
			return null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, poiid);
			rs = pstmt.executeQuery();
			while(rs.next()){
				ret = new Poi();
				ret.setId(rs.getString("ID"));
				ret.setLongitude(rs.getDouble("LONGITUDE"));
				ret.setLatitude(rs.getDouble("LATITUDE"));
				ret.setPoiName(rs.getString("PNAME"));
			}
		}catch(Exception e){
			System.err.println("getPoiById :- " + e.getClass() + ": " + e.getMessage());
			ret = null;
		} finally {
			try{rs.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{con.close();}catch(Exception e1){;}
		}
		return ret;
	}
	
	private static void enqueueQuerry(QueueElement queueElement){
		queueLock.lock();
		waitingQueue.add(queueElement);
		if(waitingQueue.size() >= 10)
			executeQueue();
		queueLock.unlock();
		// The following out commented lines are for return value purposes (self made handshake).
		// They are currently not in use, but don't touch them just in case
		//queueElement.waitForExecution();
	}
	
	private static void executeQueue(){
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = ConnectionManager.getConnection();
			QueueElement element = waitingQueue.poll();
			while(element != null){
				try {
					// The following out commented lines are for return value purposes (self made handshake).
					// They are currently not in use, but don't touch them just in case
					//if(element.isSomebodyWaiting()){
						pstmt = element.prepareStatement(con);
						element.setRet(pstmt.executeUpdate() == 1);
						//element.wake();
						//waitingQueue.remove(element);
					//}
				} catch (Exception e2) {
					try { pstmt.close(); } catch (Exception e1) {}
					try { con.close(); } catch (Exception e1) {}
					try { con = ConnectionManager.getConnection(); } catch (Exception e1) {}
				}
				element = waitingQueue.poll();
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			try { pstmt.close(); } catch (Exception e1) {}
			try { con.close(); } catch (Exception e1) {}
		}
	}
	
	public static void processList(){
		queueLock.lock();
		executeQueue();
		queueLock.unlock();
	}
}
