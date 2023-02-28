package controller;

import model.Favourite;
import model.Poi;
import model.Shop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBZugriff
{
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
	}

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
	public static List<Shop> search(String sCategory, String sName, Poi sPoi, double sDistance) {
		List<Shop> ret = new ArrayList<Shop>();
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM MARS.SHOPS" +
						" WHERE TRUE " +
						((sCategory != null) ? "AND CATEGORY = ?" : "") +
                        ((sName != null) ? " AND SHOPNAME LIKE ? ESCAPE '!'" : "") +
                        ((sPoi != null) ? " AND LONGITUDE <= ? AND LONGITUDE >= ? " +
                                          " AND LATITUDE <= ? AND LATITUDE >= ?" : "") + ";";
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
                pstmt.setDouble(counter, sPoi.getLongitude() + sDistance);
                counter++;
                pstmt.setDouble(counter, sPoi.getLongitude() - sDistance);
                counter++;
                pstmt.setDouble(counter, sPoi.getLatitude() + sDistance);
                counter++;
                pstmt.setDouble(counter, sPoi.getLatitude() - sDistance);
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
				ret.add(s);
			}

		}catch(Exception e){
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
			System.err.println(e.getClass() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
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
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
			System.err.println(e.getClass() + ": " + e.getMessage());
			ret = null;
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
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
			System.err.println(e.getClass() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}


	/**
	 * adds a favourite to the database
	 */
	public static Boolean addFavourite(Favourite favourite){
		Boolean ret;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO MARS.FAVOURITES (FNAME, SCATEGORY, SNAME, SPOI, SDISTANCE) " +
						" VALUES (?, ?, ?, ?, ?);";
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, favourite.getName());
			pstmt.setString(2, favourite.getsCategory());
			pstmt.setString(3, favourite.getsName());
			pstmt.setString(4, favourite.getsPoi() != null ? favourite.getsPoi().getId() : null);
			pstmt.setInt(5, favourite.getsDistance() != null ? favourite.getsDistance() : 0);
			ret = pstmt.executeUpdate() == 1;
		}catch(SQLException e){
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
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
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			ret = null;
		}
		System.out.println("isFAV: " + ret);
		return ret;
	}

	public static Boolean deleteFavourite(String name) {
		Boolean ret;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM MARS.FAVOURITES " +
				" WHERE FNAME = ?;";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, name);
			ret = pstmt.executeUpdate() > 0;
		}catch(Exception e){
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
			ret = null;
		}
		return ret;
	}

	public static Boolean updateFavourite(Favourite favourite) {
		Boolean ret;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE MARS.FAVOURITES " +
						" SET SCATEGORY = ?," +
						" 	  SNAME = ?, " +
						" 	  SPOI = ?, " +
						" 	  SDISTANCE = ? " +
						" WHERE FNAME = ?;";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, favourite.getsCategory());
			pstmt.setString(2, favourite.getsName());
			pstmt.setString(3, favourite.getsPoi() != null ? favourite.getsPoi().getId() : null);
			pstmt.setInt(4, favourite.getsDistance() != null ? favourite.getsDistance() : 0);
			pstmt.setString(5, favourite.getName());
			ret = pstmt.executeUpdate() == 1;
		}catch(Exception e){
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
			ret = null;
		}
		return ret;
	}

	public static Boolean addShop(Shop shop){
		Boolean ret;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO MARS.SHOPS (LONGITUDE, LATITUDE, CATEGORY, SHOPNAME, OPENINGHOURS, WEBSITE) " +
				" VALUES (?, ?, ?, ?, ?, ?);";
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setDouble(1, shop.getLongitude());
			pstmt.setDouble(2, shop.getLatitude());
			pstmt.setString(3, shop.getCategory());
			pstmt.setString(4, shop.getShopname());
			pstmt.setString(5, shop.getOpeningHours());
			pstmt.setString(6, shop.getWebsite());
			ret = pstmt.executeUpdate() == 1;
		}catch(SQLException e){
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			ret = null;
		}
		return ret;
	}
	
	public static Boolean deleteShop(long shopId){
		Boolean ret;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM MARS.SHOPS " +
				" WHERE ID = ?;";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, shopId);
			ret = pstmt.executeUpdate() > 0;
		}catch(Exception e){
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
			ret = null;
		}
		return ret;
	}
	
	public static Boolean updateShop(Shop shop){
		Boolean ret;
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE MARS.SHOPS " +
				" SET LONGITUDE = ?," +
				" 	  LATITUDE = ?, " +
				" 	  CATEGORY = ?, " +
				" 	  SHOPNAME = ?, " +
				" 	  OPENINGHOURS = ?, " +
				" 	  WEBSITE = ? " +
				" WHERE ID = ?;";
		ResultSet rs = null;
		try{
			con = ConnectionManager.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setDouble(1, shop.getLongitude());
			pstmt.setDouble(2, shop.getLatitude());
			pstmt.setString(3, shop.getCategory());
			pstmt.setString(4, shop.getShopname());
			pstmt.setString(5, shop.getOpeningHours());
			pstmt.setString(6, shop.getWebsite());
			pstmt.setLong(7, shop.getId());
			ret = pstmt.executeUpdate() == 1;
		}catch(Exception e){
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
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
			try{con.close();}catch(Exception e1){;}
			try{pstmt.close();}catch(Exception e1){;}
			try{rs.close();}catch(Exception e1){;}
			System.err.println(e.getClass() + ": " + e.getMessage());
			ret = null;
		}
		return ret;
	}
}
