package com.namely.seebee.test.tester.parquet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.schema.MessageType;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ParquetFile {

    private final Path path;
    private final ParquetMetadata footer;
    private final MessageType schema;

    public ParquetFile(File file) {
        Configuration configuration = new Configuration();
        path = new Path(file.getAbsolutePath());
        try {
            footer = ParquetFileReader.readFooter(configuration, path, ParquetMetadataConverter.NO_FILTER);
            schema = footer.getFileMetaData().getSchema();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<ParquetGroup> stream() {
        Configuration configuration = new Configuration();
        try {
            ParquetFileReader reader = new ParquetFileReader(configuration, path, footer);
            Iterable<ParquetGroup> iterable = () -> iterate(reader);
            return StreamSupport.stream(iterable.spliterator(), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> metadata() {
        return footer.getFileMetaData().getKeyValueMetaData();
    }

    private Iterator<ParquetGroup> iterate(ParquetFileReader reader) throws RuntimeException {
        return new Iterator<ParquetGroup>() {
            private RecordReader<Group> recordReader = null;
            private long rowsLeft = advance();

            @Override
            public boolean hasNext() {
                return rowsLeft > 0;
            }

            @Override
            public ParquetGroup next() {
                Group group = recordReader.read();
                ParquetGroup  result = new ParquetGroup(group);
                if (--rowsLeft == 0) {
                    rowsLeft = advance();
                }
                return result;
            }

            private long advance() throws RuntimeException {
                try {
                    do {
                        PageReadStore pages = reader.readNextRowGroup();
                        if (pages == null) {
                            return 0;
                        }
                        MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
                        recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
                        long rowCount = pages.getRowCount();
                        if (rowCount > 0) {
                            return rowCount;
                        }
                    } while (true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
