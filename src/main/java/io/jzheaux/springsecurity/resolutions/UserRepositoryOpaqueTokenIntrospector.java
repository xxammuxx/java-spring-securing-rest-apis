package io.jzheaux.springsecurity.resolutions;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRepositoryOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private final OpaqueTokenIntrospector delegate;
    private final UserRepository users;

    public UserRepositoryOpaqueTokenIntrospector(UserRepository users,OpaqueTokenIntrospector delegate) {
        this.delegate = delegate;
        this.users = users;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2AuthenticatedPrincipal principal = this.delegate.introspect(token);
        User user = this.users.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("no user"));
        Collection<GrantedAuthority> authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.substring(6))//strip off the "SCOPE_" prefix
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        Collection<GrantedAuthority> userAuthorities = user.getUserAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());
        authorities.retainAll(userAuthorities);
        boolean isPremium = "premium".equals(user.getSubscription());
        boolean hasResolutionWrite = authorities.contains(new SimpleGrantedAuthority("resolution:write"));
        if (isPremium && hasResolutionWrite) {
            authorities.add(new SimpleGrantedAuthority("resolution:share"));
        }
        //return new DefaultOAuth2AuthenticatedPrincipal(principal.getAttributes(), authorities);
        return new UserOAuth2AuthenticatedPrincipal(user, principal.getAttributes(), authorities);
    }

    @Bean
    public OpaqueTokenIntrospector introspector(
            UserRepository users, OAuth2ResourceServerProperties properties) {
        OpaqueTokenIntrospector introspector = new NimbusOpaqueTokenIntrospector(
                properties.getOpaquetoken().getIntrospectionUri(),
                properties.getOpaquetoken().getClientId(),
                properties.getOpaquetoken().getClientSecret());
        return new UserRepositoryOpaqueTokenIntrospector(users, introspector);
    }

    private static class UserOAuth2AuthenticatedPrincipal extends User implements OAuth2AuthenticatedPrincipal {
        private Map<String, Object> attributes;
        private Collection<GrantedAuthority> authorities;

        public UserOAuth2AuthenticatedPrincipal(
                User user, Map<String, Object> attributes, Collection<GrantedAuthority> authorities) {
            super(user);
            this.attributes = attributes;
            this.authorities = authorities;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return this.attributes;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return this.authorities;
        }

        @Override
        public String getName() {
            return this.username;
        }
    }
}
