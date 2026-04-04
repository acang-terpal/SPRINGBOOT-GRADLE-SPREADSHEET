package entity.sqlite;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "ms_htb")
public class EntityHtb {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "class_id", nullable = false)
    @Getter @Setter
    private String class_id;
    @Getter @Setter
    private String parent_id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String upload_limit_at;
    @Getter @Setter
    private String upload_max_limit_at;
    @Getter @Setter
    private String download_limit_at;
    @Getter @Setter
    private String download_max_limit_at;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false) // Ini baru nama kolom di DB
    private EntityHtb parentHtbClass; // <--- NAMA VARIABEL INI yang dipake di mappedBy!

    @Getter @Setter
    @OneToMany(mappedBy = "parentHtbClass", fetch = FetchType.LAZY) // <--- Harus "parent"
    private Set<EntityHtb> childrenHtbClass = new LinkedHashSet<>();
}