/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.Nuno.myapplication.housync_backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.io.IOException;

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

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHitoName")
    public MyBean sayHitoName(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi there, " + name);

        return response;
    }

    /*@ApiMethod(name = "sayHi")
    public MyBean sayHi(User user) throws OAuthRequestException, IOException {
        if (user == null) throw new OAuthRequestException("User is Not Valid");

        MyBean response = new MyBean();
        response.setData("Hi there, "+ user.getNickname());

        return response;
    }*/

}
