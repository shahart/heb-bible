package edu.hebbible;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import edu.hebbible.model.Pasuk;
import edu.hebbible.service.Svc;
import edu.hebbible.service.impl.ServiceImpl;

public class Handler implements HttpRequestHandler {

    Svc svc = new ServiceImpl();

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) throws IOException, ServletException {
        String requestURI = (String) request.getAttribute("FC_REQUEST_URI");
        int lastSlash = requestURI.lastIndexOf("/");
        String name = requestURI.substring(lastSlash+1);
        List<Pasuk> result = svc.psukim( // findPsukim(log,
                name,false);
        String output = "Total Psukim: " + result.size();
        context.getLogger().info(output);
        OutputStream out = response.getOutputStream();
        out.write(new String(output).getBytes());
        out.flush();
        out.close();
    }


}