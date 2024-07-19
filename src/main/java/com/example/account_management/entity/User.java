package unicore.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.util.Collection;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @ManyToOne(optional = true)
    @JoinColumn(name = "environment_id")
    private Environment environment;

    @OneToMany (mappedBy="user", fetch=FetchType.EAGER)
    @OrderBy("changeTimestamp DESC")
    private Collection<Ticket> tickets;

    @OneToMany (mappedBy="support", fetch=FetchType.EAGER)
    private Collection<Ticket> supportTickets;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    public void print()
    {
        System.out.println("========================================");
        System.out.println("Id      : " + getId());
        System.out.println("Email   : " + getEmail());
        System.out.println("========================================");
    }
}
