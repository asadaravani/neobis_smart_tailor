//package kg.neobis.smarttailor.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.Nullable;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
////    BlackListTokenService blackListTokenService;
//    JwtUtil jwtUtil;
//    UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain filterChain) throws ServletException, IOException {
//        final String authHeader = request.getHeader("Authorization");
//        final String userEmail;
//        final String jwt;
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            if (filterChain != null)
//                filterChain.doFilter(request, response);
//            return;
//        }
//        jwt = authHeader.substring(7);
//        userEmail = jwtUtil.extractUsername(jwt);
//        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
////            if (blackListTokenService.isTokenBlacklisted(jwt)) {
////                if (filterChain != null)
////                    filterChain.doFilter(request, response);
////                return;
////            }
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//            if (jwtUtil.isTokenValid(jwt, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        if (filterChain != null) {
//            filterChain.doFilter(request, response);
//        }
//    }
//}