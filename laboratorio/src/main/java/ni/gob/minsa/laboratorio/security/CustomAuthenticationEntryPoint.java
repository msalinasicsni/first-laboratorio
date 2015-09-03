package ni.gob.minsa.laboratorio.security;

import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by FIRSTICT on 9/2/2015.
 * V1.0
 */
public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
        String xrequestedWith = ((HttpServletRequest) request).getHeader("x-requested-with");
        if (xrequestedWith != null && xrequestedWith.equals("XMLHttpRequest")
                && authException != null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        }
        else{
            super.commence(request, response, authException);
        }
    }
}
