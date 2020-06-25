package io.micronaut.cache.hazelcast

import com.hazelcast.core.HazelcastInstance
import io.micronaut.cache.tck.AbstractAsyncCacheSpec
import io.micronaut.context.ApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.IgnoreIf
import spock.lang.Shared

@Testcontainers
@spock.lang.Retry
@IgnoreIf({System.getenv('GITHUB_WORKFLOW')})
class HazelcastClientAsyncCacheSpec extends AbstractAsyncCacheSpec implements HazelcastClientSupport {

    @Shared
    GenericContainer hazelcast = new GenericContainer("hazelcast/hazelcast:4.0")
            .withExposedPorts(5701)

    @Shared
    HazelcastInstance hazelcastServerInstance


    @Override
    ApplicationContext createApplicationContext() {
        return ApplicationContext.run(
                "hazelcast.client.clusterName": 'dev',
                "hazelcast.client.network.addresses": ["127.0.0.1:${hazelcast.firstMappedPort}"]
        )
    }

}
