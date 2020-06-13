package com.julian.mapapplication;

import java.util.List;

public class Example {
    public List<Result> results;
    public String status;

    public Result getResult() {
        return results.get(0);
    }

    public List<Result> getResults() {
        return results;
    }
}
