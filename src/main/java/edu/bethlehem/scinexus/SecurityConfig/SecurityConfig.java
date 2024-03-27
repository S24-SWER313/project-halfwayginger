package edu.bethlehem.scinexus.SecurityConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import edu.bethlehem.scinexus.Journal.Journal;
import edu.bethlehem.scinexus.Journal.JournalNotFoundException;
import edu.bethlehem.scinexus.Journal.JournalRepository;
import edu.bethlehem.scinexus.Journal.Visibility;
import edu.bethlehem.scinexus.User.UserService;
import edu.bethlehem.scinexus.User.User;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;
        private final JournalRepository journalRepository;

        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                httpSecurity
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(HttpRequestAuthorizer -> HttpRequestAuthorizer
                                                .requestMatchers("/api/v1/auth/**").permitAll()

                                                .requestMatchers(HttpMethod.GET, "/articles/{journalId}")
                                                .access(readJournals())
                                                .requestMatchers(HttpMethod.PUT, "/articles/{journalId}")
                                                .access(journalOwnerContributors())
                                                .requestMatchers(HttpMethod.PATCH, "/articles/{journalId}")
                                                .access(journalOwnerContributors())

                                                .requestMatchers(HttpMethod.DELETE, "/articles/{journalId}")
                                                .access(journalOwner())

                                                .requestMatchers("/journals/{journalId}/contributors/{contributorId}")
                                                .access(journalOwner())

                                                .anyRequest().authenticated())
                                .sessionManagement(sessionConfigurer -> sessionConfigurer
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .formLogin(Customizer.withDefaults())
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return httpSecurity.build();
        }


//        @Bean
//        public InMemoryUserDetailsManager userDetailsManager(){
//                InMemoryUserDetailsManager inMemoryUserDetailsManager=new InMemoryUserDetailsManager();
//                UserDetails admin= org.springframework.security.core.userdetails.User.withUsername("admin").password("1234").authorities("admin").build();
//                inMemoryUserDetailsManager.createUser(admin);
//                return inMemoryUserDetailsManager;
//        }


        AuthorizationManager<RequestAuthorizationContext> readJournals() {
                return new AuthorizationManager<RequestAuthorizationContext>() {
                        @Override
                        public AuthorizationDecision check(Supplier<Authentication> authentication,
                                        RequestAuthorizationContext object) {
                                Long journalId = Long.parseLong(object.getVariables().get("journalId"));
                                Journal journal = journalRepository.findById(journalId)
                                                .orElseThrow(() -> new JournalNotFoundException(journalId,
                                                                HttpStatus.NOT_FOUND));
                                User user = (User) authentication.get().getPrincipal();
                                // check if the user is the publisher journal
                                if (journal.getVisibility().equals(Visibility.PUBLIC)) {
                                        return new AuthorizationDecision(true);
                                } else if (journal.getPublisher().getId().equals(user.getId())) {
                                        return new AuthorizationDecision(true);
                                }
                                Boolean isContributor = journal.getContributors().stream()
                                                .anyMatch(contributor -> contributor.getId().equals(user.getId()));
                                if (isContributor)
                                        return new AuthorizationDecision(true);

                                // add LINKS Visibility check
                                if (journal.getVisibility().equals(Visibility.LINKS)) {
                                        Long count = entityManager.createQuery(
                                                        "SELECT COUNT(u) FROM User u JOIN u.links l " +
                                                                        "WHERE u.id = :userId1 AND l.id = :userId2",
                                                        Long.class)
                                                        .setParameter("userId1", user.getId())
                                                        .setParameter("userId2", journal.getPublisher().getId())
                                                        .getSingleResult();

                                        if (count > 0)
                                                return new AuthorizationDecision(true);
                                }
                                return new AuthorizationDecision(false);

                        }
                };
        }

        AuthorizationManager<RequestAuthorizationContext> journalOwner() {
                return new AuthorizationManager<RequestAuthorizationContext>() {
                        @Override
                        public AuthorizationDecision check(Supplier<Authentication> authentication,
                                        RequestAuthorizationContext object) {
                                System.out.println("journalOwnerFIRST");
                                Long journalId = Long.parseLong(object.getVariables().get("journalId"));
                                Journal journal = journalRepository.findById(journalId)
                                                .orElseThrow(() -> new JournalNotFoundException(journalId,
                                                                HttpStatus.NOT_FOUND));
                                User user = (User) authentication.get().getPrincipal();
                                // check if the user is the publisher journal

                                if (journal.getPublisher().getId().equals(user.getId())) {
                                        return new AuthorizationDecision(true);
                                }

                                return new AuthorizationDecision(false);

                        }
                };
        }

        AuthorizationManager<RequestAuthorizationContext> journalOwnerContributors() {
                return new AuthorizationManager<RequestAuthorizationContext>() {
                        @Override
                        public AuthorizationDecision check(Supplier<Authentication> authentication,
                                        RequestAuthorizationContext object) {
                                Long journalId = Long.parseLong(object.getVariables().get("journalId"));
                                Journal journal = journalRepository.findById(journalId)
                                                .orElseThrow(() -> new JournalNotFoundException(journalId,
                                                                HttpStatus.NOT_FOUND));
                                User user = (User) authentication.get().getPrincipal();
                                // check if the user is the publisher journal
                                if (journal.getPublisher().getId().equals(user.getId())) {
                                        return new AuthorizationDecision(true);
                                }
                                Boolean isContributor = journal.getContributors().stream()
                                                .anyMatch(contributor -> contributor.getId().equals(user.getId()));
                                if (isContributor)
                                        return new AuthorizationDecision(true);

                                return new AuthorizationDecision(false);

                        }
                };
        }

}