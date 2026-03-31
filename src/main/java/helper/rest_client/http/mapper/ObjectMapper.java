package helper.rest_client.http.mapper;

public interface ObjectMapper {

    <T> T readValue(String value, Class<T> valueType);

    String writeValue(Object value);
}
