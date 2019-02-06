package com.andj.HelpLinguo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Configuration
    @Order(1)
    public static class SecurityConfigurationOne extends WebSecurityConfigurerAdapter {

        @Autowired
        private BCryptPasswordEncoder bCryptPasswordEncoder;

        @Autowired
        private DataSource dataSource;

        @Value("${spring.queries.users-query}")
        private String usersQuery;

        @Value("${spring.queries.roles-query}")
        private String rolesQuery;

        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {
            auth.
                    jdbcAuthentication()
                    .usersByUsernameQuery(usersQuery)
                    .authoritiesByUsernameQuery(rolesQuery)
                    .dataSource(dataSource)
                    .passwordEncoder(bCryptPasswordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.
                    authorizeRequests()
                    .antMatchers("/").permitAll()
                    .antMatchers("/pl").permitAll()
                    .antMatchers("/landing").permitAll()
                    .antMatchers("/registration").permitAll()
                    .antMatchers("/browse").permitAll()
                    .antMatchers("/landing_pl").permitAll()
                    .antMatchers("/registration_pl").permitAll()
                    .antMatchers("/browse_pl").permitAll()
                    .antMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
                    .authenticated().and().csrf().disable().formLogin()
                    .loginPage("/landing").failureUrl("/landing?error=true")
                    .defaultSuccessUrl("/admin/home")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .and().logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/").and().exceptionHandling()
                    .accessDeniedPage("/access-denied");
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
        }

    }


    @Configuration
    @Order(2)
    public static class SecurityConfigurationTwo extends WebSecurityConfigurerAdapter {

        @Autowired
        private BCryptPasswordEncoder bCryptPasswordEncoder;

        @Autowired
        private DataSource dataSource;

        @Value("${spring.queries.users-query}")
        private String usersQuery;

        @Value("${spring.queries.roles-query}")
        private String rolesQuery;

        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {
            auth.
                    jdbcAuthentication()
                    .usersByUsernameQuery(usersQuery)
                    .authoritiesByUsernameQuery(rolesQuery)
                    .dataSource(dataSource)
                    .passwordEncoder(bCryptPasswordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.
                    authorizeRequests()
                    .antMatchers("/").permitAll()
                    .antMatchers("/pl").permitAll()
                    .antMatchers("/landing_pl").permitAll()
                    .antMatchers("/registration_pl").permitAll()
                    .antMatchers("/browse_pl").permitAll()
                    .antMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
                    .authenticated().and().csrf().disable().formLogin()
                    .loginPage("/landing_pl").failureUrl("/landing_pl?error=true")
                    .defaultSuccessUrl("/admin/home_pl")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .and().logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout_pl"))
                    .logoutSuccessUrl("/pl").and().exceptionHandling()
                    .accessDeniedPage("/access-denied");
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
        }

    }
}