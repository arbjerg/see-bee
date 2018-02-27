package com.namely.seebee.crudreactor.sqlserver.internal;

import com.namely.seebee.configuration.ConfigurationResolver;
import com.namely.seebee.crudreactor.sqlserver.internal.eventpolling.PollingStrategy;

@ConfigurationResolver.ConfigurationBean(key = "sqlserver-reactor")
public class SqlServerReactorConfiguration {
    private PollingStrategy pollingStrategy = PollingStrategy.SNAPSHOT;

    public PollingStrategy pollingStrategy() {
        return pollingStrategy;
    }

    public void setPollingStrategy(String strategyName) {
        PollingStrategy.fromString(strategyName).ifPresent(newStrategy -> pollingStrategy = newStrategy);
    }

}
