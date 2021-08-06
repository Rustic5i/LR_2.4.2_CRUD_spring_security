package web.model;

import org.hibernate.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users")
@DynamicUpdate(value = true) // По умолчанию Hibernate если мы обновляем только одно поле.
// То он все равно обновляет все поля. Он делает это, потому что с нами параллельно может кто то работать с этим обьектом,
// и мы могли получить не то что хотели. По этому Hibernate дает нам возможность вк флажки. Мы говорим. Меняй только те поля,
// которые я обновляю
//@DynamicInsert(value = true)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId(mutable = true)
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 3, max = 30, message = "Name should be between 2 and 30 characters")
    @Column
    private String username;

    @Min(value = 0, message = "Age should be greater than 0")
    @Column
    private int age;

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    @Column
    private String email;

    @Column
    @NotEmpty(message = "password should not be empty")
    private String password;


    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "users_role"
            , joinColumns = @JoinColumn(name = "user_id")
            , inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String name, int age, String email) {
        this.username = name;
        this.age = age;
        this.email = email;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUser().remove(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //Предоставленные Полномочия
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { //срок Действия Учетной Записи Не Истек
        return false;
    } // срок Действия Учетной Записи Не Истек

    @Override
    public boolean isAccountNonLocked() { //не Заблокирована Ли Учетная запись
        return false;
    } // не Заблокирована Ли Учетная запись

    @Override
    public boolean isCredentialsNonExpired() { //не Истек Ли Срок Действия Учетных Данных
        return false;
    } // не Истек Ли Срок Действия Учетных Данных

    @Override
    public boolean isEnabled() { // включено
        return false;
    } //ВКЛЮЧЕН

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}