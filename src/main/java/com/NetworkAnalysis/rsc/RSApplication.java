package com.NetworkAnalysis.rsc;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.NetworkAnalysis.app.HelloWorldService;
import com.NetworkAnalysis.app.SearchManagement;
import com.NetworkAnalysis.app.TwitterRequests;
import com.NetworkAnalysis.app.UserManagement;

public class RSApplication extends Application
{
    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(HelloWorldService.class);
        s.add(SearchManagement.class);
        s.add(TwitterRequests.class);
        s.add(UserManagement.class);
        return s;
    }
}