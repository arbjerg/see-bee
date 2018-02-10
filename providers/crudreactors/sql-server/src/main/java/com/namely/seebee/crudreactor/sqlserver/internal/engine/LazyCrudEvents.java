package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.TableCrudEvents;

import java.util.stream.Stream;

class LazyCrudEvents implements CrudEvents {
    private final ConfigurationState configState;
    private final TrackedTableSet tables;

    private final SqlServerNumberedVersion startVersion;
    private final SqlServerNumberedVersion endVersion;

    LazyCrudEvents(ConfigurationState configState,
                   TrackedTableSet tables,
                   SqlServerNumberedVersion startVersion,
                   SqlServerNumberedVersion endVersion) {
        this.configState = configState;
        this.tables = tables;
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    @Override
    public String endVersion() {
        return endVersion.dumpToString();
    }

    @Override
    public String startVersion() {
        return startVersion.dumpToString();
    }

    @Override
    public Stream<TableCrudEvents> tableEvents() {
        return tables.tables().stream()
                .map(table -> new LazyCrudEventsStreamSupplier(configState, table, startVersion, endVersion));
    }

}
