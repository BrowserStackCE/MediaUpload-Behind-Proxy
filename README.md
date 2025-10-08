# webdriverio-appium-app-browserstack
This repository is to test if media upload is wroking as expected behind an SSL proxy

<div align="center">
<img src = "https://www.browserstack.com/images/layout/browserstack-logo-600x315.png" > <br>
</div>


## Setup

### Requirements
* JDK 11 

### Install the dependencies

```sh
mvn clean install
```

### Run sample test:
  - Test script is available in `src/main/java/com/browserstack/MediaUpload/TestMediaUpload.java` directory under examples folder

## Configure Environment Variables
  -  Configure BrowserStack username and accesskey them as environment variables using the below commands.
  
  - For \*nix based and Mac machines:

  ```sh
  export BROWSERSTACK_USERNAME=<browserstack-username> &&
  export BROWSERSTACK_ACCESS_KEY=<browserstack-access-key>
  ```

  - For Windows:

  ```shell
  set BROWSERSTACK_USERNAME=<browserstack-username>
  set BROWSERSTACK_ACCESS_KEY=<browserstack-access-key>
  ```
  - Set the proxy details in the code on the appropriate line as seen below 
```sh
      final String proxyHost = System.getProperty("http.proxyHost", "your-proxy-host");
      final String proxyPortStr = System.getProperty("http.proxyPort", "your-proxy-port");
  ```

## Running your tests
- To run tests, open in your IDE and trigger the main function