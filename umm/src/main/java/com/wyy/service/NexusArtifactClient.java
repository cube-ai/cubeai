package com.wyy.service;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

@Service
public class NexusArtifactClient {

    @Value("${nexus.maven.url}")
    private String url;

    @Value("${nexus.maven.username}")
    private String userName;

    @Value("${nexus.maven.password}")
    private String password;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean deleteArtifact(String longUrl) {
        try {
            CloseableHttpClient httpClient;
            if (getUserName() != null && getPassword() != null) {
                URL url = new URL(getUrl());
                HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new AuthScope(httpHost),
                    new UsernamePasswordCredentials(getUserName(), getPassword()));
                httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
            } else {
                httpClient = HttpClientBuilder.create().build();
            }
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
            restTemplate.delete(new URI(longUrl));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteDockerImage(String longUrl) {
        String imageTagPrefix = longUrl.substring(0, longUrl.indexOf('/'));
        String tempStr = longUrl.substring(longUrl.indexOf('/') + 1);
        String imageName = tempStr.substring(0, tempStr.indexOf(':'));
        String imageTag = tempStr.substring(tempStr.indexOf(':') + 1);

        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            // create basic authorization header
            String userMsg = userName + ":" + password;
            requestHeaders.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(userMsg.getBytes()));
            requestHeaders.set("Accept", "application/vnd.docker.distribution.manifest.v2+json");
            HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
            RestTemplate restTemplate = new RestTemplate();
            // get image's digest number
            String imageUrl = "https://" + imageTagPrefix + "/v2/" + imageName + "/manifests/";
            ResponseEntity<String> getEntity = restTemplate.exchange(imageUrl + imageTag, HttpMethod.GET,
                requestEntity, String.class);
            String digest = getEntity.getHeaders().getValuesAsList("Docker-Content-Digest").get(0);
            // delete image
            ResponseEntity<String> deleteEntity = restTemplate.exchange(imageUrl + digest, HttpMethod.DELETE,
                requestEntity, String.class);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
