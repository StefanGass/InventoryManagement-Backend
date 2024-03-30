package net.inventorymanagement.usercontrolwebservice.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

/**
 * User configuration rest configuration, provides ldap-authentication-instance and -interface.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log4j2
public class WebSecurityConfig {

    @Value("${active.directory.domain}")
    private String adDomain;

    @Value("${active.directory.url}")
    private String adUrl;

    private final Environment environment;

    @Autowired
    public WebSecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(request -> corsConfigurationSource())
                // csrf-protection is enabled by default
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()) // allow any requests, but only if the user is authenticated
                .httpBasic(Customizer.withDefaults()); // use basic authentication with default (autowired) configuration
        return http.build();
    }

    // since cors is not enabled by default, do not allow any requests from cross-origins
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(Arrays.asList("http://example.com"));
        //configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        //configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowedOrigins(Collections.emptyList());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // default configuration for authentication
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (isProduction()) {
            ActiveDirectoryLdapAuthenticationProvider adProvider =
                    new ActiveDirectoryLdapAuthenticationProvider(adDomain, adUrl);
            adProvider.setConvertSubErrorCodesToExceptions(true);
            adProvider.setUseAuthenticationRequestCredentials(true);
            auth.authenticationProvider(adProvider);
        } else {
            auth
                    .inMemoryAuthentication()
                    .withUser("Super Admin")
                    .password("{noop}password")
                    .roles("USER");
        }
    }

    // to stop Spring from auto-generating security passwords
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    private boolean isProduction() {
        String[] environments = this.environment.getActiveProfiles();
        for (String env : environments){
            if (env.equalsIgnoreCase("prod")) {
                return true;
            }
        }
        return false;
    }

}
