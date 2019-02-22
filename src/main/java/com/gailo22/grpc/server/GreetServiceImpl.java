package com.gailo22.grpc.server;

import com.gailo22.proto.greet.GreetRequest;
import com.gailo22.proto.greet.GreetResponse;
import com.gailo22.proto.greet.GreetServiceGrpc;
import com.gailo22.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String result = String.format("Hello %s, %s", greeting.getFirstName(), greeting.getLastName());

        GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
