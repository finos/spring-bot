package com.github.deutschebank.symphony.spring.app.config;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@PropertySource("classpath:/ssl/ssl-defaults.properties")
@Order(99)
public class SymphonyAppWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

}
