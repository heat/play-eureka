package bootstrap;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class EurekaStartup {

    ApplicationLifecycle lifecycle;

    @Inject
    public EurekaStartup(ApplicationLifecycle lifecycle,
                         ApplicationInfoManager applicationInfoManager,
                         EurekaClient eurekaClient
                         ) {
        this.lifecycle = lifecycle;

        System.out.println("Registrando o aplicativo");
        //fazer alguma configuração do Eureka


        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);

        lifecycle.addStopHook(() -> {
            applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
            eurekaClient.shutdown();
            return CompletableFuture.completedFuture(null);
        });
    }
}
