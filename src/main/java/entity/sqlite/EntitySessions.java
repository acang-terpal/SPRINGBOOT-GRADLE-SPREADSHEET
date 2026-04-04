package entity.sqlite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@SqlResultSetMapping(
    name = "SessionsWithCustomAliasMapping",
    entities = @EntityResult(
        entityClass = EntitySessions.class,
        fields = {
            @FieldResult(name = "session_id", column = "session_id"),
            @FieldResult(name = "created", column = "created"),
            @FieldResult(name = "modified", column = "modified"),
            @FieldResult(name = "session_value", column = "session_value"),
            @FieldResult(name = "timestamp_expired", column = "timestamp_expired")
        }
    )
)
@NamedNativeQuery(
        name="getCookie",
        query="select id as session_id, created, modified, data as session_value, expires as timestamp_expired from sessions where id = :sessionId ",
        resultSetMapping = "SessionsWithCustomAliasMapping"
)

@Entity
@Table(name = "sessions")
public class EntitySessions {
    @Id
    @Column(name = "id", nullable = false)
    @Getter @Setter
    private String session_id;

    @Column(name = "created")
    @Getter @Setter
    private String created;

    @Column(name = "modified")
    @Getter @Setter
    private String modified;

    @Column(name = "data")
    @Getter @Setter
    private String session_value;

    @Column(name = "expires")
    @Getter @Setter
    private String timestamp_expired;
}
