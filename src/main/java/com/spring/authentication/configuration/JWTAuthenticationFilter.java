package com.spring.authentication.configuration;
import com.spring.authentication.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/*
* This is the first thing the http request will face which is the filter according to the architecture diagram.
* */
@Service
@RequiredArgsConstructor // this basically creates a constructor with all the final data members of the class.
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService; // this will fetch the user from the database.
    /*
    * We use OncePerRequestFilter class because everytime the http request is fired filer has to process the request,.
    * */
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            validateJWT(request, response, filterChain);
            filterChain.doFilter(request, response);
        }catch (AuthenticationException ex) {
            // Let Spring Security handle the exception
            SecurityContextHolder.clearContext();
            throw ex;  // Propagate the exception to be handled by Spring Security
        }


    }

    private void validateJWT(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        /*
        * request : The Http request
        * response : Our response from the database
        * filterChain : A FilterChain is an object provided by the servlet container to the developer giving a view into the invocation chain of a filtered request
        * for a resource. Filters use the FilterChain to invoke the next filter in the chain, or if the calling filter is the last filter in the chain,
        * to invoke the resource at the end of the chain.
        * */
        final String authHeader = request.getHeader("Authorization"); // extracts the header from the jwt token. header's key is Authorization
        final String jwtToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) { // the header always starts with key : Bearer
            filterChain.doFilter(request,response);
    return;
        // we dont want to continue because the token is not available
        }
        jwtToken = authHeader.substring(7); // 7 because after Bearer and the space the next index is 7
        userEmail = jwtService.extractUserName(jwtToken);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { // the token has not been authenticated yet
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwtToken, userDetails)) { // this basically means our Token is validated
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // because we don't have credentials
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Giving the authToken more details
                SecurityContextHolder.getContext().setAuthentication(authToken); // letting the framework know that this has been authenticated so that the next time
                // line 45 will not be null
            }
        }


    }
}
