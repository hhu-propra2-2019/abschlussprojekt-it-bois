package mops.gruppen2.service;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import mops.gruppen2.domain.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public final class CsvService {

    private CsvService() {}

    static List<User> read(InputStream stream) throws IOException {
        CsvMapper mapper = new CsvMapper();

        CsvSchema schema = mapper.schemaFor(User.class).withHeader().withColumnReordering(true);
        ObjectReader reader = mapper.readerFor(User.class).with(schema);

        return reader.<User>readValues(stream).readAll();
    }
}
