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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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
    /**Method to process google Sign In) */
    @ApiMethod(name = "signInGoogle")
    public HouSyncUser signInGoogle(@Named("googleUserId") String googleUserId, HouSyncUser user) {
        HouSyncUser response = new HouSyncUser();

        String url = getDBUrl();
        StringWriter error = new StringWriter();

        if (url == null){
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


    /**Method to process Facebook Sign In) */
    @ApiMethod(name = "signInFacebook")
    public HouSyncUser signInFacebook(@Named("facebookUserId") String facebookUserId, HouSyncUser user) {
        HouSyncUser response = new HouSyncUser();

        String url = getDBUrl();
        StringWriter error = new StringWriter();

        if (url == null){
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

    private String getDBUrl() {
        String url;
        try {
            // Load the class that provides the new "jdbc:google:mysql://" prefix.
            Class.forName("com.mysql.jdbc.GoogleDriver");
            url = "jdbc:google:mysql://housync-android:housync-db/housync_user?user=root";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

}
