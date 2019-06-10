package com.wyy.service;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;

import org.codehaus.plexus.util.IOUtil;
import org.apache.maven.wagon.*;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagonAuthenticator;
import org.apache.maven.wagon.providers.http.LightweightHttpsWagon;
import org.apache.maven.wagon.repository.Repository;
import org.springframework.web.client.RestTemplate;
// import javax.annotation.PostConstruct;

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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String addArtifact(String shortUrl, File file) {
        long fileLength = file.length();
        StreamWagon streamWagon = null;
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
            streamWagon = this.createWagon();
            streamWagon.putFromStream(inputStream, shortUrl, fileLength, -1);
        } catch (Exception e) {
            return null;
        } finally {
            IOUtil.close(inputStream);
            if (streamWagon != null) {
                try {
                    streamWagon.disconnect();
                } catch (ConnectionException e) {
                }
            }
        }

        return this.url + "/" + shortUrl;  // 返回直接访问Nexus中文件的URL
    }

    public ByteArrayOutputStream getArtifact(String longUrl) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamWagon streamWagon = null;
        String shortUrl = longUrl.replace(this.url, "");

        try {
            streamWagon = this.createWagon();
            streamWagon.getToStream(shortUrl, outputStream);
        } catch (Exception e) {
            return null;
        } finally {
            IOUtil.close(outputStream);
            if (streamWagon != null) {
                try {
                    streamWagon.disconnect();
                } catch (ConnectionException e) {
                }
            }
        }

        return outputStream;
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

    private StreamWagon createWagon() throws ConnectionException, AuthenticationException, IllegalStateException {
        LightweightHttpWagon lw;
        if (this.getUrl().startsWith("http:")) {
            lw = new LightweightHttpWagon();
        } else if (this.getUrl().startsWith("https:")) {
            lw = new LightweightHttpsWagon();
        } else {
            throw new IllegalStateException("Unknown protocol in repository url: " + this.getUrl());
        }

        lw.setAuthenticator(new LightweightHttpWagonAuthenticator());
        lw.setPreemptiveAuthentication(true);
        AuthenticationInfo authenticationInfo = new AuthenticationInfo();
        authenticationInfo.setUserName(this.getUserName());
        authenticationInfo.setPassword(this.getPassword());
        Repository repository = new Repository("", this.getUrl());
        StreamWagon streamWagon = lw;
        streamWagon.connect(repository, authenticationInfo);
        return streamWagon;
    }
}
