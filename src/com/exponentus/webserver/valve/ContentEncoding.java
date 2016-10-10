package com.exponentus.webserver.valve;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

public class ContentEncoding extends ValveBase {

    public ContentEncoding() {
        super();
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String requestURI = request.getRequestURI();

        if (requestURI.endsWith(".js.gz")) {
            response.addHeader("Content-Encoding", "gzip");
        }

        getNext().invoke(request, response);
    }
}
