package com.example.moneymanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IpController {

    @GetMapping("/ip")
    public String getIp() throws Exception{
        java.net.URL url=new java.net.URL("https://api.ipify.org");
        try(java.io.BufferedReader in=new java.io.BufferedReader(new java.io.InputStreamReader(url.openStream()))){
            return in.readLine();
        }
    }
}
