package edu.bethlehem.scinexus.Auth;


import edu.bethlehem.scinexus.User.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RegisterRequest {

    private String name;
    private String username;
    private String email;
    private String password;
    private Role role;
}