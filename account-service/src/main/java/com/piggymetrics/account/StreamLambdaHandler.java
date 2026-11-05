package com.piggymetrics.account;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {
    static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> HANDLER;
    static {
        // API Gateway forwards /accounts/...; the container ignores server.servlet.context-path.
        LambdaContainerHandler.getContainerConfig().setServiceBasePath("/accounts");
        LambdaContainerHandler.getContainerConfig().setStripBasePath(true);
        try {
            HANDLER = SpringBootLambdaContainerHandler.getAwsProxyHandler(AccountApplication.class);
        } catch (ContainerInitializationException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        HANDLER.proxyStream(input, output, context);
    }
}
