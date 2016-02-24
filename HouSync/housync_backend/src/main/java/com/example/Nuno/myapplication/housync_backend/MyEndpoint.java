/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.Nuno.myapplication.housync_backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
    name = "myApi",
    version = "v1",
    namespace = @ApiNamespace(
    ownerDomain = "housync_backend.myapplication.Nuno.example.com",
    ownerName = "housync_backend.myapplication.Nuno.example.com",
    packagePath=""
    )
)
public class MyEndpoint {
    private final static int USER_DB = 1;
    private final static int HOUSE_DB = 2;


    /**
     * Process google Sign In
     *
     * @param googleUserId
     * @param user
     * @return
     */
    @ApiMethod(name = "signInGoogle")
    public HouSyncUser signInGoogle(@Named("googleUserId") String googleUserId, HouSyncUser user) {
        HouSyncUser response = new HouSyncUser();

        String url = null;
        StringWriter error = new StringWriter();

        try {
            url = getDBUrl(USER_DB);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setErrorCode(-1);
            return response;
        }

        String statement;
        PreparedStatement stmt;
        ResultSet resultSet;

        try {
            Connection conn = DriverManager.getConnection(url);
            try {
                if (user.getUserId() != 0){
                    //User is LogedIn
                    statement = "SELECT id_google FROM google_user " +
                            "WHERE id_housync = ?;";
                    stmt = conn.prepareStatement(statement);
                    stmt.setInt(1, user.getUserId());
                    resultSet = stmt.executeQuery();

                    if (resultSet.first()){
                        //User already associated to a google account
                        if (resultSet.getString("id_google") != googleUserId){
                            //User is associated to a different Google Account
                            response.setErrorCode(1);
                        }else{
                            //User and Google account already associated
                            response = user;
                        }
                    }else{
                        //User not associated to a google account
                        statement = "INSERT INTO google_user (id_google, id_housync) " +
                                "VALUES (?, ?);";
                        stmt = conn.prepareStatement(statement);
                        stmt.setString(1, googleUserId);
                        stmt.setInt(2, user.getUserId());
                        stmt.execute();

                        response = user;
                    }
                }else{
                    //User is not loged in
                    statement = "SELECT id_housync FROM google_user " +
                            "where id_google = ?;";
                    stmt = conn.prepareStatement(statement);
                    stmt.setString(1, googleUserId);
                    resultSet = stmt.executeQuery();

                    if (resultSet.first()){
                        //User already exists in the HouSync DB
                        response.setUserId(resultSet.getInt("id_housync"));
                    }else {
                        //User not in the HouSync DB
                        int userId = createNewUserInDB(googleUserId, conn);

                        response.setUserId(userId);
                        response.setUserName(user.getUserName());
                        response.setEmail(user.getEmail());

                        updateUserData(response, conn);

                        statement = "INSERT INTO google_user (id_google, id_housync) " +
                                "VALUES (?, ?);";
                        stmt = conn.prepareStatement(statement);
                        stmt.setString(1, googleUserId);
                        stmt.setInt(2, response.getUserId());
                        stmt.execute();
                    }

                    HouSyncUser userInDB = getUserData(response.getUserId(), conn);
                    response.setUserName(userInDB.getUserName());
                    response.setEmail(userInDB.getEmail());

                }
            }finally {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(new PrintWriter(error));
            response.setUserName(error.toString());
            response.setErrorCode(2);
        }

        return response;
    }



    /**
     * Process Facebook Sign In
     *
     * @param facebookUserId
     * @param user
     * @return
     */
    @ApiMethod(name = "signInFacebook")
    public HouSyncUser signInFacebook(@Named("facebookUserId") String facebookUserId, HouSyncUser user) {
        HouSyncUser response = new HouSyncUser();

        String url = null;
        StringWriter error = new StringWriter();

        try {
            url = getDBUrl(USER_DB);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setErrorCode(-1);
            return response;
        }

        String statement;
        PreparedStatement stmt;
        ResultSet resultSet;

        try {
            Connection conn = DriverManager.getConnection(url);
            try {
                if (user.getUserId() != 0){
                    //User is LogedIn
                    statement = "SELECT id_facebook FROM facebook_user " +
                            "WHERE id_housync = ?;";
                    stmt = conn.prepareStatement(statement);
                    stmt.setInt(1, user.getUserId());
                    resultSet = stmt.executeQuery();

                    if (resultSet.first()){
                        //User already associated to a facebook account
                        if (resultSet.getString("id_facebook") != facebookUserId){
                            //User is associated to a different Google Account
                            response.setErrorCode(1);
                        }else{
                            //User and Google account already associated
                            response = user;
                        }
                    }else{
                        //User not associated to a facebook account
                        statement = "INSERT INTO facebook_user (id_facebook, id_housync) " +
                                "VALUES (?, ?);";
                        stmt = conn.prepareStatement(statement);
                        stmt.setString(1, facebookUserId);
                        stmt.setInt(2, user.getUserId());
                        stmt.execute();

                        response = user;
                    }
                }else{
                    //User is not loged in
                    statement = "SELECT id_housync FROM facebook_user " +
                            "where id_facebook = ?;";
                    stmt = conn.prepareStatement(statement);
                    stmt.setString(1, facebookUserId);
                    resultSet = stmt.executeQuery();

                    if (resultSet.first()){
                        //User already exists in the HouSync DB
                        response.setUserId(resultSet.getInt("id_housync"));
                    }else {
                        //User not in the HouSync DB
                        int userId = createNewUserInDB(facebookUserId, conn);

                        response.setUserId(userId);
                        response.setUserName(user.getUserName());
                        response.setEmail(user.getEmail());

                        updateUserData(response, conn);

                        statement = "INSERT INTO facebook_user (id_facebook, id_housync) " +
                                "VALUES (?, ?);";
                        stmt = conn.prepareStatement(statement);
                        stmt.setString(1, facebookUserId);
                        stmt.setInt(2, response.getUserId());
                        stmt.execute();
                    }

                    HouSyncUser userInDB = getUserData(response.getUserId(), conn);
                    response.setUserName(userInDB.getUserName());
                    response.setEmail(userInDB.getEmail());

                }
            }finally {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(new PrintWriter(error));
            response.setUserName(error.toString());
            response.setErrorCode(2);
        }

        return response;
    }


    @ApiMethod(name = "createHouse")
    public HouSyncHouse createHouse(HouSyncHouse house){
        HouSyncHouse response = null;

        try {
            String url = getDBUrl(HOUSE_DB);
            Connection connection = DriverManager.getConnection(url);
            try {
                response = createHouse(house.getHouseName(), house.getAdminId(), connection);

            }finally {
                connection.close();
            }
        } catch (ClassNotFoundException e) {
            response = getErrorResponse (e, -1);
        } catch (SQLException e) {
            response = getErrorResponse (e, -2);
        }

        return response;
    }

    @ApiMethod(name = "getHouseData")
    public HouSyncHouse getHouseData(@Named("houseId")int houseId){
        HouSyncHouse response = null;

        try {
            String url = getDBUrl(HOUSE_DB);
            Connection connection = DriverManager.getConnection(url);
            try {
                response = getHouseData(houseId, connection);

            }finally {
                connection.close();
            }
        } catch (ClassNotFoundException e) {
            response = getErrorResponse (e, -1);
        } catch (SQLException e) {
            response = getErrorResponse (e, -2);
        }

        return response;
    }

    @ApiMethod(name = "getAllHouses")
    public List<HouSyncHouse> getAllHouses(@Named("userId") int houSyncUserId){
        List<HouSyncHouse> houSyncHouses = new ArrayList<HouSyncHouse>();

        try {
            String url = getDBUrl(HOUSE_DB);
            Connection connection = DriverManager.getConnection(url);
            try {
                String statement = "SELECT * " +
                        "FROM house inner join user_house " +
                        "WHERE id = id_house " +
                        "AND id_user = ?";
                PreparedStatement stmt = connection.prepareStatement(statement);
                stmt.setInt(1, houSyncUserId);
                ResultSet resultSet = stmt.executeQuery();

                while (resultSet.next()){
                    int houseId = resultSet.getInt("id");
                    String houseName = resultSet.getString("name");
                    int adminId = resultSet.getInt("id_admin");
                    HouSyncHouse house = new HouSyncHouse(houseId, houseName, adminId);
                    houSyncHouses.add(house);
                }

            }finally {
                connection.close();
            }
        } catch (ClassNotFoundException e) {
            HouSyncHouse response = getErrorResponse (e, -1);
            houSyncHouses.clear();
            houSyncHouses.add(response);
        } catch (SQLException e) {
            HouSyncHouse response = getErrorResponse (e, -2);
            houSyncHouses.clear();
            houSyncHouses.add(response);
        }

        return houSyncHouses;

    }


    private String getDBUrl(int db) throws ClassNotFoundException {
        String url = null;

        Class.forName("com.mysql.jdbc.GoogleDriver");
        switch (db){
            case (USER_DB):
                url = "jdbc:google:mysql://housync-android:housync-db/housync_user?user=root";
                break;
            case (HOUSE_DB):
                url = "jdbc:google:mysql://housync-android:housync-db/housync_house?user=root";
                break;
        }

        return url;
    }
    
    private int createNewUserInDB(String socialUserId, Connection connection) throws SQLException{
        String statement = "INSERT INTO user (name) " +
                "VALUES (?);";
        PreparedStatement stmt = connection.prepareStatement(statement);
        String socialIdHash = Integer.toString(socialUserId.hashCode());
        stmt.setString(1, socialIdHash);
        stmt.execute();

        statement = "SELECT id FROM user " +
                "WHERE name = ? " +
                "ORDER BY create_time DESC;";
        stmt = connection.prepareStatement(statement);
        stmt.setString(1, socialIdHash);
        ResultSet resultSet = stmt.executeQuery();
        resultSet.first();
        
        return resultSet.getInt("id");
    }
    
    private void updateUserData(HouSyncUser request, Connection connection) throws SQLException{
        String statement = "UPDATE user " +
                "SET name = ?," +
                "email = ? " +
                "WHERE id = ?;";
        PreparedStatement stmt = connection.prepareStatement(statement);
        if (request.getUserName()!= null)
            stmt.setString(1, request.getUserName());
        else
            stmt.setNull(1, Types.VARCHAR);

        if (request.getEmail()!= null)
            stmt.setString(2, request.getEmail());
        else
            stmt.setNull(2, Types.VARCHAR);

        stmt.setInt(3, request.getUserId());
        stmt.executeUpdate();
    }

    private HouSyncUser getUserData(int userId, Connection connection) throws SQLException{
        HouSyncUser response = new HouSyncUser();

        String statement = "SELECT name, email FROM user " +
                "WHERE id = ? " +
                "LIMIT 1;";
        PreparedStatement stmt = connection.prepareStatement(statement);
        stmt.setInt(1, userId);
        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.first()){
            response.setUserName(resultSet.getString("name"));
            response.setEmail(resultSet.getString("email"));
        }else{
            response.setErrorCode(3);
        }


        return response;
    }

    private HouSyncHouse getErrorResponse(Exception e, int errorCode) {
        HouSyncHouse response = new HouSyncHouse();
        StringWriter error = new StringWriter();

        e.printStackTrace(new PrintWriter(error));
        response.setHouseName(error.toString());
        response.setErrorCode(errorCode);

        return response;
    }

    private HouSyncHouse getHouseData(int houseId, Connection connection) throws SQLException{
        HouSyncHouse response = new HouSyncHouse(houseId);

        String statement = "SELECT name, id_admin, snapshot, snapshot_user, create_time, last_sync FROM house " +
                "WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(statement);
        stmt.setInt(1, houseId);
        ResultSet resultSet = stmt.executeQuery();

        resultSet.first();
        response.setHouseName(resultSet.getString("name"));
        response.setAdminId(resultSet.getInt("id_admin"));
        response.setSnapShot(resultSet.getString("snapshot"));
        response.setSnapShotUser(resultSet.getString("snapshot_user"));
        response.setCreateTime(resultSet.getString("create_time"));
        response.setLastSync(resultSet.getString("last_sync"));

        return response;
    }

    private HouSyncHouse createHouse(String houseName, int adminId, Connection connection) throws SQLException {
        String statement = "INSERT INTO house (name, id_admin) " +
                "VALUES (?, ?);";
        PreparedStatement stmt = connection.prepareStatement(statement);
        stmt.setString(1, houseName);
        stmt.setInt(2, adminId);
        stmt.execute();

        statement = "SELECT id FROM house " +
                "WHERE id_admin = ? " +
                "ORDER BY create_time DESC;";
        stmt = connection.prepareStatement(statement);
        stmt.setInt(1, adminId);
        ResultSet resultSet = stmt.executeQuery();

        resultSet.first();
        int houseId = resultSet.getInt("id");

        insertUserInHouse(houseId, adminId, connection);

        return getHouseData(houseId, connection);

    }

    private void insertUserInHouse(int houseId, int userId, Connection connection) throws SQLException {
        String statement = "INSERT INTO user_house (id_user, id_house) " +
                "VALUES (?, ?);";
        PreparedStatement stmt = connection.prepareStatement(statement);
        stmt.setInt(1, userId);
        stmt.setInt(2, houseId);
        stmt.execute();
    }

}
