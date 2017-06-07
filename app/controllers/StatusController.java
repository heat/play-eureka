package controllers;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class StatusController extends Controller {

    EurekaClient eurekaClient;

    @Inject
    public StatusController(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    public Result getStatus() {
        InstanceInfo nextServerInfo = null;

        nextServerInfo = eurekaClient.getNextServerFromEureka("localhost", false);
        return ok(Json.toJson(nextServerInfo.getStatus()));
    }
}
