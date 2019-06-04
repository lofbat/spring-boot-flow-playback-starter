package io.github.lofbat.flow.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by geqi on 2019/6/3.
 */
@Configuration
@Data
public class InterceptConfig {

    @Value("${fpb.start}")
    private Boolean start ;

    public Boolean isStart(){
        return start;
    }
}
