package edu.bethlehem.scinexus.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.persistence.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import edu.bethlehem.scinexus.Journal.Journal;
import edu.bethlehem.scinexus.Media.Media;
import edu.bethlehem.scinexus.Notification.Notification;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

// @Entity
// @Table(name = "_user")

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @NotNull(message = "First Name is mandatory")
    @NotBlank(message = "First Name is mandatory")
    @Size(min = 2, max = 30, message = "First Name must be between 2 and 30 Characters")
    private String firstName;

    @NotNull(message = "Last Name is mandatory")
    @NotBlank(message = "Last Name is mandatory")
    @Size(min = 2, max = 30)
    private String lastName;


    @Column(unique = true)
    @NotNull(message = "Username is mandatory")
    @NotBlank(message = "Username is mandatory")
    @Size(min = 1, max = 50)
    private String username;


    @NotNull(message = "Email is mandatory")
    @NotBlank(message = "Email Name is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z0-9]+[._-]*[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}(?:\\.[a-zA-Z]{2,})?$",
            message = "It must start with alphanumeric characters." +
                    "It may contain dots, hyphens, and underscores." +
                    "It must have exactly one '@' symbol." +
                    "It may contain alphanumeric characters in the domain part."+
                    "It may have a maximum of two dots following the '@' symbol")
    @Column(unique = true)
    @Size(min = 3, max = 320, message = "The Email Address should be Between 3 and 320 Characters")
    private String email;

    /*
    Password pattern validation Will Not be held here because the password is stored in the database in encrypted version
    However The password vaildation will be in the RegisterRequest class
     */
    @NotNull(message = "Password is Mandatory")
    @NotBlank(message = "Password is Mandatory")
    private String password;



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_picture_id")
    private Media profilePicture;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_cover_id")
    private Media profileCover;

    @Size(min = 0,max = 220, message = "The bio should be in maximum 220 characters")
    private String bio;


    @Column(unique = true)
    @NotNull(message = "Phone Number is Mandatory")
    @NotBlank(message = "Phone Number is Mandatory")
    //This Regex Status that phone number is valid in any of these formats :
    //(123) 456-7890
    //(123)456-7890
    //123-456-7890
    //123.456.7890
    //1234567890
    @Pattern(regexp = "^\\s*(?:\\+?(\\d{1,3}))?([-. (]*(\\d{3})[-. )]*)?((\\d{3})[-. ]*(\\d{2,4})(?:[-.x ]*(\\d+))?)\\s*$", message = "Phone Number is Not Valid")
    private String phoneNumber;


    @Nullable
    @Size(max = 320)
    private String fieldOfWork;


    @JdbcTypeCode(SqlTypes.JSON)
    private JSONPObject userSettings;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher")
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Journal> journals;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "notifications")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Notification> notifications;

    @ManyToMany
    @JoinTable(name = "journal_user_contributors", joinColumns = @JoinColumn(name = "contributs"), inverseJoinColumns = @JoinColumn(name = "contributors"))
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Journal> contributs;

    @ManyToMany
    @JoinTable(name = "user_link_user", joinColumns = @JoinColumn(name = "linkFrom"), inverseJoinColumns = @JoinColumn(name = "LinkTo"))
    @JdbcTypeCode(SqlTypes.JSON)
    private List<User> links;

    public User(String firstName, String username, String password, String email) {
        this.firstName = firstName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}