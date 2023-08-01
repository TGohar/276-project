package trackour.trackour.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.views.login.LoginPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Session-based authentication using {@linkplain VaadinWebSecurity}
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

        @Autowired
        CustomUserDetailsService customUserDetailsService;

        SecurityConfiguration(CustomUserDetailsService customUserDetailsService) {
                this.customUserDetailsService = customUserDetailsService;
        }

        /**
         * {@link DaoAuthenticationProvider} object to authenticate username & password
         * 
         * @return {@link DaoAuthenticationProvider}
         */
        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

                authProvider.setUserDetailsService(this.customUserDetailsService);
                authProvider.setPasswordEncoder(CustomUserDetailsService.passwordEncoder());

                return authProvider;
        }

        /**
         * Convert {@link AuthenticationManager} bean
         * 
         * @param authConfiguration
         * @return
         * @throws Exception
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration)
                        throws Exception {
                return authConfiguration.getAuthenticationManager();
        }

        /**
         * {@summary} Custom filter chain configuration & {@link VaadinWebSecurity}
         * handles the login logout behaviour
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
                // Can be used for rest api endpoints
                // http.authorizeHttpRequests(requests ->
                // requests.requestMatchers("/resetPassword/**").permitAll());
                // http.authorizeHttpRequests(requests ->
                // requests.requestMatchers("/resetPassword/**").hasAnyAuthority("ADMIN"));
                // http.authorizeHttpRequests(requests ->
                // requests.requestMatchers("/secret").hasAnyRole("ADMIN"));

                // http
                // .authorizeHttpRequests(auth ->
                // auth
                // .requestMatchers("/*").hasAnyRole(Role.ADMIN.getName()));
                super.configure(http);

                // Register your login view to the view access checker mechanism
                setLoginView(http, LoginPage.class, "/login");
                // session fixatioon protection
                // VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
        }

}
