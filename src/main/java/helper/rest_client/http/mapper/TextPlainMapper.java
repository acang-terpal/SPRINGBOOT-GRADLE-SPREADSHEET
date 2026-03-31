package helper.rest_client.http.mapper;

public class TextPlainMapper implements ObjectMapper {

    @Override
    public <T> T readValue(String value, Class<T> valueType) {
        throw new UnsupportedOperationException("Cannot convert text content to type: " + valueType.getSimpleName());
    }

    @Override
    public String writeValue(Object value) {
        return String.valueOf(value);
    }
}
