import bootstrap.EurekaStartup;
import com.google.inject.AbstractModule;
import java.time.Clock;

import com.google.inject.Injector;
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
import play.Configuration;
import play.Environment;
import services.ApplicationTimer;
import services.AtomicCounter;
import services.Counter;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    private final Environment environment;
    private final Configuration configuration;

    public Module(
            Environment environment,
            Configuration configuration) {
        this.environment = environment;
        this.configuration = configuration;
    }


    @Override
    public void configure() {

        // Use the system clock as the default implementation of Clock
        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.
        bind(ApplicationTimer.class).asEagerSingleton();
        // Set AtomicCounter as the implementation for Counter.
        bind(Counter.class).to(AtomicCounter.class);


        DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory.getInstance();
        EurekaInstanceConfig instanceConfig = new MyDataCenterInstanceConfig();
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();

        //Define a applicationInfo
        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);
        bind(ApplicationInfoManager.class).toInstance(applicationInfoManager);

        //configura o client
        EurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
        EurekaClient eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);

        bind(EurekaClient.class).toInstance(eurekaClient);
        bind(EurekaStartup.class).asEagerSingleton();

    }

}
