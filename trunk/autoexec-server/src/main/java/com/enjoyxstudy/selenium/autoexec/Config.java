package com.enjoyxstudy.selenium.autoexec;

/**
 * @author onozaty
 */
public class Config {

    /** browsers */
    private String[] browsers;

    /** startURL */
    private String startURL;

    /** suite */
    private String suite;

    /** generateSuite */
    private boolean generateSuite;

    /** result */
    private String result;

    /** timeoutInSeconds */
    private int timeoutInSeconds = 60 * 60;

    /**
     * @return browsers
     */
    public String[] getBrowsers() {
        return browsers;
    }

    /**
     * @param browsers browsers
     */
    public void setBrowsers(String[] browsers) {
        this.browsers = browsers;
    }

    /**
     * @return generateSuite
     */
    public boolean isGenerateSuite() {
        return generateSuite;
    }

    /**
     * @param generateSuite generateSuite
     */
    public void setGenerateSuite(boolean generateSuite) {
        this.generateSuite = generateSuite;
    }

    /**
     * @return result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return startURL
     */
    public String getStartURL() {
        return startURL;
    }

    /**
     * @param startURL startURL
     */
    public void setStartURL(String startURL) {
        this.startURL = startURL;
    }

    /**
     * @return suite
     */
    public String getSuite() {
        return suite;
    }

    /**
     * @param suite suite
     */
    public void setSuite(String suite) {
        this.suite = suite;
    }

    /**
     * @return timeoutInSeconds
     */
    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    /**
     * @param timeoutInSeconds timeoutInSeconds
     */
    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

}
